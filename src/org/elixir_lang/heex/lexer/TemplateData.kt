package org.elixir_lang.heex.lexer

import com.intellij.lexer.Lexer
import com.intellij.lexer.MergingLexerAdapterBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.OuterLanguageElementType
import org.elixir_lang.heex.Language
import org.elixir_lang.heex.lexer.LookAhead
import org.elixir_lang.heex.psi.Types

/**
 * Merges together all HEEx opening, body, and closing tokens into a single HEEx type
 */
class TemplateData : MergingLexerAdapterBase(LookAhead()) {
    private val mergeFunction: MergeFunction = MergeFunction()

    override fun getMergeFunction(): com.intellij.lexer.MergeFunction = mergeFunction

    private inner class MergeFunction : com.intellij.lexer.MergeFunction {
        override fun merge(type: IElementType, originalLexer: Lexer): IElementType = if (type !== Types.DATA) {
            while (true) {
                val originalTokenType = originalLexer.tokenType

                if (originalTokenType != null && originalTokenType !== Types.DATA) {
                    originalLexer.advance()
                } else {
                    break
                }
            }

            HEEX
        } else {
            type
        }
    }

    companion object {
        @JvmField
        val HEEX: IElementType = OuterLanguageElementType("HEEx", Language.INSTANCE)
    }
}