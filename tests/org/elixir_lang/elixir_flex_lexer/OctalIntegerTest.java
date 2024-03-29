package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 11/28/14.
 */
@RunWith(Parameterized.class)
public class OctalIntegerTest extends TokenTest {
    /*
     * Constructors
     */
    public OctalIntegerTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger OCTAL_WHOLE_NUMBER state
        CharSequence fullCharSequence = "0o" + charSequence;
        super.start(fullCharSequence);
        // consume '0'
        lexer.advance();
        // consume 'o'
        lexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                { " ", TokenType.WHITE_SPACE, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE },
                { "!", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "#", ElixirTypes.COMMENT, ElixirFlexLexer.YYINITIAL },
                { "$", TokenType.BAD_CHARACTER, ElixirFlexLexer.YYINITIAL },
                { "%", ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "&", ElixirTypes.CAPTURE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "'", ElixirTypes.LINE_PROMOTER, ElixirFlexLexer.GROUP },
                { "'''", ElixirTypes.HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "(", ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE },
                { ")", ElixirTypes.CLOSING_PARENTHESIS, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE },
                { "*", ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "**", ElixirTypes.POWER_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "+", ElixirTypes.ADDITION_OPERATOR, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE },
                { ",", ElixirTypes.COMMA, ElixirFlexLexer.YYINITIAL },
                { "-", ElixirTypes.SUBTRACTION_OPERATOR, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE },
                { ".", ElixirTypes.DOT_OPERATOR, ElixirFlexLexer.DOT_OPERATION },
                { "/", ElixirTypes.DIVISION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "0", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "1", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "2", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "3", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "4", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "5", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "6", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "7", ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "8", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "9", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START },
                { ";", ElixirTypes.SEMICOLON, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE },
                { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "?", ElixirTypes.CHAR_TOKENIZER, ElixirFlexLexer.CHAR_TOKENIZATION },
                { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "A", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "B", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "C", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "D", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "E", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "F", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "G", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "H", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "I", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "J", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "K", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "L", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "M", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "N", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "O", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "P", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "Q", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "R", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "S", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "T", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "U", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "V", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "W", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "X", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "Y", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "Z", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "[", ElixirTypes.OPENING_BRACKET, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE },
                { "\"", ElixirTypes.LINE_PROMOTER, ElixirFlexLexer.GROUP },
                { "\"\"\"", ElixirTypes.HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                { "\n", ElixirTypes.EOL, ElixirFlexLexer.YYINITIAL },
                { "\r\n", ElixirTypes.EOL, ElixirFlexLexer.YYINITIAL },
                { "]", ElixirTypes.CLOSING_BRACKET, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE },
                { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE },
                { "_", ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "`", TokenType.BAD_CHARACTER, ElixirFlexLexer.YYINITIAL },
                { "a", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "b", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "c", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "d", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "e", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "f", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "g", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "h", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "i", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "j", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "k", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "l", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "m", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "n", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "o", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "p", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "q", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "r", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "s", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "t", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "u", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "v", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "w", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "x", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "y", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "z", ElixirTypes.INVALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER },
                { "{", ElixirTypes.OPENING_CURLY, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE },
                { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE },
                { "}", ElixirTypes.CLOSING_CURLY, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE },
                { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL }
        });
    }
}
