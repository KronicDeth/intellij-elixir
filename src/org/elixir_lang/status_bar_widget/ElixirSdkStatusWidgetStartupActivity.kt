package org.elixir_lang.status_bar_widget

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.elixir_lang.settings.ElixirExperimentalSettingsListener

private val LOG = logger<ElixirSdkStatusWidgetStartupActivity>()

class ElixirSdkStatusWidgetStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        LOG.debug("Executing startup activity for project: ${project.name}")

        // Initial trigger to show widget if setting is already enabled
        refreshWidgetVisibility(project)

        // Listen for future setting changes
        val connection = project.messageBus.connect()
        connection.subscribe(
            ElixirExperimentalSettings.SETTINGS_CHANGED_TOPIC,
            object : ElixirExperimentalSettingsListener {
                override fun settingsChanged(oldState: ElixirExperimentalSettings.State, newState: ElixirExperimentalSettings.State) {
                    if (oldState.enableStatusBarWidget != newState.enableStatusBarWidget) {
                        LOG.debug("Setting changed in ${project.name}, refreshing widget")
                        refreshWidgetVisibility(project)
                    }
                }
            }
        )
    }

    private fun refreshWidgetVisibility(project: Project) {
        // We use invokeLater to ensure the StatusBar is fully initialized in the UI
        // before we ask the manager to update the widget.
        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                LOG.debug("Requesting StatusBarWidgetsManager update for ${project.name}")
                @Suppress("IncorrectServiceRetrieving")
                val manager = project.getService(StatusBarWidgetsManager::class.java)
                manager?.updateWidget(ElixirSdkStatusWidgetFactory::class.java)
            }
        }
    }
}
