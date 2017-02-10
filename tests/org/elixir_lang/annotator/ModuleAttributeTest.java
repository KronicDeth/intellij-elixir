package org.elixir_lang.annotator;


import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class ModuleAttributeTest extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    /**
     * See https://github.com/KronicDeth/intellij-elixir/issues/413
     */
    public void testIssue413() {
        myFixture.configureByFiles("typespec_test.exs");
        myFixture.checkHighlighting(false, false, true);
    }

    /**
     * See https://github.com/KronicDeth/intellij-elixir/issues/438
     */
    public void testIssue438() {
        myFixture.configureByFiles("missing_type_operator.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue469() {
        myFixture.configureByFile("issue_469.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue471() {
        myFixture.configureByFile("issue_471.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue525() {
        myFixture.configureByFile("issue_525.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue605() {
        myFixture.configureByFile("issue_605.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testMatch() {
        myFixture.configureByFile("match.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/annotator/module_attribute";
    }
}
