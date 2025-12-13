package org.elixir_lang.status_bar_widget

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.util.messages.MessageBusConnection
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.elixir_lang.settings.ElixirExperimentalSettingsListener

private val LOG = logger<ElixirSdkStatusWidgetFactory>()

class ElixirSdkStatusWidgetFactory : StatusBarWidgetFactory {
    private var messageBusConnection: MessageBusConnection? = null

    init {
        setupSettingsListener()
    }
    
    override fun getId(): String = ElixirSdkStatusWidget.ID

    override fun getDisplayName(): String = "Elixir SDK Status"

    override fun isAvailable(project: Project): Boolean {
        // Widget is available when the experimental setting is enabled
        val isAvailable = ElixirExperimentalSettings.instance.state.enableStatusBarWidget
        LOG.debug("isAvailable called for project ${project.name}, returning $isAvailable")
        return isAvailable
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        // Widget can be enabled when the experimental setting is enabled
        return ElixirExperimentalSettings.instance.state.enableStatusBarWidget
    }

    override fun createWidget(project: Project): StatusBarWidget {
        LOG.debug("Creating widget for project: ${project.name}")
        return ElixirSdkStatusWidget(project)
    }

    // Mark this widget as configurable so users can enable/disable it
    override fun isConfigurable(): Boolean = true

    // isEnabledByDefault checks the experimental settings for each project
    override fun isEnabledByDefault(): Boolean {
        // Since this is called without a project context, return false
        // The actual availability is determined by isAvailable(project)
        return false
    }

    private fun setupSettingsListener() {
        val messageBus = ApplicationManager.getApplication().messageBus
        messageBusConnection = messageBus.connect()
        messageBusConnection?.subscribe(
            ElixirExperimentalSettings.SETTINGS_CHANGED_TOPIC,
            object : ElixirExperimentalSettingsListener {
                override fun settingsChanged(oldState: ElixirExperimentalSettings.State, newState: ElixirExperimentalSettings.State) {
                    LOG.debug("Settings changed: statusBarWidget ${oldState.enableStatusBarWidget} -> ${newState.enableStatusBarWidget}")
                    if (oldState.enableStatusBarWidget != newState.enableStatusBarWidget) {
                        LOG.debug("Status bar widget setting changed, updating widget visibility")
                        updateWidgetVisibility(newState.enableStatusBarWidget)
                    }
                }
            }
        )
    }

    private fun updateWidgetVisibility(shouldShow: Boolean) {
        ApplicationManager.getApplication().invokeLater {
            for (project in ProjectManager.getInstance().openProjects) {
                if (!project.isDisposed) {
                    updateProjectWidget(project, shouldShow)
                }
            }
        }
    }

    private fun updateProjectWidget(project: Project, shouldShow: Boolean) {
        val statusBar = WindowManager.getInstance().getStatusBar(project)
        if (statusBar != null) {
            val widget = statusBar.getWidget(ElixirSdkStatusWidget.ID) as ElixirSdkStatusWidget?

            LOG.debug("Project ${project.name}: widget exists=${widget != null}, shouldShow=$shouldShow")

            when {
                shouldShow && widget == null -> {
                    LOG.debug("Adding widget for project: ${project.name}")
                    val newWidget = createWidget(project)
                    statusBar.addWidget(newWidget, "before Position")
                }

                !shouldShow && widget != null -> {
                    LOG.debug("Removing widget for project: ${project.name}")
                    statusBar.removeWidget(ElixirSdkStatusWidget.ID)
                }

                else -> {
                    LOG.debug("Widget state already correct for project: ${project.name}")
                }
            }
        }
    }
}
