package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 11/22/14.
 */
public class ListParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        doTest(true);
    }

    public void testKeywordKey() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/list_parsing_test_case";
    }
}
