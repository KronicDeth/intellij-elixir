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
public class UnknownBaseIntegerTest extends TokenTest {
    /*
     * Constructors
     */
    public UnknownBaseIntegerTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger UNKNOWN_BASE_INTEGER state
        CharSequence fullCharSequence = "0z" + charSequence;
        super.reset(fullCharSequence);
        // consume '0'
        flexLexer.advance();
        // consume 'z'
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
                { "0", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "1", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "2", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "3", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "4", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "5", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "6", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "7", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "8", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "9", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                { ";", ElixirTypes.SEMICOLON, INITIAL_STATE },
                { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "?", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "A", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "B", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "C", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "D", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "E", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "F", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "G", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "H", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "I", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "J", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "K", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "L", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "M", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "N", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "O", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "P", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "Q", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "R", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "S", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "T", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "U", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "V", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "W", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "X", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "Y", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "Z", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "[", ElixirTypes.OPENING_BRACKET, INITIAL_STATE },
                { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "\n", ElixirTypes.EOL, INITIAL_STATE },
                { "\r\n", ElixirTypes.EOL, INITIAL_STATE },
                { "]", ElixirTypes.CLOSING_BRACKET, INITIAL_STATE },
                { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "_", ElixirTypes.IDENTIFIER, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "`", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "a", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "b", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "c", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "d", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "e", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "f", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "g", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "h", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "i", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "j", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "k", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "l", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "m", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "n", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "o", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "p", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "q", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "r", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "s", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "t", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "u", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "v", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "w", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "x", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "y", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "z", ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS, ElixirFlexLexer.UNKNOWN_BASE_INTEGER },
                { "{", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "}", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL }
        });
    }
}
