package org.elixir_lang.annotator;


import org.elixir_lang.PlatformTestCase;

public class ModuleAttributeTest extends PlatformTestCase {
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

    public void testIssue557() {
        myFixture.configureByFile("issue_557.ex");
        myFixture.checkHighlighting(false, false, false);
    }

    public void testIssue559() {
        myFixture.configureByFile("issue_559.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue605() {
        myFixture.configureByFile("issue_605.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue632() {
        myFixture.configureByFile("issue_632.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue694() {
        myFixture.configureByFile("issue_694.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue699() {
        myFixture.configureByFile("issue_699.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue2198() {
        myFixture.configureByFile("issue_2198.ex");
        myFixture.checkHighlighting(false, false, false);
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
