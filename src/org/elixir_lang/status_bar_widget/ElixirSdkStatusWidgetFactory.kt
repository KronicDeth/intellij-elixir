package org.elixir_lang.status_bar_widget

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import org.elixir_lang.settings.ElixirExperimentalSettings

private val LOG = logger<ElixirSdkStatusWidgetFactory>()

class ElixirSdkStatusWidgetFactory : StatusBarWidgetFactory {
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

    // if the experimental setting is enabled, make it so the widget immediately appears in the status bar
    // this was previously set to false, which required the user to enable it in project settings, and then enable
    // it again on the status bar.
    override fun isEnabledByDefault(): Boolean =  true
}
