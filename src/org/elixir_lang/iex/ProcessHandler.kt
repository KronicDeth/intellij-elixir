package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.util.Key
import org.elixir_lang.run.DoubleSignalTerminator
import org.elixir_lang.run.ElixirDoubleSignalTerminator

class ProcessHandler : KillableProcessHandler {
    private val generalCommandLine: GeneralCommandLine
    private val terminator: DoubleSignalTerminator = ElixirDoubleSignalTerminator()

    // Original constructor - creates process from GeneralCommandLine
    constructor(generalCommandLine: GeneralCommandLine) : super(generalCommandLine) {
        this.generalCommandLine = generalCommandLine
    }

    // New constructor - accepts already-created process (for WSL-safe creation)
    constructor(process: Process, commandLineString: String, generalCommandLine: GeneralCommandLine)
        : super(process, commandLineString) {
        this.generalCommandLine = generalCommandLine
    }

    override fun startNotify() {
        notifyTextAvailable("cd ${generalCommandLine.workDirectory}\n", ProcessOutputTypes.SYSTEM)
        generalCommandLine.effectiveEnvironment.forEach { (name, value) ->
            notifyTextAvailable("export $name=$value\n", ProcessOutputTypes.SYSTEM)
        }
        super.startNotify()
    }

    override fun notifyTextAvailable(text: String, outputType: Key<*>) {
        super.notifyTextAvailable(text.replace("\n", "\r\n"), outputType)
    }

    override fun destroyProcessImpl() {
        terminator.performDoubleSignalTermination(process) {
            super.destroyProcessImpl()
        }
    }
}
