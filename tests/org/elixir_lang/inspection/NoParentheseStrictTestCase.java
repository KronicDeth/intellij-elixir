package org.elixir_lang.inspection;

import org.elixir_lang.PlatformTestCase;

/**
 * Created by luke.imhoff on 12/6/14.
 */
public class NoParentheseStrictTestCase extends PlatformTestCase {
    public void testFunctionSpaceEmptyParentheses() {
        myFixture.configureByFiles("FunctionSpaceEmptyParentheses.ex");
        myFixture.enableInspections(NoParenthesesStrict.class);
        myFixture.checkHighlighting();
    }

    public void testFunctionSpaceKeywordsInParentheses() {
        myFixture.configureByFiles("FunctionSpaceKeywordsInParentheses.ex");
        myFixture.enableInspections(NoParenthesesStrict.class);
        myFixture.checkHighlighting();
    }

    public void testFunctionSpacePositinalsAndKeywordsInParentheses() {
        myFixture.configureByFiles("FunctionSpacePositionalsAndKeywordsInParentheses.ex");
        myFixture.enableInspections(NoParenthesesStrict.class);
        myFixture.checkHighlighting();
    }

    public void testFunctionSpacePositinalsInParentheses() {
        myFixture.configureByFiles("FunctionSpacePositionalsInParentheses.ex");
        myFixture.enableInspections(NoParenthesesStrict.class);
        myFixture.checkHighlighting();
    }

    public void testQualifierDotQuoteParentheses() {
        myFixture.configureByFile("QualifierDotQuoteParentheses.ex");
        myFixture.enableInspections(NoParenthesesStrict.class);
        myFixture.checkHighlighting();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/inspection/no_parentheses_strict_test_case";
    }
}
