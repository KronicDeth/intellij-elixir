package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

object Level {
    fun from(term: OtpErlangObject): Int? =
            when (term) {
                is OtpErlangLong -> from(term)
                else -> {
                    LOGGER.error("Level (${inspect(term)}) is not an OtpErlangLong")

                    null
                }
            }

    private fun from(term: OtpErlangLong): Int? = term.intValue()

    private val LOGGER = Logger.getInstance(Level::class.java)
}
