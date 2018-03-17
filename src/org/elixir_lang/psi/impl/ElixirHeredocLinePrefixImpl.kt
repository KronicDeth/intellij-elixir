package org.elixir_lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.Factory
import com.intellij.psi.tree.IElementType
import org.elixir_lang.psi.ElixirHeredocLinePrefix
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * Returns a virtual PsiElement representing the spaces at the end of charListHeredocLineWhitespace that are not
 * consumed by prefixLength.
 *
 * @return null if prefixLength is greater than or equal to text length of charListHeredocLineWhitespace.
 */
@Contract(pure = true)
fun ElixirHeredocLinePrefix.excessWhitespace(fragmentType: IElementType, prefixLength: Int): ASTNode? {
    val availableLength = textLength
    val excessLength = availableLength - prefixLength
    var excessWhitespaceASTNode: ASTNode? =
            null

    if (excessLength > 0) {
        val excessWhitespaceChars = CharArray(excessLength)
        Arrays.fill(excessWhitespaceChars, ' ')
        val excessWhitespaceString = String(excessWhitespaceChars)
        excessWhitespaceASTNode = Factory.createSingleLeafElement(
                fragmentType,
                excessWhitespaceString,
                0,
                excessLength, null,
                manager
        )
    }

    return excessWhitespaceASTNode
}
