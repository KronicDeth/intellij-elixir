package org.elixir_lang.erl

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.elixir_lang.erl.configuration.Editor
import org.elixir_lang.run.fromArguments
import org.elixir_lang.run.toArguments

class Configuration(name: String, project: Project, configurationFactory: ConfigurationFactory) :
        org.elixir_lang.run.Configuration(name, project, configurationFactory) {
    override fun getProgramParameters(): String? = erlArguments

    override fun setProgramParameters(value: String?) {
        erlArguments = value
    }

    private var erlArgumentList: MutableList<String> = mutableListOf()

    var erlArguments: String?
        get() = erlArgumentList.toArguments()
        set(arguments) = erlArgumentList.fromArguments(arguments)

    private var elixirArgumentList: MutableList<String> = mutableListOf()

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = Editor()

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
            State(environment, this)
}
