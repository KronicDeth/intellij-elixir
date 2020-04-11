package org.elixir_lang;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class Issue470 extends BasePlatformTestCase {
    /*
     * Tests
     */

    public void testInMatchInMatch() {
        myFixture.configureByFile("in_match_in_match.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testInMatchMatch() {
        myFixture.configureByFile("in_match_match.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testInMatchMatchMatch() {
        myFixture.configureByFile("in_match_match_match.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testMatchInMatch() {
        myFixture.configureByFile("match_in_match.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testMatchInMatchMatch() {
        myFixture.configureByFile("match_in_match_match.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testMatchMatch() {
        myFixture.configureByFile("match_match.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/issue_470";
    }
}
