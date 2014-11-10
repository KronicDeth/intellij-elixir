package org.elixir_lang;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Created by luke.imhoff on 8/2/14.
 */
public class ElixirSyntaxHighlighter extends SyntaxHighlighterBase {
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

    public static final TextAttributesKey CHAR_TOKEN = createTextAttributesKey(
            "ELIXIR_CHAR_TOKEN",
            DefaultLanguageHighlighterColors.MARKUP_ENTITY
    );

    public static final TextAttributesKey COMMENT = createTextAttributesKey(
            "ELIXIR_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    public static final TextAttributesKey EXPRESSION_SUBSTITUTION_MARK = createTextAttributesKey(
            "ELIXIR_EXPRESSION_SUBSTITUTION_MARK",
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    public static final TextAttributesKey IDENTIFIER = createTextAttributesKey(
            "ELIXIR_IDENTIFIER",
            DefaultLanguageHighlighterColors.IDENTIFIER
    );

    public static final TextAttributesKey NUMBER = createTextAttributesKey(
            "ELIXIR_NUMBER",
            DefaultLanguageHighlighterColors.NUMBER
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

    public static final TextAttributesKey STRING = createTextAttributesKey(
            "ELIXIR_STRING",
            DefaultLanguageHighlighterColors.STRING
    );

    public static final TextAttributesKey VALID_ESCAPE_SEQUENCE = createTextAttributesKey(
            "ELIXIR_VALID_ESCAPE_SEQUENCE",
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    private static final TextAttributesKey[] ATOM_KEYS = new TextAttributesKey[]{ATOM};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] CHAR_LIST_KEYS = new TextAttributesKey[]{CHAR_LIST};
    private static final TextAttributesKey[] CHAR_TOKEN_KEYS = new TextAttributesKey[]{CHAR_TOKEN};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] EXPRESSION_SUBSTITUTION_MARK_KEYS = new TextAttributesKey[]{EXPRESSION_SUBSTITUTION_MARK};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] OPERATION_SIGN_KEYS = new TextAttributesKey[]{OPERATION_SIGN};
    private static final TextAttributesKey[] SIGIL_KEYS = new TextAttributesKey[]{SIGIL};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] VALID_ESCAPE_SEQUENCE_KEYS = new TextAttributesKey[]{VALID_ESCAPE_SEQUENCE};

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
    private static final TokenSet EXPRESSION_SUBSTITUTION_MARKS =  TokenSet.create(
            ElixirTypes.INTERPOLATION_START,
            ElixirTypes.INTERPOLATION_END
    );
    private static final TokenSet OPERATION_SIGNS = TokenSet.create(
            ElixirTypes.AND_OPERATOR,
            ElixirTypes.ARROW_OPERATOR,
            ElixirTypes.ASSOCIATION_OPERATOR,
            ElixirTypes.AT_OPERATOR,
            ElixirTypes.CAPTURE_OPERATOR,
            ElixirTypes.COMPARISON_OPERATOR,
            ElixirTypes.DUAL_OPERATOR,
            ElixirTypes.HAT_OPERATOR,
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

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ElixirLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (ATOMS.contains(tokenType)) {
            return ATOM_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (CHAR_LISTS.contains(tokenType)) {
            return CHAR_LIST_KEYS;
        } else if (tokenType == ElixirTypes.CHAR_TOKEN) {
            return CHAR_TOKEN_KEYS;
        } else if (tokenType.equals(ElixirTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (EXPRESSION_SUBSTITUTION_MARKS.contains(tokenType)) {
            return EXPRESSION_SUBSTITUTION_MARK_KEYS;
        } else if (tokenType.equals(ElixirTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else if (tokenType.equals(ElixirTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (OPERATION_SIGNS.contains(tokenType)) {
            return OPERATION_SIGN_KEYS;
        } else if (SIGILS.contains(tokenType)) {
            return SIGIL_KEYS;
        } else if (STRINGS.contains(tokenType)) {
            return STRING_KEYS;
        } else if (tokenType.equals(ElixirTypes.VALID_ESCAPE_SEQUENCE)) {
            return VALID_ESCAPE_SEQUENCE_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
