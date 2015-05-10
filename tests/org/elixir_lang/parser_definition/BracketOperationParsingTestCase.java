package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class BracketOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedExpression
     */

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
        return super.getTestDataPath() + "/bracket_operation_parsing_test_case";
    }
}
