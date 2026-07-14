package org.elixir_lang.tool_manager

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

private val LOG = logger<ToolManagerSdkCheckerStartupActivity>()

/**
 * Startup activity that eagerly initialises [ToolManagerSdkCheckerService] and
 * [ToolManagerSdkAnalyser] at project open.
 *
 * Both services are lazy by default (created on first access). Accessing them here causes their
 * `init` blocks to run, which subscribe to message-bus topics and schedule the initial scan.
 * Without this activity the services would not activate until something else accessed them.
 *
 * Registered in the base `plugin.xml`, so it runs in every IDE including small IDEs (RubyMine).
 */
internal class ToolManagerSdkCheckerStartupActivity : ProjectActivity, DumbAware {

    override suspend fun execute(project: Project) {
        LOG.debug("Initialising tool manager services for project: ${project.name}")
        ToolManagerSdkCheckerService.getInstance(project)
        ToolManagerSdkAnalyser.getInstance(project)
    }
}
