package org.elixir_lang.parser_definition.exclamation.matched_expression;

/**
 * Created by luke.imhoff on 12/22/14.
 */
public class PrefixOperationParsingTestCase extends ParsingTestCase {
    public void testAtMatchedExpressionPrefixOperation() {
        doTest(true);
    }

    public void testAtom() {
        doTest(true);
    }

    public void testCaptureMatchedExpressionPrefixOperation() {
        doTest(true);
    }

    public void testCaretMatchedExpressionPrefixOperation() {
        doTest(true);
    }

    public void testCharList() {
        doTest(true);
    }

    public void testCharListHeredoc() {
        doTest(true);
    }

    public void testExclamationMatchedExpressionPrefixOperation() {
        doTest(true);
    }

    public void testFalse() {
        doTest(true);
    }

    public void testList() {
        doTest(true);
    }

    public void testNil() {
        doTest(true);
    }

    public void testNotMatchedExpressionPrefixOperation() {
        doTest(true);
    }

    public void testOpeningParenthesisSemicolonClosingParenthesis() {
        doTest(true);
    }

    public void testQualifiedAlias() {
        doTest(true);
    }

    public void testQualifiedIdentifier() {
        doTest(true);
    }

    public void testSigil() {
        doTest(true);
    }

    public void testTildeTildeTildeMatchedExpressionPrefixOperation() {
        doTest(true);
    }

    public void testTrue() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/prefix_operation_parsing_test_case";
    }
}
