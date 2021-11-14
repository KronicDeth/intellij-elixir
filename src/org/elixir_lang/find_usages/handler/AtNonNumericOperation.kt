package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.psi.AtOperation

class AtNonNumericOperation(atOperation: AtOperation) : FindUsagesHandler(atOperation) {
    private val _primaryElements by lazy {
        val resolvedElements = resolvedElements()

        if (resolvedElements.isNotEmpty()) {
            resolvedElements
        } else {
            super.getPrimaryElements()
        }
    }

    override fun getPrimaryElements(): Array<PsiElement> = _primaryElements

    private fun resolvedElements() =
            super
                    .getPrimaryElements()
                    .flatMap { it.references.toList() }
                    .flatMap { it.toPsiElementList() }
                    .toTypedArray()
}
