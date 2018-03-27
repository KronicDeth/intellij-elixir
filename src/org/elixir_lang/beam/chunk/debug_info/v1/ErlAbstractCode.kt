package org.elixir_lang.beam.chunk.debug_info.v1

import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.DebugInfo
import org.elixir_lang.beam.chunk.debug_info.V1
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.elixir_lang.beam.term.inspect

fun erlAbstractCode(v1: V1): DebugInfo {
    val metadata = v1.metadata
    val erlAbstractCode = ErlAbstractCode(v1)

    return when (metadata) {
        is OtpErlangTuple -> erlAbstractCodeFromMetadata(metadata, erlAbstractCode)
        else -> {
            logger.error("""
                         Dbgi :debug_info_v1 version :erl_abstract_code backend metaadata is known

                         ## metadata
                         ${inspect(metadata)}
                         """.trimIndent())

            erlAbstractCode
        }
    }
}

private fun erlAbstractCodeFromMetadata(metadata: OtpErlangTuple, erlAbstractCode: ErlAbstractCode): DebugInfo {
    val arity = metadata.arity()

    return if (arity == 2) {
        val (abstractCode, compilerOptions) = metadata

        AbstractCodeCompileOptions(erlAbstractCode, abstractCode, compilerOptions)
    } else {
        logger.error("""
                     Dbgi :debug_info_v1 version :erl_abstract_code metadata arity ($arity) does not match (2)
                     """.trimIndent())

        erlAbstractCode
    }
}

class ErlAbstractCode(val v1: V1): DebugInfo
