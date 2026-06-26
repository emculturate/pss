package sql.diagnostics;

import org.antlr.v4.runtime.Token;

import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;

/**
 * Shared diagnostics packaging helpers for SQL parse/walk logic.
 */
public class SqlParseDiagnosticService {

	private final SqlASTWalkerHelper walker;

	public SqlParseDiagnosticService(SqlASTWalkerHelper walker) {
		this.walker = walker;
	}

	public void emitFatal(String code, String message, Token token, String tokenText) {
		Integer line = token == null ? null : token.getLine();
		Integer charPos = token == null ? null : token.getCharPositionInLine();
		walker.addWalkerFatal(code, message, line, charPos, tokenText);
	}

	public void emitIntoOnlyAllowedOnFirstSetMember(String setOperationType, int memberPosition, Token token) {
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER);
		String diagMessageTemplate = walker.getDiagnosticMessage(
				SqlASTWalkerHelper.DIAG_SQL_INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER);
		String diagMessage = String.format(
				diagMessageTemplate,
				setOperationType,
				String.valueOf(memberPosition));

		emitFatal(diagCode, diagMessage, token, "INTO");
	}

	public void emitRelationalModifierAliasConflict(
			String retainedAlias,
			Token retainedAliasToken,
			String ignoredAlias,
			Token ignoredAliasToken) {
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_RELATIONAL_MODIFIER_ALIAS_CONFLICT);
		String diagMessageTemplate = walker
				.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_RELATIONAL_MODIFIER_ALIAS_CONFLICT);

		Integer retainedLine = retainedAliasToken == null ? null : retainedAliasToken.getLine();
		Integer retainedChar = retainedAliasToken == null ? null : retainedAliasToken.getCharPositionInLine();
		Integer ignoredLine = ignoredAliasToken == null ? null : ignoredAliasToken.getLine();
		Integer ignoredChar = ignoredAliasToken == null ? null : ignoredAliasToken.getCharPositionInLine();

		String diagMessage = String.format(
				diagMessageTemplate,
				String.valueOf(retainedAlias),
				String.valueOf(retainedLine),
				String.valueOf(retainedChar),
				String.valueOf(ignoredAlias),
				String.valueOf(ignoredLine),
				String.valueOf(ignoredChar));

		walker.addWalkerDiagnostic(
				ParseDiagnostic.Severity.SEVERE_WARNING,
				diagCode,
				diagMessage,
				retainedLine,
				retainedChar,
				walker.getClass().getSimpleName(),
				null,
				String.valueOf(retainedAlias),
				true,
				"ast-walk",
				null,
				null);
	}
}