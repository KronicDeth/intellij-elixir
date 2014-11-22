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
public class YYInitialTest extends TokenTest {
    /*
     * Constants
     */

    private static final int INITIAL_STATE = ElixirFlexLexer.YYINITIAL;

    /*
     * Constructors
     */

    public YYInitialTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
        super(charSequence, tokenType, lexicalState, consumeAll);
    }

    /*
     * Methods
     */

    protected int initialState() {
        return INITIAL_STATE;
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        { " ", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL, true },
                        { "!", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true},
                        { "!=", ElixirTypes.COMPARISON_OPERATOR, INITIAL_STATE, true },
                        { "!==", ElixirTypes.COMPARISON_OPERATOR, INITIAL_STATE, true },
                        { "", null, INITIAL_STATE, true },
                        { "#", ElixirTypes.COMMENT, ElixirFlexLexer.YYINITIAL, true },
                        { "%", ElixirTypes.STRUCT_OPERATOR, INITIAL_STATE, true },
                        { "%{}", ElixirTypes.MAP_OPERATOR, INITIAL_STATE, true },
                        { "&", ElixirTypes.CAPTURE_OPERATOR, INITIAL_STATE, true },
                        { "&&", ElixirTypes.AND_OPERATOR, INITIAL_STATE, true},
                        { "&&&", ElixirTypes.AND_OPERATOR, INITIAL_STATE, true},
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP, true },
                        { "'''", ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START, true },
                        { "(", ElixirTypes.OPENING_PARENTHESIS, INITIAL_STATE, true },
                        { ")", ElixirTypes.CLOSING_PARENTHESIS, INITIAL_STATE, true },
                        { "+", ElixirTypes.DUAL_OPERATOR, INITIAL_STATE, true},
                        { "++", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "-", ElixirTypes.DUAL_OPERATOR, INITIAL_STATE, true},
                        { "--", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "->", ElixirTypes.STAB_OPERATOR, INITIAL_STATE, true },
                        { ".", ElixirTypes.DOT_OPERATOR, INITIAL_STATE, true },
                        { "..", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "...", ElixirTypes.IDENTIFIER, INITIAL_STATE, true },
                        { "001234567", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL, true },
                        { "0B10", ElixirTypes.NUMBER, INITIAL_STATE, false },
                        { "0X0123456789abcdefABCDEF", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL, false },
                        { "0b10", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL, true },
                        { "0o01234567", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL, true },
                        { "0x0123456789abcdefABCDEF", ElixirTypes.NUMBER, ElixirFlexLexer.YYINITIAL, true },
                        { "1.", ElixirTypes.NUMBER, INITIAL_STATE, false },
                        { "1.0", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "1.0e+1", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "1.0e-1", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "1.0e1", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "1234567890", ElixirTypes.NUMBER, INITIAL_STATE, true },
                        { "1_", ElixirTypes.NUMBER, INITIAL_STATE, false},
                        { "1_2_3_4_5_6_7_8_9_0", ElixirTypes.NUMBER, INITIAL_STATE, true},
                        { ": ", ElixirTypes.COLON, INITIAL_STATE, false },
                        { ":", ElixirTypes.COLON, ElixirFlexLexer.ATOM_START, true },
                        { "::", ElixirTypes.TYPE_OPERATOR, INITIAL_STATE, true },
                        { ":\n", ElixirTypes.COLON, INITIAL_STATE, false },
                        { ":\r", ElixirTypes.COLON, INITIAL_STATE, false },
                        { ":\t", ElixirTypes.COLON, INITIAL_STATE, false },
                        { ";", ElixirTypes.SEMICOLON, ElixirFlexLexer.YYINITIAL, true },
                        { "<", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { "<-", ElixirTypes.IN_MATCH_OPERATOR, INITIAL_STATE, true },
                        { "<<<", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<<>>", ElixirTypes.BIT_STRING_OPERATOR, INITIAL_STATE, true },
                        { "<<~", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<=", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { "<>", ElixirTypes.TWO_OPERATOR, INITIAL_STATE, true},
                        { "<|>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<~", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "<~>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "=", ElixirTypes.MATCH_OPERATOR, INITIAL_STATE, true },
                        { "==", ElixirTypes.COMPARISON_OPERATOR, INITIAL_STATE, true },
                        { "===", ElixirTypes.COMPARISON_OPERATOR, INITIAL_STATE, true },
                        { "=>", ElixirTypes.ASSOCIATION_OPERATOR, INITIAL_STATE, true },
                        { "=~", ElixirTypes.COMPARISON_OPERATOR, INITIAL_STATE, true },
                        { ">", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { ">=", ElixirTypes.RELATIONAL_OPERATOR, INITIAL_STATE, true },
                        { ">>>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "@", ElixirTypes.AT_OPERATOR, INITIAL_STATE, true },
                        { "Enum", ElixirTypes.ALIAS, INITIAL_STATE, true },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP, true },
                        { "\"\"\"",ElixirTypes.STRING_HEREDOC_PROMOTER,ElixirFlexLexer.GROUP_HEREDOC_START, true },
                        { "\\;", TokenType.BAD_CHARACTER, INITIAL_STATE, false },
                        { "\\\\", ElixirTypes.IN_MATCH_OPERATOR, INITIAL_STATE, true },
                        { "\\\n", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\\\r\n", TokenType.WHITE_SPACE, INITIAL_STATE, true },
                        { "\f", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL, true },
                        { "\n", ElixirTypes.EOL, ElixirFlexLexer.YYINITIAL, true },
                        { "\r\n", ElixirTypes.EOL, INITIAL_STATE, true },
                        { "\t", TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL, true },
                        { "^", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true},
                        { "_identifier", ElixirTypes.IDENTIFIER, INITIAL_STATE, true },
                        { "and", ElixirTypes.AND_OPERATOR, INITIAL_STATE, true},
                        { "defmodule", ElixirTypes.IDENTIFIER, INITIAL_STATE, true },
                        { "identifier!", ElixirTypes.IDENTIFIER, INITIAL_STATE, true },
                        { "identifier", ElixirTypes.IDENTIFIER, INITIAL_STATE, true },
                        { "identifier9", ElixirTypes.IDENTIFIER, INITIAL_STATE, true },
                        { "identifier?", ElixirTypes.IDENTIFIER, INITIAL_STATE, true },
                        { "in", ElixirTypes.IN_OPERATOR, INITIAL_STATE, true },
                        { "not", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true},
                        { "or", ElixirTypes.OR_OPERATOR, INITIAL_STATE, true },
                        { "when", ElixirTypes.WHEN_OPERATOR, INITIAL_STATE, true },
                        { "{}", ElixirTypes.TUPLE_OPERATOR, INITIAL_STATE, true },
                        { "|", ElixirTypes.PIPE_OPERATOR, INITIAL_STATE, true },
                        { "|>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "||", ElixirTypes.OR_OPERATOR, INITIAL_STATE, true },
                        { "|||", ElixirTypes.OR_OPERATOR, INITIAL_STATE, true },
                        { "~", ElixirTypes.TILDE, ElixirFlexLexer.SIGIL, true },
                        { "~>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "~>>", ElixirTypes.ARROW_OPERATOR, INITIAL_STATE, true },
                        { "~~~", ElixirTypes.UNARY_OPERATOR, INITIAL_STATE, true },
                }
        );
    }
}
