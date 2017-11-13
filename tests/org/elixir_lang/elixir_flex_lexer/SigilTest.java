package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/4/14.
 */
@RunWith(Parameterized.class)
public class SigilTest extends TokenTest {
    /*
     * Constructors
     */

    public SigilTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void start(CharSequence charSequence) {
        // start to trigger SIGIL state
        CharSequence fullCharSequence = "~" + charSequence;
        super.start(fullCharSequence);
        // consume '~'
        lexer.advance();
    }

    @Parameterized.Parameters(
         name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { ";", TokenType.BAD_CHARACTER, ElixirFlexLexer.SIGIL },
                        { "C", ElixirTypes.LITERAL_CHAR_LIST_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "R", ElixirTypes.LITERAL_REGEX_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "S", ElixirTypes.LITERAL_STRING_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "W", ElixirTypes.LITERAL_WORDS_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "X", ElixirTypes.LITERAL_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "\n", TokenType.BAD_CHARACTER, ElixirFlexLexer.SIGIL },
                        { "\r\n", TokenType.BAD_CHARACTER, ElixirFlexLexer.SIGIL },
                        { "c", ElixirTypes.INTERPOLATING_CHAR_LIST_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "r", ElixirTypes.INTERPOLATING_REGEX_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "s", ElixirTypes.INTERPOLATING_STRING_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "w", ElixirTypes.INTERPOLATING_WORDS_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL },
                        { "x", ElixirTypes.INTERPOLATING_SIGIL_NAME, ElixirFlexLexer.NAMED_SIGIL }
                }
        );
    }
}
