package org.elixir_lang.heex.html;

import com.intellij.lexer.HtmlLexer;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLLexer extends HtmlLexer {
    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        CharSequence maskedBuffer = maskRelativeComponentDots(buffer, startOffset, endOffset);

        super.start(maskedBuffer, startOffset, endOffset, initialState);
    }

    /**
     * The HTML lexer does not support tag names beginning with `.`. This method masks these dots by replacing with 'C',
     * allowing the lexer to properly process HEEx relative component tags (e.g. <.button>).
     */
    private CharSequence maskRelativeComponentDots(@NotNull CharSequence buffer, int startOffset, int endOffset) {
        int startIndex = 0;
        StringBuilder stringBuilder = new StringBuilder(endOffset);

        for (int i = startOffset; i < endOffset; i++) {
            if (buffer.charAt(i) == '<') {
                if (endOffset > i + 1 && buffer.charAt(i + 1) == '.') {
                    stringBuilder
                      .append(buffer.subSequence(startIndex, i + 1))
                      .append('C');

                    startIndex = i + 2;
                    i += 1;
                } else if (endOffset > i + 2 && buffer.charAt(i + 1) == '/' && buffer.charAt(i + 2) == '.') {
                    stringBuilder
                      .append(buffer.subSequence(startIndex, i + 2))
                      .append('C');

                    startIndex = i + 3;
                    i += 2;
                }
            }
        }

        stringBuilder.append(buffer.subSequence(startIndex, endOffset));

        return stringBuilder;
    }
}
