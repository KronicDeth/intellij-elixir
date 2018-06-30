package org.elixir_lang.exunit

import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.options.SettingsEditorGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.ExUnit
import org.elixir_lang.Level.V_1_4
import org.elixir_lang.debugged.Modules
import org.elixir_lang.debugger.configuration.Debuggable
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import org.elixir_lang.exunit.configuration.Factory
import org.elixir_lang.file.LevelPropertyPusher.level
import org.elixir_lang.mix.ensureMostSpecificSdk
import org.elixir_lang.run.*
import org.elixir_lang.sdk.elixir.Type.mostSpecificSdk
import org.jdom.Element

class Configuration(name: String, project: Project) :
        org.elixir_lang.run.Configuration(name, project, Factory),
        Debuggable<org.elixir_lang.exunit.Configuration>,
        RunConfigurationWithSuppressedDefaultRunAction,
        RunConfigurationWithSuppressedDefaultDebugAction {
    override var inheritApplicationModuleFilters: Boolean = true
    override var moduleFilterList: MutableList<ModuleFilter> = mutableListOf()

    override fun debuggerConfiguration(name: String, configPath: String, javaPort: Int): org.elixir_lang.debugger.Configuration {
        val debugger = org.elixir_lang.debugger.Configuration(name, project, factory)
        debugger.erlArgumentList.addAll(erlArgumentList)
        debugger.erlArgumentList.addAll(arrayOf("-name", name))
        debugger.erlArgumentList.addAll(arrayOf("-config", configPath))

        debugger.elixirArgumentList.addAll(elixirArgumentList)

        debugger.javaPort = javaPort

        debugger.workingDirectory = workingDirectory
        debugger.isPassParentEnvs = isPassParentEnvs

        val debuggedEnvs = mutableMapOf<String, String>()
        debuggedEnvs.putAll(envs)
        debuggedEnvs.putIfAbsent("MIX_ENV", "test")
        debugger.envs = debuggedEnvs

        debugger.configurationModule.module = configurationModule.module

        debugger.inheritApplicationModuleFilters = inheritApplicationModuleFilters
        debugger.moduleFilterList = moduleFilterList

        return debugger
    }

    override fun debuggedConfiguration(name: String, configPath: String): Configuration {
        val debugged = Configuration(this.name, project)

        debugged.erlArgumentList.addAll(erlArgumentList)
        debugged.erlArgumentList.addAll(arrayOf("-name", name))
        debugged.erlArgumentList.addAll(arrayOf("-config", configPath))
        debugged.erlArgumentList.addAll(Modules.erlArgumentList())

        debugged.elixirArgumentList.addAll(elixirArgumentList)

        debugged.mixTestArgumentList.addAll(mixTestArgumentList)
        debugged.mixTestArgumentList.add("--trace")

        debugged.workingDirectory = workingDirectory
        debugged.isPassParentEnvs = isPassParentEnvs
        debugged.envs = envs
        debugged.configurationModule.module = configurationModule.module

        return debugged
    }

    val task
        get() =
            if (level(sdk()) >= V_1_4) {
                "test"
            } else {
                "test_with_formatter"
            }

    private fun sdk(): Sdk? {
        val module = configurationModule.module

        return if (module != null) {
            mostSpecificSdk(module)
        } else {
            mostSpecificSdk(project)
        }
    }

    override fun getProgramParameters(): String? = mixTestArguments

    override fun setProgramParameters(value: String?) {
        mixTestArguments = value
    }

    var erlArgumentList: MutableList<String> = mutableListOf()

    var erlArguments: String?
        get() = erlArgumentList.toArguments()
        set(arguments) = erlArgumentList.fromArguments(arguments)

    var elixirArgumentList: MutableList<String> = mutableListOf()

    var elixirArguments: String?
        get() = elixirArgumentList.toArguments()
        set(arguments) = elixirArgumentList.fromArguments(arguments)

    private var mixTestArguments: String?
        get() = mixTestArgumentList.toArguments()
        set(arguments) = mixTestArgumentList.fromArguments(arguments)

    var mixTestArgumentList: MutableList<String> = mutableListOf()

    fun commandLine(): GeneralCommandLine {
        val workingDirectory = ensureWorkingDirectory()
        val module = ensureModule()
        val sdk = ensureMostSpecificSdk(module)
        val commandLine = ExUnit.commandLine(envs, workingDirectory, sdk, erlArgumentList, elixirArgumentList)
        commandLine.addParameters(mixTestArgumentList)

        return commandLine
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
            SettingsEditorGroup<Configuration>().apply {
                this.addEditor("Configuration", org.elixir_lang.exunit.configuration.Editor())
                this.addEditor("Interpreted Modules", org.elixir_lang.debugger.configuration.interpreted_modules.Editor<Configuration>())
            }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
            State(environment, this)

    override fun readExternal(element: Element) {
        super.readExternal(element)
        element.readExternalArgumentList(ERL, erlArgumentList)
        element.readExternalArgumentList(ELIXIR, elixirArgumentList)
        element.readExternalArgumentList(MIX_TEST, mixTestArgumentList)
        workingDirectoryURL = element.readExternalWorkingDirectory()
        EnvironmentVariablesComponent.readExternal(element, envs)
        element.readExternalModule(this)
        element.readModuleFilters(moduleFilterList) { inheritApplicationModuleFilters ->
            this.inheritApplicationModuleFilters = inheritApplicationModuleFilters
        }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.writeExternalArgumentList(ERL, erlArgumentList)
        element.writeExternalArgumentList(ELIXIR, elixirArgumentList)
        element.writeExternalArgumentList(MIX_TEST, mixTestArgumentList)
        element.writeExternalWorkingDirectory(workingDirectoryURL)
        EnvironmentVariablesComponent.writeExternal(element, envs)
        element.writeExternalModule(this)
        element.writeModuleFilters(moduleFilterList, inheritApplicationModuleFilters)
    }
}

private const val MIX_TEST = "mix-test"
