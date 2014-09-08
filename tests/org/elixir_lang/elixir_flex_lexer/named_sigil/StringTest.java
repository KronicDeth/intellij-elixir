package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/4/14.
 */
public class StringTest extends Test {
    @Override
    protected IElementType heredocPromoterType() {
        return ElixirTypes.STRING_SIGIL_HEREDOC_PROMOTER;
    }

    @Override
    protected IElementType promoterType() {
        return ElixirTypes.STRING_SIGIL_PROMOTER;
    }

    @Override
    protected char sigilName() {
        return 's';
    }
}
