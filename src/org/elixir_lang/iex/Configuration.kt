package org.elixir_lang.iex

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.elixir_lang.IEx
import org.elixir_lang.iex.configuration.Editor
import org.elixir_lang.mix.ensureMostSpecificSdk
import org.elixir_lang.run.fromArguments
import org.elixir_lang.run.toArguments

class Configuration(name: String, project: Project, configurationFactory: ConfigurationFactory) :
        org.elixir_lang.run.Configuration(name, project, configurationFactory),
        RunConfigurationWithSuppressedDefaultRunAction,
        RunConfigurationWithSuppressedDefaultDebugAction {
    override fun getProgramParameters(): String? = iexArguments

    override fun setProgramParameters(value: String?) {
        iexArguments = value
    }

    private var erlArgumentList: MutableList<String> = mutableListOf()

    var erlArguments: String?
        get() = erlArgumentList.toArguments()
        set(arguments) = erlArgumentList.fromArguments(arguments)

    private var iexArguments: String?
        get() = iexArgumentList.toArguments()
        set(arguments) = iexArgumentList.fromArguments(arguments)

    private var iexArgumentList: MutableList<String> = mutableListOf()

    fun commandLine(): GeneralCommandLine {
        val workingDirectory = ensureWorkingDirectory()
        val module = ensureModule()
        val sdk = ensureMostSpecificSdk(module)
        val commandLine = IEx.commandLine(envs, workingDirectory, sdk, erlArgumentList)
        commandLine.addParameters(iexArgumentList)

        return commandLine
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = Editor()

    override fun getState(executor: Executor, environment: ExecutionEnvironment): State =
            State(environment, this)
}
