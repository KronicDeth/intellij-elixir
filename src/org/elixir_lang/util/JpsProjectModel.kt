package org.elixir_lang.util

import com.intellij.openapi.project.Project
import com.intellij.workspaceModel.ide.JpsProjectLoadingManager
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Suspends until the JPS project model has been synchronized with the on-disk `.iml` / `jdk.table.xml` files,
 * resuming immediately if the sync already completed.
 *
 * At startup the platform first loads the workspace model from a cache, then runs the `DelayedProjectSynchronizer`
 * project activity which reads the actual `.iml` and `jdk.table.xml` files from disk and applies them to the
 * workspace model (WSL projects: 5–13 seconds). Any project-model change committed before that sync completes - e.g.
 * assigning a missing Elixir SDK - is overwritten when the sync applies the persisted state. Callers that read or
 * mutate authoritative module/SDK configuration from an automated startup activity must await this gate first.
 *
 * ## Why the deprecated, internal [JpsProjectLoadingManager]?
 *
 * [JpsProjectLoadingManager] is `@ApiStatus.Internal` **and** `@Deprecated` in favour of
 * `com.intellij.platform.backend.workspace.impl.WorkspaceModelInternal.awaitSynchronizationWithJpsModel`. We do not
 * migrate, because for a Marketplace plugin the replacement is no better:
 *
 * - It is `@ApiStatus.Experimental` declared on the `@ApiStatus.Internal WorkspaceModelInternal` interface, so it is
 *   itself an internal API - switching would relocate the plugin-verifier internal-API violation, not remove it.
 * - It requires a `(workspaceModel as WorkspaceModelInternal)` cast (the platform confirms the cast is safe, but it
 *   is still internal surface).
 * - It is absent from our minimum supported build 253 (verified against tag `idea/253.28294.334`); calling it there
 *   throws `NoSuchMethodError`.
 *
 * [JpsProjectLoadingManager] works across the whole supported range (253 → 261+) and is the mechanism the platform
 * itself endorses for the "automated startup activity that may change project configuration" case - which is exactly
 * this one (see the IJPL-249625 discussion). The JetBrains PyCharm plugin uses it for the same purpose.
 *
 * ## Migration - this is the single place to change
 *
 * Watch these tickets; when a public, cast-free API ships and our `sinceBuild` is past the build that first carries
 * it, replace the body here (all call sites update automatically):
 *
 * - **IJPL-249625** (open) - make `awaitSynchronizationWithJpsModel` a cast-free, official public API.
 *   https://youtrack.jetbrains.com/issue/IJPL-249625
 *   The comment thread carries the platform's rationale (Lucy Kornilova) that this gate is needed only for startup
 *   activities that change project configuration - i.e. this use case - and confirms the replacement stays internal.
 * - **IJPL-241069** (open) - "Remove JpsProjectLoadingManager". https://youtrack.jetbrains.com/issue/IJPL-241069
 * - **IJPL-240839** (fixed) - introduced the (internal) replacement. https://youtrack.jetbrains.com/issue/IJPL-240839
 */
@Suppress("UnstableApiUsage", "DEPRECATION")
suspend fun awaitJpsProjectLoaded(project: Project) {
    suspendCancellableCoroutine { cont ->
        JpsProjectLoadingManager.getInstance(project).jpsProjectLoaded {
            cont.resumeWith(Result.success(Unit))
        }
    }
}
