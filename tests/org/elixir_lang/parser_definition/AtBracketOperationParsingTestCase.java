package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class AtBracketOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedExpression
     */

    public void testAfter() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAndOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testArrowOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCaptureOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testComparisonOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testDo() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testDualOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testInMatchOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testInOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMultiplicationOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testOrOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testPipeOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testRelationalOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testStabOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testThreeOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testTwoOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnaryOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testWhenOperator() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedDotCallOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedQualifiedAliasOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedQualifiedCallOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedQualifiedIdentifier() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedUnqualifiedCallOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedUnqualifiedIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * accessExpression
     */

    public void testAtNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCaptureNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnaryNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testEmptyBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testList() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testSigil() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtomKeyword() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAlias() {
        assertParsedAndQuotedCorrectly(false);
    }

    /*
     * numeric
     */

    public void testCharToken() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testBinaryWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testHexadecimalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testOctalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedAndQuotedAroundError(false);
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testStringLine() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testStringHeredoc() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly(false);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/at_bracket_operation_parsing_test_case";
    }
}
