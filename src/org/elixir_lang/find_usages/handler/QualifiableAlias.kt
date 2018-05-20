package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import org.elixir_lang.beam.psi.impl.ModuleElementImpl.COMPILED_ELEMENT
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.psi.QualifiableAlias

class QualifiableAlias(qualifiableAlias: QualifiableAlias) : FindUsagesHandler(qualifiableAlias) {
    private val _primaryElements by lazy {
        val resolvedElementList = resolvedElementList()

        if (resolvedElementList.isNotEmpty()) {
            if (resolvedElementList.any(PsiElement::isSourceElement)) {
                resolvedElementList.filter(PsiElement::isSourceElement)
            } else {
                // all compiled, no preferred between environments
                resolvedElementList
            }.toTypedArray()
        } else {
            super.getPrimaryElements()
        }
    }

    override fun getPrimaryElements(): Array<PsiElement> = _primaryElements

    private fun resolvedElementList() = super
            .getPrimaryElements()
            .flatMap { it.references.toList() }
            .flatMap { it.toPsiElementList() }
}

private fun PsiElement.isSourceElement(): Boolean = this.getUserData(COMPILED_ELEMENT) == null
