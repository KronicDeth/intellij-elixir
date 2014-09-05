package org.elixir_lang.parser_definition.triple_double_quotes;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class YYInitialParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        doTest(true);
    }

    public void testMinimal() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/yyinitial_parsing_test_case";
    }
}
