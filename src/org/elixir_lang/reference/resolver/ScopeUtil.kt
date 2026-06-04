package org.elixir_lang.reference.resolver

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.module.impl.scopes.JdkScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.LibraryScopeCache
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.concurrency.annotations.RequiresReadLock

/**
 * Builds a search scope that covers the module owning [element], its transitive
 * dependencies, and the SDK + non-SDK library sources attached to that specific module.
 *
 * Falls back to [GlobalSearchScope.allScope] when no module can be determined
 * (e.g. scratches, injected fragments).
 *
 * Elixir stubs are indexed from .ex source files (not .beam class files), so
 * defmodule declarations live in SDK and library source roots.
 * [GlobalSearchScope.moduleWithDependenciesAndLibrariesScope] only covers class roots
 * for library dependencies ([LibraryOrderEntry]), so we must explicitly unite with:
 *   - SDK source roots via [LibraryScopeCache.getScopeForSdk] per [JdkOrderEntry]
 *   - Non-SDK library source roots via [JdkScope] over each [LibraryOrderEntry]'s
 *     [OrderRootType.SOURCES] roots (e.g. `deps/ecto/lib`)
 *
 * Using the module's own root model (not
 * [com.intellij.openapi.roots.ProjectFileIndex.getOrderEntriesForFile]) keeps the
 * scope scoped to this module's dependencies only, preventing spurious "Choose Declaration"
 * results from sibling modules that share a module name but use a different SDK or library version.
 */
@RequiresReadLock
fun narrowedScope(element: PsiElement, project: Project): GlobalSearchScope {
    val module = ModuleUtil.findModuleForPsiElement(element)
        ?: return GlobalSearchScope.allScope(project)

    val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
    val virtualFile = element.containingFile?.originalFile?.virtualFile
    val includeTests = virtualFile?.let { projectFileIndex.isInTestSourceContent(it) } ?: false

    val orderEntries = ModuleRootManager.getInstance(module).orderEntries

    val moduleScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, includeTests)

    val sdkSourceScope = orderEntries
        .filterIsInstance<JdkOrderEntry>()
        .fold(GlobalSearchScope.EMPTY_SCOPE as GlobalSearchScope) { acc, entry ->
            acc.uniteWith(LibraryScopeCache.getInstance(project).getScopeForSdk(entry))
        }

    // moduleWithDependenciesAndLibrariesScope covers library CLASS roots (ebin/) but not SOURCE
    // roots (deps/<lib>/lib/).  Without this, multiResolveProject finds only the beam ModuleImpl
    // and never the source defmodule, causing navigation to land on the decompiled .beam file
    // instead of the .ex source even when source roots are attached to the library.
    val librarySourceScope = orderEntries
        .filterIsInstance<LibraryOrderEntry>()
        .fold(GlobalSearchScope.EMPTY_SCOPE as GlobalSearchScope) { acc, entry ->
            val sourceRoots = entry.getRootFiles(OrderRootType.SOURCES)
            if (sourceRoots.isEmpty()) acc
            else acc.uniteWith(JdkScope(project, emptyArray(), sourceRoots, entry.libraryName))
        }

    return moduleScope.uniteWith(sdkSourceScope).uniteWith(librarySourceScope)
}
