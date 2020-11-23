package org.elixir_lang.elixir

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.executeState
import com.intellij.execution.ui.RunContentDescriptor

class Runner : GenericProgramRunner<RunnerSettings>() {
    override fun getRunnerId(): String = "ElixirRunner"

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
            DefaultRunExecutor.EXECUTOR_ID == executorId &&
                    profile is org.elixir_lang.run.Configuration

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        return executeState(state, environment, this)
    }
}
