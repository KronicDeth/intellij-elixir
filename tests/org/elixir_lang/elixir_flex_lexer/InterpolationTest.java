package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/1/14.
 */
@RunWith(Parameterized.class)
public class InterpolationTest extends TokenTest {
    /*
     * Constants
     */

    private static final int INITIAL_STATE = ElixirFlexLexer.INTERPOLATION;

    /*
     * Constructors
     */

    public InterpolationTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
        super(charSequence, tokenType, lexicalState, consumeAll);
    }

    /*
     * Methods
     */

    @Override
    protected int initialState() {
        return INITIAL_STATE;
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { " ", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "!", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "!=", ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "!==", ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "#", ElixirTypes.COMMENT, INITIAL_STATE, true },
                        { "%", ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "%{}", ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, false },
                        { "&", ElixirTypes.CAPTURE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "&&", ElixirTypes.AND_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "&&&", ElixirTypes.AND_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP, true },
                        { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START, true },
                        { "(", ElixirTypes.OPENING_PARENTHESIS, INITIAL_STATE, true },
                        { ")", ElixirTypes.CLOSING_PARENTHESIS, INITIAL_STATE, true },
                        { "+", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.DUAL_OPERATION, true},
                        { "++", ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { ",", ElixirTypes.COMMA, INITIAL_STATE, true },
                        { "-", ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.DUAL_OPERATION, true},
                        { "--", ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "->", ElixirTypes.STAB_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { ".", ElixirTypes.DOT_OPERATOR, ElixirFlexLexer.DOT_OPERATION, true },
                        { "..", ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "...", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "001234567", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                        { "0B10", ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE, false },
                        { "0X0123456789abcdefABCDEF", ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE, false },
                        { "0b10", ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE, false },
                        { "0o01234567", ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE, false },
                        { "0x0123456789abcdefABCDEF", ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE, false },
                        { "1.", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, false },
                        { "1.0", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, false },
                        { "1.0e+1", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, false },
                        { "1.0e-1", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, false },
                        { "1.0e1", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, false },
                        { "1234567890", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, true },
                        { "1_", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, false},
                        { "1_2_3_4_5_6_7_8_9_0", ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER, false },
                        { ": ", ElixirTypes.COLON, INITIAL_STATE, false },
                        { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START, true },
                        { "::", ElixirTypes.TYPE_OPERATOR, INITIAL_STATE, true },
                        { ":\n", ElixirTypes.COLON, INITIAL_STATE, false },
                        { ":\r\n", ElixirTypes.COLON, INITIAL_STATE, false },
                        { ":\t", ElixirTypes.COLON, INITIAL_STATE, false },
                        { "]", ElixirTypes.CLOSING_BRACKET, INITIAL_STATE, true },
                        { ";", ElixirTypes.SEMICOLON, INITIAL_STATE, true },
                        { "<", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "<-", ElixirTypes.IN_MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "<<", ElixirTypes.OPENING_BIT, INITIAL_STATE, true },
                        { "<<<", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "<<>>", ElixirTypes.OPENING_BIT, INITIAL_STATE, false },
                        { "<<~", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "<=", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "<>", ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "<|>", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "<~", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "<~>", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "=", ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "==", ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "===", ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "=>", ElixirTypes.ASSOCIATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "=~", ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { ">", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { ">=", ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { ">>", ElixirTypes.CLOSING_BIT, INITIAL_STATE, true },
                        { ">>>", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "?", ElixirTypes.CHAR_TOKENIZER, ElixirFlexLexer.CHAR_TOKENIZATION, true },
                        { "@", ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "Enum", ElixirTypes.ALIAS_TOKEN, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "[", ElixirTypes.OPENING_BRACKET, INITIAL_STATE, true },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP, true },
                        { "\"\"\"", ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START, true },
                        { "\\;", TokenType.BAD_CHARACTER, INITIAL_STATE, false },
                        { "\\\\", ElixirTypes.IN_MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "\\\n", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\\\r\n", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\f", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\n", ElixirTypes.EOL, INITIAL_STATE, true },
                        { "\r\n", ElixirTypes.EOL, INITIAL_STATE, true },
                        { "\t", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "^", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "_identifier", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "after", ElixirTypes.AFTER, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "afterwards", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "and", ElixirTypes.AND_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "androids", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true},
                        { "catch", ElixirTypes.CATCH, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "catchall", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "defmodule", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "do", ElixirTypes.DO, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "done", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "else", ElixirTypes.ELSE, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "elsewhere", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "end", ElixirTypes.END, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "ending", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "false", ElixirTypes.FALSE, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "falsehood", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "fn", ElixirTypes.FN, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "fnctn", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "identifier!", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "identifier", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "identifier9", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "identifier?", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "in", ElixirTypes.IN_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "inner", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "nil", ElixirTypes.NIL, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "nils", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "not", ElixirTypes.NOT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true},
                        { "notifiers", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true},
                        { "or", ElixirTypes.OR_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "order", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "rescue", ElixirTypes.RESCUE, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "rescuer", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "true", ElixirTypes.TRUE, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "truest", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "when", ElixirTypes.WHEN_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "whenever", ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE, true },
                        { "{}", ElixirTypes.OPENING_CURLY, ElixirFlexLexer.YYINITIAL, false },
                        { "|", ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "|>", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "||", ElixirTypes.OR_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "|||", ElixirTypes.OR_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL, true },
                        { "~>", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "~>>", ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true },
                        { "~~~", ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE, true }
                }
        );
    }

}
