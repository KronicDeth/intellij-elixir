package org.elixir_lang.run

import com.intellij.execution.process.KillableColoredProcessHandler

/**
 * Process handler for Elixir/Erlang processes that require double SIGINT to exit gracefully.
 *
 * The Erlang VM (BEAM) treats the first SIGINT as entering BREAK mode, and requires a second
 * SIGINT to actually exit. This handler implements that behavior using a DoubleSignalTerminator.
 */
class ElixirProcessHandler(process: Process, commandLineString: String) :
    KillableColoredProcessHandler(process, commandLineString) {

    private val terminator: DoubleSignalTerminator = ElixirDoubleSignalTerminator()

    override fun destroyProcessImpl() {
        terminator.performDoubleSignalTermination(process) {
            super.destroyProcessImpl()
        }
    }
}
