package org.elixir_lang.annotator;


import org.elixir_lang.PlatformTestCase;

import java.util.List;

/**
 * Two kinds of test live here and they want opposite fixtures, so one that looks redundant beside
 * another usually is not. A test named for an issue pins the shape that was reported, however tangled; a
 * test named for a construct pins one branch of the annotator and only does so while its fixture holds
 * that construct alone. Merging the two loses what the second proves.
 */
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

    public void testIssue1796() {
        myFixture.configureByFile("issue_1796.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    public void testIssue1835() {
        myFixture.configureByFile("issue_1835.ex");
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
     * Constructs the annotator must be able to classify
     *
     * Each fixture holds one construct and reaches one branch, so deleting that branch fails exactly
     * this test.
     */

    public void testModuleAttributeInType() {
        assertNoTypeHighlightingError("module_attribute_in_type.ex");
    }

    public void testDocHeredocBelowUnfinishedType() {
        assertNoTypeHighlightingError("doc_heredoc_below_unfinished_type.ex");
    }

    public void testCharTokenInType() {
        assertNoTypeHighlightingError("char_token_in_type.ex");
    }

    public void testEmptyParenthesesArgument() {
        assertNoTypeHighlightingError("empty_parentheses_argument.ex");
    }

    public void testKeywordsWithoutParentheses() {
        assertNoTypeHighlightingError("keywords_without_parentheses.ex");
    }

    public void testNestedWhenInType() {
        assertNoTypeHighlightingError("nested_when_in_type.ex");
    }

    public void testMapUpdateInType() {
        assertNoTypeHighlightingError("map_update_in_type.ex");
    }

    /**
     * The control for the tests above: they assert an absence, so they would all pass if the annotator
     * stopped reporting at all. If captures gain a branch, swap the fixture for another unclassifiable
     * construct rather than deleting this.
     */
    public void testUnclassifiableTypeElementIsReported() {
        myFixture.configureByFile("capture_in_type.ex");

        List<LoggedError> reported = loggedErrors();

        assertFalse(
                "Expected an unclassifiable element in a type to be reported, but nothing was logged",
                reported.isEmpty()
        );
        assertEquals("Cannot highlight types", reported.get(0).getTitle());
    }

    /*
     * Private Instance Methods
     */

    /**
     * Every error the annotator logs while highlighting the configured file, suppressed rather than
     * rethrown so that a failure names the offending element.
     */
    private List<LoggedError> loggedErrors() {
        return captureLoggedErrors(true, () -> myFixture.doHighlighting()).getSecond();
    }

    private void assertNoTypeHighlightingError(String fixtureFileName) {
        myFixture.configureByFile(fixtureFileName);

        assertEmpty(loggedErrors());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/annotator/module_attribute";
    }
}
