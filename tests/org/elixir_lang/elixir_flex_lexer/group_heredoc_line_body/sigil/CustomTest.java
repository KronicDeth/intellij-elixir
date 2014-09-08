package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body.sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/3/14.
 */
public class CustomTest extends PromoterTest {
    @NotNull
    @Override
    protected IElementType fragmentType() {
        return ElixirTypes.SIGIL_FRAGMENT;
    }

    @Override
    protected char sigilName() {
        // arbitrary character so long as it is not a defined sigil in the Elixir standard library
        return 'x';
    }
}
