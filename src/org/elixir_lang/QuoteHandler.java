package org.elixir_lang;

import com.google.common.collect.ImmutableMap;
import com.intellij.codeInsight.editorActions.MultiCharQuoteHandler;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.lexer.StackFrame;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class QuoteHandler implements MultiCharQuoteHandler {
    /*
     * CONSTANTS
     */

    private final static TokenSet LITERALS = TokenSet.create(
            ElixirTypes.ATOM_FRAGMENT,
            ElixirTypes.FRAGMENT
    );

    /*
     * Instance Methods
     */

    @Nullable
    @Override
    public CharSequence getClosingQuote(HighlighterIterator highlighterIterator, int offset) {
        CharSequence closingQuote = null;

        if (highlighterIterator.getStart() > 0) {
            highlighterIterator.retreat();

            try {
                IElementType tokenType = highlighterIterator.getTokenType();

                if (tokenType == ElixirTypes.HEREDOC_PROMOTER || tokenType == ElixirTypes.LINE_PROMOTER) {
                    Document document = highlighterIterator.getDocument();

                    if (document != null) {
                        String promoter = document.getText(
                                new TextRange(highlighterIterator.getStart(), highlighterIterator.getEnd())
                        );
                        String terminator = StackFrame.TERMINATOR_BY_PROMOTER.get(promoter);

                        if (terminator != null) {
                            if (terminator.length() >= 3) {
                                closingQuote = "\n" + terminator;
                            } else {
                                closingQuote = terminator;
                            }
                        }
                    }
                }
            } finally {
                highlighterIterator.advance();
            }
        }

        return closingQuote;
    }

    /**
     *
     * @param editor
     * @param highlighterIterator
     * @param offset the offset of the element with {@link HighlighterIterator#getTokenType()}
     * @return {@code true} to automatically insert a closing quote
     *   (from {@link #getClosingQuote(HighlighterIterator, int)}
     * @see <a href="https://github.com/JetBrains/intellij-community/blob/eeefa20d7c43856143c825ca65de6d5089241b35/platform/lang-impl/src/com/intellij/codeInsight/editorActions/TypedHandler.java#L472-L478">TypeHandler#handleQuote</a>
     * @see <a href="https://github.com/JetBrains/intellij-community/blob/eeefa20d7c43856143c825ca65de6d5089241b35/platform/lang-impl/src/com/intellij/codeInsight/editorActions/TypedHandler.java#L529">TypeHandler#hasNonClosedLiterals</a>
     */
    @Override
    public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator highlighterIterator, int offset) {
        /* it is safe to always return true based on XMLQuoteHandler
           @see https://github.com/JetBrains/intellij-community/blob/eeefa20d7c43856143c825ca65de6d5089241b35/xml/impl/src/com/intellij/codeInsight/editorActions/XmlQuoteHandler.java#L38 */
        return true;
    }

    /**
     *
     * @param highlighterIterator
     * @param offset the current offset in the file of the {@code highlighterIterator}
     * @return {@code true} if {@link HighlighterIterator#getTokenType()} is {@link ElixirTypes#HEREDOC_TERMINATOR} or
     *    {@link ElixirTypes#LINE_TERMINATOR} and {@code offset} is {@link HighlighterIterator#getStart()}
     */
    @Override
    public boolean isClosingQuote(HighlighterIterator highlighterIterator, int offset) {
        IElementType tokenType;

        try {
            tokenType = highlighterIterator.getTokenType();
        } catch (IndexOutOfBoundsException e) {
            tokenType = null;
        }

        boolean isClosingQuote;

        if (tokenType == ElixirTypes.HEREDOC_TERMINATOR || tokenType == ElixirTypes.LINE_TERMINATOR) {
            int start = highlighterIterator.getStart();
            int end = highlighterIterator.getEnd();
            isClosingQuote = end - start >= 1 && offset == end - 1;
        } else {
            isClosingQuote = false;
        }

        return isClosingQuote;
    }

    @Override
    public boolean isInsideLiteral(HighlighterIterator highlighterIterator) {
        return LITERALS.contains(highlighterIterator.getTokenType());
    }

    @Override
    public boolean isOpeningQuote(HighlighterIterator highlighterIterator, int offset) {
        IElementType tokenType;

        try {
            tokenType = highlighterIterator.getTokenType();
        } catch (IndexOutOfBoundsException e) {
            tokenType = null;
        }

        boolean isOpeningQuote;

        if (tokenType == ElixirTypes.HEREDOC_PROMOTER || tokenType == ElixirTypes.LINE_PROMOTER) {
            int start = highlighterIterator.getStart();
            isOpeningQuote = offset == start;
        } else {
            isOpeningQuote = false;
        }

        return isOpeningQuote;
    }
}
