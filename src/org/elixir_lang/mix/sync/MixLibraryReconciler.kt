package org.elixir_lang.mix.sync

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.libraries.LibraryEx
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import org.elixir_lang.mix.library.Kind

/**
 * Decides whether the Mix libraries a project was left with still describe what is on disk, and asks
 * for a full re-sync only when they do not.
 *
 * Nothing re-syncs an already-configured project at startup, and the service is otherwise driven
 * purely by VFS events - so a `deps/` directory removed while the IDE was closed leaves libraries
 * pointing at nothing, with order entries that stay *valid* (the library object still exists) and are
 * therefore invisible to stale-entry pruning.
 *
 * Answering it by simply syncing everything would undo the pipeline's main cost saving: a full sync
 * canonicalises `_build/<env>/lib/<dep>/ebin` per dep per build environment, which is real
 * filesystem I/O, and in the steady state the diff that follows emits no write ops at all. This check
 * reads only library state already held in memory, so the common case - nothing wrong - costs no
 * directory traversal and no symlink resolution.
 */
internal object MixLibraryReconciler {

    /**
     * Whether [project]'s Mix libraries disagree with the project's current content roots.
     *
     * Reports true when a Mix-Kind library has a root the VFS cannot resolve, or carries a scope
     * token naming a content root the project no longer has - which also covers names written before
     * scope tokens became project-relative, so those migrate on the next open rather than lazily.
     *
     * Accuracy is bounded by the VFS: a deletion the refresh has not yet observed still reads as
     * valid, so this is a cheap trigger rather than a guarantee.
     */
    suspend fun needsResync(project: Project): Boolean = readAction {
        if (project.isDisposed) return@readAction false

        // Only the form the plugin writes today counts as current - the same test the write plan
        // applies. Accepting an older form here would make a project whose libraries are entirely
        // in that form look healthy, which is precisely the project that needs the re-sync.
        val currentTokens = ProjectRootManager.getInstance(project).contentRoots
            .mapTo(HashSet()) { contentRootToken(project, it.url) }

        val mixLibraries = LibraryTablesRegistrar.getInstance().getLibraryTable(project).libraries
            .filterIsInstance<LibraryEx>()
            .filter { it.kind == Kind && !it.isDisposed }

        val trigger = mixLibraries.firstOrNull { libraryEx ->
            val danglingRoot = libraryEx.getInvalidRootUrls(OrderRootType.CLASSES).isNotEmpty() ||
                libraryEx.getInvalidRootUrls(OrderRootType.SOURCES).isNotEmpty()

            // A null token is an unscoped or consolidated name, neither of which this check owns.
            val foreignScope = libraryEx.name
                ?.let { scopedLibraryNameToken(it) }
                ?.let { token -> token !in currentTokens }
                ?: false

            danglingRoot || foreignScope
        }

        trigger != null
    }
}
