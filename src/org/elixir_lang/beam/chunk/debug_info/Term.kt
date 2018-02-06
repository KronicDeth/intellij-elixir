package org.elixir_lang.beam.chunk.debug_info

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.binaryToTerm
import org.elixir_lang.beam.chunk.Chunk
import org.elixir_lang.beam.chunk.DebugInfo
import org.elixir_lang.beam.term.inspect

val logger = Logger.getInstance(Term::class.java)

fun from(chunk: Chunk): DebugInfo {
    val data = chunk.data
    var offset = 0

    val (term, termByteCount) = binaryToTerm(data, offset)
    offset += termByteCount

    val dataSize = data.size
    assert(termByteCount == dataSize) {
        "Expected Dbgi binary_to_term binary (size $termByteCount) to fill the whole chunk (size $dataSize)"
    }

    val debugInfo = Term(term)

    return when (term) {
        is OtpErlangTuple -> fromTuple(term, debugInfo)
        else -> {
            logger.error("""
                        Dbgi term is not a tuple

                        ## Dbgi Term

                        ```elixir
                        ${inspect(term)}
                        ```
                        """.trimIndent())

            Term(term)
        }
    }
}


private fun fromTuple(tuple: OtpErlangTuple, term: Term): DebugInfo =
        if (tuple.arity() > 0) {
            fromTupleTag(tuple.elementAt(0), term)
        } else {
            logger.error("Dbgi is an empty tuple")

            term
        }

private fun fromTupleTag(tag: OtpErlangObject, term: Term): DebugInfo =
        when (tag) {
            is OtpErlangAtom -> {
                val atom = tag.atomValue()

                when (atom) {
                    "debug_info_v1" -> org.elixir_lang.beam.chunk.debug_info.v1(term)
                    else -> {
                        logger.error("""
                                    Dbgi tuple tag (${inspect(tag)}) is unrecognized

                                    ## Dbgi Tuple

                                    ```elixir
                                    ${inspect(term.tuple!!)}
                                    ```
                                    """.trimIndent())

                        term
                    }
                }
            }
            else -> {
                logger.error("""
                        Dbgi tuple tag (${inspect(tag)}) is not an atom

                        ## Dbgi Tuple

                        ```elixir
                        ${inspect(term.tuple!!)}
                        ```
                        """.trimIndent())

                term
            }
        }

class Term(val term: OtpErlangObject): DebugInfo {
    val tuple: OtpErlangTuple? = term as? OtpErlangTuple
}
