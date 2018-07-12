package org.elixir_lang.elixir

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner

class Runner : DefaultProgramRunner() {
    override fun getRunnerId(): String = RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
            DefaultRunExecutor.EXECUTOR_ID == executorId && profile is Configuration
}

private const val RUNNER_ID = "ElixirRunner"
