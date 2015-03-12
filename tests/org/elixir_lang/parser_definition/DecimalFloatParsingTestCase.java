package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class DecimalFloatParsingTestCase extends ParsingTestCase {
    public void testInvalidIntegralDecimalMarkFractional() {
        assertParsedAndQuotedAroundError();
    }

    public void testIntegralDecimalMarkFractional() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIntegralDecimalMarkFractionalLowerCaseExponent() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIntegralDecimalMarkFractionalMinusExponent() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIntegralDecimalMarkFractionalMinusInvalidExponent() {
        assertParsedAndQuotedAroundError();
    }

    public void testIntegralDecimalMarkFractionalPlusExponent() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIntegralDecimalMarkFractionalUpperCaseExponent() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIntegralDecimalMarkInvalidFractional() {
        assertParsedAndQuotedAroundError();
    }

    public void testLeadingDecimalMark() {
        assertParsedWithError();
    }

    public void testTrailingDecimalMark() {
        assertParsedWithError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/decimal_float_parsing_test_case";
    }
}
