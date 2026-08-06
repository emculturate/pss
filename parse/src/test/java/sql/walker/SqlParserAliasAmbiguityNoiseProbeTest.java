package sql.walker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/**
 * Decision probe for optional Phase 20.9: does FULL_CONTEXT / AMBIGUITY noise
 * around {@code ... as alias} come only from Jinja table sources, or also from
 * plain tables / subqueries / nested set-ops?
 *
 * Clones the shape of {@code subqueryUnionJinjaSourceUnionInterfaceValidationV1Test}
 * / Except twin, plus a minimal {@code source(...) as alias} form.
 */
public class SqlParserAliasAmbiguityNoiseProbeTest {

	private static final class NoiseCounts {
		final String label;
		final int fullContext;
		final int ambiguity;
		final int otherRecoverable;

		NoiseCounts(String label, int fullContext, int ambiguity, int otherRecoverable) {
			this.label = label;
			this.fullContext = fullContext;
			this.ambiguity = ambiguity;
			this.otherRecoverable = otherRecoverable;
		}

		@Override
		public String toString() {
			return String.format("%-28s FULL_CONTEXT=%d AMBIGUITY=%d otherRecoverable=%d",
					label, fullContext, ambiguity, otherRecoverable);
		}
	}

	private NoiseCounts measure(String label, String query) {
		SqlParserAccess accessObject = new SqlParserAccess(true, true, true);
		accessObject.executeTheParse(query, SQLPARSER_SQL_TREE_KEY);
		Snippet snippet = accessObject.getSnippet();
		Assert.assertEquals(label + " should parse with 0 fatals: " + snippet.getFatalErrorStringList(),
				0, snippet.getFatalErrorCount());

		int fullContext = 0;
		int ambiguity = 0;
		int otherRecoverable = 0;
		List<ParseDiagnostic> diags = snippet.getParserDiagnosticList();
		if (diags != null) {
			for (ParseDiagnostic d : diags) {
				if (d == null || d.code() == null) {
					continue;
				}
				if ("FULL_CONTEXT".equals(d.code())) {
					fullContext++;
				} else if ("AMBIGUITY".equals(d.code())) {
					ambiguity++;
				} else if (d.recoverable()) {
					otherRecoverable++;
				}
			}
		}
		return new NoiseCounts(label, fullContext, ambiguity, otherRecoverable);
	}

	private static String unionTemplate(String leftFrom, String rightFrom) {
		return "select *\n"
				+ "from\n"
				+ "(   select mail_contacts.eab_contact_id\n"
				+ "    ,mail_contacts.audience\n"
				+ "    ,mail_contacts.stream_key\n"
				+ "    ,cast(mail_contacts.intake_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from " + leftFrom + "\n"
				+ "    union\n"
				+ "    select  offset_marketing.eab_contact_id\n"
				+ "    ,offset_marketing.audience\n"
				+ "    ,offset_marketing.stream_key\n"
				+ "    ,cast(offset_marketing.sent_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from " + rightFrom + "\n"
				+ ") as paper_data";
	}

	private static String exceptTemplate(String leftFrom, String rightFrom) {
		return "select *\n"
				+ "from\n"
				+ "(   select mail_contacts.eab_contact_id\n"
				+ "    ,mail_contacts.audience\n"
				+ "    ,mail_contacts.stream_key\n"
				+ "    ,cast(mail_contacts.intake_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from " + leftFrom + "\n"
				+ "    except\n"
				+ "    select  offset_marketing.eab_contact_id\n"
				+ "    ,offset_marketing.audience\n"
				+ "    ,offset_marketing.stream_key\n"
				+ "    ,cast(offset_marketing.sent_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from " + rightFrom + "\n"
				+ ") as paper_data";
	}

	@Test
	public void compareAliasAmbiguityNoiseAcrossFromShapes() {
		Map<String, NoiseCounts> results = new LinkedHashMap<String, NoiseCounts>();

		// --- Minimal: single FROM ... AS alias ---
		results.put("simple-jinja", measure("simple-jinja",
				"select * from {{ source('raw', 'orders') }} as mail_contacts"));
		results.put("simple-table", measure("simple-table",
				"select * from pdp_ams_mail_contacts as mail_contacts"));
		results.put("simple-subquery", measure("simple-subquery",
				"select * from (select 1 as eab_contact_id) as mail_contacts"));
		results.put("simple-nested-setop", measure("simple-nested-setop",
				"select * from ((select 1 as eab_contact_id) union (select 2 as eab_contact_id)) as mail_contacts"));

		// --- Clone of subqueryUnionJinjaSourceUnionInterfaceValidationV1Test ---
		results.put("union-jinja", measure("union-jinja", unionTemplate(
				"{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }} as mail_contacts",
				"{{ source('PDP_AMS', 'pdp_ams_offset_marketing') }} as offset_marketing")));
		results.put("union-table", measure("union-table", unionTemplate(
				"pdp_ams.pdp_ams_mail_contacts as mail_contacts",
				"pdp_ams.pdp_ams_offset_marketing as offset_marketing")));
		results.put("union-subquery", measure("union-subquery", unionTemplate(
				"(select eab_contact_id, audience, stream_key, intake_dt from pdp_ams_mail_contacts) as mail_contacts",
				"(select eab_contact_id, audience, stream_key, sent_dt from pdp_ams_offset_marketing) as offset_marketing")));
		results.put("union-nested-setop", measure("union-nested-setop", unionTemplate(
				"((select eab_contact_id, audience, stream_key, intake_dt from t1) union (select eab_contact_id, audience, stream_key, intake_dt from t2)) as mail_contacts",
				"((select eab_contact_id, audience, stream_key, sent_dt from t3) except (select eab_contact_id, audience, stream_key, sent_dt from t4)) as offset_marketing")));

		// --- Clone of subqueryExceptJinjaSourceExceptInterfaceValidationV1Test ---
		results.put("except-jinja", measure("except-jinja", exceptTemplate(
				"{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }} as mail_contacts",
				"{{ source('PDP_AMS', 'pdp_ams_offset_marketing') }} as offset_marketing")));
		results.put("except-table", measure("except-table", exceptTemplate(
				"pdp_ams.pdp_ams_mail_contacts as mail_contacts",
				"pdp_ams.pdp_ams_offset_marketing as offset_marketing")));
		results.put("except-subquery", measure("except-subquery", exceptTemplate(
				"(select eab_contact_id, audience, stream_key, intake_dt from pdp_ams_mail_contacts) as mail_contacts",
				"(select eab_contact_id, audience, stream_key, sent_dt from pdp_ams_offset_marketing) as offset_marketing")));
		results.put("except-nested-setop", measure("except-nested-setop", exceptTemplate(
				"((select eab_contact_id, audience, stream_key, intake_dt from t1) union (select eab_contact_id, audience, stream_key, intake_dt from t2)) as mail_contacts",
				"((select eab_contact_id, audience, stream_key, sent_dt from t3) except (select eab_contact_id, audience, stream_key, sent_dt from t4)) as offset_marketing")));

		System.out.println();
		System.out.println("=== Phase 20.9 alias / set-op ambiguity noise probe ===");
		for (NoiseCounts c : results.values()) {
			System.out.println(c);
		}
		System.out.println("=== end probe ===");
		System.out.println();

		// Observational probe — keep green as long as every variant parses.
		Assert.assertEquals(12, results.size());
	}
}
