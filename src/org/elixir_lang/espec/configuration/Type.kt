package org.elixir_lang.espec.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.extensions.Extensions

class Type : ConfigurationTypeBase(TYPE_ID, TYPE_NAME, "Runs Mix espec", Icons.TYPE) {
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(Factory)

    companion object {
        private const val TYPE_ID = "MixESpecRunConfigurationType"
        internal const val TYPE_NAME = "Elixir Mix ESpec"

        val INSTANCE: Type
            get() = ConfigurationType.CONFIGURATION_TYPE_EP.findExtensionOrFail(Type::class.java)
    }
}
