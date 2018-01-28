package org.elixir_lang.beam.assembly

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.beam.assembly.psi.Types.*


class SyntaxHighlighter: SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = LookAheadLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
            when (tokenType) {
                ATOM, KEY -> ElixirSyntaxHighlighter.ATOM_KEYS
                ATOM_KEYWORD -> ElixirSyntaxHighlighter.ATOM_KEYWORD_KEYS
                CHARLIST -> ElixirSyntaxHighlighter.CHAR_LIST_KEYS
                CLOSING_BRACKET, OPENING_BRACKET -> ElixirSyntaxHighlighter.BRACKETS_KEYS
                CLOSING_CURLY, OPENING_CURLY -> ElixirSyntaxHighlighter.BRACES_KEYS
                CLOSING_PARENTHESIS, OPENING_PARENTHESIS -> ElixirSyntaxHighlighter.PARENTHESES_KEYS
                DOT_OPERATOR, MAP_OPERATOR, NAME_ARITY_SEPARATOR, REFERENCE_OPERATOR, SYMBOLIC_OPERATOR ->
                    ElixirSyntaxHighlighter.OPERATION_SIGN_KEYS
                INTEGER -> ElixirSyntaxHighlighter.DECIMAL_KEYS
                NAME -> ElixirSyntaxHighlighter.KEYWORD_KEYS
                STRING -> ElixirSyntaxHighlighter.STRING_KEYS
                QUALIFIED_ALIAS -> ElixirSyntaxHighlighter.ALIAS_KEYS
                else -> ElixirSyntaxHighlighter.EMPTY_KEYS
            }
}
