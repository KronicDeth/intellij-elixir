package org.elixir_lang.parser_definition;

public class MapOperationParsingTestCase extends ParsingTestCase {
    public void testAssociationOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAssociationOperations() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAssociationOperationsAndKeywords() {
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

    public void testMapConstructionArgumentsWithPipes() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMapExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMapUpdateArgumentsWithTrailingComma() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedPipeMapExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedPipeMapExpressionWIthTrailingComma() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedPipeMatchedAssociationMatched() {
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
