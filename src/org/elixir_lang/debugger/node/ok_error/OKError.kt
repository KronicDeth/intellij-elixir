package org.elixir_lang.debugger.node.ok_error

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

sealed class OKError {
    companion object {
        private val LOGGER by lazy { Logger.getInstance(OKError::class.java) }

        fun from(term: OtpErlangObject): OKError? =
            when (term) {
                is OtpErlangAtom -> from(term)
                else -> {
                    LOGGER.error("OKError (${inspect(term)}) is not an OtpErlangAtom")

                    null
                }
            }

        private fun from(atom: OtpErlangAtom): OKError? =
                when (atom.atomValue()) {
                    "ok" -> OK
                    "error" -> Error
                    else -> {
                        LOGGER.error("OKError OtpErlangAtom (${inspect(atom)}) is not :ok or :error")

                        null
                    }
                }
    }
}

object OK: OKError()
object Error: OKError()
