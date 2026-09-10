package org.elixir_lang.beam

import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.diagnostic.Logger
import java.util.concurrent.CancellationException

/**
 * Reports a file that IS a BEAM but whose contents this decompiler could not fully decode - an
 * opcode range or chunk version newer than it knows, a LitT chunk it could not inflate or parse.
 */
fun Logger.reportUnexpectedBeamData(message: String, cause: Throwable? = null) {
    // Wrapping the cause below would hide it from Logger, whose guard reads only the throwable it
    // is handed. Hand-rolled because neither platform predicate is callable across both legs:
    // `rethrowControlFlowException` is @Internal at 261, and `Logger.shouldRethrow` becomes
    // @Internal at 262, where CI's internal API usage check fails on it.
    if (cause is ControlFlowException || cause is CancellationException) throw cause
    warnInProduction(if (cause != null) Throwable(message, cause) else Throwable(message))
}
