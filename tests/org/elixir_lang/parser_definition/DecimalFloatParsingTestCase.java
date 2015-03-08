package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class DecimalFloatParsingTestCase extends ParsingTestCase {
    public void testInvalidAndValidDigits() {
        assertParsedAndQuotedAroundError();
    }

    public void testInvalidDigits() {
        // No QuotedAroundError because InvalidDigits is valid Alias
        assertParsedAndQuotedCorrectly();
    }

    public void testValidDigits() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/decimal_whole_number_parsing_test_case";
    }
}
