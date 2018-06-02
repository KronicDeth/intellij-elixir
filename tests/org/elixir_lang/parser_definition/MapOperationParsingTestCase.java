package org.elixir_lang.parser_definition;

public class MapOperationParsingTestCase extends ParsingTestCase {
    public void testAssociationOperation() {
        assertParsedAndQuotedCorrectly(false);
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

    public void testEmptyStruct() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywords() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMapConstructionArgumentsWithPipes() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMapExpression() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMapUpdateArgumentsWithTrailingComma() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMapUpdateKeywords() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedPipeAsssociationAndKeywords() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedPipeMapExpression() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedPipeMapExpressionWIthTrailingComma() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedPipeMatchedAssociationMatched() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMaxDotCallStruct() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMaxQualifiedAliasStruct() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMaxQualifiedNoArgumentsCallStruct() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMaxQualifiedParenthesesCallStruct() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testQualifiedNoParenthesesCallAssociation() {
        assertParsedAndQuotedAroundError();
    }

    public void testStructConstruction() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStructUpdate() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoParenthesesCallAssociation() {
        assertParsedAndQuotedAroundError();
    }

    public void testVariableStruct() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/map_operation_parsing_test_case";
    }
}
