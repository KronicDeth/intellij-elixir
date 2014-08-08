package org.elixir_lang.parser_definition.double_quotes;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class BodyParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        doTest(true);
    }

    public void testEscapeSequences() {
        doTest(true);
    }

    /* Tests that '#' for comments and '#' for interpolation are treated differently.  Covers bug reported in
     * {@link https://github.com/KronicDeth/intellij-elixir/issues/2}.
     */
    public void testInterpolationRegression() {
        doTest(true);
    }

    public void testWithInterpolation() {
        doTest(true);
    }

    public void testWithNestedInterpolation() {
        doTest(true);
    }

    public void testWithoutInterpolation() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/body_parsing_test_case";
    }
}
