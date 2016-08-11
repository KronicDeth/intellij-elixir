package org.elixir_lang;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirDoBlock;
import org.elixir_lang.psi.ElixirTypes;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BraceMatcher implements PairedBraceMatcher {
    /*
     * CONSTANTS
     */

    private final static BracePair[] BRACE_PAIRS = new BracePair[]{
            new BracePair(ElixirTypes.DO,                  ElixirTypes.END,                 true),
            new BracePair(ElixirTypes.FN,                  ElixirTypes.END,                 true),
            new BracePair(ElixirTypes.OPENING_BIT,         ElixirTypes.CLOSING_BIT,         false),
            new BracePair(ElixirTypes.OPENING_BRACKET,     ElixirTypes.CLOSING_BRACKET,     false),
            new BracePair(ElixirTypes.OPENING_CURLY,       ElixirTypes.CLOSING_CURLY,       false),
            new BracePair(ElixirTypes.OPENING_PARENTHESIS, ElixirTypes.CLOSING_PARENTHESIS, false)
    };

    /*
     * Instance Methods
     */

    /**
     * Returns the start offset of the code construct which owns the opening structural brace at the specified offset.
     * For example, if the opening brace belongs to an 'if' statement, returns the start offset of the 'if' statement.
     * This is used for the scope highlighting.
     *
     * @param file               the file in which brace matching is performed.
     * @param openingBraceOffset the offset of an opening structural brace.
     * @return the offset of corresponding code construct, or the same offset if not defined.
     */
    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        int offset = openingBraceOffset;

        PsiElement element = file.findElementAt(openingBraceOffset);

        if (element != null) {
            PsiElement parent = element.getParent();

            if (parent instanceof ElixirDoBlock) {
                PsiElement grandParent = parent.getParent();

                if (grandParent instanceof Call) {
                    offset = grandParent.getTextOffset();
                }
            }
        }

        return offset;
    }

    /**
     * Returns the array of definitions for brace pairs that need to be matched when
     * editing code in the language.
     *
     * @return the array of brace pair definitions.
     */
    @Contract(pure = true)
    @Override
    public BracePair[] getPairs() {
        return BRACE_PAIRS;
    }

    /**
     * Returns true if paired rbrace should be inserted after lbrace of given type when lbrace is encountered before
     * contextType token. It is safe to always return true, then paired brace will be inserted anyway.
     *
     * @param lbraceType  lbrace for which information is queried
     * @param contextType token type that follows lbrace
     * @return true / false as described
     */
    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

}
