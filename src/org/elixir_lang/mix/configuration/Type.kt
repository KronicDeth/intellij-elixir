package org.elixir_lang.mix.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.extensions.Extensions

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunConfigurationType.java
 */
class Type: ConfigurationTypeBase(TYPE_ID, TYPE_NAME, "Runs a Mix command", Icons.TYPE) {
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(Factory)

    companion object {
        val INSTANCE: Type
            get() = ConfigurationType.CONFIGURATION_TYPE_EP.findExtensionOrFail(Type::class.java)
    }
}

const val TYPE_ID = "MixRunConfigurationType"
const val TYPE_NAME = "Elixir Mix"
