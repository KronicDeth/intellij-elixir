package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.TokenTypeState;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by luke.imhoff on 9/1/14.
 */
@RunWith(Parameterized.class)
public class InterpolationTest extends Test {
    private final CharSequence charSequence;
    private final List<TokenTypeState> tokenTypeStates;

    /*
     * Constructors
     */

    public InterpolationTest(CharSequence charSequence, List<TokenTypeState> tokenTypeStates) {
        this.charSequence = charSequence;
        this.tokenTypeStates = tokenTypeStates;
    }

    /*
     * Methods
     */

    @Parameterized.Parameters(
            name = "\"{0}\" parses"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        {
                                " ",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "!",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "!=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "!==",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "#",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMMENT, ElixirFlexLexer.INTERPOLATION)
                                )
                        },
                        {
                                "#\n",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMMENT, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "%",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "%{}",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STRUCT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.OPENING_CURLY, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.CLOSING_CURLY, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "&",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CAPTURE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "&&",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "&&&",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "'",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.CHAR_LIST_FRAGMENT, ElixirFlexLexer.GROUP)
                                )
                        },
                        {
                                "'''",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START),
                                        new TokenTypeState(TokenType.BAD_CHARACTER, ElixirFlexLexer.GROUP_HEREDOC_START),
                                        new TokenTypeState(TokenType.BAD_CHARACTER, ElixirFlexLexer.GROUP_HEREDOC_START)
                                )
                        },
                        {
                                "(",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OPENING_PARENTHESIS, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ")",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CLOSING_PARENTHESIS, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "+",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.NUMBER_OR_BADARITH_OPERATOR, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "++",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ",",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMMA, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "-",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.NEGATE_OPERATOR, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "--",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "->",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STAB_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ".",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.DOT_OPERATOR, ElixirFlexLexer.DOT_OPERATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "..",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RANGE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "...",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "001234567",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "0B10",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE),
                                        new TokenTypeState(ElixirTypes.OBSOLETE_BINARY_WHOLE_NUMBER_BASE, ElixirFlexLexer.BINARY_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "0X0123456789abcdefABCDEF",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE),
                                        new TokenTypeState(ElixirTypes.OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE, ElixirFlexLexer.HEXADECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "0b10",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE),
                                        new TokenTypeState(ElixirTypes.BINARY_WHOLE_NUMBER_BASE, ElixirFlexLexer.BINARY_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_BINARY_DIGITS, ElixirFlexLexer.BINARY_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "0o01234567",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE),
                                        new TokenTypeState(ElixirTypes.OCTAL_WHOLE_NUMBER_BASE, ElixirFlexLexer.OCTAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_OCTAL_DIGITS, ElixirFlexLexer.OCTAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "0x0123456789abcdefABCDEF",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.BASE_WHOLE_NUMBER_PREFIX, ElixirFlexLexer.BASE_WHOLE_NUMBER_BASE),
                                        new TokenTypeState(ElixirTypes.HEXADECIMAL_WHOLE_NUMBER_BASE, ElixirFlexLexer.HEXADECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_HEXADECIMAL_DIGITS, ElixirFlexLexer.HEXADECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1.",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.DOT_OPERATOR, ElixirFlexLexer.DOT_OPERATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1.0",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.DECIMAL_MARK, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1.0e+1",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.DECIMAL_MARK, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.EXPONENT_MARK, ElixirFlexLexer.DECIMAL_EXPONENT_SIGN),
                                        new TokenTypeState(ElixirTypes.SIGN_OPERATOR, ElixirFlexLexer.DECIMAL_EXPONENT),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_EXPONENT),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1.0e-1",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.DECIMAL_MARK, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.EXPONENT_MARK, ElixirFlexLexer.DECIMAL_EXPONENT_SIGN),
                                        new TokenTypeState(ElixirTypes.SIGN_OPERATOR, ElixirFlexLexer.DECIMAL_EXPONENT),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_EXPONENT),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1.0e1",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.DECIMAL_MARK, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_FRACTION),
                                        new TokenTypeState(ElixirTypes.EXPONENT_MARK, ElixirFlexLexer.DECIMAL_EXPONENT_SIGN),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_EXPONENT),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1234567890",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1_",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "1_2_3_4_5_6_7_8_9_0",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.NUMBER_SEPARATOR, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ": ",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COLON, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ":",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COLON, ElixirFlexLexer.ATOM_START),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "::",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TYPE_OPERATOR, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ":\n",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COLON, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ":\r\n",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COLON, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ":\t",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COLON, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "]",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CLOSING_BRACKET, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ";",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.SEMICOLON, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<-",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IN_MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<<",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OPENING_BIT, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<<<",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<<>>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OPENING_BIT, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.CLOSING_BIT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<<~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TWO_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<|>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "<~>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "==",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "===",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "=>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ASSOCIATION_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "=~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.COMPARISON_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ">",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ">=",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RELATIONAL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ">>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CLOSING_BIT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                ">>>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "?",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CHAR_TOKENIZER, ElixirFlexLexer.CHAR_TOKENIZATION),
                                        new TokenTypeState(ElixirTypes.CHAR_LIST_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP)
                                )
                        },
                        {
                                "@",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "Enum",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ALIAS_TOKEN, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "[",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OPENING_BRACKET, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\"",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_FRAGMENT, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\"\"\"",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STRING_HEREDOC_PROMOTER, ElixirFlexLexer.GROUP_HEREDOC_START),
                                        new TokenTypeState(TokenType.BAD_CHARACTER, ElixirFlexLexer.GROUP_HEREDOC_START),
                                        new TokenTypeState(TokenType.BAD_CHARACTER, ElixirFlexLexer.GROUP_HEREDOC_START)
                                )
                        },
                        {
                                "\\;",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.BAD_CHARACTER, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.SEMICOLON, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\\\\",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IN_MATCH_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\\\n",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\\\r\n",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\f",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\n",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\r\n",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "\t",
                                Arrays.asList(
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.INTERPOLATION),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "^",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "_identifier",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "after",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AFTER, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "afterwards",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "and",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.AND_WORD_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "androids",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "catch",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.CATCH, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "catchall",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "defmodule",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "do",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.DO, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "done",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "else",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ELSE, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "elsewhere",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "end",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.END, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "ending",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "false",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.FALSE, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "falsehood",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "fn",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.FN, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "fnctn",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "identifier!",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "identifier",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "identifier9",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "identifier?",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "in",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IN_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "inner",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "nil",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.NIL, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "nils",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "not",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.NOT_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "notifiers",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "or",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OR_WORD_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "order",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "rescue",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.RESCUE, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "rescuer",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "true",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TRUE, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "truest",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "when",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.WHEN_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "whenever",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "{}",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OPENING_CURLY, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.CLOSING_CURLY, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "|",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.PIPE_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "|>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "||",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OR_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "|||",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.OR_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.TILDE, ElixirFlexLexer.SIGIL),
                                        new TokenTypeState(TokenType.BAD_CHARACTER, ElixirFlexLexer.SIGIL),
                                        new TokenTypeState(TokenType.BAD_CHARACTER, ElixirFlexLexer.SIGIL)
                                )
                        },
                        {
                                "~>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "~>>",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.ARROW_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        },
                        {
                                "~~~",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.UNARY_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_MAYBE),
                                        new TokenTypeState(ElixirTypes.INTERPOLATION_END, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE)
                                )
                        }
                }
        );
    }

    @org.junit.Test
    public void identifierCall() {
        start(charSequence);

        assertEquals(ElixirTypes.STRING_PROMOTER, lexer.getTokenType());
        lexer.advance();
        assertEquals(ElixirFlexLexer.GROUP, lexer.getState());

        assertEquals(ElixirTypes.INTERPOLATION_START, lexer.getTokenType());
        lexer.advance();
        assertEquals(ElixirFlexLexer.INTERPOLATION, lexer.getState());

        for (TokenTypeState tokenTypeState : tokenTypeStates) {
            assertEquals(tokenTypeState.tokenType, lexer.getTokenType());

            lexer.advance();

            assertEquals(tokenTypeState.state, lexer.getState());
        }

        assertNull("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType());
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        CharSequence fullCharSequence = "\"#{" + charSequence + "}\"";
        super.start(fullCharSequence);
    }
}
