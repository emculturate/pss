package errorhandling;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

/**
 * Structured context for parse-strategy {@code REPORT_ERROR} and listener
 * {@code SYNTAX_ERROR} fatals. Recovery cascades and semantic walker fatals
 * do not use this helper.
 *
 * <p>Optional diagnostic {@code details} keys: {@link #DETAIL_CONTEXT_SNIPPET},
 * {@link #DETAIL_SYNTAX_CLASS}, {@link #DETAIL_PARSER_RULES}.
 */
public final class ParseSyntaxErrorContext {

	public static final String DETAIL_CONTEXT_SNIPPET = "contextSnippet";
	public static final String DETAIL_SYNTAX_CLASS = "syntaxClass";
	public static final String DETAIL_PARSER_RULES = "parserRules";

	public static final String SYNTAX_CLASS_TEMPLATE_LIKE = "TEMPLATE_LIKE";
	public static final String SYNTAX_CLASS_DIALECT_EXTENSION = "DIALECT_EXTENSION";
	public static final String SYNTAX_CLASS_GRAMMAR_GAP = "GRAMMAR_GAP";

	private static final int MAX_LINE_SNIPPET = 160;
	private static final int WINDOW_CHARS = 40;
	private static final int LINE_PROBE_CHARS = 512;
	private static final int MAX_RULES = 3;
	private static final Pattern JINJA_DELIMITERS = Pattern.compile("\\{\\{|\\}\\}|\\{%|%\\}|\\{#|#\\}");

	private final String tokenText;
	private final int line;
	private final int charPositionInLine;
	private final String contextSnippet;
	private final String parserRule;
	private final String parserRules;
	private final String syntaxClass;

	private ParseSyntaxErrorContext(
			String tokenText,
			int line,
			int charPositionInLine,
			String contextSnippet,
			String parserRule,
			String parserRules,
			String syntaxClass) {
		this.tokenText = tokenText;
		this.line = line;
		this.charPositionInLine = charPositionInLine;
		this.contextSnippet = contextSnippet;
		this.parserRule = parserRule;
		this.parserRules = parserRules;
		this.syntaxClass = syntaxClass;
	}

	public static ParseSyntaxErrorContext capture(Parser recognizer, Token offendingToken) {
		String tokenText = offendingToken == null ? null : offendingToken.getText();
		int line = offendingToken == null ? -1 : offendingToken.getLine();
		int charPositionInLine = offendingToken == null ? -1 : offendingToken.getCharPositionInLine();
		String contextSnippet = extractContextSnippet(offendingToken);
		String parserRules = extractParserRules(recognizer);
		String parserRule = innermostRule(parserRules);
		String syntaxClass = classifySyntax(tokenText, contextSnippet);
		return new ParseSyntaxErrorContext(
				tokenText,
				line,
				charPositionInLine,
				contextSnippet,
				parserRule,
				parserRules,
				syntaxClass);
	}

	public String tokenText() {
		return tokenText;
	}

	public int line() {
		return line;
	}

	public int charPositionInLine() {
		return charPositionInLine;
	}

	public String contextSnippet() {
		return contextSnippet;
	}

	public String parserRule() {
		return parserRule;
	}

	public String parserRules() {
		return parserRules;
	}

	public String syntaxClass() {
		return syntaxClass;
	}

	/**
	 * Short one-line fatal message: location, offending token, parser rule, and snippet.
	 */
	public String formatFatalMessage() {
		StringBuilder message = new StringBuilder();
		message.append("Line ").append(line).append(':').append(charPositionInLine);
		if (tokenText != null) {
			message.append(" - unexpected input: '").append(tokenText).append("'");
		} else {
			message.append(" - syntax error");
		}
		if (parserRule != null && !parserRule.isBlank()) {
			message.append(" in rule ").append(parserRule);
		}
		if (contextSnippet != null && !contextSnippet.isBlank()) {
			message.append(": ").append(contextSnippet);
		}
		return message.toString();
	}

	public Map<String, String> toDetails() {
		Map<String, String> details = new LinkedHashMap<>();
		if (contextSnippet != null && !contextSnippet.isBlank()) {
			details.put(DETAIL_CONTEXT_SNIPPET, contextSnippet);
		}
		if (syntaxClass != null && !syntaxClass.isBlank()) {
			details.put(DETAIL_SYNTAX_CLASS, syntaxClass);
		}
		if (parserRules != null && !parserRules.isBlank()) {
			details.put(DETAIL_PARSER_RULES, parserRules);
		}
		return details.isEmpty() ? null : details;
	}

