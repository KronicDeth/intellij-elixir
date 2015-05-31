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
    public DecimalIntegerTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
        super(charSequence, tokenType, lexicalState, consumeAll);
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
                { " ", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                { "!", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "#", ElixirTypes.COMMENT, INITIAL_STATE, true },
                { "$", TokenType.BAD_CHARACTER, INITIAL_STATE, true },
                { "%", ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "&", ElixirTypes.CAPTURE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP, true },
                { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START, true },
                { "(", ElixirTypes.OPENING_PARENTHESIS, INITIAL_STATE, true },
                { ")", ElixirTypes.CLOSING_PARENTHESIS, INITIAL_STATE, true },
                { "*", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "+", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.DUAL_OPERATION, true },
                { ",", ElixirTypes.COMMA, INITIAL_STATE, true },
                { "-", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.DUAL_OPERATION, true },
                { ".", ElixirTypes.DOT_OPERATOR, ElixirFlexLexer.DOT_OPERATION, true },
                { ".2", ElixirTypes.DECIMAL_MARK, ElixirFlexLexer.DECIMAL_FRACTION, false },
                { "/", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "0", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "1", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "2", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "3", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "4", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "5", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "6", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "7", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "8", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "9", null, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START, true },
                { ";", ElixirTypes.SEMICOLON, INITIAL_STATE, true },
                { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "?", ElixirTypes.CHAR_TOKENIZER, ElixirFlexLexer.CHAR_TOKENIZATION, true },
                { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "A", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "B", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "C", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "D", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "E", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "F", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "G", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "H", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "I", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "J", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "K", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "L", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "M", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "N", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "O", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "P", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "Q", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "R", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "S", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "T", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "U", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "V", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "W", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "X", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "Y", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "Z", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "[", ElixirTypes.OPENING_BRACKET, INITIAL_STATE, true },
                { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP, true },
                { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START, true },
                { "\n", ElixirTypes.EOL, INITIAL_STATE, true },
                { "\r\n", ElixirTypes.EOL, INITIAL_STATE, true },
                { "]", ElixirTypes.CLOSING_BRACKET, INITIAL_STATE, true },
                { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "_", ElixirTypes.DECIMAL_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "`", TokenType.BAD_CHARACTER, INITIAL_STATE, true },
                { "a", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "b", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "c", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "d", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "e", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "f", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "g", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "h", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "i", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "j", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "k", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "l", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "m", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "n", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "o", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "p", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "q", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "r", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "s", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "t", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "u", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "v", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "w", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "x", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "y", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "z", ElixirTypes.INVALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                { "{", ElixirTypes.OPENING_CURLY, INITIAL_STATE, true },
                { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                { "}", ElixirTypes.CLOSING_CURLY, INITIAL_STATE, true },
                { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL, true }
        });
    }
}
