package org.elixir_lang.beam.term

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.openapi.diagnostic.Logger

val logger = Logger.getInstance("#org.elixir_lang.beam.term")

fun line(term: OtpErlangObject): Int? =
        when (term) {
            is OtpErlangLong -> unsignedIntToInt(term.longValue())
            else -> {
                logger.error("""
                             Line `${term.javaClass}` is not an `OtpErlangLong`

                             ## line

                             ```elixir
                             ${inspect(term)}
                             ```
                             """)

                null
            }
        }
