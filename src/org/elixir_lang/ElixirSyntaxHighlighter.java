package org.elixir_lang;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Reader;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Created by luke.imhoff on 8/2/14.
 */
public class ElixirSyntaxHighlighter extends SyntaxHighlighterBase {
    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey(
            "ELIXIR_BAD_CHARACTER",
            HighlighterColors.BAD_CHARACTER
    );

    public static final TextAttributesKey CHAR_LIST = createTextAttributesKey(
            "ELIXIR_CHAR_LIST",
            DefaultLanguageHighlighterColors.STRING
    );

    public static final TextAttributesKey COMMENT = createTextAttributesKey(
            "ELIXIR_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    public static final TextAttributesKey EXPRESSION_SUBSTITUTION_MARK = createTextAttributesKey(
            "ELIXIR_EXPRESSION_SUBSTITUTION_MARK",
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    public static final TextAttributesKey NUMBER = createTextAttributesKey(
            "ELIXIR_NUMBER",
            DefaultLanguageHighlighterColors.NUMBER
    );

    public static final TextAttributesKey STRING = createTextAttributesKey(
            "ELIXIR_STRING",
            DefaultLanguageHighlighterColors.STRING
    );

    public static final TextAttributesKey VALID_ESCAPE_SEQUENCE = createTextAttributesKey(
            "ELIXIR_VALID_ESCAPE_SEQUENCE",
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] CHAR_LIST_KEYS = new TextAttributesKey[]{CHAR_LIST};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] EXPRESSION_SUBSTITUTION_MARK_KEYS = new TextAttributesKey[]{EXPRESSION_SUBSTITUTION_MARK};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] VALID_ESCAPE_SEQUENCE_KEYS = new TextAttributesKey[]{VALID_ESCAPE_SEQUENCE};


    private static final TokenSet CHAR_LISTS = TokenSet.create(
            ElixirTypes.SINGLE_QUOTE,
            ElixirTypes.CHAR_LIST_FRAGMENT,
            ElixirTypes.TRIPLE_SINGLE_QUOTE
    );
    private static final TokenSet EXPRESSION_SUBSTITUTION_MARKS =  TokenSet.create(
            ElixirTypes.INTERPOLATION_START,
            ElixirTypes.INTERPOLATION_END
    );
    private static final TokenSet STRINGS = TokenSet.create(
            ElixirTypes.DOUBLE_QUOTES,
            ElixirTypes.STRING_FRAGMENT,
            ElixirTypes.TRIPLE_DOUBLE_QUOTES
    );

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ElixirLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (CHAR_LISTS.contains(tokenType)) {
            return CHAR_LIST_KEYS;
        } else if (tokenType.equals(ElixirTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (EXPRESSION_SUBSTITUTION_MARKS.contains(tokenType)) {
            return EXPRESSION_SUBSTITUTION_MARK_KEYS;
        } else if (tokenType.equals(ElixirTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (STRINGS.contains(tokenType)) {
            return STRING_KEYS;
        } else if (tokenType.equals(ElixirTypes.VALID_ESCAPE_SEQUENCE)) {
            return VALID_ESCAPE_SEQUENCE_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
