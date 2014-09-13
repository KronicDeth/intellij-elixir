package org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.string;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/8/14.
 */
public class TripleDoubleQuotesTest extends org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.string.Test {
    protected IElementType fragmentType() {
        return ElixirTypes.STRING_FRAGMENT;
    }

    protected String promoter() {
        return "\"\"\"";
    }

    protected IElementType terminatorType() {
        return ElixirTypes.STRING_HEREDOC_TERMINATOR;
    }

    @Override
    @Test
    public void tripleDoubleQuotes() throws IOException {
        super.tripleDoubleQuotes();

        assertEquals(terminatorType(), flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
    }

    @Override
    @Test
    public void tripleSingleQuotes() throws IOException {
        super.tripleSingleQuotes();

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, flexLexer.yystate());
    }
}
