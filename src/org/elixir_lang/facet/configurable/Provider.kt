package org.elixir_lang.facet.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider

class Provider(private val project: com.intellij.openapi.project.Project): ConfigurableProvider() {
    override fun canCreateConfigurable(): Boolean = true

    override fun createConfigurable(): Configurable =
        TopLevelElixirConfigurableFactory.getInstance().create(project)
}
