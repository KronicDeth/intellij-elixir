package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangMap
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.DebugInfo
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.ElixirErl
import org.elixir_lang.beam.chunk.inspect

fun v1(metadata: OtpErlangObject, elixirErl: ElixirErl): DebugInfo =
    when (metadata) {
        is OtpErlangTuple -> v1(metadata, elixirErl)
        else -> {
            logger.error("""
                         Dbgi :debug_info_v1 version :elixir_erl backend :elixir_v1 version metadata is not a tuple

                         ## Metdata

                         ```elixir
                         ${inspect(metadata)}
                         ```
                         """.trimIndent())

            elixirErl
        }
    }

private fun v1(metadata: OtpErlangTuple, elixirErl: ElixirErl): DebugInfo {
    val arity = metadata.arity()

    return if (arity == 3) {
        V1(elixirErl, metadata)
    } else {
        logger.error("""
                     Dbgi :debug_info_v1 version :elixir_erl backend :elixir_v1 version metadata arity (${arity}) does not match expected (3)

                     ## Metdata

                     ```elixir
                     ${inspect(metadata)}
                     ```
                     """.trimIndent())

        elixirErl
    }
}


class V1(val elixirErl: ElixirErl, val metadata: OtpErlangTuple): DebugInfo {
    val map: OtpErlangMap? by lazy { metadata.elementAt(1) as? OtpErlangMap }
    val specs: OtpErlangList? by lazy { metadata.elementAt(2) as? OtpErlangList }
}
