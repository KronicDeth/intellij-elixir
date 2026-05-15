package org.elixir_lang.facet.configurable

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import org.elixir_lang.settings.ElixirTopLevelConfigurable

interface TopLevelElixirConfigurableFactory {
    fun create(project: Project): Configurable

    companion object {
        fun getInstance(): TopLevelElixirConfigurableFactory =
            ApplicationManager.getApplication().getService(TopLevelElixirConfigurableFactory::class.java)
    }
}

class SmallIdeTopLevelElixirConfigurableFactory : TopLevelElixirConfigurableFactory {
    override fun create(project: Project): Configurable = Project(project)
}

class RichPlatformTopLevelElixirConfigurableFactory : TopLevelElixirConfigurableFactory {
    override fun create(project: Project): Configurable = ElixirTopLevelConfigurable()
}
