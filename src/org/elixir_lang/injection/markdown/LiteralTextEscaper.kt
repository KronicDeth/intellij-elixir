package org.elixir_lang.injection.markdown

import com.intellij.openapi.util.TextRange
import org.elixir_lang.psi.ElixirLine
import org.elixir_lang.psi.Heredoc
import org.elixir_lang.psi.Parent

class LiteralTextEscaper(parent: Parent) : com.intellij.psi.LiteralTextEscaper<Parent>(parent) {
    override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
        outChars.append(rangeInsideHost.substring(myHost.text))

        return true
    }

    override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int =
        rangeInsideHost.startOffset + offsetInDecoded;

    override fun getRelevantTextRange(): TextRange =
        when (myHost) {
            is Heredoc -> {
                val heredocLineList = myHost.heredocLineList

                val relevantStartOffset = heredocLineList.first().textRange.startOffset
                val relevantEndOffset = heredocLineList.last().textRange.endOffset

                val hostTextRange = myHost.textRange
                val startOffset = relevantStartOffset - hostTextRange.startOffset
                val length = relevantEndOffset - relevantStartOffset

                TextRange.from(startOffset, length)
            }
            is ElixirLine -> myHost.lineBody?.textRangeInParent ?: TextRange.from(1, 0)
            else -> super.getRelevantTextRange()
        }

    override fun isOneLine(): Boolean = false
}
