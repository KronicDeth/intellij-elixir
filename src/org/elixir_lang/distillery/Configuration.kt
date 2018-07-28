package org.elixir_lang.distillery

import com.intellij.execution.Executor
import com.intellij.execution.ExternalizablePath
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.options.SettingsEditorGroup
import com.intellij.openapi.project.Project
import com.intellij.util.io.exists
import org.elixir_lang.Distillery
import org.elixir_lang.debugger.Modules
import org.elixir_lang.debugger.configuration.Debuggable
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import org.elixir_lang.distillery.configuration.editor.CodeLoadingMode
import org.elixir_lang.run.*
import org.jdom.Element
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class Configuration(name: String, project: Project, configurationFactory: ConfigurationFactory) :
        org.elixir_lang.run.Configuration(name, project, configurationFactory),
        Debuggable<Configuration>,
        RunConfigurationWithSuppressedDefaultRunAction,
        RunConfigurationWithSuppressedDefaultDebugAction {
    override val cookie: String?
        get() = vmArgsPath?.let(::vmArgsPathToCookie)

    override var inheritApplicationModuleFilters: Boolean = true
    override var moduleFilterList: MutableList<ModuleFilter> = mutableListOf()

    override val nodeName: String?
        get() = vmArgsPath?.let(::vmArgsPathToNodeName)

    override fun debuggedConfiguration(name: String, cookie: String): Configuration {
        val debugged = Configuration(name, project, factory!!)

        debugged.workingDirectory = workingDirectory
        debugged.isPassParentEnvs = isPassParentEnvs

        val envs: MutableMap<String, String> = mutableMapOf()
        envs.putAll(this.envs)
        envs.compute(ERL_OPTS) { _, current ->
            val erlArgumentList=  listOfNotNull(current) + Modules.erlArgumentList()

            erlArgumentList.joinToString(" ")
        }
        // shows a full `erlexec` call done by
        envs[VERBOSE] = "true"
        debugged.envs = envs

        debugged.configurationModule.module = configurationModule.module

        // options not stored in `envs`
        debugged.wantsPTY = wantsPTY
        debugged.releaseCLIURL = releaseCLIURL
        debugged.releaseCLIArgumentList = releaseCLIArgumentList

        return debugged
    }

    var wantsPTY: Boolean = false
    val pty
      get() = wantsPTY || needsPTY

    var releaseCLIPath: String?
        get() = ExternalizablePath.localPathValue(releaseCLIURL)
        set(localPath) {
            releaseCLIURL = ExternalizablePath.urlValue(localPath)
        }

    override fun getProgramParameters(): String? = releaseCLIArguments

    override fun setProgramParameters(value: String?) {
        releaseCLIArguments = value
    }

    var releaseCLIArgumentList: MutableList<String> = mutableListOf()

    var releaseCLIArguments: String?
        get() = releaseCLIArgumentList.toArguments()
        set(arguments) = releaseCLIArgumentList.fromArguments(arguments)

    var erlArguments: String?
        get() = _envs[ERL_OPTS]
        set(erlArguments) {
            if (erlArguments.isNullOrBlank()) {
                _envs.remove(ERL_OPTS)
            } else {
                _envs[ERL_OPTS] = erlArguments!!
            }
        }

    var extraArguments: String?
        get() = _envs[EXTRA_OPTS]
        set(extraArguments) {
            if (extraArguments.isNullOrBlank()) {
                _envs.remove(EXTRA_OPTS)
            } else {
                _envs[EXTRA_OPTS] = extraArguments!!
            }
        }

    var codeLoadingMode: CodeLoadingMode?
        get() = _envs[CODE_LOADING_MODE]?.let { CodeLoadingMode.valueOf(it.toUpperCase()) }
        set(codeLoadingMode) {
            if (codeLoadingMode == null) {
                _envs.remove(CODE_LOADING_MODE)
            } else {
                _envs[CODE_LOADING_MODE] = codeLoadingMode.toString()
            }
        }

    var logDirectory: String?
        get() = _envs[RUNNER_LOG_DIR]
        set(logDirectory) {
            if (logDirectory.isNullOrBlank()) {
                _envs.remove(RUNNER_LOG_DIR)
            } else {
                _envs[RUNNER_LOG_DIR] = logDirectory!!
            }
        }

    var replaceOSVars: Boolean?
        get() = _envs[REPLACE_OS_VARS]?.toBoolean() ?: DEFAULT_REPLACE_OS_VARS
        set(replaceOSVars) {
            if (replaceOSVars != DEFAULT_REPLACE_OS_VARS) {
                _envs[REPLACE_OS_VARS] = replaceOSVars.toString()
            } else {
                _envs.remove(REPLACE_OS_VARS)
            }
        }

    var sysConfigPath: String?
        get() = _envs[SYS_CONFIG_PATH]
        set(sysConfigPath) {
            if (sysConfigPath.isNullOrBlank()) {
                _envs.remove(SYS_CONFIG_PATH)
            } else {
                _envs[SYS_CONFIG_PATH] = sysConfigPath!!
            }
        }

    var releaseConfigDirectory: String?
        get() = _envs[RELEASE_CONFIG_DIR]
        set(releaseConfigDirectory) {
            if (releaseConfigDirectory.isNullOrBlank()) {
                _envs.remove(RELEASE_CONFIG_DIR)
            } else {
                _envs[RELEASE_CONFIG_DIR] = releaseConfigDirectory!!
            }
        }

    var pipeDirectory: String?
        get() = _envs[PIPE_DIR]
        set(pipeDirectory) {
            if (pipeDirectory.isNullOrBlank()) {
                _envs.remove(PIPE_DIR)
            } else {
                _envs[PIPE_DIR] = pipeDirectory!!
            }
        }

    fun commandLine(): GeneralCommandLine {
        val workingDirectory = ensureWorkingDirectory()
        val commandLine = Distillery.commandLine(
                pty = pty,
                environment = envs,
                workingDirectory = workingDirectory,
                exePath = releaseCLIPath!!
        )
        commandLine.addParameters(releaseCLIArgumentList)

        return commandLine
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
            SettingsEditorGroup<Configuration>().apply {
                this.addEditor("Configuration", org.elixir_lang.distillery.configuration.Editor())
                this.addEditor("Interpreted Modules", org.elixir_lang.debugger.configuration.interpreted_modules.Editor<Configuration>())
            }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): State =
            State(environment, this)

    override fun readExternal(element: Element) {
        super.readExternal(element)

        releaseCLIURL = element.readExternalURL(RELEASE_EXECUTABLE)
        wantsPTY = element.readExternalWantsPTY(RELEASE_EXECUTABLE)
        element.readExternalArgumentList(RELEASE_EXECUTABLE, releaseCLIArgumentList)

        workingDirectoryURL = element.readExternalWorkingDirectory()
        EnvironmentVariablesComponent.readExternal(element, envs)
        element.readExternalModule(this)
        element.readModuleFilters(moduleFilterList) { inheritApplicationModuleFilters ->
            this.inheritApplicationModuleFilters = inheritApplicationModuleFilters
        }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)

        element.writeExternalArgumentList(RELEASE_EXECUTABLE, releaseCLIArgumentList)
        element.writeExternalURL(RELEASE_EXECUTABLE, releaseCLIURL)
        element.writeExternalWantsPTY(RELEASE_EXECUTABLE, wantsPTY)

        element.writeExternalWorkingDirectory(workingDirectoryURL)
        EnvironmentVariablesComponent.writeExternal(element, envs)
        element.writeExternalModule(this)
        element.writeModuleFilters(moduleFilterList, inheritApplicationModuleFilters)
    }

    private val needsPTY
            get() = releaseCLIArgumentList.needsPTY

    private var releaseCLIURL: String? = null

    private val vmArgsPath: String?
        get() = releaseCLIPath?.let {
            val releasesPath = Paths.get(it).parent.parent.resolve("releases")

            releasesPath
                    .resolve("start_erl.data")
                    .toGroupValue(START_ERL_DATA_REGEX, START_ERL_DATA_REGEX_RELEASE_INDEX)
                    ?.let { release ->
                        val vmArgsPath = releasesPath.resolve(release).resolve("vm.args")

                        if (vmArgsPath.exists()) {
                            vmArgsPath.toString()
                        } else {
                            null
                        }
                    }
        }
}

