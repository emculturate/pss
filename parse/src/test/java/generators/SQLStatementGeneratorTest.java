package generators;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SQLStatementGeneratorTest {

    @Test
    public void concatenationFormulaTest() {
        final String astString = "{SQL={select={1={concatenate={1={function={parameters={1={column={name=strm, table_ref=null}}, 2={literal=1}, 3={literal=2}}, function_name=substr}}, 2={parentheses={calc={left={function={parameters={1={column={name=strm, table_ref=null}}, 2={literal=3}, 3={literal=1}}, function_name=substr}}, right={literal=1}, operator=+}}}, 3={function={parameters={1={column={name=strm, table_ref=null}}, 2={literal=4}, 3={literal=1}}, function_name=substr}}}}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT substr(strm, 1, 2) || (substr(strm, 3, 1) + 1) || substr(strm, 4,1) from tab1";
        String generated = runGenerationBasic("concatenationFormulaTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    // ===== Variable Name and Casting Tests (from Event Walker) =====

    @Test
    public void simpleVariableName1Test() {
        final String query = "SELECT a.<simple>, a.<with blanks in name> FROM tab1 as a";
        final String astString = "{SQL={select={1={column={substitution={name=<simple>, type=column}, table_ref=a}}, 2={column={substitution={name=<with blanks in name>, type=column}, table_ref=a}}}, from={table={alias=a, table=tab1}}}}";
        String generated = runGenerationBasic("simpleVariableName1Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void simpleVariableNameWithDotTest() {
        final String query = "SELECT a.<simple>, a.<with.dots.in.name> FROM tab1 as a";
        final String astString = "{SQL={select={1={column={substitution={name=<simple>, type=column}, table_ref=a}}, 2={column={substitution={name=<with.dots.in.name>, type=column}, table_ref=a}}}, from={table={alias=a, table=tab1}}}}";
        String generated = runGenerationBasic("simpleVariableNameWithDotTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void simpleVariableNameWithDashTest() {
        final String query = "SELECT a.<simple>, a.<with-dash-in - name> FROM tab1 as a";
        final String astString = "{SQL={select={1={column={substitution={name=<simple>, type=column}, table_ref=a}}, 2={column={substitution={name=<with-dash-in - name>, type=column}, table_ref=a}}}, from={table={alias=a, table=tab1}}}}";
        String generated = runGenerationBasic("simpleVariableNameWithDashTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void extendedVariableName1Test() {
        final String query = "SELECT a.<[simple]>, a.<[DOMAIN].[ENTITY].[ATTRIBUTE]>, a.<[another].[item]> FROM <[DOMAIN].[ENTITY]>  as a";
        final String astString = "{SQL={select={1={column={substitution={name=<[simple]>, parts={1=[simple]}, type=column}, table_ref=a}}, 2={column={substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}, table_ref=a}}, 3={column={substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}, table_ref=a}}}, from={table={alias=a, substitution={name=<[DOMAIN].[ENTITY]>, parts={1=[DOMAIN], 2=[ENTITY]}, type=tuple}}}}";
        String generated = runGenerationBasic("extendedVariableName1Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void extendedVariableNameWithDots2Test() {
        final String query = "SELECT  a.<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]> FROM <[DOMAIN].[ENTITY]>  as a";
        final String astString = "{SQL={select={1={column={substitution={name=<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>, parts={1=[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]}, type=column}, table_ref=a}}}, from={table={alias=a, substitution={name=<[DOMAIN].[ENTITY]>, parts={1=[DOMAIN], 2=[ENTITY]}, type=tuple}}}}";
        String generated = runGenerationBasic("extendedVariableNameWithDots2Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void extendedVariableNameWithDashTest() {
        final String query = "SELECT  a.<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]> FROM <[DOMAIN].[ENTITY]>  as a";
        final String astString = "{SQL={select={1={column={substitution={name=<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>, parts={1=[PREFIX-DOMAIN-SUFFIX], 2=[ENTITY-SUFFIX], 3=[Prefix-ATTRIBUTE]}, type=column}, table_ref=a}}}, from={table={alias=a, substitution={name=<[DOMAIN].[ENTITY]>, parts={1=[DOMAIN], 2=[ENTITY]}, type=tuple}}}}";
        String generated = runGenerationBasic("extendedVariableNameWithDashTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void castingWithPredicandVariableTest() {
        final String query = "SELECT cast(<var1> as string) a FROM tab1";
        final String astString = "{SQL={select={1={function={function_name=cast, data_type={type=STRING}, type=CAST, value={substitution={name=<var1>, type=predicand}}}, alias=a}}, from={table={alias=null, table=tab1}}}}";
        String generated = runGenerationBasic("castingWithPredicandVariableTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void castingWithColumnVariableTest() {
        final String query = "SELECT cast(tab1.<var1> as string) a FROM tab1";
        final String astString = "{SQL={select={1={function={function_name=cast, data_type={type=STRING}, type=CAST, value={column={substitution={name=<var1>, type=column}, table_ref=tab1}}}, alias=a}}, from={table={alias=null, table=tab1}}}}";
        String generated = runGenerationBasic("castingWithColumnVariableTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void concatenationInTest() {
        final String astString = "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={in={item={concatenate={1={column={name=subj_cd, table_ref=null}}, 2={column={name=crs_nm, table_ref=null}}}}, in_list={select={1={column={name=fld, table_ref=null}}}, from={table={alias=null, table=orange}}}}}}}";
        final String query = "SELECT apple from tab1 where subj_cd || crs_nm in (select fld from orange)";
        String generated = runGenerationBasic("concatenationInTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectList1Test() {
        final String astString = "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT * FROM tab1";
        String generated = runGenerationBasic("basicSelectList1Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectList2Test() {
        final String astString = "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT a,b,c FROM tab1";
        String generated = runGenerationBasic("basicSelectList2Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectList3Test() {
        final String astString = "{SQL={select={1={alias=a, calc={left={literal=1}, right={literal=2}, operator=+}}, 2={parentheses={calc={left={literal=1}, right={literal=2}, operator=+}}, alias=b}, 3={parentheses={column={name=d, table_ref=null}}, alias=c}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT 1 + 2 as a,(1+2) b, (d) as c FROM tab1";
        String generated = runGenerationBasic("basicSelectList3Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectTableNameV1Test() {
        final String astString = "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT * FROM tab1";
        String generated = runGenerationBasic("basicSelectTableNameV1Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectTableNameV2Test() {
        final String astString = "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=schema, alias=null, table=tab1}}}}";
        final String query = "SELECT * FROM schema.tab1";
        String generated = runGenerationBasic("basicSelectTableNameV2Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectTableNameV3Test() {
        final String astString = "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=schema, dbname=dbname, alias=null, table=tab1}}}}";
        final String query = "SELECT * FROM dbname.schema.tab1";
        String generated = runGenerationBasic("basicSelectTableNameV3Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    
    @Test
    public void basicSelectQuotedTableNameV1Test() {
        final String astString = "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=schema, dbname=dbname, alias=null, table=tab1}}}}";
        final String query = "SELECT * FROM \"dbname.schema.tab1\"";
        String generated = runGenerationBasic("basicSelectQuotedTableNameV1Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
        Assert.assertEquals("Generated SQL does not match expected AST", query, generated);
    }

    @Test
    public void basicSelectDistinctQualifierListTest() {
        final String astString = "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, qualifier=distinct, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT distinct a,b,c FROM tab1";
        String generated = runGenerationBasic("basicSelectDistinctQualifierListTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectDistinctListWithEmbeddedAllListQualifierTest() {
        final String astString = "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, qualifier=distinct, from={table={alias=tab1, query={select={1={column={name=b, table_ref=null}}, 2={column={name=c, table_ref=null}}}, qualifier=all, from={table={alias=null, table=tab2}}}}}}}";
        final String query = "SELECT distinct a,b,c FROM (select all b,c from tab2) tab1";
        String generated = runGenerationBasic("basicSelectDistinctListWithEmbeddedAllListQualifierTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void aggregateFunctionWithDistinctQualifierTest() {
        final String astString = "{SQL={select={1={function={function_name=max, qualifier=distinct, parameters={column={name=a, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT max(distinct a) FROM tab1";
        String generated = runGenerationBasic("aggregateFunctionWithDistinctQualifierTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectListAliasing1Test() {
        final String astString = "{SQL={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT a as x,b as y,c as z FROM tab1";
        String generated = runGenerationBasic("basicSelectListAliasing1Test", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectListNumericPrefixAliasingTest() {
        final String astString = "{SQL={select={1={column={name=a, table_ref=null}, alias=01_x}, 2={column={name=b, table_ref=null}, alias=02_y}, 3={column={name=c, table_ref=null}, alias=999_z}}, from={table={alias=null, table=tab1}}}}";
        final String query = "SELECT a as 01_x,b as 02_y,c as 999_z FROM tab1";
        String generated = runGenerationBasic("basicSelectListNumericPrefixAliasingTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void basicSelectListQuotedNumericPrefixColumnTest() {
        final String astString = "{SQL={select={1={column={name=\"09_a\", table_ref=null}, alias=01_x}, 2={column={name=\"22_b\", table_ref=null}, alias=02_y}, 3={column={name=\"36_c\", table_ref=null}, alias=\"999_z\"}}, from={table={alias=null, table=\"99tab1\"}}}}";
        final String query = "SELECT \"09_a\" as 01_x, \"22_b\" as 02_y,\"36_c\" as \"999_z\" FROM \"99tab1\"";
        String generated = runGenerationBasic("basicSelectListQuotedNumericPrefixColumnTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void real1SelectListNumericPrefixAliasingTest() {
        final String astString = "{SQL={select={1={column={name=Degree_Code, table_ref=sub}, alias=01_DEGREE_CD}, 2={column={name=Degree_Name, table_ref=sub}, alias=02_DEGREE_NAME}, 3={column={name=f1, table_ref=sub}}}, from={table={alias=sub, query={select={1={column={name=f1, table_ref=t}}, 2={column={name=*, table_ref=t}}}, from={table={schema=pantoresultprod, alias=t, table=hive_result_pit_5223_164728_46090704}}}}}}}";
        final String query = "SELECT sub.Degree_Code AS 01_DEGREE_CD, sub.Degree_Name AS 02_DEGREE_NAME, sub.f1 FROM (SELECT t.f1, t.* FROM pantoresultprod.hive_result_pit_5223_164728_46090704 t) sub";
        String generated = runGenerationBasic("real1SelectListNumericPrefixAliasingTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }

    @Test
    public void nestedQueryDemoTest() {
        final String astString = "{SQL={select={1={column={name=a, table_ref=tab1}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=max_D}, 3={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=min, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=min_D}, 4={column={name=w, table_ref=kk}}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join}, 3={table={alias=kk, query={select={1={column={name=w, table_ref=null}}}, from={table={alias=null, table=jj}}}}}}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}}}}}}";
        final String query = "select tab1.a aa, (select max(D) from ee) max_D, (select min(D) from ee) min_D, kk.w from tab1 join (select w from jj) kk where a in (select c from ff)";
        String generated = runGenerationBasic("nestedQueryDemoTest", astString, query);
        Assert.assertNotNull("Generated SQL should not be null", generated);
        Assert.assertFalse("Generated SQL should not be blank", generated.isBlank());
    }



    
    private String runGenerationBasic(String testName, String astString, String query) {
        SQLStatementGenerator generator = new SQLStatementGenerator();
        Map<String, Object> ast = astStringToHashMap(astString);
        System.out.println("[" + testName + "] AST:  " + ast);

        String generated = generator.generateStatement(ast);

        System.out.println("[" + testName + "] Expected query:  " + query);
        System.out.println("[" + testName + "] Generated query: " + generated);

        return generated;
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Object> astStringToHashMap(String astString) {
        Object parsed = new AstMapStringParser(astString).parseValue();
        if (!(parsed instanceof Map<?, ?> parsedMap)) {
            throw new IllegalArgumentException("AST root must be a map string");
        }
        return new HashMap<>((Map<String, Object>) parsedMap);
    }

    private static final class AstMapStringParser {
        private final String input;
        private int index;

        AstMapStringParser(String input) {
            this.input = input == null ? "" : input.trim();
            this.index = 0;
        }

        Object parseValue() {
            skipWhitespace();
            if (peek() == '{') {
                return parseMap();
            }
            return parseAtom();
        }

        private Map<String, Object> parseMap() {
            expect('{');
            skipWhitespace();
            Map<String, Object> map = new LinkedHashMap<>();
            if (peek() == '}') {
                index++;
                return map;
            }

            while (index < input.length()) {
                String key = parseKey();
                expect('=');
                Object value = parseValue();
                map.put(key, value);

                // After a value, skip whitespace and any commas (robust to trailing commas/whitespace)
                boolean foundDelimiter = false;
                while (index < input.length()) {
                    skipWhitespace();
                    char ch = peek();
                    if (ch == ',') {
                        index++;
                        foundDelimiter = true;
                        break; // Next key-value pair
                    } else if (ch == '}') {
                        index++;
                        return map; // End of map
                    } else if (ch == '\0') {
                        return map; // End of input
                    } else {
                        // If not a delimiter, check if we're inside a nested structure
                        // Defensive: break to avoid infinite loop
                        break;
                    }
                }
                // If we didn't find a delimiter and didn't return, continue parsing (should not happen in valid input)
            }
            return map;
        }

        private String parseKey() {
            skipWhitespace();
            int start = index;
            while (index < input.length() && input.charAt(index) != '=') {
                index++;
            }
            return input.substring(start, index).trim();
        }

        private Object parseAtom() {
            skipWhitespace();
            int start = index;
            int angleDepth = 0;
            while (index < input.length()) {
                char ch = input.charAt(index);
                if (ch == '<') {
                    angleDepth++;
                } else if (ch == '>') {
                    angleDepth = Math.max(0, angleDepth - 1);
                } else if ((ch == ',' || ch == '}') && angleDepth == 0) {
                    break;
                }
                // Treat '[' and ']' as normal characters, do not break on them
                index++;
            }
            String token = input.substring(start, index).trim();
            if ("null".equals(token)) {
                return null;
            }
            return token;
        }

        private void expect(char expected) {
            skipWhitespace();
            char actual = peek();
            if (actual != expected) {
                throw new IllegalArgumentException("Expected '" + expected + "' but found '" + actual + "' at index " + index);
            }
            index++;
        }

        private char peek() {
            if (index >= input.length()) {
                return '\0';
            }
            return input.charAt(index);
        }

        private void skipWhitespace() {
            while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
                index++;
            }
        }
    }
}
