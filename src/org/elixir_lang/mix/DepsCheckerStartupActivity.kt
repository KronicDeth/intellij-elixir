package org.elixir_lang.mix

import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elixir_lang.notification.setup_sdk.Notifier

private val LOG = logger<DepsCheckerStartupActivity>()

/**
 * Checks if Mix dependencies are outdated when a project is opened.
 *
 * Dependencies are considered outdated if:
 * - mix.lock exists but deps/ folder doesn't exist
 * - mix.lock exists but deps/ folder is empty
 * - mix.lock is newer than deps/ folder (timestamp comparison)
 */
class DepsCheckerStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        LOG.debug("DepsCheckerStartupActivity: Checking Mix dependencies status")

        withContext(Dispatchers.IO) {
            val projectRoots = ProjectRootManager.getInstance(project).contentRootsFromAllModules

            for (root in projectRoots) {
                LOG.debug("Checking root: ${root.path}")

                if (areDepsOutdated(root)) {
                    LOG.debug("Dependencies are outdated for root: ${root.path}")

                    withContext(Dispatchers.EDT) {
                        Notifier.mixDepsOutdated(project)
                    }
                    // Only show notification once, even if multiple roots have outdated deps
                    break
                }
            }

            LOG.debug("DepsCheckerStartupActivity: Check complete")
        }
    }

    private fun areDepsOutdated(projectRoot: VirtualFile): Boolean {
        // Check if mix.lock exists
        val mixLock = projectRoot.findChild("mix.lock")
        if (mixLock == null || !mixLock.exists()) {
            LOG.debug("No mix.lock found in ${projectRoot.path}, skipping deps check")
            return false
        }

        // Check if mix.exs exists (confirm this is a Mix project)
        val mixExs = projectRoot.findChild("mix.exs")
        if (mixExs == null || !mixExs.exists()) {
            LOG.debug("No mix.exs found in ${projectRoot.path}, skipping deps check")
            return false
        }

        // Check deps folder
        val depsFolder = projectRoot.findChild("deps")
        // TODO: This should probably be handled by parsing `mix deps` output, but this does the basics.
        return when {
            depsFolder == null || !depsFolder.exists() -> {
                LOG.debug("deps/ folder doesn't exist in ${projectRoot.path}")
                true
            }
            !depsFolder.isDirectory -> {
                LOG.debug("deps/ exists but is not a directory in ${projectRoot.path}")
                true
            }
            depsFolder.children.isEmpty() -> {
                LOG.debug("deps/ folder is empty in ${projectRoot.path}")
                true
            }
            depsFolder.timeStamp < mixLock.timeStamp -> {
                LOG.debug("mix.lock (${mixLock.timeStamp}) is newer than deps/ (${depsFolder.timeStamp}) in ${projectRoot.path}")
                true
            }
            else -> {
                LOG.debug("Dependencies appear up-to-date in ${projectRoot.path}")
                false
            }
        }
    }
}
