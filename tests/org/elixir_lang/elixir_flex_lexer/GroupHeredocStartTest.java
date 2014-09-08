package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
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
public class GroupHeredocStartTest extends org.elixir_lang.elixir_flex_lexer.Test {
    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger GROUP_HEREDOC_START state
        CharSequence fullCharSequence = "'''" + charSequence;
        super.reset(fullCharSequence);
        // consume "'''"
        flexLexer.advance();
    }

    @Test
    public void eol() throws IOException {
        reset("\n");

        assertEquals(ElixirTypes.EOL, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_LINE_START, flexLexer.yystate());
    }

    @Test
    public void character() throws IOException {
        reset("a");

        assertEquals(TokenType.BAD_CHARACTER, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_START, flexLexer.yystate());
    }
}
