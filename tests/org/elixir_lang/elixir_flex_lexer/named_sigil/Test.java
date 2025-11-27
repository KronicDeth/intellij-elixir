package org.elixir_lang.elixir_flex_lexer.named_sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

import static org.elixir_lang.psi.ElixirTypes.HEREDOC_PROMOTER;
import static org.elixir_lang.psi.ElixirTypes.LINE_PROMOTER;

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

    protected abstract char name();

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger NAMED_SIGIL state
        CharSequence fullCharSequence = "~" + name() + charSequence;
        super.start(fullCharSequence);
        // consume '~'
        lexer.advance();
        // consume sigil name
        lexer.advance();
    }

    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { "\"\"\"", HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "'''", HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "/", LINE_PROMOTER, ElixirFlexLexer.GROUP },
                        { "|", LINE_PROMOTER, ElixirFlexLexer.GROUP },
                        { "{", LINE_PROMOTER, ElixirFlexLexer.GROUP },
                        { "[", LINE_PROMOTER, ElixirFlexLexer.GROUP },
                        { "<", LINE_PROMOTER, ElixirFlexLexer.GROUP },
                        { "\"", LINE_PROMOTER, ElixirFlexLexer.GROUP },
                        { "(", LINE_PROMOTER, ElixirFlexLexer.GROUP },
                        { "'", LINE_PROMOTER, ElixirFlexLexer.GROUP }
                }
        );
    }
}
