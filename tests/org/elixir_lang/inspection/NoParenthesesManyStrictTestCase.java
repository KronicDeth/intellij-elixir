package org.elixir_lang.inspection;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * Created by luke.imhoff on 12/5/14.
 */
public class NoParenthesesManyStrictTestCase extends LightCodeInsightFixtureTestCase {
    public void testSingleNestedKeywordValue() {
        myFixture.configureByFiles("SingleNestedKeywordValue.ex");
        myFixture.enableInspections(NoParenthesesManyStrict.class);
        myFixture.checkHighlighting();
    }

    public void testSingleNestedPositional() {
        myFixture.configureByFiles("SingleNestedPositional.ex");
        myFixture.enableInspections(NoParenthesesManyStrict.class);
        myFixture.checkHighlighting();
    }

    @Override
    protected String getTestDataPath() {
        return System.getenv("INTELLIJ_ELIXIR_PATH") + "/testData/org/elixir_lang/inspection/no_parentheses_many_strict_test_case";
    }
}
