package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body.sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/3/14.
 */
public class RegexTest extends PromoterTest {
    @NotNull
    @Override
    protected IElementType fragmentType() {
        return ElixirTypes.REGEX_FRAGMENT;
    }

    @Override
    protected char sigilName() {
        return 'r';
    }
}
