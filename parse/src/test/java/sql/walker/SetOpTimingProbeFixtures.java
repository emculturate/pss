package sql.walker;

/**
 * Shared SQL builders for Phase 2.8 set-op timing probes. Each branch uses a distinct
 * {@code probe_branch_NNN} table, {@code M} qualified select-list columns ({@code col_00}…),
 * and {@code M/2} fully qualified {@code ORDER BY} keys on non-output columns
 * ({@code sort_col_00}…).
 */
public final class SetOpTimingProbeFixtures {

	private SetOpTimingProbeFixtures() {
	}

	public static int orderByCountForSelectCount(int selectColumnCount) {
		return selectColumnCount / 2;
	}

	public static String selectColumnName(int columnIndex) {
		return "col_" + String.format("%02d", columnIndex);
	}

	public static String orderByColumnName(int orderIndex) {
		return "sort_col_" + String.format("%02d", orderIndex);
	}

	public static String branchTableName(int branchIndex) {
		return "probe_branch_" + String.format("%03d", branchIndex);
	}

	public static String buildQuery(String setOpKeyword, int joinerCount, int selectColumnCount, int orderByColumnCount) {
		int branchCount = joinerCount + 1;
		StringBuilder sql = new StringBuilder();
		for (int branchIndex = 0; branchIndex < branchCount; branchIndex++) {
			if (branchIndex > 0) {
				sql.append('\n').append(setOpKeyword).append(' ');
			}
			String tableName = branchTableName(branchIndex);
			sql.append("SELECT ");
			for (int columnIndex = 0; columnIndex < selectColumnCount; columnIndex++) {
				if (columnIndex > 0) {
					sql.append(", ");
				}
				String columnName = selectColumnName(columnIndex);
				sql.append(tableName).append('.').append(columnName).append(" AS ").append(columnName);
			}
			sql.append(" FROM ").append(tableName);
			sql.append(" ORDER BY ");
			for (int orderIndex = 0; orderIndex < orderByColumnCount; orderIndex++) {
				if (orderIndex > 0) {
					sql.append(", ");
				}
				String orderByColumnName = orderByColumnName(orderIndex);
				sql.append(tableName).append('.').append(orderByColumnName);
			}
		}
		return sql.toString();
	}

	public static String buildQuery(String setOpKeyword, int joinerCount, int selectColumnCount) {
		return buildQuery(
				setOpKeyword,
				joinerCount,
				selectColumnCount,
				orderByCountForSelectCount(selectColumnCount));
	}
}
