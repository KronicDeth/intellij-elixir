package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.lang.findUsages.LanguageFindUsages
import com.intellij.psi.PsiElement
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.find_usages.Handler

class Factory : com.intellij.find.findUsages.FindUsagesHandlerFactory() {
    override fun createFindUsagesHandler(element: PsiElement, forHighlightUsages: Boolean): FindUsagesHandler =
        Handler(element)

    override fun canFindUsages(element: PsiElement): Boolean =
            LanguageFindUsages.INSTANCE.forLanguage(ElixirLanguage).canFindUsagesFor(element)
}
