package org.elixir_lang.parser_definition;

public class FunctionReferenceParsingTestCase extends ParsingTestCase {
    public void testAliasDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAliasDotOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomDotOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOperator() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/function_reference_parsing_test_case";
    }
}
