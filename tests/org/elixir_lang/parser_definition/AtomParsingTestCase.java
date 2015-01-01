package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class AtomParsingTestCase extends ParsingTestCase {
    public void testDoubleQuoted() {
        doTest(true);
    }

    public void testLiteral() {
        doTest(true);
        assertQuotedCorrectly();
    }

    public void testOperator() {
        doTest(true);
    }

    public void testSingleQuoted() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/atom_parsing_test_case";
    }
}
