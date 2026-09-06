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
 * {@link LexerTestCase#checkCorrectRestartUsingPosition} is stronger and now also runs: it captures a
 * {@code LexerPosition} at every token and asserts the {@code getCurrentPosition()}/{@code restore()}
 * round-trip from each, so it reaches offsets state-based restart never does. Both run via
 * {@link #checkRestarts}.
 *
 * {@code doTest(text, expected)} is deliberately not used - on platform 262 it silently
 * grows an implicit {@code checkCorrectRestart} call, so relying on it would make this class's
 * effective coverage differ across the CI legs in .github/ci-versions.json. Calling the checks
 * explicitly keeps behaviour identical on every leg.
 */
public class RestartabilityTest extends LexerTestCase {
    public void testNestedBraces() {
        checkRestarts("before {%{a: 1, b: [2, 3]}} after");
    }

    public void testMidScript() {
        checkRestarts("<script>\n  var x = 1;\n</script>");
    }

    public void testMidTag() {
        checkRestarts("<div class=\"a\"><%= @x %></div>");
    }

    public void testTagSpanningMultipleTags() {
        checkRestarts("<%= if @ok do %><p>yes</p><% else %><p>no</p><% end %>");
    }

    public void testComment() {
        checkRestarts("before <%# a comment %> after");
    }

    public void testLongComment() {
        checkRestarts("before <%!-- a %> comment --%> after");
    }

    public void testEscapedOpening() {
        checkRestarts("before <%% literal %> after");
    }

    private void checkRestarts(String text) {
        checkCorrectRestart(text);
        checkCorrectRestartUsingPosition(text);
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
