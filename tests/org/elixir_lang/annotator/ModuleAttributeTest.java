package org.elixir_lang.annotator;


import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.PlatformTestCase;

import java.util.List;
import java.util.stream.Collectors;

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

    public void testIssue1194() {
        myFixture.configureByFile("issue_1194.ex");
        myFixture.checkHighlighting(false, false, true);
    }

    /**
     * A dot call with parentheses is what a qualified type looks like part-way through being typed, as in
     * {@code String.()} on the way to {@code String.t()}. Covers both halves of the annotator's handling: the
     * empty-parentheses form the original report hit, and a non-empty one, whose argument is the only
     * observable output the handling produces.
     */
    public void testIssue1397() {
        myFixture.configureByFile("issue_1397.ex");
        myFixture.checkHighlighting(false, false, true);
        assertDotCallArgumentHighlightedAsType();
    }

    /**
     * {@code checkHighlighting} cannot see this: {@link Highlighter} enforces attributes rather than naming a
     * key, so {@code forcedTextAttributesKey} is null and the {@code textAttributesKey=} markup has nothing to
     * match. Resolved attributes are compared instead, the same way {@link BeamHighlightingTest} does and for
     * the same reason.
     * <p>
     * Without this the test would only pin that the annotator does not throw - a branch that swallowed the
     * element and highlighted nothing would still pass, because an empty {@code String.()} highlights nothing
     * either way.
     */
    private void assertDotCallArgumentHighlightedAsType() {
        String argument = "integer";
        int dotCallStart = myFixture.getEditor().getDocument().getText().indexOf("String.(" + argument);
        assertTrue("Fixture must contain a dot call with an argument", dotCallStart >= 0);

        int argumentStart = dotCallStart + "String.(".length();
        int argumentEnd = argumentStart + argument.length();

        TextAttributes expected = EditorColorsManager
                .getInstance()
                .getGlobalScheme()
                .getAttributes(ElixirSyntaxHighlighter.TYPE);

        List<HighlightInfo> covering = myFixture
                .doHighlighting()
                .stream()
                .filter(info -> info.getStartOffset() <= argumentStart && info.getEndOffset() >= argumentEnd)
                .collect(Collectors.toList());

        assertTrue(
                "`" + argument + "` inside a dot call in a @callback should carry " +
                        ElixirSyntaxHighlighter.TYPE.getExternalName() + " attributes; " +
                        covering.size() + " HighlightInfo(s) cover " + argumentStart + ".." + argumentEnd + ": " +
                        covering.stream()
                                .map(info -> info.getStartOffset() + ".." + info.getEndOffset() + " " +
                                        info.forcedTextAttributes)
                                .collect(Collectors.joining(" ; ")),
                covering.stream().anyMatch(info -> expected.equals(info.forcedTextAttributes))
        );
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
    /**
     * A construct the annotator does not colour keeps its default colour, and that is not an error.
     * A capture is such a construct: it can appear in a type while one is being written, and the
     * annotator has nothing to say about it.
     */
    public void testCaptureInTypeIsNotReported() {
        assertNoTypeHighlightingError("capture_in_type.ex");
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
