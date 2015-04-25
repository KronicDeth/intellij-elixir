package org.elixir_lang.inspection;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * Created by luke.imhoff on 12/5/14.
 */
public class KeywordsNotAtEndTestCase extends LightCodeInsightFixtureTestCase {
    public void testMatchedCallOperationInMiddle() {
        myFixture.configureByFiles("MatchedCallOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testMatchedDotMatchedCallOperationInMiddle() {
        myFixture.configureByFiles("MatchedDotMatchedCallOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    public void testMatchedWhenNoParenthesesKeywordsOperationInMiddle() {
        myFixture.configureByFiles("MatchedWhenNoParenthesesKeywordsOperationInMiddle.ex");
        myFixture.enableInspections(KeywordsNotAtEnd.class);
        myFixture.checkHighlighting();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/inspection/keywords_not_at_end_test_case";
    }
}
