package org.elixir_lang.console;

import com.intellij.execution.filters.Filter.ResultItem;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import org.jetbrains.annotations.NotNull;

/**
 * A link that outranks the terminal's own bare-path link over the same text.
 *
 * <p>{@code TerminalGenericFileFilter} is registered ahead of every plugin's and marks up the same
 * path, invisibly and opening at line 1. Both default to {@link HighlighterLayer#HYPERLINK} and a
 * tie goes to whichever was added first, which is the terminal's, so one layer up is enough.
 */
final class AboveGenericFileLinks extends ResultItem {
    AboveGenericFileLinks(int highlightStartOffset,
                          int highlightEndOffset,
                          @NotNull HyperlinkInfo hyperlinkInfo) {
        super(highlightStartOffset, highlightEndOffset, hyperlinkInfo);
    }

    @Override
    public int getHighlighterLayer() {
        return HighlighterLayer.HYPERLINK + 1;
    }
}
