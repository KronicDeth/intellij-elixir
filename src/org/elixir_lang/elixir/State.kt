package org.elixir_lang.elixir

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment

class State(environment: ExecutionEnvironment) : CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun commandLine() {

    }

    companion object {

    }
}
