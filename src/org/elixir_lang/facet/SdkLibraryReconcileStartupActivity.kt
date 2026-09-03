package org.elixir_lang.facet

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.elixir_lang.util.awaitJpsProjectLoaded

/**
 * Re-attaches an assigned Elixir SDK's roots at startup for projects configured by a version that
 * never copied them - see [SdkLibraryReconciler].
 *
 * Waits for the JPS model first, for the same reason [org.elixir_lang.mix.sync.MixLibraryReconcileStartupActivity]
 * does: the IDE loads the workspace model from a binary cache and applies the real `.iml` state over
 * it afterwards, so both halves would otherwise be wrong - the check would read cached state, and
 * the repair would be discarded when the on-disk model landed on top of it.
 */
class SdkLibraryReconcileStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        awaitJpsProjectLoaded(project)
        if (project.isDisposed) return

        SdkLibraryReconciler.repair(project)
    }
}

