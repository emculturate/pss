package sql.diagnostics;

import org.antlr.v4.runtime.Token;

import astwalkers.SqlASTWalkerHelper;

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
}