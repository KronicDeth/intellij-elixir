package org.elixir_lang.parser_definition;

/**
 * atom is invalid to the right of `.`, so unlike in {@link MatchedDotOperationParsingTestcase}, this tests only when
 * atom is left of `.` and the right operand varies based on the test name.
 */
public class MatchedQualifiedAliasOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedExpression
     */

    public void testMatchedCaptureNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedInMatchOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedWhenNoParenthesesKeywordsOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedWhenOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedTypeOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedPipeOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedMatchOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedOrOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedAndOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedComparisonOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedRelationalOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedArrowOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedInOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedTwoOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedAdditionOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedMultiplicationOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedUnaryNonNumericOperation() {
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

    public void testMatchedAtNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedUnqualifiedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testVariable() {
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
        assertParsedAndQuotedAroundError();
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
        return super.getTestDataPath() + "/matched_qualified_alias_operation_parsing_test_case";
    }
}
