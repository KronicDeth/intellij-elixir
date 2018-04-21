package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.lang.findUsages.LanguageFindUsages
import com.intellij.psi.PsiElement
import org.elixir_lang.ElixirLanguage

class Factory : com.intellij.find.findUsages.FindUsagesHandlerFactory() {
    override fun createFindUsagesHandler(element: PsiElement, forHighlightUsages: Boolean): FindUsagesHandler =
            when (element) {
                is org.elixir_lang.psi.call.Call -> Call(element)
                is org.elixir_lang.psi.QualifiableAlias -> QualifiableAlias(element)
                else -> throw IllegalArgumentException("Cannot create FindUsageHandler for ${element.javaClass.canonicalName}")
            }

    override fun canFindUsages(element: PsiElement): Boolean =
            LanguageFindUsages.INSTANCE.forLanguage(ElixirLanguage).canFindUsagesFor(element)
}
