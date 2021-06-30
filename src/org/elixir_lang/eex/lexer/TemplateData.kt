package org.elixir_lang.eex.lexer

import com.intellij.lexer.Lexer
import com.intellij.lexer.MergingLexerAdapterBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.OuterLanguageElementType
import org.elixir_lang.eex.Language
import org.elixir_lang.eex.psi.Types

/**
 * Merges together all EEx opening, body, and closing tokens into a single EEx type
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

            EEX
        } else {
            type
        }
    }

    companion object {
        @JvmField
        val EEX: IElementType = OuterLanguageElementType("EEx", Language.INSTANCE)
    }
}
