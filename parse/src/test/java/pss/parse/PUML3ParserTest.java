package pss.parse;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;

public class PUML3ParserTest {

	@Test
	public void testPassingConditions() {

		// Add conditions to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("true");
		conds.add("false");
		conds.add("[ATTR] > 5");
		conds.add("NOT([ATTR] = 'abc')");
		conds.add("[ATTR] = key");
		conds.add("((([ATTR] = 'def')))");
		conds.add("(true) AND (false)");
		conds.add("NOT(true and false or true and false)");
		conds.add("(age > 15)");
		conds.add("age > [ATTR]");
		conds.add("age > minimum_age");
		conds.add("age >= '21'");
		conds.add("(gpa >= 3.0)");
		conds.add("NOT(gpa > min_gpa OR gpa < max_gpa AND gpa = avg_gpa)");
		conds.add("(gpa < 4.0)");
		conds.add("gpa <= 3.1");
		conds.add("gpa < [ATTR]");
		conds.add("gpa <= [ATTR]");
		conds.add("NOT([ATTR] = 5)");
		conds.add("([ATTR] = 'abc' OR (name = [STUDENT NAME] and maJOR = [STUDENT MAJOR]))");
		conds.add("[ATTR] % 5 = 4");
		conds.add("[ATTR] * 2 = [MAX AGE]");
		conds.add("age = 21");
		conds.add("age != 21");
		conds.add("age != 21 AND gpa <= 4.0 OR age + 5 > 30");
		conds.add("NOT(age % 3 > 2 AND length(ltrim(key)) = 0)");
		conds.add("age % 3 > 2");
		conds.add("gpa * 2.1 = 5");
		conds.add("(gpa / 0.1) < 10");
		conds.add("'name'||'ite' = 'nameite'");
		conds.add("age + 5 > 12");
		conds.add("(gpa > 5) and (age < 12)");
		conds.add("(concat(name, address) = key)");
		conds.add("count(keys) >= 5");
		conds.add("count(keys) != [KEY COUNT]");
		// conds.add("value in ('1', '2', '3', 4, 5)");
		conds.add("instr('Informatica', 'Inform') = 1");
		conds.add("instr('Informatica', 'orm') = 3");
		conds.add("instr('Informatica', 'rmat', 3) = 5");
		conds.add("instr('dotdotdotdot', 'dot', 2, 2) = 4");
		conds.add("length(hello) > 0");
		conds.add("length('hello') = 5");
		conds.add("log(x) > 10");
		conds.add("log(5.6) > 10");
		conds.add("lower(5.2) = 5");
		conds.add("length(lpad(name, 5)) > 1");
		conds.add("length(lpad(name, 5, '0')) > 1");
		conds.add("length(ltrim(name)) > 0");
		conds.add("max(3, 5) = 5");
		conds.add("min(10, 15) = 10");
		conds.add("not(false)");
		conds.add("NOT((length(ltrim(val)) = 0) OR (val = null))");
		conds.add("val = null");
		conds.add("[VAL] = null");
		conds.add("[ATTR] = [VAL1] and [KEY 1] < [Key 2]");
		conds.add("val = null or length(rtrim(val)) = 0");
		conds.add("power(3, 2) = 9");
		conds.add("power([ATTR]) = [TARGET VALUE]");
		conds.add("round(3.5) = 3");
		conds.add("round([ATTR]) = [TARGET VALUE]");
		conds.add("rpad(abc, 3) = def");
		conds.add("rpad([ATTR], [LEN]) = [VALUE]");
		conds.add("rpad('x', 3) = 'x  '");
		conds.add("rpad('z', 5, '0') = 'z0000'");
		conds.add("rtrim(key) = value");
		conds.add("rtrim([ATTR]) = value");
		conds.add("rtrim(' hello    ') = ' hello'");
		conds.add("sqrt(9) = 3");
		conds.add("sqrt([ATTR]) = datum");
		conds.add("NOT(substring(name, 0, 5) = fname)");
		conds.add("not(substring([ATTR], 0, 5) = fname)");
		conds.add("substring(name, 0, 5) = 'frank'");
		conds.add("to_date(substring(col, 1, 11), 'DD-MON-YYYY') = startDate");
		conds.add("to_date(substring([KEY], 5, 7), 'MMM') = endMonth");
		conds.add("upper(addr) = upper(lower(addr))");
		conds.add("uPPer([ATTR]) = uppER(LOWer(point))");
		conds.add("(age > 15) and [ATTR] = 'class' or sqrt(9) >= 3 anD field = 'VT'");
		conds.add("(age > 15) or ([ATTR] = 'class' and sqrt(9) >= 3) oR field = 'VT'");
		conds.add("age > 15 and ([ATTR] = 'class' or sqrt(9) >= 3) And field = 'VT'");
		conds.add("not(age > 15 and ([ATTR] = 'class' or sqrt(9) >= 3) And field = 'VT')");
		conds.add("(age > 15) and (([ATTR] = 'class') or ((sqrt(9) = 3) and (field = 'VT')))");
		conds.add("not(age > 15) and not([ATTR] = 'class') or not(sqrt(9) = 3) and not(field = 'VT')");
		conds.add("(age > 15 and [ATTR] = 'class') or (sqrt(9) >= 3 ANd field = 'VT')");
		conds.add("(age > 15 or [ATTR] = 'class') and (sqrt(9) >= 3 OR field = 'VT')");
		conds.add("age > 15 or ([ATTR] = 'class' and sqrt(9) >= 3) oR field = 'VT'");
		conds.add("((age > 15 or [ATTR] = 'class') and sqrt(9) >= 3) or field = 'VT'");
		conds.add("age > 15 or ([ATTR] = 'class' and (sqrt(9) >= 3 or field = 'VT'))");
		conds.add("not(age > 15 or [ATTR] = 'class') and not(sqrt(9) >= 3 and field = 'VT')");
		conds.add("(age > 15 or not([ATTR] = 'class')) and not(sqrt(9) >= 3) or field = 'VT'");
		conds.add("(age > 15 or not([ATTR] = 'class')) and (not(sqrt(9) >= 3)) or field = 'VT'");
		conds.add("NOT([ATTR] matches 'hello')");
		conds.add("key is null");
		conds.add("key isnull");
		conds.add("value is not null");
		conds.add("[ATTR] matches 'hello'");
		conds.add("[ATTR] contains 'hello'");
		conds.add("[ATTR] starts with 'hello'");
		conds.add("[ATTR] ends with 'hello'");
		conds.add("[ATTR] not matches 'hello'");
		conds.add("[ATTR] not contains 'hello'");
		conds.add("[ATTR] not starts with 'hello'");
		conds.add("[ATTR] not ends with 'hello'");
		conds.add("key matches 'hello'");
		conds.add("key conTaiNS 'hello'");
		conds.add("key starts with 'hello'");
		conds.add("key ENDS With 'hello'");
		conds.add("key nOT maTches 'hello'");
		conds.add("key not contains 'hello'");
		conds.add("[ATTR] not sTArts with key");
		conds.add("[ATTR] not ends with key");
		conds.add("coALesce(x1, x2, x3) = [ATTR]");
		conds.add("regexP_extract(f1, f2) = [ATTR]");
		conds.add("regEXp_replace(f1, f2) = [ATTR]");
		conds.add("coaLEsce(x1, x2, x3) = f1");
		conds.add("regEXp_extract(f1, f2) = f1");
		conds.add("regexp_REPlace(f1, f2) = f1");
		conds.add("cOALesce(x1, x2, x3) = 'x'");
		conds.add("reGExp_eXTRact(f1, f2) = 'x'");
		conds.add("REGexp_replace(f1, f2) = 'x'");
		conds.add(
				"LTRIM(RTRIM(ADVISOR_FIRST_NAME))||IF(ISNULL(ADVISOR_LAST_NAME) OR LTRIM(ADVISOR_LAST_NAME)='','',' '||LTRIM(RTRIM(ADVISOR_LAST_NAME))) = ''");
		conds.add("is_Date(f1)");

		// Iterate over the conditions and check for parsing errors
		for (String cond : conds) {
			final PUML3Parser parser = parse(cond);
			try {
				// There should be zero errors
				parser.condition();
				final int numErrors = parser.getNumberOfSyntaxErrors();
				Assert.assertEquals("Expected no failures with " + cond, numErrors, 0);
			} catch (RecognitionException e) {
				System.err.println("Exception parsing cond: " + cond);
			}
		}
	}

