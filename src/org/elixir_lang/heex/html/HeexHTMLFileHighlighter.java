package org.elixir_lang.heex.html;

import com.intellij.ide.highlighter.HtmlFileHighlighter;
import com.intellij.lexer.Lexer;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLFileHighlighter extends HtmlFileHighlighter {
    public @NotNull Lexer getHighlightingLexer() {
        return new HeexHTMLLexer(true);
    }
}
