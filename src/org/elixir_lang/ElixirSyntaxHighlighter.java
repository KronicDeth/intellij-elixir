package org.elixir_lang;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.xmlb.annotations.Text;
import com.thaiopensource.xml.dtd.om.Def;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Created by luke.imhoff on 8/2/14.
 */
public class ElixirSyntaxHighlighter extends SyntaxHighlighterBase {
    static final TextAttributesKey ALIAS = createTextAttributesKey(
            "ELIXIR_ALIAS",
            DefaultLanguageHighlighterColors.CLASS_NAME
    );

    static final TextAttributesKey ATOM = createTextAttributesKey(
            "ELIXIR_ATOM",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
    );

    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey(
            "ELIXIR_BAD_CHARACTER",
            HighlighterColors.BAD_CHARACTER
    );

    public static final TextAttributesKey CHAR_LIST = createTextAttributesKey(
            "ELIXIR_CHAR_LIST",
            DefaultLanguageHighlighterColors.STRING
    );

    public static final TextAttributesKey CHAR_TOKEN_TOKEN = createTextAttributesKey(
            "ELIXIR_CHAR_TOKEN",
            DefaultLanguageHighlighterColors.MARKUP_ENTITY
    );

    public static final TextAttributesKey COMMENT = createTextAttributesKey(
            "ELIXIR_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    public static final TextAttributesKey DECIMAL = createTextAttributesKey(
            "ELIXIR_DECIMAL",
            DefaultLanguageHighlighterColors.NUMBER
    );

    public static final TextAttributesKey DOCUMENTATION_MODULE_ATTRIBUTE = createTextAttributesKey(
            "ELIXIR_DOCUMENTATION_MODULE_ATTRIBUTE",
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG
    );

    public static final TextAttributesKey DOCUMENTATION_TEXT = createTextAttributesKey(
            "ELIXIR_DOCUMENTATION_TEXT",
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE
    );

