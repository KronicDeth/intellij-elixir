package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.openapi.util.Key

class ProcessHandler(commandLine: GeneralCommandLine) : KillableProcessHandler(commandLine) {
    override fun notifyTextAvailable(text: String, outputType: Key<*>) {
        super.notifyTextAvailable(text.replace("\n", "\r\n"), outputType)
    }
}
