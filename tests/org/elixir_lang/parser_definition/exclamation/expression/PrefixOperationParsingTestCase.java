package org.elixir_lang.parser_definition.exclamation.expression;

/**
 * Created by luke.imhoff on 12/23/14.
 */
public class PrefixOperationParsingTestCase extends ParsingTestCase {
    public void testAtExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaretExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNotExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTildeTildeTildeExpressionPrefixOperation() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/prefix_operation_parsing_test_case";
    }
}
