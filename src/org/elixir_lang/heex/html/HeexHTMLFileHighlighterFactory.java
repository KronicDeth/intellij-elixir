package org.elixir_lang.heex.html;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLFileHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {
    protected @NotNull SyntaxHighlighter createHighlighter() {
        return new HeexHTMLFileHighlighter();
    }
}
