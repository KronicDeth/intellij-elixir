package org.elixir_lang.elixir_flex_lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luke.imhoff on 9/1/14.
 */
@RunWith(Parameterized.class)
public class BodyTest  extends org.elixir_lang.elixir_flex_lexer.Test {
    /*
     * Fields
     */

    private CharSequence charSequence;
    private int lexicalState;
    private IElementType tokenType;

    /*
     * Constructors
     */

    public BodyTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        this.charSequence = charSequence;
        this.lexicalState = lexicalState;
        this.tokenType = tokenType;
    }

    @Parameterized.Parameters(
            name = "{0} parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
                        { " ", TokenType.WHITE_SPACE, ElixirFlexLexer.BODY },
                        { "#", ElixirTypes.COMMENT, ElixirFlexLexer.BODY },
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                        { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "001234567", ElixirTypes.NUMBER, ElixirFlexLexer.BODY },
                        { "0B10", ElixirTypes.NUMBER, ElixirFlexLexer.BODY },
                        { "0X0123456789abcdefABCDEF", ElixirTypes.NUMBER, ElixirFlexLexer.BODY },
                        { "0b10", ElixirTypes.NUMBER, ElixirFlexLexer.BODY },
                        { "0o01234567", ElixirTypes.NUMBER, ElixirFlexLexer.BODY },
                        { "0x0123456789abcdefABCDEF", ElixirTypes.NUMBER, ElixirFlexLexer.BODY },
                        { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                        { "\"\"\"",ElixirTypes.STRING_HEREDOC_PROMOTER,ElixirFlexLexer.GROUP_HEREDOC_START },
                        { "\f", TokenType.WHITE_SPACE, ElixirFlexLexer.BODY },
                        { "\n \t\f", ElixirTypes.EOL, ElixirFlexLexer.BODY },
                        { "\n", ElixirTypes.EOL, ElixirFlexLexer.BODY },
                        { "\t", TokenType.WHITE_SPACE, ElixirFlexLexer.BODY },
                        { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL }
                }
        );
    }

    @Test
    public void token() throws IOException {
        reset(charSequence);

        assertEquals(tokenType, flexLexer.advance());
        assertEquals(lexicalState, flexLexer.yystate());
        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    }
}
