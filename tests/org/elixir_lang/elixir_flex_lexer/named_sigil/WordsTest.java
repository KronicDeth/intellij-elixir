package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/4/14.
 */
public class WordsTest extends Test {
    @Override
    protected IElementType heredocPromoterType() {
        return ElixirTypes.WORDS_HEREDOC_PROMOTER;
    }

    @Override
    protected IElementType promoterType() {
        return ElixirTypes.WORDS_PROMOTER;
    }

    @Override
    protected char sigilName() {
        return 'w';
    }
}
