package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/4/14.
 */
public abstract class Test extends TokenTest {
    /*
     * Constructors
     */

    public Test(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    protected abstract Sigil instanceSigil();

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger NAMED_SIGIL state
        CharSequence fullCharSequence = "~" + instanceSigil().name() + charSequence;
        super.reset(fullCharSequence);
        // consume '~'
        flexLexer.advance();
        // consume sigil name
        flexLexer.advance();
    }

    public static Collection<Object[]> generateData(Sigil sigil) {
        return Arrays.asList(new Object[][]{
                        { "\"\"\"", sigil.heredocPromoterType(), ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "'''", sigil.heredocPromoterType(), ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "/", sigil.promoterType(), ElixirFlexLexer.GROUP },
                        { "|", sigil.promoterType(), ElixirFlexLexer.GROUP },
                        { "{", sigil.promoterType(), ElixirFlexLexer.GROUP },
                        { "[", sigil.promoterType(), ElixirFlexLexer.GROUP },
                        { "<", sigil.promoterType(), ElixirFlexLexer.GROUP },
                        { "\"", sigil.promoterType(), ElixirFlexLexer.GROUP },
                        { "(", sigil.promoterType(), ElixirFlexLexer.GROUP },
                        { "'", sigil.promoterType(), ElixirFlexLexer.GROUP }
                }
        );
    }
}
