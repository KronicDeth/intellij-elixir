package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

sealed class OKErrorReason {
    companion object {
        private val LOGGER by lazy { Logger.getInstance(OKErrorReason::class.java) }

        fun from(term: OtpErlangObject): OKErrorReason? =
            when (term) {
                is OtpErlangAtom -> from(term)
                is OtpErlangTuple -> ErrorReason.from(term)
                else -> {
                    LOGGER.error("OKErrorReason (${inspect(term)}) is neither an OtpErlangAtom nor an OtpErlangTuple")

                    null
                }
            }

        private fun from(atom: OtpErlangAtom): OK? =
                when (atom.atomValue()) {
                    "ok" -> OK
                    else -> {
                        LOGGER.error("OKErrorReason OtpErlangAtom (${inspect(atom)}) is not :ok")

                        null
                    }
                }
    }
}

object OK: OKErrorReason()
data class ErrorReason(val reason: OtpErlangObject): OKErrorReason() {
    companion object {
        private const val ARITY = 2

        private val LOGGER by lazy { Logger.getInstance(ErrorReason::class.java) }

        fun from(tuple: OtpErlangTuple): ErrorReason? {
            val arity = tuple.arity()

            return if (arity == ARITY) {
                val tag = tuple.elementAt(0)

                if (tag is OtpErlangAtom) {
                    if (tag.atomValue() == "error") {
                        ErrorReason(tuple.elementAt(1))
                    } else {
                        LOGGER.error("tag (${inspect(tag)}) is not :error")

                        null
                    }
                } else {
                    LOGGER.error("tag (${inspect(tag)}) is not an OtpErlangAtom")

                    null
                }
            } else {
                LOGGER.error("ErrorReason (${inspect(tuple)}) arity ($arity) is not $ARITY")

                null
            }
        }
    }
}
