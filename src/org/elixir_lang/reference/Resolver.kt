package org.elixir_lang.reference

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import org.elixir_lang.navigation.isDecompiled

object Resolver {
    fun <T : ResolveResult> preferred(element: PsiElement, incompleteCode: Boolean, resolveResultList: List<T>): List<T> {
        val validResolveResultList = preferIsValidResult(incompleteCode, resolveResultList)
        val sameModuleResolveResultList = preferUnderSameModule(element, validResolveResultList)

        return preferSource(sameModuleResolveResultList)
    }

    fun <T : ResolveResult> preferIsValidResult(incompleteCode: Boolean, resolveResultList: List<T>): List<T> = if (incompleteCode) {
        resolveResultList
    } else {
        preferIsValidResult(resolveResultList)
    }

    fun <T : ResolveResult> preferIsValidResult(resolveResultList: List<T>): List<T> =
            filterIsValidResult(resolveResultList).takeIf(List<T>::isNotEmpty) ?: resolveResultList

    fun <T : ResolveResult> preferUnderModule(module: com.intellij.openapi.module.Module, resolveResultList: List<T>): List<T> =
            filterUnderModule(module, resolveResultList).takeIf(List<T>::isNotEmpty) ?: resolveResultList

    fun <T : ResolveResult> preferUnderSameModule(element: PsiElement, resolveResultList: List<T>): List<T> =
            filterUnderSameModule(element, resolveResultList)
                    .takeIf(List<T>::isNotEmpty)
                    ?: resolveResultList

    private fun <T : ResolveResult> preferSource(resolveResultList: List<T>): List<T> =
            filterSource(resolveResultList).takeIf(List<T>::isNotEmpty) ?: resolveResultList

    private fun <T : ResolveResult> filterIsValidResult(resolveResultList: List<T>): List<T> =
            resolveResultList.filter(ResolveResult::isValidResult)

    private fun <T : ResolveResult> filterUnderSameModule(element: PsiElement, resolveResultList: List<T>): List<T> = if (resolveResultList.isNotEmpty()) {
        ModuleUtil
                .findModuleForPsiElement(element)
                ?.let { module -> filterUnderModule(module, resolveResultList) }
                ?: emptyList()
    } else {
        resolveResultList
    }

    private fun <T : ResolveResult> filterUnderModule(module: com.intellij.openapi.module.Module, resolveResultList: List<T>): List<T> = if (resolveResultList.isNotEmpty()) {
        val contentRootSet = ModuleRootManager.getInstance(module).contentRoots.toSet()
        resolveResultList.filter { resolveResult ->
            // The `Module` of a Library source will be `null`, so have to check for a library in `deps` of module
            // using path instead.
            resolveResult
                    .element
                    ?.containingFile
                    ?.virtualFile
                    ?.let { virtualFile -> VfsUtilCore.isUnder(virtualFile, contentRootSet) }
                    ?: false
        }
    } else {
        emptyList()
    }

    private fun <T : ResolveResult> filterSource(resolveResultList: List<T>): List<T> =
            resolveResultList.filter { resolveResult ->
                resolveResult
                        .element
                        ?.let { element -> !element.isDecompiled() }
                        ?: false
            }
}
