package org.elixir_lang.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import org.elixir_lang.PlatformTestCase;

import java.util.List;

/**
 * Created by kadie.enheduanna.inanna on 12/6/14.
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

    public void testFunctionSpaceEmptyParenthesesQuickFix() {
        assertQuickFixRewrites("function ()", "function()");
    }

    public void testFunctionSpacePositionalsInParenthesesQuickFix() {
        assertQuickFixRewrites("function (one, two)", "function(one, two)");
    }

    public void testQualifierDotQuoteParenthesesQuickFix() {
        assertQuickFixRewrites("One.\"two\" ()", "One.\"two\"()");
    }

    private void assertQuickFixRewrites(String before, String after) {
        myFixture.configureByText("remove_space.ex", before);
        myFixture.enableInspections(NoParenthesesStrict.class);

        List<IntentionAction> quickFixes = myFixture.getAllQuickFixes();

        assertEquals(1, quickFixes.size());
        assertEquals("Remove space between function name and parentheses", quickFixes.get(0).getText());

        myFixture.launchAction(quickFixes.get(0));

        myFixture.checkResult(after);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/inspection/no_parentheses_strict_test_case";
    }
}
