package org.elixir_lang.beam.chunk.elixir_documentation

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.chunk.ElixirDocumentation.Companion.doc
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.beam.term.line

/**
 * `ModuleDoc(line, null)` - `@moduledoc false`
 * `ModuleDoc(line, String)` - `@moduledoc String.t`
 */
data class ModuleDoc(val line: Int, val doc: Any?) {
    companion object {
        private val logger = Logger.getInstance(ModuleDoc::class.java)

        fun from(term: OtpErlangObject?): ModuleDoc? =
            when (term) {
                null -> null
                is OtpErlangTuple -> from(term)
                else -> {
                    logger.error("""
                                 :moduledoc value is neither a tuple nor null

                                 ## term

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """.trimIndent())

                    null
                }
            }

        private fun from(tuple: OtpErlangTuple): ModuleDoc? {
            val arity = tuple.arity()

            return if (arity == 2) {
                val line = line(tuple.elementAt(0))
                val doc = doc(tuple.elementAt(1))

                line?.let {
                    ModuleDoc(line, doc)
                }
            } else {
                logger.error("""
                             :moduledoc tuple arity ($arity) is not 2.

                             ## tuple

                             ```elixir
                             ${inspect(tuple)}
                             ```
                             """.trimIndent())

                null
            }
        }
    }
}
