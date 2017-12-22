package org.elixir_lang.mix.runner.exunit

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Level
import org.elixir_lang.file.LevelPropertyPusher.level
import org.elixir_lang.jps.builder.ParametersList
import org.elixir_lang.mix.runner.MixRunConfigurationBase
import org.elixir_lang.sdk.elixir.Type.mostSpecificSdk

class MixExUnitRunConfiguration internal constructor(name: String, project: Project) :
        MixRunConfigurationBase(name, project, MixExUnitRunConfigurationFactory.getInstance()) {
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return MixExUnitRunConfigurationEditorForm()
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return MixExUnitRunningState(environment, this)
    }

    override fun mixParametersList(): ParametersList {
        val superParametersList = super.mixParametersList()
        val parametersList = ParametersList()

        parametersList.add(task())

        parametersList.addAll("--formatter", "TeamCityExUnitFormatter")
        parametersList.addAll(superParametersList.parameters)

        return parametersList
    }

    private fun level(): Level = level(sdk())

    private fun sdk(): Sdk? {
        val module = configurationModule.module

        return if (module != null) {
            mostSpecificSdk(module)
        } else {
            mostSpecificSdk(project)
        }
    }

    private fun task(): String {
        return if (level() >= Level.V_1_4) {
            "test"
        } else {
            "test_with_formatter"
        }
    }
}
