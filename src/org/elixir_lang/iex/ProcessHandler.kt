package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.openapi.util.Key

class ProcessHandler(commandLine: GeneralCommandLine) : ColoredProcessHandler(commandLine) {
    override fun coloredTextAvailable(text: String, attributes: Key<*>) {
        super.coloredTextAvailable(text.replace("\n", "\r\n"), attributes)
    }
}
