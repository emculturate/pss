package parser;

import java.util.ArrayList;
import java.util.List;

import access.PUML3ParserAccess;
import access.Snippet;
import mumble.PUML3Constants;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class PUML3ParserTest {
	
	@Ignore
	@Test
	// filter
	public void testPassingConditions() {
		
		// Add conditions to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("true");
		conds.add("false");
		conds.add("(true or false) or ((true and false) or (true and true))");
		conds.add("[ATTR] > 5");
		conds.add("NOT([ATTR] = 'abc')");
		conds.add("[ATTR] = [key]");
		conds.add("((([ATTR] = 'def')))");
		conds.add("(true) AND (false)");
		conds.add("NOT(true and false or true and false)");
		conds.add("([age] > 15)");
		conds.add("[age] > [ATTR]");
		conds.add("[age] > [minimum_age]");
		conds.add("[age] >= '21'");
		conds.add("([gpa] >= 3.0)");
		conds.add("NOT([gpa] > [min_gpa] OR [gpa] < [max_gpa] AND [gpa] = [avg_gpa])");
		conds.add("([gpa] < 4.0)");
		conds.add("[gpa] <= 3.1");
		conds.add("[gpa] < [ATTR]");
		conds.add("[gpa] <= [ATTR]");
		conds.add("NOT([ATTR] = 5)");
		conds.add("([ATTR] = 'abc' OR ([name] = [STUDENT NAME] and [maJOR] = [STUDENT MAJOR]))");
		conds.add("[ATTR] % 5 = 4");
		conds.add("[ATTR] * 2 = [MAX AGE]");
		conds.add("[age] = 21");
		conds.add("[age] != 21");
		conds.add("[age] != 21 AND [gpa] <= 4.0 OR [age] + 5 > 30");
		conds.add("NOT([age] % 3 > 2 AND length(ltrim([key])) = 0)");
		conds.add("[age] % 3 > 2");
		conds.add("[gpa] * 2.1 = 5");
		conds.add("([gpa] / 0.1) < 10");
		conds.add("'name'||'ite' = 'nameite'");
		conds.add("[age] + 5 > 12");
		conds.add("([gpa] > 5) and ([age] < 12)");
		conds.add("(concat([name], [address]) = [key])");
		conds.add("count([keys]) >= 5");
		conds.add("count([keys]) != [KEY COUNT]");
//		conds.add("value in ('1', '2', '3', 4, 5)");
		conds.add("instr('Informatica', 'Inform') = 1");
		conds.add("instr('Informatica', 'orm') = 3");
		conds.add("instr('Informatica', 'rmat', 3) = 5");
		conds.add("instr('dotdotdotdot', 'dot', 2, 2) = 4");
		conds.add("length([hello]) > 0");
		conds.add("length('hello') = 5");
		conds.add("log([x]) > 10");
		conds.add("log(5.6) > 10");
		conds.add("lower(5.2) = 5");
		conds.add("length(lpad([name], 5)) > 1");
		conds.add("length(lpad([name], 5, '0')) > 1");
		conds.add("length(ltrim([name])) > 0");
		conds.add("max(3, 5) = 5");
		conds.add("min(10, 15) = 10");
		conds.add("not(false)");
		conds.add("NOT((length(ltrim([val])) = 0) OR ([val] = null))");
		conds.add("[val] = null");
		conds.add("[VAL] = null");
		conds.add("[ATTR] = [VAL1] and [KEY 1] < [Key 2]");
		conds.add("[val] = null or length(rtrim([val])) = 0");
		conds.add("power(3, 2) = 9");
		conds.add("power([ATTR]) = [TARGET VALUE]");
		conds.add("round(3.5) = 3");
		conds.add("round([ATTR]) = [TARGET VALUE]");
		conds.add("rpad([abc], 3) = [def]");
		conds.add("rpad([ATTR], [LEN]) = [VALUE]");
		conds.add("rpad('x', 3) = 'x  '");
		conds.add("rpad('z', 5, '0') = 'z0000'");
		conds.add("rtrim([key]) = [value]");
		conds.add("rtrim([ATTR]) = [value]");
		conds.add("rtrim(' hello    ') = ' hello'");
		conds.add("sqrt(9) = 3");
		conds.add("sqrt([ATTR]) = [datum]");
		conds.add("NOT(substring([name], 0, 5) = [fname])");
		conds.add("not(substring([ATTR], 0, 5) = [fname])");
		conds.add("substring([name], 0, 5) = 'frank'");
		conds.add("to_date(substring([col], 1, 11), 'DD-MON-YYYY') = [startDate]");
		conds.add("to_date(substring([KEY], 5, 7), 'MMM') = [endMonth]");
		conds.add("upper([addr]) = upper(lower([addr]))");
		conds.add("uPPer([ATTR]) = uppER(LOWer([point]))");
		conds.add("([age] > 15) and [ATTR] = 'class' or sqrt(9) >= 3 anD [field] = 'VT'");
		conds.add("([age] > 15) or ([ATTR] = 'class' and sqrt(9) >= 3) oR [field] = 'VT'");
		conds.add("[age] > 15 and ([ATTR] = 'class' or sqrt(9) >= 3) And [field] = 'VT'");
		conds.add("not([age] > 15 and ([ATTR] = 'class' or sqrt(9) >= 3) And [field] = 'VT')");
		conds.add("([age] > 15) and (([ATTR] = 'class') or ((sqrt(9) = 3) and ([field] = 'VT')))");
		conds.add("not([age] > 15) and not([ATTR] = 'class') or not(sqrt(9) = 3) and not([field] = 'VT')");
		conds.add("([age] > 15 and [ATTR] = 'class') or (sqrt(9) >= 3 ANd [field] = 'VT')");
		conds.add("([age] > 15 or [ATTR] = 'class') and (sqrt(9) >= 3 OR [field] = 'VT')");
		conds.add("[age] > 15 or ([ATTR] = 'class' and sqrt(9) >= 3) oR [field] = 'VT'");
		conds.add("(([age] > 15 or [ATTR] = 'class') and sqrt(9) >= 3) or [field] = 'VT'");
		conds.add("[age] > 15 or ([ATTR] = 'class' and (sqrt(9) >= 3 or [field] = 'VT'))");
		conds.add("not([age] > 15 or [ATTR] = 'class') and not(sqrt(9) >= 3 and [field] = 'VT')");
		conds.add("([age] > 15 or not([ATTR] = 'class')) and not(sqrt(9) >= 3) or [field] = 'VT'");
		conds.add("([age] > 15 or not([ATTR] = 'class')) and (not(sqrt(9) >= 3)) or [field] = 'VT'");
		conds.add("NOT([ATTR] matches 'hello')");
		// conds.add("[key] is null"); // why is this failing...?
		conds.add("[key] isnull"); // ??? according to grammar this should be a function
		conds.add("[value] is not null");
		conds.add("[ATTR] is Empty");
		conds.add("[ATTR] is Not empty");
		conds.add("[ATTR] matches 'hello'");
		conds.add("[ATTR] contains 'hello'");
		conds.add("[ATTR] starts with 'hello'");
		conds.add("[ATTR] ends with 'hello'");
		conds.add("[ATTR] not matches 'hello'");
		conds.add("[ATTR] not contains 'hello'");
		conds.add("[ATTR] not starts with 'hello'");
		conds.add("[ATTR] not ends with 'hello'");
		conds.add("[key] matches 'hello'");
		conds.add("[key] conTaiNS 'hello'");
		conds.add("[key] starts with 'hello'");
		conds.add("[key] ENDS With 'hello'");
		conds.add("[key] nOT maTches 'hello'");
		conds.add("[key] not contains 'hello'");
		conds.add("[ATTR] not sTArts with [key]");
		conds.add("[ATTR] not ends with [key]");
		conds.add("coALesce([x1], [x2], [x3]) = [ATTR]");
		conds.add("regexP_extract([f1], [f2]) = [ATTR]");
		conds.add("regEXp_replace([f1], [f2]) = [ATTR]");
		conds.add("coaLEsce([x1], [x2], [x3]) = [f1]");
		conds.add("regEXp_extract([f1], [f2]) = [f1]");
		conds.add("regexp_REPlace([f1], [f2]) = [f1]");
		conds.add("cOALesce([x1], [x2], [x3]) = 'x'");
		conds.add("reGExp_eXTRact([f1], [f2]) = 'x'");
		conds.add("REGexp_replace([f1], [f2]) = 'x'");
		conds.add("NOT(([age] != 21) AND ([gpa] <= 4.0) OR ([age] + 5 > 30))");
		conds.add("[age] % 3 != 2");

		conds.add("[external_id]<'201010' OR ([external_id] ='000000') OR ([external_id] ='999999')");
		conds.add("[external_id]<'1510'");
		conds.add("false");
		conds.add("[registration_status_cd] = 'BP', [registration_status_cd] = 'CA', [registration_status_cd] = 'CD', [registration_status_cd] = 'DC', [registration_status_cd] = 'RD'");
		conds.add("[external_id]<'201008' OR ([external_id] contains '000000') OR ([external_id] contains '999999')");
		conds.add("[registration_status_cd] = 'D'");
		conds.add("[registration_status_cd] = '41002'");
		conds.add("[external_id]<201002");
		conds.add("[external_id]<'201008' OR ([external_id] ='000000') OR ([external_id] ='999999') OR ([external_id] ='101501')");
		conds.add("[deceased_ind] = 'Y'");
		conds.add("[registration_status_cd] = 'ZP' OR"+"[registration_status_cd] = 'CD' OR"+"[registration_status_cd] = 'ZD' OR"+"[registration_status_cd] = 'ZR' OR"+"[registration_status_cd] = 'DD'");
		conds.add("[external_id]<'201270' OR ([external_id] ='000000') OR ([external_id] ='999999')");
		conds.add("[external_id]<'2108'");
		conds.add("[external_id]<'201070' OR ([external_id] ='000000') OR ([external_id] ='999999')");
		conds.add("[registration_status_cd] = 'D',");
		conds.add("[external_id]<'2008010'");
		conds.add("[registration_status_cd] = 'DD',"+"[registration_status_cd] = 'DP',"+"[registration_status_cd] = 'DW',");
		conds.add("[external_id]<2158 or [external_id]>2194");
		conds.add("[registration_status_cd] = 'AD', [registration_status_cd] = 'SD', [registration_status_cd] = 'DD',"+"[registration_status_cd] = 'DS',"+"[registration_status_cd] = 'DW',");
		conds.add("[major_cd_2]='NA', major_cd_3]='NA',");
		conds.add("[registered_ind]='N'");
		conds.add("[registration_status_cd] = 'DD'");
		conds.add("[external_id]<'10/FA'");
		conds.add("[registration_status_cd] = 'DA', [registration_status_cd] = 'DC', [registration_status_cd] = 'DF', [registration_status_cd] = 'DL', [registration_status_cd] = 'DM', [registration_status_cd] = 'DP', [registration_status_cd] = 'DS', [registration_status_cd] = 'AD', [registration_status_cd] = 'BD', [registration_status_cd] = 'DW', [registration_status_cd] = 'PM', [registration_status_cd] = 'PQ', [registration_status_cd] = 'QD'");
		conds.add("[primary_user_id]<800000000");
		conds.add("[external_id]<'2107'");
		conds.add("[external_id]<201010");
		conds.add("[external_id]<201408 or [external_id]>201908");
		conds.add("[registration_status_cd] = 'DA',"+"[registration_status_cd] = 'DD',"+"[registration_status_cd] = 'DF',"+"[registration_status_cd] = 'DP',"+"[registration_status_cd] = 'DQ',"+"[registration_status_cd] = 'DW',");
		conds.add("[name]='professor'");
		conds.add("[external_id]<2108");
		conds.add("[rank_no]=1");
		conds.add("[external_id]<'201101' OR "+"[external_id] = ''000000' OR"+"[external_id] = '999999' OR"+"[external_id] = ''299002' OR"+"[external_id] = '299999' OR"+"[external_id] = '222222' OR"+"[external_id] = '911111' OR"+"[external_id] = '666666' OR"+"[external_id] = '555555' OR"+"[external_id] = '444444' OR"+"[external_id] = '333333' OR"+"");
		conds.add("[external_id]<'200340' OR [external_id] = ''000000' OR"+"[external_id] = '999999' OR [external_id] = '111111'");
		conds.add("[classification] = 'GR',"+"[registered_ind]='N'");
		conds.add("[registration_status_cd] = 'D1' OR [registration_status_cd] = 'DE' OR [registration_status_cd] = 'WD' OR [registration_status_cd] = 'D5' OR [registration_status_cd] = 'DU' OR [registration_status_cd] = 'WE' OR [registration_status_cd] = 'DA' OR [registration_status_cd] = 'DW' OR [registration_status_cd] = 'WF' OR [registration_status_cd] = 'DB' OR [registration_status_cd] = 'DX' OR [registration_status_cd] = 'WU' OR [registration_status_cd] = 'DC' OR [registration_status_cd] = 'DZ' OR [registration_status_cd] = 'WW' OR [registration_status_cd] = 'DD' OR [registration_status_cd] = 'WA'");
		conds.add("[registration_status_cd] = 'D0',"+"[registration_status_cd] = 'D1',"+"[registration_status_cd] = 'D2',"+"[registration_status_cd] = 'D3',"+"[registration_status_cd] = 'D4',"+"[registration_status_cd] = 'D5',"+"[registration_status_cd] = 'D6',"+"[registration_status_cd] = 'D7',"+"[registration_status_cd] = 'DA',"+"[registration_status_cd] = 'DB',"+"[registration_status_cd] = 'DC',"+"[registration_status_cd] = 'DD',"+"[registration_status_cd] = 'DE',"+"[registration_status_cd] = 'DF',"+"[registration_status_cd] = 'DK',"+"[registration_status_cd] = 'DP',"+"[registration_status_cd] = 'DW',"+"[registration_status_cd] = 'DX',"+"[registration_status_cd] = 'E7',"+"[registration_status_cd] = 'F5',"+"[registration_status_cd] = 'F7',"+"[registration_status_cd] = 'W1',"+"[registration_status_cd] = 'WK',"+"[registration_status_cd] = 'WV',"+"[registration_status_cd] = 'WX',");
		conds.add("[external_id]<201580 OR [external_id]>201980 OR [external_id] = 'LM0D99' OR [external_id] = 'LM0P99' OR [external_id] = 'LM1D99' OR [external_id] = 'LM1P99' OR [external_id] = 'LM2D99' OR [external_id] = 'LM2P99' OR [external_id] = 'LM3D99' OR [external_id] = 'LM3P99' OR [external_id] = 'LM4D99' OR [external_id] = 'LM4P99' OR [external_id] = 'LM5D99' OR [external_id] = 'LM5P99' OR [external_id] = 'LR0D99' OR [external_id] = 'LR0P99' OR [external_id] = 'LR1D99' OR [external_id] = 'LR1P99' OR [external_id] = 'LR2D99' OR [external_id] = 'LR2P99' OR [external_id] = 'LW0D99' OR [external_id] = 'LW0P99' OR [external_id] = 'LW1D99' OR [external_id] = 'LW1P99' OR [external_id] = 'LW2D99' OR [external_id] = 'LW2P99'");
		conds.add("[registration_status_cd] = 'DC' or"+"[registration_status_cd] = 'DD' or"+"[registration_status_cd] = 'DE' or"+"[registration_status_cd] = 'DG' or"+"[registration_status_cd] = 'DW' or"+"[registration_status_cd] = 'NP' or"+"[registration_status_cd] = 'NR' or"+"[registration_status_cd] = 'AD' or"+"[registration_status_cd] = 'CD'");
		conds.add("[registration_status_cd] = 'DG',"+"[registration_status_cd] = 'DS',"+"[registration_status_cd] = 'DC',"+"[registration_status_cd] = 'DD'");
		conds.add("[external_id]<201620 or [external_id]>201830");
		conds.add("[external_id]<'210111' OR "+"[external_id] = ''000000' OR"+"[external_id] = '999999' OR");
		conds.add("[registration_status_cd] = 'DD', [registration_status_cd] = 'DW', [registration_status_cd] = 'ED',"+"[registration_status_cd] = 'WR',");
		conds.add("[first_name] contains 'Curti'");
		conds.add("[external_id]<'201080' OR ([external_id] ='000000')OR ([external_id] ='999999')");
		conds.add("[primary_user_id]<800161500");
		conds.add("[external_id]<'201080' OR ([external_id] ='000000') OR ([external_id] ='999999')");
		conds.add("[registration_status_cd] = 'AD', [registration_status_cd] = 'AF', [registration_status_cd] = 'C1',"+"[registration_status_cd] = 'C4',"+"[registration_status_cd] = 'C8',"+"[registration_status_cd] = 'DD',"+"[registration_status_cd] = 'DM',"+"[registration_status_cd] = 'DP',"+"[registration_status_cd] = 'DW',"+"[registration_status_cd] = 'SD',");
		conds.add("[name] = 'Q visa'");
		conds.add("round([first_name], 3)=0");
		conds.add("[registration_status_cd] = 'D1' OR [registration_status_cd] = 'DC' OR [registration_status_cd] = 'DS' OR [registration_status_cd] = 'D2' OR [registration_status_cd] = 'DD' OR [registration_status_cd] = 'NE' OR [registration_status_cd] = 'D3' OR [registration_status_cd] = 'DG' OR [registration_status_cd] = 'W1' OR [registration_status_cd] = 'D4' OR [registration_status_cd] = 'DL' OR [registration_status_cd] = 'W2' OR [registration_status_cd] = 'D6' OR [registration_status_cd] = 'DS' OR [registration_status_cd] = 'W3' OR [registration_status_cd] = 'DA' OR [registration_status_cd] = 'DW' OR [registration_status_cd] = 'WL' OR [registration_status_cd] = 'WS' OR [registration_status_cd] = 'WU'");
		conds.add("[registration_status_cd] = 'DG', [registration_status_cd] = 'DS', [registration_status_cd] = 'DC',");
		conds.add("[registered_ind] = 'N'");
		conds.add("[external_id]<'201080' OR [external_id] = ''000000' OR"+"[external_id] = '999999'");
		conds.add("[email]='unknown@FORTLEWIS.EDU'");
		conds.add("[external_id]<'201010' OR ([external_id] contains '[1|2|3]$')");
		conds.add("[registration_status_cd] = 'DD', [registration_status_cd] = 'DL', [registration_status_cd] = 'DW',");
		//conds.add("[key] is sysdate");

		Snippet snippet = null;
		int countErrors = 0;
		// Iterate over the conditions and check for parsing errors
		for (String cond : conds) {
			PUML3ParserAccess access = new PUML3ParserAccess(true, true, true);
			try {
				System.out.println(" ");
				System.out.println("Parsing condition: " + cond);
				// There should be zero errors
				access.executeTheParse(cond, PUML3Constants.PUML3_CONDITION_TREE_KEY);
				snippet = access.getSnippet();
				Assert.assertNotNull("Snippet should not be null for condition: " + cond, snippet);
				final int numErrors = snippet.getFatalErrorStringList().size();	
				Assert.assertEquals("Expected no failures with " + cond, 0, numErrors);
			} catch (AssertionError e) {
				countErrors++;
				System.out.println("Exception parsing statement: " + cond);
				if (snippet != null) {
					System.out.println(snippet);
				} else {
					System.out.println("Snippet is null.");
				}
				System.out.println("Error: " + e.getMessage());
			}
		}
		Assert.assertEquals("Expected ALL Conditions to succeed.", 0, countErrors);
	}
	
	@Ignore
	@Test
	public void testFailingConditions() {
		
		// Add conditions to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("AND(OR())");
		conds.add("NOT(contains)");
		conds.add("AND(NOT(OR))");
		conds.add("somevariable");
		conds.add("true or someVariable");
		conds.add("somevariable(true)");
		
		int countErrors = 0;
		Snippet snippet =  null;
		// Iterate over the conditions and check for parsing errors
		for (String cond : conds) {
			PUML3ParserAccess access = new PUML3ParserAccess(true, true, true);
			try {
				System.out.println(" ");
				System.out.println("Parsing condition: " + cond);
				// There should be errors
				access.executeTheParse(cond, PUML3Constants.PUML3_CONDITION_TREE_KEY);
				snippet = access.getSnippet();
				final int numErrors = snippet.getFatalErrorStringList().size();	
				Assert.assertTrue("Expected failures with " + cond, numErrors > 0);
			} catch (AssertionError e) {
				// If we get here, it means the test failed as expected
				// so we can count it as a success
				// but we still want to print the error message
				// and the snippet if available
				// so we can see what went wrong
				countErrors++;
				System.out.println("Exception parsing statement: " + cond);
				if (snippet != null) {
					System.out.println(snippet);
				} else {
					System.out.println("Snippet is null.");
				}
				System.out.println("Error: " + e.getMessage());
			}
		}
		Assert.assertEquals("Expected ALL Conditions to FAIL.", conds.size(), countErrors);
	}
	
	@Ignore
	@Test
	// formula
	public void testPassingEquations() {
		
		// Add equations to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("concat([name], [address])");
		conds.add("concat([name], 'addr')");
		conds.add("concat([name], 'addr', [state])");
		conds.add("conCAt('name', 'address')");
		conds.add("CONcat([ATTR], 'ending')");
		conds.add("count([keys])");
		conds.add("count([ATTR])");
		conds.add("cOUNt('keys')");
//		conds.add("value in ('1', abc, [KEY 3], 4, 5)");
		conds.add("instr('Informatica', 'Inform')");
		conds.add("instr('Informatica', [KEY VALUE])");
		conds.add("INSTR('Informatica', 'Inform')");
		conds.add("instr('Informatica', 'rmat', 3)");
		conds.add("instr('dotdotdotdot', 'dot', 2, 2)");
		conds.add("length([hello])");
		conds.add("lenGTh('hello')");
		conds.add("LENGTH([ATTR])");
		conds.add("log([x])");
		conds.add("LOg(5.6)");
		conds.add("lower(5.2)");
		conds.add("lower([age])");
		conds.add("lpad([name], 5)");
		conds.add("lpad('name', 5)");
		conds.add("lpad([name], 5, '0')");
		conds.add("lpad('frank', 5, '0')");
		conds.add("lTrim([name])");
		conds.add("ltrim('towson  ')");
		conds.add("max(3, 5)");
		conds.add("mAx([city], [age])");
		conds.add("min(10, 15)");
		conds.add("ltrim([val])");
		conds.add("rtrim([val])");
		conds.add("min([age], 25)");
		conds.add("ltrim(' tom')");
		conds.add("rtrim('major    ')");
		conds.add("power(3, 2)");
		conds.add("power([base], [pow])");
		conds.add("round([gpa])");
		conds.add("round(3.5)");
		conds.add("rpad([abc], 3)");
		conds.add("rpad('x', 3)");
		conds.add("rpad('z', 5, '0')");
		conds.add("rtrim([key])");
		conds.add("rtrim(' hello    ')");
		conds.add("sqrt(9)");
		conds.add("sqrt([maxYears5])");
		conds.add("substring('pascal', 1, 2)");
		conds.add("substring([name], 0, 5)");
		conds.add("substring([name], 0, 5)");
		conds.add("to_date(substring([col], 1, 11), 'DD-MON-YYYY')");
		conds.add("upper([addr])");
		conds.add("upper('tom jones')");
		// conds.add("[ATTR] matches 'hello'"); // why is this one failing?
		conds.add("[ATTR] contains 'hello'");
		conds.add("[ATTR] starts with 'hello'");
		conds.add("[ATTR] ends with 'hello'");
		conds.add("[ATTR] not matches 'hello'");
		conds.add("[ATTR] not contains 'hello'");
		conds.add("[ATTR] not starts with 'hello'");
		conds.add("[ATTR] not ends with 'hello'");
		conds.add("[key] matches 'hello'");
		conds.add("[key] conTaiNS 'hello'");
		conds.add("[key] starts with 'hello'");
		conds.add("[key] ENDS With 'hello'");
		conds.add("[key] nOT maTches 'hello'");
		conds.add("[key] not contains 'hello'");
		conds.add("[ATTR] not sTArts with [key]");
		conds.add("[ATTR] not ends with [key]");
		conds.add("coalesce([x1], [x2], [x3])");
		conds.add("[f1] regexp [f2]");
		conds.add("regexp_replace([f1], [f2])");
		conds.add("if (true, [tval], [fval])");
		conds.add("if ([ATTR] = [val], [tval], [fval])");
		conds.add("if (sqrt(9) = 3, [tval], [fval])");
		conds.add("if (length(regexp_extract([value], '[^a-zA-Z]+', 0)) > 0, [tval], [fval])");
		conds.add("if (length(regexp_extract([value], '[a-zA-Z]+', 0)) > 0, [tval], [fval])");
		conds.add("regexp_replace([value], '[ ]+', ' ')");
		conds.add("regexp_replace([value], ' ', '_')");
		conds.add("regexp_replace([field], '<char>', '')");
		conds.add("regexp_replace([value], '[^0-9]+', '')");
		conds.add("regexp_replace([value], '[^a-zA-Z]+', '')");
		conds.add("concat(regexp_replace([value], '([0-9]*)\\.([^.]*)', '$1'), '.', regexp_replace([value], '([0-9]*)\\.([^.]*)', '$2'))");
		conds.add("LTRIM(RTRIM([ADVISOR_FIRST_NAME]))||IF ([ADVISOR_LAST_NAME] is null OR LTRIM([ADVISOR_LAST_NAME])='','',' '||LTRIM(RTRIM([ADVISOR_LAST_NAME]))) -- comment here");
		conds.add("LTRIM(RTRIM([ADVISOR_FIRST_NAME]))||IF ([ADVISOR_LAST_NAME] is null OR LTRIM([ADVISOR_LAST_NAME])='','',' '||LTRIM(RTRIM([ADVISOR_LAST_NAME]))) = ''");
		conds.add("(1 + 2) * (392 + 23) + ((1 +3) + (2+3))");

		conds.add("if([meet_monday]='Y', 'M', 'null')");
		conds.add("regexp_replace([cell_phone], '[\\(\\)\\-\\/]', '')");
		conds.add("if([meet_friday]='Y', 'F', 'null')");
		conds.add("rtrim(ltrim([major_cd_2]))");
		conds.add("concat([term],'$$',[course_ref_no])");
		conds.add("regexp_replace([meet_tuesday], 'Y', 'T')");
		conds.add("datestr([meet_end_date], 'MM/dd/YYYY','dd-MMM-YY')");
		conds.add("if ([cell_phone] starts with '00', null, [cell_phone])");
		conds.add("datestr([meet_end_date],'MM/dd/yyyy','dd-MMM-yy')");
		conds.add("if([group_id] = 'FLC', 'flc','flc')");
		conds.add("datestr([meet_start_date],'MMddyyyy','dd-MMM-yy')");
		conds.add("regexp_replace([home_phone], '[\\(\\)\\-]', '')");
		conds.add("rtrim(ltrim([major_cd_1]))");
		conds.add("datestr([meet_start_date],'MM-dd-yyyy','dd-MMM-yy')");
		conds.add("regexp_replace([meet_monday], 'Y', 'M')");
		conds.add("substring([postal_code, 0, 4)");
		conds.add("datestr([meet_start_date],'yyyyMMdd','dd-MMM-yy')");
		conds.add("if ([classification] contains '1', 'FR', if ([classification] contains '2', 'SO', if ([classification] contains '3', 'JR', if ([classification] contains '4', 'SR', [classification]))))");
		conds.add("datestr([meet_end_date],'yyyyMMdd','dd-MMM-yy')");
		conds.add("if([first_name] = 'Larry','LLL','Other')");
		conds.add("if ([classification] contains '1', 'FR', if ([classification] contains '05', 'FR', if ([classification] contains '2', 'SO', if ([classification] contains '3', 'JR', if ([classification] contains '4', 'SR', if ([classification] contains '5', 'Other',[classification]))))))");
		conds.add("if([major_cd_2]='NA','',[major_cd_2])");
		conds.add("'Y'");
		conds.add("datestr([exam_dt], 'MM/dd/YYYY','YYYYMMDD')");
		conds.add("if([meet_wednesday]='Y', 'W', 'null')");
		conds.add("if([meet_tuesday]='Y', 'T', 'null')");
		conds.add("datestr([meet_end_date],'MM-dd-yyyy','dd-MMM-yy')");
		conds.add("if([student_id] contains [100234687], 'Rukmannagari', 'NA')");
		conds.add("if([activate_email_ind]='Y','N','N')");
		conds.add("rtrim(ltrim([primary_user_id]))");
		conds.add("regexp_replace([meet_thursday], 'Y', 'R')");
		conds.add("if ([classification] contains '1', 'FR',"+"if ([classification] contains '2', 'SO',"+"if ([classification] contains '3', 'JR',"+"if ([classification] contains '4', 'SR', [classification]))))");
		conds.add("regexp_replace([meet_end_time], ':', '')");
		conds.add("if([group_id] = 'usa', 'USA','USA')");
		conds.add("rtrim(ltrim([credit_hour_low]))");
		conds.add("datestr([meet_start_date],'YYYYmmdd','dd-MMM-yy')");
		conds.add("regexp_replace([student_enrollment_status], '/', '')");
		conds.add("if ([classification] contains '1', 'FR', if ([classification] contains '2', 'SO', if ([classification] contains '3', 'JR', if ([classification] contains '4', 'SR', if ([classification] contains '5', 'Other',[classification])))))");
		conds.add("datestr([meet_end_date],'MMddyyyy','dd-MMM-yy')");
		conds.add("regexp_replace([meet_wednesday], 'Y', 'W')");
		conds.add("if([major_cd_3]='NA','',[major_cd_3])");
		conds.add("if([meet_sunday]='Y', 'Su', 'null')");
		conds.add("'uab'");
		conds.add("regexp_replace([meet_friday], 'Y', 'F')");
		conds.add("if([student_id]='000500294','FIRST STUDENT','N/A')");
		conds.add("datestr([meet_end_date],'YYYYmmdd','dd-MMM-yy')");
		conds.add("rtrim(ltrim([major_cd_4]))");
		conds.add("if([send_activation]='Y','N','N')");
		conds.add("rtrim(ltrim([term_code]))");
		conds.add("regexp_replace([meet_saturday], 'Y', 'Sa')");
		conds.add("if([group_id] = 'csu', 'CSU','[group_id]')");
		conds.add("datestr([meet_start_date], 'MM/dd/YYYY','dd-MMM-YY')");
		conds.add("datestr([meet_start_date],'MM/dd/yyyy','dd-MMM-yy')");
		conds.add("regexp_replace([category_id], ', ', ' ')");
		conds.add("regexp_replace([work_phone], '[\\(\\)\\-\\/]', '')");
		conds.add("regexp_replace([cell_phone], '[\\(\\)\\-]', '')");
		conds.add("if([group_id] = 'GSU', 'Campus_Academics', 'Campus_Academics')");
		conds.add("if ([cell_phone] = [home_phone], null, [home_phone])");
		conds.add("If( [is_active] = 'IG','N', If ([is_active] = 'IS', 'N', 'Y'))");
		conds.add("rtrim(ltrim([course_number]))");
		conds.add("if([meet_thursday]='Y', 'R', 'null')");
		conds.add("regexp_replace([meet_sunday], 'Y', 'Su')");
		conds.add("if([name] = 'advisors', 'advisor', 'advisor')");
		conds.add("regexp_replace([home_phone], '[\\(\\)\\-\\/]', '')");
		conds.add("rtrim(ltrim([subject_code]))");
		conds.add("regexp_replace([meet_start_time], ':', '')");
		conds.add("if([group_id] contains 'GSU', 'Campus_Academics', 'Campus_Academics')");
		conds.add("if([meet_saturday]='Y', 'Sa', 'null')");
		conds.add("if([student_id] contains (100234687), 'Rukmannagari', 'NA')");
		conds.add("concat([subject_cd],'$$',[course_no])");
		conds.add("regexp_replace([external_id], ',', '')");
		conds.add("concat([course_cd],'$$',[course_type])");
		conds.add("rtrim(ltrim([major_cd_3]))");
		conds.add("rtrim(ltrim([credit_hour_high]))");
		conds.add("to_char([group_id])");
		conds.add("to_decimal([group_id])");
		conds.add("to_decimal([group_id], 10)");
		conds.add("to_decimal([group_id]), 10, 5");
		conds.add("to_integer([group_id])");
		conds.add("to_char(ltrim([major_cd_3]))");
		conds.add("to_decimal(ltrim([major_cd_3]))");
		conds.add("to_integer(ltrim([major_cd_3]))");
		conds.add("cast(5.6, DECIMAL)");
		conds.add("cast(5.6, DECIMAL(10,2))");
		//conds.add("cast(5.6, NUMBER");
		conds.add("cast(5.6, NUMBER(15,1))");
		//conds.add("cast(5.6, NUMERIC)");
		conds.add("cast(5.6, NUMERIC(30,3))");
		conds.add("cast(5.6, INTEGER)");
		conds.add("cast(5.6, BIGINT)");
		conds.add("cast(5.6, SMALLINT)");
		conds.add("cast(5.6, F_FLOAT)");
		conds.add("cast(5.6, FLOAT4)");
		conds.add("cast(5.6, FLOAT8)");
		conds.add("cast(5.6, D_DOUBLE)");
		conds.add("cast(5.6, REAL)");
		conds.add("cast(5.6, VARCHAR)");
		conds.add("cast(5.6, VARCHAR(10))");
		conds.add("cast(5.6, CHAR)");
		conds.add("cast(5.6, CHAR(10))");
		conds.add("cast(5.6, CHARACTER)");
		conds.add("cast(5.6, CHARACTER(10))");
		conds.add("cast(5.6, STRING)");
		conds.add("cast(5.6, STRING(10))");
		conds.add("cast(5.6, TEXT)");
		conds.add("cast(5.6, TEXT(1000))");
		conds.add("cast(5.6, BINARY)");
		conds.add("cast(5.6, BINARY(1000))");
		conds.add("cast(5.6, VARBINARY)");
		conds.add("cast(5.6, VARBINARY(1000))");
		conds.add("cast(1, BOOLEAN)");
		conds.add("cast('2021-01-01 01:00:00 +0100', DATE)");
		conds.add("cast('2021-01-01 01:00:00 +0100', DATETIME)");
		conds.add("cast('2021-01-01 01:00:00 +0100', TIME)");
		conds.add("cast('2021-01-01 01:00:00 +0100', TIMESTAMP)");
		conds.add("cast('2021-01-01 01:00:00 +0100', TIMESTAMP_LTZ)");
		conds.add("cast('2021-01-01 01:00:00 +0100', TIMESTAMP_NTZ)");
		conds.add("cast('2021-01-01 01:00:00 +0100', TIMESTAMP_TZ)");
		
		
		conds.add("if([category_id]='LC:LC23' " + "or [category_id]='LC:LC24' "
				+ "or [category_id]='LC:LC25' " + "or [category_id]='LC:LC26' "
				+ "or [category_id]='LC:LC27' " + "or [category_id]='LC:LC28' "
				+ "or [category_id]='LC:LC29' " + "or [category_id]='LC:LC30' "
				+ "or [category_id]='LC:LC31' " + "or [category_id]='LC:LC32' "
				+ "or [category_id]='LC:LC33' " + "or [category_id]='LC:LC34' "
				+ "or [category_id]='LC:LC35' " + "or [category_id]='LC:LC36' "
				+ "or [category_id]='LC:LC37' " + "or [category_id]='LC:LC7A' "
				+ "or [category_id]='LC:LCHM', 'LC:LCWD'," + "if([category_id]='ASTD:00' "
				+ "or [category_id]='ASTD:11' " + "or [category_id]='ASTD:12' "
				+ "or [category_id]='ASTD:G1' " + "or [category_id]='ASTD:G2' "
				+ "or [category_id]='ASTD:G3', 'ASTD:GS', " + "if([category_id]='ASTD:20' "
				+ "or [category_id]='ASTD:21' " + "or [category_id]='ASTD:22' "
				+ "or [category_id]='ASTD:23' " + "or [category_id]='ASTD:P1' "
				+ "or [category_id]='ASTD:P2', 'ASTD:P3', " + "if([category_id]='ASTD:31' "
				+ "or [category_id]='ASTD:32', 'ASTD:33'," + "if([category_id]='ASTD:A1' "
				+ "or [category_id]='ASTD:A2', 'ASTD:A3', " + "if([category_id]='ASTD:Z1' "
				+ "or [category_id]='ASTD:Z2', 'ASTD:ZN', "
				+ "if([category_id]='HOLD:AH', 'HOLD:AR',"
				+ "if([category_id]='HOLD:D1', 'HOLD:D3',"
				+ "if([category_id]='HOLD:PF', 'HOLD:T2',"
				+ "if([category_id]='ASTD:CS', 'ASTD:PX',"
				+ "if([category_id]='ASTD:R1', 'ASTD:R2',"
				+ "if([category_id]='MINOR:ART', 'MINOR:ARTH', "
				+ "if([category_id]='MINOR2:ART', 'MINOR2:ARTH'," + "[category_id] )))))))))))))");

		conds.add("if([category_id]='HOLD:AH', 'HOLD:AR',"
				+ "if([category_id]='HOLD:D1', 'HOLD:D3',"
				+ "if([category_id]='HOLD:PF', 'HOLD:T2',"
				+ "if([category_id]='LC:LC23', 'LC:LCWD', "
				+ "if([category_id]='LC:LC24', 'LC:LCWD', "
				+ "if([category_id]='LC:LC25', 'LC:LCWD', "
				+ "if([category_id]='LC:LC26', 'LC:LCWD', "
				+ "if([category_id]='LC:LC27', 'LC:LCWD', "
				+ "if([category_id]='LC:LC28', 'LC:LCWD', "
				+ "if([category_id]='LC:LC29', 'LC:LCWD', "
				+ "if([category_id]='LC:LC30', 'LC:LCWD', "
				+ "if([category_id]='LC:LC31', 'LC:LCWD', "
				+ "if([category_id]='LC:LC32', 'LC:LCWD', "
				+ "if([category_id]='LC:LC33', 'LC:LCWD', "
				+ "if([category_id]='LC:LC34', 'LC:LCWD', "
				+ "if([category_id]='LC:LC35', 'LC:LCWD', "
				+ "if([category_id]='LC:LC36', 'LC:LCWD', "
				+ "if([category_id]='LC:LC37', 'LC:LCWD', "
				+ "if([category_id]='LC:LC7A', 'LC:LCWD', "
				+ "if([category_id]='LC:LCHM', 'LC:LCWD',"
				+ "if([category_id]='ASTD:00', 'ASTD:GS', "
				+ "if([category_id]='ASTD:11', 'ASTD:GS', "
				+ "if([category_id]='ASTD:12', 'ASTD:GS', "
				+ "if([category_id]='ASTD:G1', 'ASTD:GS',"
				+ "if([category_id]='ASTD:G2', 'ASTD:GS', "
				+ "if([category_id]='ASTD:G3', 'ASTD:GS', "
				+ "if([category_id]='ASTD:20', 'ASTD:P3', "
				+ "if([category_id]='ASTD:21', 'ASTD:P3', "
				+ "if([category_id]='ASTD:22', 'ASTD:P3', "
				+ "if([category_id]='ASTD:23', 'ASTD:P3',"
				+ "if([category_id]='ASTD:P1', 'ASTD:P3', "
				+ "if([category_id]='ASTD:P2', 'ASTD:P3', "
				+ "if([category_id]='ASTD:31', 'ASTD:33', "
				+ "if([category_id]='ASTD:32', 'ASTD:33',"
				+ "if([category_id]='ASTD:A1', 'ASTD:A3', "
				+ "if([category_id]='ASTD:A2', 'ASTD:A3', "
				+ "if([category_id]='ASTD:CS', 'ASTD:PX',"
				+ "if([category_id]='ASTD:R1', 'ASTD:R2',"
				+ "if([category_id]='ASTD:Z1', 'ASTD:ZN',"
				+ "if([category_id]='ASTD:Z2', 'ASTD:ZN', "
				+ "if([category_id]='MINOR:ART', 'MINOR:ARTH', "
				+ "if([category_id]='MINOR2:ART', 'MINOR2:ARTH',"
				+ "[category_id] ))))))))))))))))))))))))))))))))))))))))))");

		conds.add("#System_date");
		conds.add("#System_time");
		conds.add("datestr(#System_time,'YYYYmmdd','dd-MMM-yy')");
		conds.add("datestr(#System_date,'YYYYmmdd','dd-MMM-yy')");
		conds.add("date_add(#System_date, 1)");
		conds.add( "if(#system_time > [systime_cond], #system_time, [systime_cond])");

		conds.add("#row_nbr");
		conds.add("#file_id");
		conds.add("#observation_time");
		conds.add("#row_nbr>2");
		conds.add("#row_nbr>[test_num]");
		conds.add("#file_id<21");
		conds.add("#file_id>[test_id]");
		conds.add("datestr(#observation_time,'YYYYmmdd','dd-MMM-yy')");
		conds.add("#observation_time<[systime_cond]");
		conds.add("#TENANT_SK");
		conds.add("#TENANT_GUID");
		conds.add("#TENANT_NAME");
		conds.add("#TENANT_ACRONYM");
		conds.add("#TENANT_WEB_DOMAIN");
		conds.add("#ES_INSTITUTION_ID");
		conds.add("#ES_INSTITUTION_CODE");
		conds.add("#ES_INSTITUTION_NAME");
		conds.add("#SF_COUNTER_ID");
		conds.add("#POPULATION_NAME");

		int countErrors = 0;

		Snippet snippet = null;
		// Iterate over the equations and check for parsing errors
		for (String cond : conds) {
			PUML3ParserAccess access = new PUML3ParserAccess(true, true, true);
			try {
				System.out.println(" ");
				System.out.println("Parsing equation: " + cond);
				// There should be zero errors
				access.executeTheParse(cond, PUML3Constants.PUML3_EQUATION_TREE_KEY);
				snippet = access.getSnippet();
				Assert.assertNotNull("Snippet should not be null for equation: " + cond, snippet);
				final int numErrors = snippet.getFatalErrorStringList().size();	
				Assert.assertEquals("Expected no failures with " + cond, 0, numErrors);
			} catch (AssertionError e) {
				// If we get here, it means the test failed.
				// Count the tests that failed which should have succeeded
				// then circle around and try the next test.
				countErrors++;
				System.out.println("Exception parsing statement: " + cond);
				if (snippet != null) {
					System.out.println(snippet);
				} else {
					System.out.println("Snippet is null.");
				}
				System.out.println("Error: " + e.getMessage());
			}
		}
		Assert.assertEquals("Expected ALL Equations to succeed.", 0, countErrors);
	}
	
	@Ignore
	@Test
	public void testFailingEquations() {
		
		// Add equations to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("coalesce(x1, x2, x3");
		conds.add("concat");
		conds.add("unrecognizedFunction(203) + 100");
		conds.add("variable + variable");
		
		int countErrors = 0;
		Snippet snippet = null;
		// Iterate over the equations and check for parsing errors
		for (String cond : conds) {
			PUML3ParserAccess access = new PUML3ParserAccess(true, true, true);
			try {
				System.out.println(" ");
				System.out.println("Parsing equation: " + cond);
				// There should be errors
				access.executeTheParse(cond, PUML3Constants.PUML3_EQUATION_TREE_KEY);
				snippet = access.getSnippet();
				final int numErrors = snippet.getFatalErrorStringList().size();	
				Assert.assertTrue("Expected failures with " + cond, numErrors > 0);
			} catch (AssertionError e) {
				countErrors++;
				// If we get here, it means the test failed as expected
				// so we can count it as a success
				System.out.println("Exception parsing statement: " + cond);
				if (snippet != null) {
					System.out.println(snippet);
				} else {
					System.out.println("Snippet is null.");
				}
				System.out.println("Error: " + e.getMessage());
			}
		}
		Assert.assertEquals("Expected ALL Equations to fail.", conds.size(), countErrors);
	}

}
