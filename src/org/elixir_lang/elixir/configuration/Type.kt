package org.elixir_lang.elixir.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType.CONFIGURATION_TYPE_EP
import com.intellij.openapi.extensions.Extensions
import org.elixir_lang.icons.ElixirIcons
import javax.swing.Icon

class Type : com.intellij.execution.configurations.ConfigurationType {
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(Factory)
    override fun getConfigurationTypeDescription(): String = "Starts an Elixir"
    override fun getIcon(): Icon = ElixirIcons.FILE
    override fun getId(): String = "org.elixir_lang.elixir.configuration.ConfigurationType"
    override fun getDisplayName(): String = "Elixir"

    companion object {
        internal fun getInstance(): Type = Extensions.findExtension(CONFIGURATION_TYPE_EP, Type::class.java)
    }
}
