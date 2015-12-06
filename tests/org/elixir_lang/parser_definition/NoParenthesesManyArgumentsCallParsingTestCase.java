package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class NoParenthesesManyArgumentsCallParsingTestCase extends ParsingTestCase {
    public void testFunctionEOLPositional() {
        assertParsedWithErrors();
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
        assertParsedAndQuotedCorrectly();
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
        assertParsedWithErrors();
    }

    public void testKeywordValueList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedAtNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedMultiplicationOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedCaptureNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValueMatchedUnaryNonNumericOperation() {
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
        assertParsedAndQuotedCorrectly();
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
        assertParsedWithErrors();
    }

    public void testPositionalInMatchOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalWhenOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalPipeOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalMatchOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalOrOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalAndOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalComparisonOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalRelationalOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalArrowOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalInOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalTwoOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalAdditionOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalMultiplicationOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalCaptureNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalUnaryNonNumericOperation() {
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
        assertParsedWithErrors();
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
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalsWithoutKeywords() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalVariable() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_many_arguments_call_parsing_test_case";
    }
}
