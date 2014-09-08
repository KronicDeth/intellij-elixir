package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/4/14.
 */
public class CustomTest extends PromoterTest {
    @Override
    protected IElementType heredocPromoterType() {
        return ElixirTypes.SIGIL_HEREDOC_PROMOTER;
    }

    @Override
    protected IElementType promoterType() {
        return ElixirTypes.SIGIL_PROMOTER;
    }

    @Override
    protected char sigilName() {
        // choice is arbitrary, just so it is not one of the sigils in the standard library
        return 'x';
    }
}
