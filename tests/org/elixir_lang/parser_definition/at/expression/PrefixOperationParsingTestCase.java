package org.elixir_lang.parser_definition.at.expression;

/**
 * Created by luke.imhoff on 12/22/14.
 */
public class PrefixOperationParsingTestCase extends ParsingTestCase {
    public void testAtExpressionPrefixOperation() {
        doTest(true);
    }

    public void testCaptureExpressionPrefixOperation() {
        doTest(true);
    }

    public void testCaretExpressionPrefixOperation() {
        doTest(true);
    }

    public void testExclamationExpressionPrefixOperation() {
        doTest(true);
    }

    public void testNoParenthesesCall() {
        doTest(true);
    }

    public void testNotExpressionPrefixOperation() {
        doTest(true);
    }

    public void testTildeTildeTildeExpressionPrefixOperation() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/prefix_operation_parsing_test_case";
    }
}
