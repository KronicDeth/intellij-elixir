package org.elixir_lang.beam.chunk.debug_info.v1

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.DebugInfo
import org.elixir_lang.beam.chunk.debug_info.V1
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1
import org.elixir_lang.beam.chunk.inspect

fun elixirErl(v1: V1): DebugInfo {
    val metadata = v1.metadata

    val elixirErl = ElixirErl(v1)

    return when (metadata) {
        is OtpErlangTuple -> elixirErlFromMetadata(metadata, elixirErl)
        else -> {
            logger.error("""
                         Dbgi :debug_info_v1 version :elixir_erl backend metadata is not a tuple

                         ## metadata

                         ```elixir
                         ${inspect(metadata)}
                         ```
                         """.trimIndent())

            elixirErl
        }
    }
}

private fun elixirErlFromMetadata(metadata: OtpErlangTuple, elixirErl: ElixirErl): DebugInfo {
    val arity = metadata.arity()

    return if (arity > 0) {
        val tag = metadata.elementAt(0)

        elixirErlFromMetadataTag(tag, metadata, elixirErl)
    } else {
        logger.error("""
                     Dbgi :debug_info_v1 version :elixir_erl backend metadata arity ($arity) does not match (2)

                     ## metadata

                     ```elixir
                     ${inspect(metadata)}
                     ```
                     """.trimIndent())

        elixirErl
    }
}

fun elixirErlFromMetadataTag(tag: OtpErlangObject, metadata: OtpErlangObject, elixirErl: ElixirErl): DebugInfo =
    when (tag) {
        is OtpErlangAtom -> elixirErlFromMetadataTag(tag, metadata, elixirErl)
        else -> {
            logger.error(
                    """
                    Dbgi :debug_info_v1 version :elixir_erl backend metadata tag (${inspect(tag)}) is not an atom

                    ## Metadata

                    ```elixir
                    ${inspect(metadata)}
                    ```
                    """.trimIndent()
            )

            elixirErl
        }
    }

fun elixirErlFromMetadataTag(tag: OtpErlangAtom, metadata: OtpErlangObject, elixirErl: ElixirErl): DebugInfo {
    val tagAtomValue = tag.atomValue()

    return when(tagAtomValue) {
        "elixir_v1" -> v1(metadata, elixirErl)
        else -> elixirErl
    }
}

private operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
private operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)

class ElixirErl(val v1: V1): DebugInfo
