package org.elixir_lang.elixir_flex_lexer.group;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/2/14.
 */
public class InterpolationTest extends org.elixir_lang.elixir_flex_lexer.group.Test {
    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start of quote to trigger GROUP state with isInterpolating being true
        CharSequence fullCharSequence = "\"" + charSequence;
        // consumes '"'
        super.reset(fullCharSequence);
    }

    @Test
    public void hashBrace() throws IOException {
        reset("#{");

        assertEquals(ElixirTypes.INTERPOLATION_START, flexLexer.advance());
        assertEquals(ElixirFlexLexer.INTERPOLATION, flexLexer.yystate());
    }

    @Test
    public void escapedDoubleQuotes() throws IOException {
        reset("\\\"");

        assertEquals(ElixirTypes.VALID_ESCAPE_SEQUENCE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void escapedSingleQuotes() throws IOException {
        reset("\\'");

        assertEquals(ElixirTypes.VALID_ESCAPE_SEQUENCE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @Test
    public void escapedInterpolationState() throws IOException {
        reset("\\#{");

        assertEquals(ElixirTypes.VALID_ESCAPE_SEQUENCE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }
}
