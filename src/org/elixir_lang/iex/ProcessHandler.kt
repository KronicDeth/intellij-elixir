package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.util.Key

class ProcessHandler(private val generalCommandLine: GeneralCommandLine) : KillableProcessHandler(generalCommandLine) {
    override fun startNotify() {
        notifyTextAvailable("cd ${generalCommandLine.workDirectory}\n", ProcessOutputTypes.SYSTEM)
        generalCommandLine.effectiveEnvironment.forEach { name, value ->
            notifyTextAvailable("export $name=$value\n", ProcessOutputTypes.SYSTEM)
        }
        super.startNotify()
    }

    override fun notifyTextAvailable(text: String, outputType: Key<*>) {
        super.notifyTextAvailable(text.replace("\n", "\r\n"), outputType)
    }
}
