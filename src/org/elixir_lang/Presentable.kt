package org.elixir_lang

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

interface Presentable {
    val presentation: ItemPresentation?

    companion object {
        private val PRESENTATION: Key<CachedValue<ItemPresentation>> =
            Key("elixir.presentable.presentation")

        fun getPresentation(psiElement: PsiElement, compute: () -> ItemPresentation): ItemPresentation =
            CachedValuesManager.getCachedValue(psiElement, PRESENTATION) {
                CachedValueProvider.Result.create(compute(), psiElement)
            }
    }
}
