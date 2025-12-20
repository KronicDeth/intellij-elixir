package org.elixir_lang.espec

import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import org.elixir_lang.tests.TestCommandLineState


class State(environment: ExecutionEnvironment, private val configuration: Configuration) :
    TestCommandLineState<Configuration>(environment, configuration) {
    override val TEST_FRAMEWORK_NAME = "ESpec"
    override fun createTestConsoleProperties(executor: Executor): TestConsoleProperties =
        TestConsoleProperties(configuration, TEST_FRAMEWORK_NAME, executor)

}
