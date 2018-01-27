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
                ATOM -> ElixirSyntaxHighlighter.ATOM_KEYS
                CLOSING_PARENTHESIS, OPENING_PARENTHESIS -> ElixirSyntaxHighlighter.PARENTHESES_KEYS
                INTEGER -> ElixirSyntaxHighlighter.DECIMAL_KEYS
                NAME -> ElixirSyntaxHighlighter.KEYWORD_KEYS
                QUALIFIED_ALIAS -> ElixirSyntaxHighlighter.ALIAS_KEYS
                else -> ElixirSyntaxHighlighter.EMPTY_KEYS
            }
}
