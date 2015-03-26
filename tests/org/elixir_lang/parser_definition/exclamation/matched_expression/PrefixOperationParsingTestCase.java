package org.elixir_lang.parser_definition.exclamation.matched_expression;

/**
 * Created by luke.imhoff on 12/22/14.
 */
public class PrefixOperationParsingTestCase extends ParsingTestCase {
    public void testAtMatchedExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtom() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureMatchedExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaretMatchedExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationMatchedExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFalse() {
        assertParsedAndQuotedCorrectly();
    }

    public void testList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNotMatchedExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOpeningParenthesisSemicolonClosingParenthesis() {
        assertParsedAndQuotedCorrectly();
    }

    public void testQualifiedAlias() {
        assertParsedAndQuotedCorrectly();
    }

    public void testQualifiedIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSigil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTildeTildeTildeMatchedExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTrue() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/prefix_operation_parsing_test_case";
    }
}
