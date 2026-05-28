package org.elixir_lang.status_bar_widget

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import kotlinx.coroutines.CoroutineScope
import org.elixir_lang.isElixirModule
import org.elixir_lang.settings.ElixirExperimentalSettings

private val LOG = logger<ElixirSdkStatusWidgetFactory>()

class ElixirSdkStatusWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = ElixirEditorBasedSdkWidget.ID

    override fun getDisplayName(): String = "Elixir SDK Status"

    override fun isAvailable(project: Project): Boolean {
        val isSettingEnabled = ElixirExperimentalSettings.instance.state.enableStatusBarWidget
        if (!isSettingEnabled) return false

        val hasElixirModule = ModuleManager.getInstance(project).modules.any { it.isElixirModule() }
        val isAvailable = hasElixirModule
        LOG.debug("isAvailable called for project ${project.name}, setting=$isSettingEnabled, hasElixirModule=$hasElixirModule, returning $isAvailable")
        return isAvailable
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        return ElixirExperimentalSettings.instance.state.enableStatusBarWidget
    }

    override fun createWidget(project: Project, scope: CoroutineScope): StatusBarWidget {
        LOG.debug("Creating widget for project: ${project.name}")
        return ElixirEditorBasedSdkWidget(project, scope)
    }

    // Mark this widget as configurable so users can enable/disable it
    override fun isConfigurable(): Boolean = true

    // if the experimental setting is enabled, make it so the widget immediately appears in the status bar
    override fun isEnabledByDefault(): Boolean = true
}
