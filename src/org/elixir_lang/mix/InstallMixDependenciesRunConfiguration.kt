package org.elixir_lang.mix

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.mix.configuration.Factory

internal const val INSTALL_MIX_DEPS_NAME: String = "Install deps and compile "
internal const val MIX_DEPS_STATUS_NAME: String = "Mix deps"
internal const val MIX_DEPS_STATUS_ARGS: String = "deps"

internal const val COMMA: String = ","
internal val INSTALL_HEX_CLI = listOf("local.hex", "--force", "--if-missing")
internal val INSTALL_REBAR_CLI = listOf("local.rebar", "--force", "--if-missing")
internal val DEPS_GET_CLI = listOf("deps.get")
internal val DEPS_COMPILE_CLI = listOf("deps.compile")
internal val COMPLETION_MESSAGE_CLI =
    listOf("run", "--no-start", "--no-compile", "--no-deps-check", "--eval", "IO.puts(\"==== COMPLETE ====\")")
internal val INSTALL_MIX_DEPS_ARGS =
    (listOf("do") + INSTALL_HEX_CLI + COMMA + INSTALL_REBAR_CLI + COMMA + DEPS_GET_CLI + COMMA + DEPS_COMPILE_CLI + COMMA + COMPLETION_MESSAGE_CLI).toMutableList()

internal fun createInstallMixDependenciesRunConfiguration(
    project: Project,
    projectRoot: VirtualFile
): RunnerAndConfigurationSettings? {
    val module = ModuleUtilCore.findModuleForFile(projectRoot, project)
        ?: ModuleManager.getInstance(project).modules.firstOrNull()
        ?: return null

    val runManager = RunManager.getInstance(project)
    val settings = runManager.createConfiguration(INSTALL_MIX_DEPS_NAME, Factory)
    val configuration = settings.configuration as Configuration

    configuration.configurationModule.module = module
    configuration.workingDirectory = projectRoot.path
    configuration.setProgramParameters(INSTALL_MIX_DEPS_ARGS)

    return settings
}

internal fun createMixDepsStatusRunConfiguration(
    project: Project,
    projectRoot: VirtualFile
): RunnerAndConfigurationSettings? {
    val module = ModuleUtilCore.findModuleForFile(projectRoot, project)
        ?: ModuleManager.getInstance(project).modules.firstOrNull()
        ?: return null

    val runManager = RunManager.getInstance(project)
    val settings = runManager.createConfiguration(MIX_DEPS_STATUS_NAME, Factory)
    val configuration = settings.configuration as Configuration

    configuration.configurationModule.module = module
    configuration.setWorkingDirectory(projectRoot.path)
    configuration.setProgramParameters(MIX_DEPS_STATUS_ARGS)

    return settings
}
