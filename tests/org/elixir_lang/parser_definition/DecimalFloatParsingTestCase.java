package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class DecimalFloatParsingTestCase extends ParsingTestCase {
    public void testLeadingDecimalMark() {
        assertParsedWithError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/decimal_float_parsing_test_case";
    }
}
