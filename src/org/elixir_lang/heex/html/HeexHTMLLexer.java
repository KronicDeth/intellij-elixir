package org.elixir_lang.heex.html;

import com.intellij.lexer.HtmlLexer;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLLexer extends HtmlLexer {
    public HeexHTMLLexer() {
        super();
    }

    public HeexHTMLLexer(boolean highlightMode) {
        super(highlightMode);
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        // Mask over the whole buffer, not just startOffset..endOffset: masking is length-preserving
        // (only `.` -> 'C' substitutions, never an insertion or deletion), so masking the full buffer
        // keeps every index valid and lets startOffset/endOffset pass through unchanged, so token
        // offsets stay in the original buffer's coordinate space for an incremental re-lex.
        CharSequence maskedBuffer = maskRelativeComponentDots(buffer, 0, buffer.length());

        super.start(maskedBuffer, startOffset, endOffset, initialState);
    }

    /**
     * The HTML lexer does not support tag names beginning with `.`. This method masks these dots by replacing with 'C',
     * allowing the lexer to properly process HEEx relative component tags (e.g. <.button>).
     */
    private CharSequence maskRelativeComponentDots(@NotNull CharSequence buffer, int startOffset, int endOffset) {
        int startIndex = startOffset;
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
