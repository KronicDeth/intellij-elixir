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
        val sameModuleResolveResultList = preferElementUnderSameModule(element, validResolveResultList)

        return sameModuleResolveResultList
    }

    private fun <T : ResolveResult> preferIsValidResult(incompleteCode: Boolean,
                                                        resolveResultList: List<T>): List<T> = if (incompleteCode) {
        resolveResultList
    } else {
        preferIsValidResult(resolveResultList)
    }

    fun <T : ResolveResult> preferIsValidResult(resolveResultList: List<T>): List<T> =
            filterIsValidResult(resolveResultList).takeIf(List<T>::isNotEmpty) ?: resolveResultList

    fun <T : ResolveResult> preferElementUnderSameModule(element: PsiElement, resolveResultList: List<T>): List<T> =
            preferUnderSameModule(element, resolveResultList, ResolveResult::getElement)

    fun <T: PsiElement> preferUnderSameModule(elementInModule: PsiElement, elementList: List<T>): List<T> =
            preferUnderSameModule(elementInModule, elementList) { it }

    private fun <T, U: PsiElement> preferUnderSameModule(elementInModule: PsiElement,
                                                 list: List<T>,
                                                 listElementToPsiElement: (listElement: T) -> U?): List<T> =
        filterUnderSameModule(elementInModule, list, listElementToPsiElement)
                .takeIf(List<T>::isNotEmpty)
                ?: list

    fun <T : ResolveResult> preferSourceElement(resolveResultList: List<T>): List<T> =
            preferSource(resolveResultList, ResolveResult::getElement)

    fun <T: PsiElement> preferSource(elementList: List<T>): List<T> =
            preferSource(elementList) { it }

    private fun <T, U: PsiElement> preferSource(list: List<T>, listElementToPsiElement: (listElement: T) -> U?): List<T> =
            filterSource(list, listElementToPsiElement).takeIf(List<T>::isNotEmpty) ?: list

    private fun <T : ResolveResult> filterIsValidResult(resolveResultList: List<T>): List<T> =
            resolveResultList.filter(ResolveResult::isValidResult)

    private fun <T, U: PsiElement> filterUnderSameModule(
            elementInModule: PsiElement,
            list: List<T>,
            listElementToPsiElement: (listElement: T) -> U?
    ): List<T> = if (list.isNotEmpty()) {
        moduleForSourceOrLibrary(elementInModule)
                ?.let { module -> filterUnderModule(module, list, listElementToPsiElement) }
                ?: emptyList()
    } else {
        list
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

    private fun <T, U: PsiElement> filterUnderModule(
            module: com.intellij.openapi.module.Module,
            list: List<T>,
            listElementToPsiElement: (listElement: T) -> U?): List<T> = if (list.isNotEmpty()) {
        val contentRootSet = ModuleRootManager.getInstance(module).contentRoots.toSet()
        list.filter { listElement ->
            listElementToPsiElement(listElement)
                    ?.let { isUnder(it, contentRootSet) }
                    ?: false
        }
    } else {
        emptyList()
    }

    private fun <T: PsiElement, V: VirtualFile> isUnder(element: T, roots: Set<V>): Boolean =
        element.containingFile?.virtualFile?.let { VfsUtilCore.isUnder(it, roots) } ?: false

    private fun <T, U: PsiElement> filterSource(list: List<T>,
                                                listElementToPsiElement: (listElement: T) -> U?): List<T> =
            list.filter { listElement ->
                listElementToPsiElement(listElement)
                        ?.let { !it.isDecompiled() }
                        ?: false
            }
}