    public static final TextAttributesKey EXPRESSION_SUBSTITUTION_MARK = createTextAttributesKey(
            "ELIXIR_EXPRESSION_SUBSTITUTION_MARK",
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    public static final TextAttributesKey IDENTIFIER = createTextAttributesKey(
            "ELIXIR_IDENTIFIER",
            DefaultLanguageHighlighterColors.IDENTIFIER
    );

    public static final TextAttributesKey INVALID_DIGIT = createTextAttributesKey(
            "ELIXIR_INVALID_DIGIT",
            HighlighterColors.BAD_CHARACTER
    );

    public static final TextAttributesKey KERNEL_FUNCTION = createTextAttributesKey(
            "ELIXIR_KERNEL_FUNCTION",
            DefaultLanguageHighlighterColors.FUNCTION_CALL
    );

    public static final TextAttributesKey KERNEL_MACRO = createTextAttributesKey(
            "ELIXIR_KERNEL_MACRO",
            DefaultLanguageHighlighterColors.FUNCTION_CALL
    );

    public static final TextAttributesKey KERNEL_SPECIAL_FORMS_MACRO = createTextAttributesKey(
            "ELIXIR_KERNEL_SPECIAL_FORMS_MACRO",
            KERNEL_MACRO
    );

    public static final TextAttributesKey KEYWORD = createTextAttributesKey(
            "ELIXIR_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
    );

    public static final TextAttributesKey MODULE_ATTRIBUTE = createTextAttributesKey(
            "ELIXIR_MODULE_ATTRIBUTE",
            // Color used for "ERL_ATTRIBUTE" in intellij-erlang
            CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES
    );

    public static final TextAttributesKey OBSOLETE_WHOLE_NUMBER_BASE = createTextAttributesKey(
            "ELIXIR_OBSOLETE_WHOLE_NUMBER_BASE",
            HighlighterColors.BAD_CHARACTER
    );

    public static final TextAttributesKey OPERATION_SIGN = createTextAttributesKey(
            "ELIXIR_OPERATION_SIGN",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    public static final TextAttributesKey SIGIL = createTextAttributesKey(
            "ELIXIR_SIGIL",
            // Based on color used for Regular expression's boundaries in Ruby
            DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR
    );

    public static final TextAttributesKey SPECIFICATION = createTextAttributesKey(
            "ELIXIR_SPECIFICATION",
            CodeInsightColors.METHOD_DECLARATION_ATTRIBUTES
    );

    public static final TextAttributesKey CALLBACK = createTextAttributesKey(
            "ELIXIR_CALLBACK",
            SPECIFICATION
    );

    public static final TextAttributesKey STRING = createTextAttributesKey(
            "ELIXIR_STRING",
            DefaultLanguageHighlighterColors.STRING
    );

    public static final TextAttributesKey TYPE = createTextAttributesKey(
            "ELIXIR_TYPE",
            // matches ERL_TYPE
            CodeInsightColors.ANNOTATION_ATTRIBUTE_NAME_ATTRIBUTES
    );

    public static final TextAttributesKey TYPE_PARAMETER = createTextAttributesKey(
            "ELIXIR_TYPE_PARAMETER",
            CodeInsightColors.TYPE_PARAMETER_NAME_ATTRIBUTES
    );

    public static final TextAttributesKey VALID_DIGIT = createTextAttributesKey(
            "ELIXIR_VALID_DIGIT",
            DefaultLanguageHighlighterColors.NUMBER
    );

    public static final TextAttributesKey VALID_ESCAPE_SEQUENCE = createTextAttributesKey(
            "ELIXIR_VALID_ESCAPE_SEQUENCE",
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    public static final TextAttributesKey WHOLE_NUMBER_BASE = createTextAttributesKey(
            "ELIXIR_WHOLE_NUMBER_BASE",
            DefaultLanguageHighlighterColors.NUMBER
    );

    private static final TextAttributesKey[] ALIAS_KEYS = new TextAttributesKey[]{ALIAS};
    private static final TextAttributesKey[] ATOM_KEYS = new TextAttributesKey[]{ATOM};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] CHAR_LIST_KEYS = new TextAttributesKey[]{CHAR_LIST};
    private static final TextAttributesKey[] CHAR_TOKEN_KEYS = new TextAttributesKey[]{CHAR_TOKEN_TOKEN};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] DECIMAL_KEYS = new TextAttributesKey[]{DECIMAL};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] EXPRESSION_SUBSTITUTION_MARK_KEYS = new TextAttributesKey[]{EXPRESSION_SUBSTITUTION_MARK};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] INVALID_DIGITS_KEYS = new TextAttributesKey[]{INVALID_DIGIT};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] OBSOLETE_WHOLE_NUMBER_BASE_KEYS = new TextAttributesKey[]{OBSOLETE_WHOLE_NUMBER_BASE};
    private static final TextAttributesKey[] OPERATION_SIGN_KEYS = new TextAttributesKey[]{OPERATION_SIGN};
    private static final TextAttributesKey[] SIGIL_KEYS = new TextAttributesKey[]{SIGIL};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] VALID_DIGITS_KEYS = new TextAttributesKey[]{VALID_DIGIT};
    private static final TextAttributesKey[] VALID_ESCAPE_SEQUENCE_KEYS = new TextAttributesKey[]{VALID_ESCAPE_SEQUENCE};
    private static final TextAttributesKey[] WHOLE_NUMBER_BASE_KEYS = new TextAttributesKey[]{WHOLE_NUMBER_BASE};

    private static final TokenSet ATOMS = TokenSet.create(
            ElixirTypes.COLON,
            ElixirTypes.ATOM_FRAGMENT
    );
    private static final TokenSet CHAR_LISTS = TokenSet.create(
            ElixirTypes.CHAR_LIST_FRAGMENT,
            ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER,
            ElixirTypes.CHAR_LIST_HEREDOC_TERMINATOR,
            ElixirTypes.CHAR_LIST_PROMOTER,
            ElixirTypes.CHAR_LIST_SIGIL_HEREDOC_PROMOTER,
            ElixirTypes.CHAR_LIST_SIGIL_HEREDOC_TERMINATOR,
            ElixirTypes.CHAR_LIST_SIGIL_PROMOTER,
            ElixirTypes.CHAR_LIST_SIGIL_TERMINATOR,
            ElixirTypes.CHAR_LIST_TERMINATOR,
            ElixirTypes.INTERPOLATING_CHAR_LIST_SIGIL_NAME,
            ElixirTypes.LITERAL_CHAR_LIST_SIGIL_NAME
    );
    private static final TokenSet DECIMAL_TOKEN_SET = TokenSet.create(
            ElixirTypes.DECIMAL_MARK,
            ElixirTypes.DECIMAL_SEPARATOR,
            ElixirTypes.EXPONENT_MARK
    );
    private static final TokenSet EXPRESSION_SUBSTITUTION_MARKS =  TokenSet.create(
            ElixirTypes.INTERPOLATION_START,
            ElixirTypes.INTERPOLATION_END
    );
    private static final TokenSet INVALID_DIGITS_TOKEN_SET = TokenSet.create(
            ElixirTypes.INVALID_BINARY_DIGITS,
            ElixirTypes.INVALID_DECIMAL_DIGITS,
            ElixirTypes.INVALID_HEXADECIMAL_DIGITS,
            ElixirTypes.INVALID_OCTAL_DIGITS,
            ElixirTypes.INVALID_UNKNOWN_BASE_DIGITS
    );
    private static final TokenSet KEYWORD_TOKEN_SET = TokenSet.create(
            ElixirTypes.AFTER,
            ElixirTypes.CATCH,
            ElixirTypes.DO,
            ElixirTypes.ELSE,
            ElixirTypes.END,
            ElixirTypes.FN,
            ElixirTypes.RESCUE
    );
    private static final TokenSet OBSOLETE_WHOLE_NUMBER_BASE_TOKEN_SET = TokenSet.create(
            ElixirTypes.OBSOLETE_BINARY_WHOLE_NUMBER_BASE,
            ElixirTypes.OBSOLETE_HEXADECIMAL_WHOLE_NUMBER_BASE
    );
    private static final TokenSet OPERATION_SIGNS = TokenSet.create(
            ElixirTypes.AND_OPERATOR,
            ElixirTypes.ARROW_OPERATOR,
            ElixirTypes.ASSOCIATION_OPERATOR,
            ElixirTypes.AT_OPERATOR,
            ElixirTypes.CAPTURE_OPERATOR,
            ElixirTypes.COMPARISON_OPERATOR,
            ElixirTypes.DUAL_OPERATOR,
            ElixirTypes.IN_OPERATOR,
            ElixirTypes.IN_MATCH_OPERATOR,
            ElixirTypes.MULTIPLICATION_OPERATOR,
            ElixirTypes.OR_OPERATOR,
            ElixirTypes.PIPE_OPERATOR,
            ElixirTypes.RELATIONAL_OPERATOR,
            ElixirTypes.STAB_OPERATOR,
            ElixirTypes.TWO_OPERATOR,
            ElixirTypes.TYPE_OPERATOR,
            ElixirTypes.UNARY_OPERATOR,
            ElixirTypes.WHEN_OPERATOR
    );
    // @todo Highlight each type of sigil separately and group char list and string with non-sigil versions
    private static final TokenSet SIGILS = TokenSet.create(
            ElixirTypes.INTERPOLATING_REGEX_SIGIL_NAME,
            ElixirTypes.INTERPOLATING_SIGIL_NAME,
            ElixirTypes.INTERPOLATING_WORDS_SIGIL_NAME,
            ElixirTypes.LITERAL_REGEX_SIGIL_NAME,
            ElixirTypes.LITERAL_SIGIL_NAME,
            ElixirTypes.LITERAL_WORDS_SIGIL_NAME,
            ElixirTypes.REGEX_FRAGMENT,
            ElixirTypes.REGEX_HEREDOC_PROMOTER,
            ElixirTypes.REGEX_HEREDOC_TERMINATOR,
            ElixirTypes.REGEX_PROMOTER,
            ElixirTypes.REGEX_TERMINATOR,
            ElixirTypes.SIGIL_FRAGMENT,
            ElixirTypes.SIGIL_HEREDOC_PROMOTER,
            ElixirTypes.SIGIL_HEREDOC_TERMINATOR,
            ElixirTypes.SIGIL_MODIFIER,
            ElixirTypes.SIGIL_PROMOTER,
            ElixirTypes.SIGIL_TERMINATOR,
            ElixirTypes.TILDE,
            ElixirTypes.WORDS_FRAGMENT,
            ElixirTypes.WORDS_HEREDOC_PROMOTER,
            ElixirTypes.WORDS_HEREDOC_TERMINATOR,
            ElixirTypes.WORDS_PROMOTER,
            ElixirTypes.WORDS_TERMINATOR
    );
    private static final TokenSet STRINGS = TokenSet.create(
            ElixirTypes.INTERPOLATING_STRING_SIGIL_NAME,
            ElixirTypes.LITERAL_STRING_SIGIL_NAME,
            ElixirTypes.STRING_FRAGMENT,
            ElixirTypes.STRING_HEREDOC_PROMOTER,
            ElixirTypes.STRING_HEREDOC_TERMINATOR,
            ElixirTypes.STRING_PROMOTER,
            ElixirTypes.STRING_SIGIL_HEREDOC_PROMOTER,
            ElixirTypes.STRING_SIGIL_HEREDOC_TERMINATOR,
            ElixirTypes.STRING_SIGIL_PROMOTER,
            ElixirTypes.STRING_SIGIL_TERMINATOR,
            ElixirTypes.STRING_TERMINATOR
    );
    private static final TokenSet VALID_DIGITS_TOKEN_SET = TokenSet.create(
            ElixirTypes.VALID_BINARY_DIGITS,
            ElixirTypes.VALID_DECIMAL_DIGITS,
            ElixirTypes.VALID_DECIMAL_DIGITS,
            ElixirTypes.VALID_HEXADECIMAL_DIGITS,
            ElixirTypes.VALID_OCTAL_DIGITS
    );
    private static final TokenSet WHOLE_NUMBER_BASE_TOKEN_SET = TokenSet.create(
            ElixirTypes.BASE_WHOLE_NUMBER_PREFIX,
            ElixirTypes.BINARY_WHOLE_NUMBER_BASE,
            ElixirTypes.HEXADECIMAL_WHOLE_NUMBER_BASE,
            ElixirTypes.OCTAL_WHOLE_NUMBER_BASE
    );

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ElixirLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(ElixirTypes.ALIAS)) {
            return ALIAS_KEYS;
        } else if (ATOMS.contains(tokenType)) {
            return ATOM_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (CHAR_LISTS.contains(tokenType)) {
            return CHAR_LIST_KEYS;
        } else if (tokenType == ElixirTypes.CHAR_TOKENIZER) {
            return CHAR_TOKEN_KEYS;
        } else if (tokenType.equals(ElixirTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (DECIMAL_TOKEN_SET.contains(tokenType)) {
            return DECIMAL_KEYS;
        } else if (EXPRESSION_SUBSTITUTION_MARKS.contains(tokenType)) {
            return EXPRESSION_SUBSTITUTION_MARK_KEYS;
        } else if (tokenType.equals(ElixirTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else if (INVALID_DIGITS_TOKEN_SET.contains(tokenType)) {
            return INVALID_DIGITS_KEYS;
        } else if (KEYWORD_TOKEN_SET.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (OBSOLETE_WHOLE_NUMBER_BASE_TOKEN_SET.contains(tokenType)) {
            return OBSOLETE_WHOLE_NUMBER_BASE_KEYS;
        } else if (OPERATION_SIGNS.contains(tokenType)) {
            return OPERATION_SIGN_KEYS;
        } else if (SIGILS.contains(tokenType)) {
            return SIGIL_KEYS;
        } else if (STRINGS.contains(tokenType)) {
            return STRING_KEYS;
        } else if (VALID_DIGITS_TOKEN_SET.contains(tokenType)) {
            return VALID_DIGITS_KEYS;
        } else if (WHOLE_NUMBER_BASE_TOKEN_SET.contains(tokenType)) {
            return WHOLE_NUMBER_BASE_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
