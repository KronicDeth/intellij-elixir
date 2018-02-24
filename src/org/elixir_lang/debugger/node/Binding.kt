package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

object Binding {
    private const val ARITY = 2

    private val LOGGER = Logger.getInstance(Binding::class.java)

    fun from(term: OtpErlangObject) =
            when (term) {
                is OtpErlangTuple -> from(term)
                else -> {
                    LOGGER.error("Binding ${inspect(term)} is not an OtpErlangTuple")

                    null
                }
            }

    private fun from(tuple: OtpErlangTuple): Pair<String, OtpErlangObject>? {
        val arity = tuple.arity()

        return if (arity == ARITY) {
            name(tuple)?.let { name ->
                val value = tuple.elementAt(1)

                Pair(name, value)
            }
        } else {
            LOGGER.error("Binding (${inspect(tuple)}) arity ($arity) is not $ARITY")

            null
        }
    }

    private fun name(tuple: OtpErlangTuple): String? {
        val name = tuple.elementAt(0)

        return when (name) {
            is OtpErlangAtom -> name.atomValue()
            else -> {
                LOGGER.error("name (${inspect(name)}) is not an OtpErlangAtom")

                null
            }
        }
    }
}
