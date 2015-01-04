package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class AtomParsingTestCase extends ParsingTestCase {
    public void testDoubleQuoted() {
        assertParsedAndQuotedCorrectly();
    }

    public void testLiteral() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSingleQuoted() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/atom_parsing_test_case";
    }
}
