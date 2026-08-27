package org.elixir_lang.heex.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;

/**
 * {@link LexerTestCase#checkCorrectRestart} re-lexes from every offset where {@link LookAhead}
 * reports state 0 (YYINITIAL - {@link LookAhead} is not a {@code RestartableLexer}, so those are
 * the only offsets it restarts from) and asserts the remaining tokens match a full lex from the
 * start. Only this outer lexer is covered; {@link org.elixir_lang.heex.html.HeexHTMLLexerTest}
 * covers the inner HTML lexer's offsets.
 *
 * {@code checkCorrectRestartUsingPosition} is NOT used here - it does not exist on this plugin's
 * minimum supported platform (2025.3.6 / build 253); it was added in 261.
 *
 * {@code doTest(text, expected)} is deliberately not used either - on platform 262 it silently
 * grows an implicit {@code checkCorrectRestart} call, so relying on it would make this class's
 * effective coverage differ across the CI legs in .github/ci-versions.json. Calling
 * {@code checkCorrectRestart} explicitly keeps behaviour identical on every leg.
 */
public class RestartabilityTest extends LexerTestCase {
    public void testNestedBraces() {
        checkCorrectRestart("before {%{a: 1, b: [2, 3]}} after");
    }

    public void testMidScript() {
        checkCorrectRestart("<script>\n  var x = 1;\n</script>");
    }

    public void testMidTag() {
        checkCorrectRestart("<div class=\"a\"><%= @x %></div>");
    }

    public void testTagSpanningMultipleTags() {
        checkCorrectRestart("<%= if @ok do %><p>yes</p><% else %><p>no</p><% end %>");
    }

    public void testComment() {
        checkCorrectRestart("before <%# a comment %> after");
    }

    public void testLongComment() {
        checkCorrectRestart("before <%!-- a %> comment --%> after");
    }

    public void testEscapedOpening() {
        checkCorrectRestart("before <%% literal %> after");
    }

    @Override
    protected Lexer createLexer() {
        return new LookAhead();
    }

    @Override
    protected String getDirPath() {
        return "testData/org/elixir_lang/heex/lexer";
    }
}
