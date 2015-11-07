package org.elixir_lang.inspection;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * Created by luke.imhoff on 12/5/14.
 */
public class KeywordsNotAtEndTestCase extends LightCodeInsightFixtureTestCase {
    public void testBug195() {
        myFixture.configureByFiles("Bug195.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testNoParenthesesWithMatchedCallOperationInMiddle() {
        myFixture.configureByFiles("NoParenthesesWithMatchedCallOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testNoParenthesesWithMatchedDotMatchedCallOperationInMiddle() {
        myFixture.configureByFiles("NoParenthesesWithMatchedDotMatchedCallOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testNoParenthesesWithMatchedWhenNoParenthesesKeywordsOperationInMiddle() {
        myFixture.configureByFiles("NoParenthesesWithMatchedWhenNoParenthesesKeywordsOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testParenthesesWithMatchedCallOperationInMiddle() {
        myFixture.configureByFiles("ParenthesesWithMatchedCallOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testParenthesesWithMatchedDotMatchedCallOperationInMiddle() {
        myFixture.configureByFiles("ParenthesesWithMatchedDotMatchedCallOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testParenthesesWithMatchedWhenNoParenthesesKeywordsOperationInMiddle() {
        myFixture.configureByFiles("ParenthesesWithMatchedWhenNoParenthesesKeywordsOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/inspection/keywords_not_at_end_test_case";
    }
}
