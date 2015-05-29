package org.elixir_lang.parser_definition;

public class MapOperationParsingTestCase extends ParsingTestCase {
    public void testAssociations() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywords() {
        assertParsedAndQuotedCorrectly();
    }

    public void testQualifiedNoParenthesesCallAssociation() {
        assertParsedAndQuotedAroundError();
    }

    public void testUnqualifiedNoParenthesesCallAssociation() {
        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/map_operation_parsing_test_case";
    }
}
