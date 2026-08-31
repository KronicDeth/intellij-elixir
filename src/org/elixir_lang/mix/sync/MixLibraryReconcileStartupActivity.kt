package org.elixir_lang.mix.sync

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.elixir_lang.util.awaitJpsProjectLoaded

/**
 * Re-syncs Mix libraries at startup, but only when they no longer describe what is on disk.
 *
 * An already-configured project enqueues nothing when it opens, so changes made while the IDE was
 * closed - a deleted `deps/` above all - would otherwise never be noticed. Escalating through
 * [MixLibraryReconciler] rather than always syncing keeps the usual open free of the full
 * deps/`_build` scan.
 *
 * Waits for the JPS model first. The IDE loads the workspace model from a binary cache at startup
 * and only afterwards applies the real `.iml` state over it, so both halves of this would otherwise
 * be wrong: the check would read cached state, and any resulting sync would be silently discarded
 * when the on-disk model landed on top of it.
 */
class MixLibraryReconcileStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        awaitJpsProjectLoaded(project)
        if (project.isDisposed) return

        if (MixLibraryReconciler.needsResync(project)) {
            LOG.debug("Mix libraries disagree with the project's content roots; requesting a full sync")
            project.service<MixDepsSyncService>().enqueue(SyncRequest.All)
        }
    }

    private companion object {
        private val LOG = logger<MixLibraryReconcileStartupActivity>()
    }
}
