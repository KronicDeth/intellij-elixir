package org.elixir_lang.debugger

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.elixir_lang.Mix
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import org.elixir_lang.mix.ensureMostSpecificSdk
import org.elixir_lang.run.fromArguments
import org.elixir_lang.run.toArguments

class Configuration(name: String, project: Project, configurationFactory: ConfigurationFactory) :
        org.elixir_lang.run.Configuration("Debugger $name", project, configurationFactory) {
    var inheritApplicationModuleFilters: Boolean = true
    var moduleFilterList: MutableList<ModuleFilter> = mutableListOf()

    override fun getProgramParameters(): String? = intellijElixirDebugArguments

    override fun setProgramParameters(value: String?) {
        intellijElixirDebugArguments = value
    }

    var erlArgumentList: MutableList<String> = mutableListOf()

    var erlArguments: String?
        get() = erlArgumentList.toArguments()
        set(arguments) = erlArgumentList.fromArguments(arguments)

    var elixirArgumentList: MutableList<String> = mutableListOf()

    var elixirArguments: String?
        get() = elixirArgumentList.toArguments()
        set(arguments) = elixirArgumentList.fromArguments(arguments)

    private var intellijElixirDebugArguments: String?
        get() = intellijElixirDebugArgumentList.toArguments()
        set(arguments) = intellijElixirDebugArgumentList.fromArguments(arguments)

    var intellijElixirDebugArgumentList: MutableList<String> = mutableListOf()

    var javaPort: Int? = null

    fun commandLine(): GeneralCommandLine {
        val workingDirectory = ensureWorkingDirectory()
        val module = ensureModule()
        val sdk = ensureMostSpecificSdk(module)
        val commandLine = Mix.commandLine(
                envs,
                workingDirectory,
                sdk,
                erlArgumentList,
                elixirArgumentList + Modules.requiresList()
        )
        commandLine.addParameter("intellij_elixir.debug")

        assert(javaPort != null) {
            "Port for Java node not set before generating Elixir debugger node command line.  " +
                    "It would not be able to communicate with Java node."
        }

        commandLine.addParameters("--debugger-port", javaPort.toString())

        enabledModuleFilterPatternList().forEach { doNotInterpretPattern ->
            commandLine.addParameters("--do-not-interpret-pattern", doNotInterpretPattern)
        }

        commandLine.addParameters(intellijElixirDebugArgumentList)

        return commandLine
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        TODO("Not Supported: This configuration is used to run the Elixir-side of the debugger.  It is temporary and not meant for user editing.")
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
            State(environment, this)

    private fun enabledModuleFilterPatternList(): List<String> {
        val inheritedPatternToModuleFilter = if (inheritApplicationModuleFilters) {
            Settings.getInstance().moduleFilters.associateBy { it.pattern }
        } else {
            emptyMap()
        }

        val configuredPatternToModuleFilter = moduleFilterList.associateBy { it.pattern }
        val inheritedPatternSet = inheritedPatternToModuleFilter.keys
        val configuredPatternSet = configuredPatternToModuleFilter.keys
        val patternList = inheritedPatternSet.union(configuredPatternSet).sorted()

        return patternList.mapNotNull { pattern ->
            val moduleFilter =
                    configuredPatternToModuleFilter[pattern]
                            ?: inheritedPatternToModuleFilter[pattern]!!

            if (moduleFilter.enabled) {
                moduleFilter.pattern
            } else {
                null
            }
        }
    }
}