	@Test
	public void testFailingConditions() {

		// Add conditions to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("AND(OR())");
		conds.add("NOT(contains)");
		conds.add("AND(NOT(OR))");

		// Iterate over the conditions and check for parsing errors
		for (String cond : conds) {
			final PUML3Parser parser = parse(cond);
			try {
				// There should be errors
				parser.condition();
				final int numErrors = parser.getNumberOfSyntaxErrors();
				Assert.assertTrue("Expected failures with " + cond, numErrors > 0);
			} catch (RecognitionException e) {
				System.err.println("Exception parsing cond: " + cond);
			} catch (Exception ex) {
				System.err.println("Exception - " + ex.getClass().getName());
			}
		}
	}

	@Test
	public void testPassingEquations() {

		// Add equations to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("concat([name], [address])");
		conds.add("concat(name, 'addr')");
		conds.add("concat(name, 'addr', state)");
		conds.add("conCAt('name', 'address')");
		conds.add("CONcat([ATTR], 'ending')");
		conds.add("count(keys)");
		conds.add("count([ATTR])");
		conds.add("cOUNt('keys')");
		conds.add("instr('Informatica', 'Inform')");
		conds.add("instr('Informatica', [KEY VALUE])");
		conds.add("INSTR('Informatica', 'Inform')");
		conds.add("instr('Informatica', 'rmat', 3)");
		conds.add("instr('dotdotdotdot', 'dot', 2, 2)");
		conds.add("length(hello)");
		conds.add("lenGTh('hello')");
		conds.add("LENGTH([ATTR])");
		conds.add("log(x)");
		conds.add("LOg(5.6)");
		conds.add("lower(5.2)");
		conds.add("lower(age)");
		conds.add("lpad(name, 5)");
		conds.add("lpad('name', 5)");
		conds.add("lpad(name, 5, '0')");
		conds.add("lpad('frank', 5, '0')");
		conds.add("lTrim(name)");
		conds.add("ltrim('towson  ')");
		conds.add("max(3, 5)");
		conds.add("mAx(city, age)");
		conds.add("min(10, 15)");
		conds.add("ltrim(val)");
		conds.add("rtrim(val)");
		conds.add("min(age, 25)");
		conds.add("ltrim(' tom')");
		conds.add("rtrim('major    ')");
		conds.add("power(3, 2)");
		conds.add("power(base, pow)");
		conds.add("round(gpa)");
		conds.add("round(3.5)");
		conds.add("rpad(abc, 3)");
		conds.add("rpad('x', 3)");
		conds.add("rpad('z', 5, '0')");
		conds.add("rtrim(key)");
		conds.add("rtrim(' hello    ')");
		conds.add("sqrt(9)");
		conds.add("sqrt(maxYears5)");
		conds.add("substring('pascal', 1, 2)");
		conds.add("substring(name, 0, 5)");
		conds.add("substring(name, 0, 5)");
		conds.add("to_date(substring(col, 1, 11), 'DD-MON-YYYY')");
		conds.add("upper(addr)");
		conds.add("upper('tom jones')");
		conds.add("if ([ATTR] matches 'hello', tval, fval)");
		conds.add("if ([ATTR] contains 'hello', tval, fval)");
		conds.add("if ([ATTR] starts with 'hello', tval, fval)");
		conds.add("if ([ATTR] ends with 'hello', tval, fval)");
		conds.add("if ([ATTR] not matches 'hello', tval, fval)");
		conds.add("if ([ATTR] not contains 'hello', tval, fval)");
		conds.add("if ([ATTR] not starts with 'hello', tval, fval)");
		conds.add("if ([ATTR] not ends with 'hello', tval, fval)");
		conds.add("if (key matches 'hello', tval, fval)");
		conds.add("if (key conTaiNS 'hello', tval, fval)");
		conds.add("if (key starts with 'hello', tval, fval)");
		conds.add("if (key ENDS With 'hello', tval, fval)");
		conds.add("if (key nOT maTches 'hello', tval, fval)");
		conds.add("if (key not contains 'hello', tval, fval)");
		conds.add("if ([ATTR] not sTArts with key, tval, fval)");
		conds.add("if ([ATTR] not ends with key, tval, fval)");
		conds.add("if (is_Date(f1), tval, fval)");
		conds.add("coalesce(x1, x2, x3)");
		conds.add("regexp_extract(f1, f2)");
		conds.add("regexp_replace(f1, f2)");
		conds.add("if (true, tval, fval)");
		conds.add("if ([ATTR] = val, tval, fval)");
		conds.add("if (sqrt(9) = 3, tval, fval)");
		conds.add("if (length(regexp_extract(value, '[^a-zA-Z]+', 0)) > 0, tval, fval)");
		conds.add("if (length(regexp_extract(value, '[a-zA-Z]+', 0)) > 0, tval, fval)");
		conds.add("regexp_replace(value, '[ ]+', ' ')");
		conds.add("regexp_replace(value, ' ', '_')");
		conds.add("regexp_replace(field, '<char>', '')");
		conds.add("regexp_replace(value, '[^0-9]+', '')");
		conds.add("regexp_replace(value, '[^a-zA-Z]+', '')");
		conds.add(
				"concat(regexp_replace(value, '([0-9]*)\\.([^.]*)', '$1'), '.', regexp_replace(value, '([0-9]*)\\.([^.]*)', '$2'))");
		conds.add(
				"LTRIM(RTRIM(ADVISOR_FIRST_NAME))||IF(ISNULL(ADVISOR_LAST_NAME) OR LTRIM(ADVISOR_LAST_NAME)='','',' '||LTRIM(RTRIM(ADVISOR_LAST_NAME))) -- comment here");
		// an equation formula should be resolvable to a boolean condition
		// statement, but the grammar fails at the moment
		/*
		 * conds.add(
		 * "LTRIM(RTRIM(ADVISOR_FIRST_NAME))||IF(ISNULL(ADVISOR_LAST_NAME) OR LTRIM(ADVISOR_LAST_NAME)='','',' '||LTRIM(RTRIM(ADVISOR_LAST_NAME))) = ''");
		 */
		// Iterate over the equations and check for parsing errors
		for (String cond : conds) {
			final PUML3Parser parser = parse(cond);
			try {
				// There should be zero errors
				parser.equation();
				final int numErrors = parser.getNumberOfSyntaxErrors();
				Assert.assertEquals("Expected no failures with " + cond, numErrors, 0);
			} catch (RecognitionException e) {
				System.err.println("Exception parsing eqn: " + cond);
			}
		}
	}

	@Test
	public void testFailingEquations() {

		// Add equations to test
		final List<String> conds = new ArrayList<String>(10);
		conds.add("coalesce(x1, x2, x3");
		conds.add("concat");
		conds.add("value in ('1', abc, [KEY 3], 4, 5)");
		
	
		// Iterate over the equations and check for parsing errors
		for (String cond : conds) {
			final PUML3Parser parser = parse(cond);
			try {
				// There should be errors
				parser.equation();
				final int numErrors = parser.getNumberOfSyntaxErrors();
				Assert.assertTrue("Expected failures with " + cond, numErrors > 0);
			} catch (RecognitionException e) {
				System.err.println("Exception parsing eqn: " + cond);
			}
		}
	}

	private static final PUML3Parser parse(final String condition) {
		CharStream input = new ANTLRInputStream(condition);
		PUML3Lexer lexer = new PUML3Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PUML3Parser parser = new PUML3Parser(tokens);

		return parser;
	}
}
