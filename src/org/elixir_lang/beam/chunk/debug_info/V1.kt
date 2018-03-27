package org.elixir_lang.beam.chunk.debug_info

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.DebugInfo
import org.elixir_lang.beam.chunk.debug_info.v1.elixirErl
import org.elixir_lang.beam.chunk.debug_info.v1.erlAbstractCode
import org.elixir_lang.beam.term.inspect

fun v1(term: Term): DebugInfo {
    val tuple = term.tuple!!
    val arity = tuple.arity()

    return if (arity == 3) {
        val (_, backend, metadata) = tuple

        v1(term, backend, metadata)
    } else {
        logger.error("""
            Dbgi tuple with :debug_info_v1 tag arity ($arity) is not 3

            ## Dbgi Tuple

            ```elixir
            ${inspect(tuple)}
            ```
            """.trimIndent())

        term
    }
}

private fun v1(term: Term, backend: OtpErlangObject, metadata: OtpErlangObject): DebugInfo {
    val v1 = V1(term, backend, metadata)

    return when (backend) {
        is OtpErlangAtom -> v1FromBackend(backend, v1)
        else -> {
            logger.error("""
                         Dbgi tuple with :debug_info_v1 tag does not have an atom for the backend (${inspect(backend)})

                         ## metadata

                         ```
                         ${inspect(metadata)}
                         ```
                         """.trimIndent())

            v1
        }
    }
}

private fun v1FromBackend(backend: OtpErlangAtom, v1: V1): DebugInfo {
    val backendAtom = backend.atomValue()

    return when (backendAtom) {
        "elixir_erl" ->
            elixirErl(v1)
        "erl_abstract_code" ->
            erlAbstractCode(v1)
        else -> {
            logger.error("""
                         Dbgi tuple with :debug_info_v1 tag has unknown backend ($backendAtom)

                         ## metadata

                         ```elixir
                         ${inspect(v1.metadata)}
                         ```
                         """.trimIndent())

            v1
        }
    }
}


open class V1(val term: Term, val backend: OtpErlangObject, val metadata: OtpErlangObject): DebugInfo {
    init {
        term.tuple!!
    }
}

private operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
private operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
private operator fun OtpErlangTuple.component3(): OtpErlangObject = this.elementAt(2)