	static String extractContextSnippet(Token token) {
		if (token == null) {
			return null;
		}
		CharStream input = token.getInputStream();
		if (input == null || input.size() <= 0) {
			return normalizeSnippet(token.getText());
		}

		int tokenStart = token.getStartIndex();
		int tokenStop = token.getStopIndex();
		if (tokenStart < 0) {
			return normalizeSnippet(token.getText());
		}
		if (tokenStop < tokenStart) {
			tokenStop = tokenStart;
		}

		int streamLast = input.size() - 1;
		int probeFrom = Math.max(0, tokenStart - LINE_PROBE_CHARS);
		int probeTo = Math.min(streamLast, tokenStop + LINE_PROBE_CHARS);
		String probe = input.getText(Interval.of(probeFrom, probeTo));
		int relativeStart = tokenStart - probeFrom;
		int lineStartInProbe = lastLineBreakBefore(probe, relativeStart);
		int lineEndInProbe = nextLineBreakAtOrAfter(probe, Math.max(relativeStart, tokenStop - probeFrom));
		int lineStart = probeFrom + lineStartInProbe;
		int lineEnd = probeFrom + lineEndInProbe;
		if (lineEnd < lineStart) {
			lineEnd = lineStart;
		}

		String line = normalizeSnippet(input.getText(Interval.of(lineStart, lineEnd)));
		if (line == null) {
			return normalizeSnippet(token.getText());
		}
		if (line.length() <= MAX_LINE_SNIPPET) {
			return line;
		}

		int windowStart = Math.max(lineStart, tokenStart - WINDOW_CHARS);
		int windowEnd = Math.min(lineEnd, tokenStop + WINDOW_CHARS);
		String window = normalizeSnippet(input.getText(Interval.of(windowStart, windowEnd)));
		if (window == null) {
			return line.substring(0, MAX_LINE_SNIPPET);
		}
		String prefix = windowStart > lineStart ? "..." : "";
		String suffix = windowEnd < lineEnd ? "..." : "";
		return prefix + window + suffix;
	}

	static String classifySyntax(String tokenText, String contextSnippet) {
		if (looksLikeTemplate(tokenText) || looksLikeTemplate(contextSnippet)) {
			return SYNTAX_CLASS_TEMPLATE_LIKE;
		}
		return SYNTAX_CLASS_GRAMMAR_GAP;
	}

	private static String extractParserRules(Parser recognizer) {
		if (recognizer == null) {
			return null;
		}
		List<String> stack = recognizer.getRuleInvocationStack();
		if (stack == null || stack.isEmpty()) {
			return null;
		}
		int count = Math.min(MAX_RULES, stack.size());
		StringBuilder joined = new StringBuilder();
		for (int i = 0; i < count; i++) {
			String rule = stack.get(i);
			if (rule == null || rule.isBlank()) {
				continue;
			}
			if (joined.length() > 0) {
				joined.append(',');
			}
			joined.append(rule);
		}
		return joined.length() == 0 ? null : joined.toString();
	}

	private static String innermostRule(String parserRules) {
		if (parserRules == null || parserRules.isBlank()) {
			return null;
		}
		int comma = parserRules.indexOf(',');
		return comma < 0 ? parserRules : parserRules.substring(0, comma);
	}

	private static boolean looksLikeTemplate(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}
		String trimmed = text.trim();
		if ("{{".equals(trimmed) || "}}".equals(trimmed)
				|| "{%".equals(trimmed) || "%}".equals(trimmed)
				|| "{#".equals(trimmed) || "#}".equals(trimmed)) {
			return true;
		}
		return JINJA_DELIMITERS.matcher(text).find();
	}

	private static String normalizeSnippet(String text) {
		if (text == null) {
			return null;
		}
		String normalized = text.replace('\t', ' ').trim();
		return normalized.isEmpty() ? null : normalized;
	}

	private static int lastLineBreakBefore(String probe, int relativeIndex) {
		int from = Math.min(Math.max(relativeIndex - 1, 0), probe.length() - 1);
		for (int i = from; i >= 0; i--) {
			char ch = probe.charAt(i);
			if (ch == '\n' || ch == '\r') {
				return i + 1;
			}
		}
		return 0;
	}

	private static int nextLineBreakAtOrAfter(String probe, int relativeIndex) {
		int from = Math.min(Math.max(relativeIndex, 0), probe.length());
		for (int i = from; i < probe.length(); i++) {
			char ch = probe.charAt(i);
			if (ch == '\n' || ch == '\r') {
				return Math.max(i - 1, 0);
			}
		}
		return probe.length() - 1;
	}
}
