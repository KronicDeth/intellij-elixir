package org.elixir_lang.status_bar_widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import com.intellij.util.messages.MessageBusConnection
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.elixir_lang.settings.ElixirExperimentalSettingsListener

class ElixirSdkStatusWidgetFactory : StatusBarWidgetFactory {
    private var messageBusConnection: MessageBusConnection? = null
    
    init {
        // Listen for experimental settings changes at application level
        setupSettingsListener()
    }
    
    override fun getId(): String = ElixirSdkStatusWidget.ID

    override fun getDisplayName(): String = "Elixir SDK Status"

    override fun isAvailable(project: Project): Boolean {
        return ElixirSdkStatusWidget.isAvailableOnProject(project)
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        val project = statusBar.project ?: return false
        return ElixirSdkStatusWidget.isAvailableOnProject(project)
    }

    override fun createWidget(project: Project): StatusBarWidget {
        return ElixirSdkStatusWidget(project)
    }

    // isEnabledByDefault, which checks the experimental settings
    override fun isEnabledByDefault(): Boolean {
        return ElixirExperimentalSettings.instance.state.enableStatusBarWidget
    }
    
    private fun setupSettingsListener() {
        val applicationMessageBus = com.intellij.openapi.application.ApplicationManager.getApplication().messageBus
        messageBusConnection = applicationMessageBus.connect()
        messageBusConnection?.subscribe(ElixirExperimentalSettings.SETTINGS_CHANGED_TOPIC, 
            object : ElixirExperimentalSettingsListener {
                override fun settingsChanged(oldState: ElixirExperimentalSettings.State, newState: ElixirExperimentalSettings.State) {
                    // Only update if the status bar widget setting changed
                    if (oldState.enableStatusBarWidget != newState.enableStatusBarWidget) {
                        updateAllProjectWidgets()
                    }
                }
            })
    }
    
    private fun updateAllProjectWidgets() {
        // Update widget visibility for all open projects
        for (project in ProjectManager.getInstance().openProjects) {
            if (!project.isDisposed) {
                project.getService(StatusBarWidgetsManager::class.java).updateWidget(this)
            }
        }
    }
}
