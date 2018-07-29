package org.elixir_lang.mix

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunner.java
 */
class Runner : DefaultProgramRunner() {
    override fun getRunnerId(): String = RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
            DefaultRunExecutor.EXECUTOR_ID == executorId &&
                    (profile is org.elixir_lang.exunit.Configuration || profile is org.elixir_lang.mix.Configuration)
}

private const val RUNNER_ID = "MixRunner"
