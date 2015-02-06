package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class AtomParsingTestCase extends ParsingTestCase {
    public void testDoubleQuotedLiteral() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDoubleQuotedInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testLiteral() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSingleQuotedLiteral() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSingleQuotedInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/atom_parsing_test_case";
    }
}
