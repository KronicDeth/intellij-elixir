package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.psi.QualifiableAlias

class QualifiableAlias(qualifiableAlias: QualifiableAlias) : FindUsagesHandler(qualifiableAlias) {
    private val _primaryElements by lazy {
        super
                .getPrimaryElements()
                .flatMap { it.references.toList() }
                .flatMap { it.toPsiElementList() }
                .toTypedArray()
    }

    override fun getPrimaryElements(): Array<PsiElement> = _primaryElements
}

