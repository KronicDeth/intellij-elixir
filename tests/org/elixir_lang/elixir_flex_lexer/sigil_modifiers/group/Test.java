package org.elixir_lang.elixir_flex_lexer.sigil_modifiers.group;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Ignore;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 10/23/14.
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

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { " ", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL },
                        { ";", ElixirTypes.SEMICOLON, ElixirFlexLexer.YYINITIAL },
                        { "A", ElixirTypes.SIGIL_MODIFIER, ElixirFlexLexer.SIGIL_MODIFIERS },
                        { "\n", ElixirTypes.EOL, ElixirFlexLexer.SIGN_OPERATION_MAYBE },
                        { "\r\n", ElixirTypes.EOL, ElixirFlexLexer.SIGN_OPERATION_MAYBE },
                        { "a", ElixirTypes.SIGIL_MODIFIER, ElixirFlexLexer.SIGIL_MODIFIERS }
                }
        );
    }

    protected abstract char promoter();
    protected abstract char terminator();

    @Override
    protected void start(CharSequence charSequence) {
        // start to trigger SIGIL_MODIFIERS after GROUP
        CharSequence fullCharSequence = "~r" + promoter() + terminator() + charSequence;
        super.start(fullCharSequence);
        // consume ~
        lexer.advance();
        // consume r
        lexer.advance();
        // consume promoter
        lexer.advance();
        // consume terminator
        lexer.advance();
    }
}
