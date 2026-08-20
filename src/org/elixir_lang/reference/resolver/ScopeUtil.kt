package org.elixir_lang.reference.resolver

import com.intellij.codeInsight.daemon.SyntheticPsiFileSupport
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.module.impl.scopes.JdkScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.LibraryScopeCache
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.concurrency.annotations.RequiresReadLock

/**
 * The on-disk [VirtualFile] that should drive module / scope selection for [element], if recoverable.
 *
 * For normal files this is just `containingFile.originalFile.virtualFile`.  Files shown in the
 * commit / VCS diff view are backed by a synthetic [com.intellij.testFramework.LightVirtualFile]
 * which - although its path points at a real source file - is not part of any module, so
 * [ModuleUtil.findModuleForFile] returns null for it.  Depending on whether the view provider's
 * event system is enabled, that light file surfaces through either `originalFile.virtualFile` or
 * `viewProvider.virtualFile`; in both cases we map it back to the real file via [recoverLocalFile].
 *
 * Returns null for scratches and injected fragments that have no recoverable on-disk file.
 */
@RequiresReadLock
private fun effectiveVirtualFile(element: PsiElement): VirtualFile? {
    val containingFile = element.containingFile ?: return null

    val backingFile = containingFile.originalFile.virtualFile
        ?: containingFile.viewProvider.virtualFile

    return recoverLocalFile(backingFile) ?: backingFile
}

/**
 * Maps a synthetic diff / VCS [file] back to its real on-disk [VirtualFile], or null when [file] is
 * already a real local file (or nothing can be recovered).
 *
 * The diff platform marks these [com.intellij.testFramework.LightVirtualFile]s via
 * [SyntheticPsiFileSupport.markFile] with the original file URL, which we resolve first; as a
 * fallback we resolve the light file's own path so navigation still works when the file was never
 * marked.
 */
private fun recoverLocalFile(file: VirtualFile): VirtualFile? {
    if (file.isInLocalFileSystem) return null

    SyntheticPsiFileSupport.getOriginalFileUrl(file)?.let { url ->
        VirtualFileManager.getInstance().findFileByUrl(url)?.let { return it }
    }

    return LocalFileSystem.getInstance().findFileByPath(file.path)
}

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
    val virtualFile = effectiveVirtualFile(element)

    val module = ModuleUtil.findModuleForPsiElement(element)
        ?: virtualFile?.let { ModuleUtil.findModuleForFile(it, project) }
        ?: return GlobalSearchScope.allScope(project)

    val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
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
