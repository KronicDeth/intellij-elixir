package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class AtBracketOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedExpression
     */

    public void testAfter() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAndOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testArrowOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testComparisonOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDo() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDualOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testHatOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInMatchOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultiplicationOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOrOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPipeOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testRelationalOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStructOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTwoOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhenOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedDotCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedQualifiedAliasOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedQualifiedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedQualifiedIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedUnqualifiedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedUnqualifiedIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * accessExpression
     */

    public void testAtNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSigil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomKeyword() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAlias() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * numeric
     */

    public void testCharToken() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBinaryWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testHexadecimalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOctalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedAndQuotedAroundError();
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/at_bracket_operation_parsing_test_case";
    }
}
