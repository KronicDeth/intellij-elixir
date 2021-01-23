package org.elixir_lang.elixir_flex_lexer;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;
import org.elixir_lang.ElixirLexer;

public class Issue1888Test extends LexerTestCase {
    public void testAtom() {
        doTest("defmodule MyModule do\n" +
                        "  def my_function([:list_atom], :argument_atom)\n" +
                        "end\n",
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
    }

    public void testColumn() {
        doTest("defmodule MyModule do\n" +
                        "  def my_function([:list_atom], :)\n" +
                        "end\n",
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
