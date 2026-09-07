package org.elixir_lang.elixir_flex_lexer;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;
import org.elixir_lang.ElixirLexer;

/**
 * {@code checkCorrectRestart} is called explicitly because {@code doTest} only performs it
 * implicitly on platform 262 - see the KDoc on
 * {@link org.elixir_lang.heex.lexer.RestartabilityTest}. Without it this class's effective
 * coverage would differ across the CI legs in .github/ci-versions.json.
 */
public class Issue1888Test extends LexerTestCase {
    public void testAtom() {
        String text = "defmodule MyModule do\n" +
                "  def my_function([:list_atom], :argument_atom)\n" +
                "end\n";

        doTest(text,
                "identifier ('defmodule')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "Alias ('MyModule')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "do ('do')\n" +
                        "WHITE_SPACE ('\\n  ')\n" +
                        "identifier ('def')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "identifier ('my_function')\n" +
                        "<zero-width-call> ('')\n" +
                        "( ('(')\n" +
                        "[ ('[')\n" +
                        ": (':')\n" +
                        "A-Z, a-z, _, @, 0-9. ?, ! ('list_atom')\n" +
                        "] (']')\n" +
                        ", (',')\n" +
                        "WHITE_SPACE (' ')\n" +
                        ": (':')\n" +
                        "A-Z, a-z, _, @, 0-9. ?, ! ('argument_atom')\n" +
                        ") (')')\n" +
                        "WHITE_SPACE ('\\n')\n" +
                        "end ('end')\n" +
                        "\\\\n, \\\\r\\\\n ('\\n')");
        checkCorrectRestart(text);
    }

    public void testColumn() {
        String text = "defmodule MyModule do\n" +
                "  def my_function([:list_atom], :)\n" +
                "end\n";

        doTest(text,
                        "identifier ('defmodule')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "Alias ('MyModule')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "do ('do')\n" +
                        "WHITE_SPACE ('\\n  ')\n" +
                        "identifier ('def')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "identifier ('my_function')\n" +
                        "<zero-width-call> ('')\n" +
                        "( ('(')\n" +
                        "[ ('[')\n" +
                        ": (':')\n" +
                        "A-Z, a-z, _, @, 0-9. ?, ! ('list_atom')\n" +
                        "] (']')\n" +
                        ", (',')\n" +
                        "WHITE_SPACE (' ')\n" +
                        ": (':')\n" +
                        ") (')')\n" +
                        "WHITE_SPACE ('\\n')\n" +
                        "end ('end')\n" +
                        "\\\\n, \\\\r\\\\n ('\\n')");
        checkCorrectRestart(text);
    }

    @Override
    protected Lexer createLexer() {
        return new ElixirLexer();
    }

    @Override
    protected String getDirPath() {
        return "testData/org/elixir_lang/elixir_flex_lexer/issue_1888";
    }
}
