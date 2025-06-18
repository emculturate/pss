/**
 * Package contains classes used to normalize, analyze, generate and perform standard operations with SQL Templates
 * 
 * SQL Templates are snippets of SQL that contain substitution variables of various types and which represent re-usable, macro-like SQL phrases.
 * 
 * SQL Templates are composable, so long as variables are filled with compatible substitution SQL by type.
 */
package mumble.sql.template;

import java.util.HashMap;

import static mumble.sql.MumbleConstants.*;

import mumble.sql.Snippet;

/**
 * The SQLNormalizer takes a SQL AST object and applies several transformations
 * to it to make it easier to compare to other SQL AST trees. It should be used
 * before the template discoveror attempts to generate a common template.
 * 
 * @author Geoff Howe January 2018
 *
 */
public class SQLNormalizer extends AbstractASTWalker {

	/**
	 * @param snip
	 */
	public SQLNormalizer(Snippet snip) {
		super();
		this.snip = snip;
	}

	@Override
	public String toString() {
		return "SQLNormalizer [snip=" + snip.getSqlAbstractTree() + "]";
	}

	// Ok now to business

	public boolean normalize() {
		boolean status = true;
		try {
			// Determine Type of Snippet
			HashMap<String, Object> hold = snip.getSqlAbstractTree();
			HashMap<String, Object> norm = new HashMap<String, Object>();

			if (hold.containsKey("SQL")) {
				// Query AST
				System.out.println("Starting walking SQL Tree");
				traverseSqlTree(hold, norm);
			} else if (hold.containsKey("PREDICAND")) {
				// Predicand AST

			} else if (hold.containsKey("CONDITION")) {
				// Condition AST

			} else {
				// Unrecognized AST type
				System.err.println("Unrecognized SQL AST: " + snip.toString());
			}

		} catch (Exception e) {
			System.err.println("Normalizing exception: " + snip.toString());
			System.err.println("Normalizing Exception: " + e.getMessage());
			return false;
		} finally {

		}
		return status;
	}
}
