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
public class DecimalIntegerTest extends TokenTest {
    /*
     * Constructors
     */
    public DecimalIntegerTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger DECIMAL_WHOLE_NUMBER state
        CharSequence fullCharSequence = "1" + charSequence;
        super.reset(fullCharSequence);
        // consume '0'
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
                { ".", ElixirTypes.DECIMAL_MARK, ElixirFlexLexer.DECIMAL_FRACTION },
                { "/", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "0", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "1", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "2", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "3", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "4", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "5", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "6", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "7", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "8", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "9", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                { ";", ElixirTypes.SEMICOLON, INITIAL_STATE },
                { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "?", ElixirTypes.CHAR_TOKENIZER, ElixirFlexLexer.CHAR_TOKENIZATION },
                { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "A", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "B", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "C", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "D", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "E", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "F", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "G", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "H", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "I", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "J", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "K", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "L", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "M", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "N", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "O", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "P", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "Q", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "R", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "S", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "T", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "U", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "V", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "W", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "X", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "Y", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "Z", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "[", ElixirTypes.OPENING_BRACKET, INITIAL_STATE },
                { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "\n", ElixirTypes.EOL, INITIAL_STATE },
                { "\r\n", ElixirTypes.EOL, INITIAL_STATE },
                { "]", ElixirTypes.CLOSING_BRACKET, INITIAL_STATE },
                { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "_", ElixirTypes.DECIMAL_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "`", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "a", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "b", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "c", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "d", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "e", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "f", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "g", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "h", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "i", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "j", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "k", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "l", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "m", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "n", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "o", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "p", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "q", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "r", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "s", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "t", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "u", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "v", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "w", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "x", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "y", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "z", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER },
                { "{", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "}", TokenType.BAD_CHARACTER, INITIAL_STATE },
                { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL }
        });
    }
}
