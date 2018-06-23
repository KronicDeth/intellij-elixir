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
import org.elixir_lang.debugged.Modules
import org.elixir_lang.debugger.configuration.Debuggable
import org.elixir_lang.iex.configuration.Editor
import org.elixir_lang.mix.ensureMostSpecificSdk
import org.elixir_lang.run.fromArguments
import org.elixir_lang.run.toArguments

class Configuration(name: String, project: Project, configurationFactory: ConfigurationFactory) :
        org.elixir_lang.run.Configuration(name, project, configurationFactory),
        Debuggable<Configuration>,
        RunConfigurationWithSuppressedDefaultRunAction,
        RunConfigurationWithSuppressedDefaultDebugAction {
    override fun debuggerConfiguration(name: String, configPath: String, javaPort: Int): org.elixir_lang.debugger.Configuration {
        val debugger= org.elixir_lang.debugger.Configuration(name, project, factory)
        debugger.erlArgumentList.addAll(erlArgumentList)
        debugger.erlArgumentList.addAll(arrayOf("-name", name))
        debugger.erlArgumentList.addAll(arrayOf("-config", configPath))

        debugger.javaPort = javaPort

        return debugger
    }

    override fun debuggedConfiguration(name: String, configPath: String): Configuration {
        val debugged = Configuration(this.name, project, factory)

        debugged.erlArgumentList.addAll(erlArgumentList)
        debugged.erlArgumentList.addAll(arrayOf("-name", name))
        debugged.erlArgumentList.addAll(arrayOf("-config", configPath))
        debugged.erlArgumentList.addAll(Modules.erlArgumentList())

        debugged.iexArgumentList.addAll(iexArgumentList)

        return debugged
    }

    override fun getProgramParameters(): String? = iexArguments

    override fun setProgramParameters(value: String?) {
        iexArguments = value
    }

    var erlArgumentList: MutableList<String> = mutableListOf()

    var erlArguments: String?
        get() = erlArgumentList.toArguments()
        set(arguments) = erlArgumentList.fromArguments(arguments)

    private var iexArguments: String?
        get() = iexArgumentList.toArguments()
        set(arguments) = iexArgumentList.fromArguments(arguments)

    var iexArgumentList: MutableList<String> = mutableListOf()

    fun commandLine(): GeneralCommandLine {
        val workingDirectory = ensureWorkingDirectory()
        val module = ensureModule()
        val sdk = ensureMostSpecificSdk(module)
        val commandLine = IEx.commandLine(
                pty = true,
                environment = envs,
                workingDirectory = workingDirectory,
                elixirSdk = sdk,
                erlArgumentList = erlArgumentList
        )
        commandLine.addParameters(iexArgumentList)

        return commandLine
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = Editor()

    override fun getState(executor: Executor, environment: ExecutionEnvironment): State =
            State(environment, this)
}
