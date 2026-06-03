package org.elixir_lang.reference.resolver

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.LibraryScopeCache
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.concurrency.annotations.RequiresReadLock

/**
 * Builds a search scope that covers the module owning [element], its transitive
 * dependencies, and *only* the SDK sources attached to that specific module.
 *
 * Falls back to [GlobalSearchScope.allScope] when no module can be determined
 * (e.g. scratches, injected fragments).
 *
 * Elixir stubs are indexed from .ex source files (not .beam class files), so
 * defmodule declarations live in SDK source roots. [GlobalSearchScope.moduleWithDependenciesAndLibrariesScope]
 * only covers class roots, so we must explicitly unite with the SDK source scope
 * obtained via [LibraryScopeCache.getScopeForSdk] per [JdkOrderEntry] from the
 * module's own root model. Using the module's own root model (not
 * [com.intellij.openapi.roots.ProjectFileIndex.getOrderEntriesForFile]) keeps the
 * scope scoped to this module's SDK only, preventing spurious "Choose Declaration"
 * results from sibling modules that share a module name but use a different SDK version.
 */
@RequiresReadLock
fun narrowedScope(element: PsiElement, project: Project): GlobalSearchScope {
    val module = ModuleUtil.findModuleForPsiElement(element)
        ?: return GlobalSearchScope.allScope(project)

    val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
    val virtualFile = element.containingFile?.originalFile?.virtualFile
    val includeTests = virtualFile?.let { projectFileIndex.isInTestSourceContent(it) } ?: false

    val moduleScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, includeTests)
    val sdkSourceScope = ModuleRootManager.getInstance(module).orderEntries
        .filterIsInstance<JdkOrderEntry>()
        .fold(GlobalSearchScope.EMPTY_SCOPE as GlobalSearchScope) { acc, entry ->
            acc.uniteWith(LibraryScopeCache.getInstance(project).getScopeForSdk(entry))
        }

    return moduleScope.uniteWith(sdkSourceScope)
}
