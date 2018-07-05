package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

object Bindings {
    private val LOGGER = Logger.getInstance(Bindings.javaClass)

    fun from(term: OtpErlangObject): List<Binding>? =
        when (term) {
            is OtpErlangList -> from(term)
            else -> {
                LOGGER.error("Bindings (${inspect(term)}) is not an OtpErlangList")

                null
            }
        }

    private fun from(list: OtpErlangList) =
            list
                    .mapNotNull { Binding.from(it) }
                    .toList()
}
