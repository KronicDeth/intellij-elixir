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
 * Created by luke.imhoff on 11/28/14.
 */
@RunWith(Parameterized.class)
public class HexadecimalIntegerTest extends TokenTest {
    /*
     * Constructors
     */
    public HexadecimalIntegerTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger HEXADECIMAL_INTEGER state
        CharSequence fullCharSequence = "0x" + charSequence;
        super.reset(fullCharSequence);
        // consume '0'
        flexLexer.advance();
        // consume 'x'
        flexLexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                { " ", TokenType.WHITE_SPACE, INITIAL_STATE },
                { "!", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "#", ElixirTypes.COMMENT, INITIAL_STATE },
                { "$", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "%", ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "&", ElixirTypes.CAPTURE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "(", ElixirTypes.OPENING_PARENTHESIS, INITIAL_STATE },
                { ")", ElixirTypes.CLOSING_PARENTHESIS, INITIAL_STATE },
                { "*", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "+", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ",", ElixirTypes.COMMA, INITIAL_STATE },
                { "-", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ".", ElixirTypes.DOT_OPERATOR, INITIAL_STATE },
                { "/", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "0", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "1", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "2", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "3", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "4", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "5", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "6", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "7", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "8", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "9", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                { ";", ElixirTypes.SEMICOLON, INITIAL_STATE },
                { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "?", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "A", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "B", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "C", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "D", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "E", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "F", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "G", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "H", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "I", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "J", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "K", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "L", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "M", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "N", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "O", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "P", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "Q", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "R", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "S", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "T", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "U", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "V", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "W", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "X", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "Y", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "Z", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "[", ElixirTypes.OPENING_BRACKET, INITIAL_STATE },
                { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "\n", ElixirTypes.EOL, INITIAL_STATE },
                { "\r\n", ElixirTypes.EOL, INITIAL_STATE },
                { "]", ElixirTypes.CLOSING_BRACKET, INITIAL_STATE },
                { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "_", ElixirTypes.IDENTIFIER, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "`", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "a", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "b", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "c", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "d", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "e", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "f", ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "g", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "h", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "i", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "j", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "k", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "l", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "m", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "n", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "o", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "p", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "q", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "r", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "s", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "t", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "u", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "v", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "w", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "x", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "y", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "z", ElixirTypes.INVALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_INTEGER },
                { "{", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "}", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL }
        });
    }
}
