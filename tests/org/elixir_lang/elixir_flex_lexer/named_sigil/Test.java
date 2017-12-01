package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/4/14.
 */
@Ignore("abstract")
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
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger NAMED_SIGIL state
        CharSequence fullCharSequence = "~" + instanceSigil().name() + charSequence;
        super.start(fullCharSequence);
        // consume '~'
        lexer.advance();
        // consume sigil name
        lexer.advance();
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
