package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.TokenTypeState;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 12/7/14.
 */
@RunWith(Parameterized.class)
public class ParenthesesCallTest extends Test {
    /*
     * Fields
     */

    private CharSequence identifierCharSequence;
    private List<TokenTypeState> tokenTypeStates;

    /*
     * Constructors
     */

    public ParenthesesCallTest(CharSequence identifierCharSequence, List<TokenTypeState> tokenTypeStates) {
        this.identifierCharSequence = identifierCharSequence;
        this.tokenTypeStates = tokenTypeStates;
    }

    /*
     * Static Methods
     */

    @Parameterized.Parameters(
            name = "{index} \"{0}\" parses"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        {
                                "&&",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "&&&",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<<<",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<<~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<|>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<~>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ">>>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "~>>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "|>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "~>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "=>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ASSOCIATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "identifier",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL)
                                )
                        },
                        {
                                "@",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<<>>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OPENING_BIT, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.CLOSING_BIT, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "&",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CAPTURE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "after",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AFTER, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "afterwards",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "and",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AND_WORD_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "androids",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "catch",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CATCH, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "catchall",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "do",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.DO, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "done",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "else",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ELSE, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "elsewhere",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "end",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.END, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "ending",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "in",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IN_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "inner",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "not",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.NOT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "notifiers",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "or",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OR_WORD_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "order",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "rescue",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RESCUE, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "rescuer",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.CALL_OR_KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.CALL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "!==",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "===",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "!=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "+",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.DUAL_OPERATION),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "-",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.DUAL_OPERATOR, ElixirFlexLexer.DUAL_OPERATION),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "^^^",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.THREE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "||",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OR_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "|||",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OR_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "~~~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "!",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "^",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<-",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IN_MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\\\\",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IN_MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "%{}",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_CURLY, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.CLOSING_CURLY, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "*",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.MULTIPLICATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "/",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.DIVISION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "|",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ">=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ">",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "->",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STAB_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "%",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "{}",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OPENING_CURLY, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.CLOSING_CURLY, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "++",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "--",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "..",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RANGE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE)
                                )
                        }
                }
        );
    }

    /*
     * Instance Methods
     */

    @org.junit.Test
    public void identifierCall() {
        start(identifierCharSequence);

        for (TokenTypeState tokenTypeState: tokenTypeStates) {
            assertEquals(tokenTypeState.tokenType, lexer.getTokenType());

            lexer.advance();

            assertEquals(tokenTypeState.state, lexer.getState());
        }
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // append "(" to trigger CALL_OR_KEYWORD_PAIR_MAYBE
        CharSequence fullCharSequence = charSequence + "(";
        super.start(fullCharSequence);
    }
}
