package org.elixir_lang.find_usages.handler

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.psi.PsiElement
import org.elixir_lang.beam.psi.impl.ModuleImpl

class ModuleImpl(moduleImpl: ModuleImpl<*>) : FindUsagesHandler(moduleImpl) {
    override fun getPrimaryElements(): Array<PsiElement> = arrayOf((psiElement as ModuleImpl<*>).mirror)
}
