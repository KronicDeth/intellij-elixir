package org.elixir_lang.reference

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import org.elixir_lang.navigation.isDecompiled

object Resolver {
    fun <T : ResolveResult> preferred(element: PsiElement, incompleteCode: Boolean, resolveResultList: List<T>): List<T> {
        val validResolveResultList = preferIsValidResult(incompleteCode, resolveResultList)
        val sameModuleResolveResultList = preferUnderSameModule(element, validResolveResultList)

        return sameModuleResolveResultList
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

    fun <T : ResolveResult> preferSource(resolveResultList: List<T>): List<T> =
            filterSource(resolveResultList).takeIf(List<T>::isNotEmpty) ?: resolveResultList

    private fun <T : ResolveResult> filterIsValidResult(resolveResultList: List<T>): List<T> =
            resolveResultList.filter(ResolveResult::isValidResult)

    private fun <T : ResolveResult> filterUnderSameModule(element: PsiElement, resolveResultList: List<T>): List<T> = if (resolveResultList.isNotEmpty()) {
        moduleForSourceOrLibrary(element)
                ?.let { module -> filterUnderModule(module, resolveResultList) }
                ?: emptyList()
    } else {
        resolveResultList
    }

    // `ModuleUtil.findModuleForPsiElement` returns `null` for library source, so need to find module a different way if a library
    private fun moduleForSourceOrLibrary(element: PsiElement): com.intellij.openapi.module.Module? =
        ModuleUtil.findModuleForPsiElement(element) ?:
        element
                .containingFile
                .virtualFile
                ?.takeIf { ProjectFileIndex.getInstance(element.project).isInLibrary(it) }
                ?.let { virtualFile ->
                    ModuleManager
                            .getInstance(element.project)
                            .modules
                            .asSequence()
                            .mapNotNull { module ->
                                val contentRootSet = ModuleRootManager.getInstance(module).contentRoots.toSet()

                                depth(virtualFile, contentRootSet)
                                        ?.let { depth -> module to depth }
                            }
                            .minBy { (_, depth) -> depth }
                            ?.first
                }

    private fun depth(virtualFile: VirtualFile, roots: Set<VirtualFile>?): Int? =
        if (roots != null && roots.isNotEmpty()) {
            var ancestor: VirtualFile? = virtualFile
            var finalDepth: Int? = null
            var currentDepth = 0

            while (ancestor != null) {
                if (roots.contains(ancestor)) {
                    finalDepth = currentDepth
                    break
                }

                ancestor = ancestor.parent
                currentDepth++
            }

            finalDepth
        } else {
            null
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
