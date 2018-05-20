package org.elixir_lang.psi.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirAtIdentifier
import org.jetbrains.annotations.Contract


private fun ElixirAtIdentifier.computeReference(): PsiReference? =
    if (parent !is AtUnqualifiedNoParenthesesCall<*>) {
        org.elixir_lang.reference.ModuleAttribute(this)
    } else {
        null
    }

/**
 * <blockquote>
 * The PSI element at the cursor (the direct tree parent of the token at the cursor position) must be either a
 * PsiNamedElement or *a PsiReference which resolves to a PsiNamedElement.*
</blockquote> *
 * @see [IntelliJ Platform SDK DevGuide | Find Usages](http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/find_usages.html?search=PsiNameIdentifierOwner)
 */
@Contract(pure = true)
fun ElixirAtIdentifier.getReference(): PsiReference? =
        getCachedValue(this) {
            CachedValueProvider.Result.create(computeReference(), this)
        }

fun ElixirAtIdentifier.identifierName(): String {
    val node = node
    val identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET)

    assert(identifierNodes.size == 1)

    val identifierNode = identifierNodes[0]
    return identifierNode.text
}
