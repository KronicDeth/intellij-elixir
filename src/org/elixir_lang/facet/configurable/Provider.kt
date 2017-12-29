package org.elixir_lang.facet.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import org.elixir_lang.sdk.ProcessOutput

class Provider(private val project: com.intellij.openapi.project.Project): ConfigurableProvider() {
    override fun canCreateConfigurable(): Boolean = ProcessOutput.isSmallIde()
    override fun createConfigurable(): Configurable = Project(project)
}
