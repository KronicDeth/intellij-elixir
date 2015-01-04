package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class BinaryWholeNumberParsingTestCase extends ParsingTestCase {
    public void testInvalidAndValidDigits() {
        assertParsedAndQuotedAroundError();
    }

    public void testInvalidDigits() {
        assertParsedAndQuotedAroundError();
    }

    public void testNoDigits() {
        doTest(true);
    }

    public void testObsoleteInvalidAndValidDigits() {
        doTest(true);
    }

    public void testObsoleteInvalidDigits() {
        doTest(true);
    }

    public void testObsoleteNoDigits() {
        doTest(true);
    }

    public void testObsoleteValidDigits() {
        doTest(true);
    }

    public void testValidDigits() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/binary_whole_number_parsing_test_case";
    }
}
