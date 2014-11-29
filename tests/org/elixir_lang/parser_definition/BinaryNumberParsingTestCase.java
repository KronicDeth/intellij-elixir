package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class BinaryNumberParsingTestCase extends ParsingTestCase {
    public void testNoDigits() {
        doTest(true);
    }

    public void testValid() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/binary_number_parsing_test_case";
    }
}
