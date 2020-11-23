package org.elixir_lang.iex.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType.CONFIGURATION_TYPE_EP
import com.intellij.openapi.extensions.Extensions
import javax.swing.Icon

class Type : com.intellij.execution.configurations.ConfigurationType {
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(Factory)
    override fun getConfigurationTypeDescription(): String = "Starts an Interactive Elixir (IEx) Console"
    override fun getIcon(): Icon = Icons.TYPE
    override fun getId(): String = "org.elixir_lang.iex.configuration.ConfigurationType"
    override fun getDisplayName(): String = "IEx"

    companion object {
        internal fun getInstance(): Type = CONFIGURATION_TYPE_EP.findExtensionOrFail(Type::class.java)
    }
}
