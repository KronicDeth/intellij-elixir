package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.*
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.stack_frame.value.Presentation.toUtf8String

class FileLine(val file: String, val line: Int) {
    operator fun component1(): String = file
    operator fun component2(): Int = line

    companion object {
        private const val ARITY = 2

        private val LOGGER = Logger.getInstance(FileLine::class.java)

        fun from(term: OtpErlangObject): FileLine? =
                when (term) {
                    is OtpErlangTuple -> from(term)
                    else -> {
                        LOGGER.error("FileLine (${inspect(term)}) is not an OtpErlangTuple")

                        null
                    }
                }

        private fun file(tuple: OtpErlangTuple): String? {
            val file = tuple.elementAt(0)

            return when (file) {
                is OtpErlangBinary -> toUtf8String(file)
                is OtpErlangString -> file.stringValue()
                else -> {
                    LOGGER.error("File (${inspect(file)}) cannot be converted to UTF-8 String")

                    null
                }
            }
        }

        private fun from(tuple: OtpErlangTuple): FileLine? {
            val arity = tuple.arity()

            return if (arity == ARITY) {
                file(tuple)?.let { file ->
                    line(tuple)?.let { line ->
                        FileLine(file, line)
                    }
                }
            } else {
                LOGGER.error("FileLine (${inspect(tuple)}) arity ($arity) is not $ARITY")

                null
            }
        }

        private fun line(tuple: OtpErlangTuple): Int? {
            val line = tuple.elementAt(1)

            return when (line) {
                is OtpErlangLong -> line.intValue()
                else -> {
                    LOGGER.error("Line (${inspect(line)}) cannot be converted to an Int")

                    null
                }
            }
        }
    }
}
