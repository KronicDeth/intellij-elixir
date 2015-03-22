package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class NoParenthesesManyArgumentsCallParsingTestCase extends ParsingTestCase {
    public void testFunctionEOLPositional() {
        assertParsedWithError();
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

    public void testKeywordValueAlias() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueAliasDotAlias() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueAliasDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueAtNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueAtom() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueAtomKeyword() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueCaptureNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueCharListHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueCharToken() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueDecimalFloat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueEmptyBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueEmptyParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueEOLComma() {
        assertParsedWithError();
    }

    public void testKeywordValueList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedNonNumericAtOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedHatOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedMultiplicationOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedNonNumericCaptureOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedNonNumericUnaryOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueNoParenthesesNoArgumentsUnqualifiedCallOrVariable() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueSigil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueStringHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueStringLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueUnaryNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesExpressionKeywordValue() {
        doTest(true);
    }

    public void testPositional() {
        doTest(true);
    }

    public void testPositionalAlias() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalAtNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalAtom() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalAtomKeyword() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalCharListHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalCharToken() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalDecimalFloat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalEmptyBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalEOLComma() {
        assertParsedWithError();
    }

    public void testPositionalHatOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalMultiplicationOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalNonNumericCaptureOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalNonNumericUnaryOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalNumericAtOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalNumericCaptureOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalNumericUnaryOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalQualifiedAlias() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalsEOLComma() {
        assertParsedWithError();
    }

    public void testPositionalSigil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalString() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalStringHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalsWithKeywords() {
        doTest(true);
    }

    public void testPositionalsWithoutKeywords() {
        doTest(true);
    }

    public void testPositionalVariable() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_many_arguments_call_parsing_test_case";
    }
}
