package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public class StringParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEscapeSequences() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() {
        doTest(true);
    }

    /* Tests that '#' for comments and '#' for interpolation are treated differently.  Covers bug reported in
     * {@link https://github.com/KronicDeth/intellij-elixir/issues/2}.
     */
    public void testInterpolationRegression() {
        doTest(true);
    }

    public void testMultiline() {
        doTest(true);
    }

    public void testNestedInterpolation() {
        doTest(true);
    }

    public void testWithoutInterpolation() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/string_parsing_test_case";
    }
}
