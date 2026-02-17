package org.elixir_lang.mix

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Checks if Mix dependencies are outdated when a project is opened.
 *
 * Dependencies are considered outdated if `mix deps` reports a non-ok status.
 */
class DepsCheckerStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        project.service<DepsCheckerService>().scheduleInitialCheck()
    }
}
