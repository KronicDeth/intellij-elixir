package org.elixir_lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.Factory
import com.intellij.psi.tree.IElementType
import org.elixir_lang.psi.ElixirHeredocLinePrefix
import org.jetbrains.annotations.Contract

/**
 * Returns a virtual PsiElement representing the whitespace at the end of charListHeredocLineWhitespace that is not
 * consumed by prefixLength. `trim_space` removes one horizontal space character per column of indentation, so the
 * rest is kept as written: a tab stays a tab.
 *
 * @return null if prefixLength is greater than or equal to text length of charListHeredocLineWhitespace.
 */
@Contract(pure = true)
fun ElixirHeredocLinePrefix.excessWhitespace(fragmentType: IElementType, prefixLength: Int): ASTNode? {
    val excessLength = textLength - prefixLength

    return if (excessLength > 0) {
        Factory.createSingleLeafElement(
                fragmentType,
                text.substring(prefixLength),
                0,
                excessLength, null,
                manager
        )
    } else {
        null
    }
}
