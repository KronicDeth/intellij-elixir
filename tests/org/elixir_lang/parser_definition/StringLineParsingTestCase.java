package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public class StringLineParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEscapeSequences() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    /* Tests that '#' for comments and '#' for interpolation are treated differently.  Covers bug reported in
     * {@link https://github.com/KronicDeth/intellij-elixir/issues/2}.
     */
    public void testInterpolationRegression() {
        // TODO replace doTest(true) with assertParsedAndQuotedCorrectly() when parser complete
        doTest(true);
    }

    public void testMultiline() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNestedInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWithoutInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/string_line_parsing_test_case";
    }
}