private const val CODE_LOADING_MODE = "CODE_LOADING_MODE"
private const val ERL_OPTS = "ERL_OPTS"
private const val EXTRA_OPTS = "EXTRA_OPTS"
private const val NAME = "NAME"
private const val PIPE_DIR = "PIPE_DIR"
private const val RELEASE_CONFIG_DIR = "RELEASE_CONFIG_DIR"
private const val RUNNER_LOG_DIR = "RUNNER_LOG_DIR"
private const val SYS_CONFIG_PATH = "SYS_CONFIG_PATH"
private const val VERBOSE = "VERBOSE"

private const val REPLACE_OS_VARS = "REPLACE_OS_VARS"
private const val DEFAULT_REPLACE_OS_VARS = false

private val List<String>.needsPTY: Boolean
    get() = firstOrNull()?.let { task -> task in NEED_PTY_TASKS } ?: false

private val NEED_PTY_TASKS = setOf("attach", "console", "console_boot", "console_clean", "remote_console")
private const val RELEASE_EXECUTABLE = "release-executable"
private const val WANTS_PTY = "wants-pty"

private val START_ERL_DATA_REGEX = Regex("([^ ]+) ([^ ]+)")
private const val START_ERL_DATA_REGEX_RELEASE_INDEX = 2

private val SETCOOKIE_REGEX = Regex("-setcookie ([^ ]+)")

private fun vmArgsPathToCookie(vmArgsPath: String): String? = pathToGroupValue(vmArgsPath, SETCOOKIE_REGEX, 1)

private val NAME_REGEX = Regex("-name ([^ ]+)")

private fun vmArgsPathToNodeName(vmArgsPath: String): String? = pathToGroupValue(vmArgsPath, NAME_REGEX, 1)

private fun Path.toGroupValue(regex: Regex, groupIndex: Int): String? = toFile().toGroupValue(regex, groupIndex)

private fun pathToGroupValue(path: String, regex: Regex, groupIndex: Int): String? =
        File(path).toGroupValue(regex, groupIndex)

private fun File.toGroupValue(regex: Regex, groupIndex: Int): String? =
        if (exists()) {
            bufferedReader()
                    .lineSequence()
                    .mapNotNull { line ->
                        regex
                                .matchEntire(line)
                                ?.let { it.groupValues[groupIndex] }
                    }
                    .firstOrNull()
        } else {
            null
        }

private fun Element.writeExternalWantsPTY(childName: String, wantsPTY: Boolean) =
        ensureChild(childName).setAttribute(WANTS_PTY, wantsPTY.toString())

private fun Element.readExternalWantsPTY(childName: String): Boolean =
        getChild(childName)?.getAttributeValue(WANTS_PTY)?.toBoolean() ?: false
