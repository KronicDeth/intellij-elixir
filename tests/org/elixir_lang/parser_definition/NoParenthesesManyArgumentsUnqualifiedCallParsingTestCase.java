package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class NoParenthesesManyArgumentsUnqualifiedCallParsingTestCase extends ParsingTestCase {
    public void testFunctionEOLPositional() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOneKeyword() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTwoKeywordsNewlineAfterComma() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTwoKeywordsSameLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValue() {
        doTest(true);
    }

    public void testKeywordValueEOLComma() {
        doTest(true);
    }

    public void testKeywordValueMatchedNonNumericCaptureOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesExpressionKeywordValue() {
        doTest(true);
    }

    public void testPositional() {
        doTest(true);
    }

    public void testPositionalEOLComma() {
        doTest(true);
    }

    public void testPositionalsEOLComma() {
        doTest(true);
    }

    public void testPositionalsWithKeywords() {
        doTest(true);
    }

    public void testPositionalsWithoutKeywords() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_many_arguments_unqualified_call_parsing_test_case";
    }
}
