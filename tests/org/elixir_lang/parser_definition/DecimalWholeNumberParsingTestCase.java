package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class DecimalWholeNumberParsingTestCase extends ParsingTestCase {
    public void testInvalidAndValidDigits() {
        assertParsedAndQuotedAroundError();
    }

    public void testInvalidDigits() {
        doTest(true);
    }

    public void testValidDigits() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/decimal_whole_number_parsing_test_case";
    }
}
