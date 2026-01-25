package org.elixir_lang.exunit

import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import org.elixir_lang.run.TestRunnerCommandLineState


class State(environment: ExecutionEnvironment, configuration: Configuration) :
    TestRunnerCommandLineState<Configuration>(environment, configuration) {
    override val TEST_FRAMEWORK_NAME = "ExUnit"

    override fun createTestConsoleProperties(executor: Executor): TestConsoleProperties =
        TestConsoleProperties(configuration, TEST_FRAMEWORK_NAME, executor)
}
