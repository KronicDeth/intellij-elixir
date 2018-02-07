package org.elixir_lang.beam.chunk.elixir_documentation

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.Macro
import org.elixir_lang.beam.NameArity
import org.elixir_lang.beam.chunk.ElixirDocumentation.Companion.doc
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.beam.term.line

data class Doc(val nameArity: NameArity, val line: Int, val kind: Kind, val arguments: List<String>, val doc: Any?) {
    enum class Kind {
        DEF,
        DEFMACRO;

        val macro: String by lazy { name.toLowerCase() }

        companion object {
            private val KIND_BY_MACRO = Kind.values().associateBy { it.macro }
            private val logger = Logger.getInstance(Doc::class.java)

            fun from(term: OtpErlangObject): Kind? =
                    when (term) {
                        is OtpErlangAtom -> from(term)
                        else -> {
                            logger.error("""
                                         Kind `${term.javaClass}` is not an `OtpErlangAtom`

                                         ## kind

                                         ```elixir
                                         ${inspect(term)}
                                         ```
                                         """)

                            null
                        }
                    }

            // Private Functions

            private fun from(atom: OtpErlangAtom): Kind? = KIND_BY_MACRO[atom.atomValue()]
        }
    }

    companion object {
        private val logger = Logger.getInstance(Doc::class.java)

        fun from(term: OtpErlangObject): Doc? =
                when (term) {
                    is OtpErlangTuple -> from(term)
                    else -> {
                        logger.error("""
                                     :docs element is not a tuple

                                     ## element

                                     ```elixir
                                     ${inspect(term)}
                                     ```
                                     """)

                        null
                    }
                }

        // Private Functions

        private fun argument(term: OtpErlangObject): String = Macro.toString(term)

        private fun arguments(list: OtpErlangList): List<String> = list.map { argument(it) }

        private fun arguments(term: OtpErlangObject): List<String>? =
            when (term) {
                is OtpErlangList -> arguments(term)
                else -> {
                    logger.error("""
                                 :doc element arguments `${term.javaClass}` is not an `OtpErlangList`

                                 ## arguments

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """)

                    null
                }
            }

        private fun from(tuple: OtpErlangTuple): Doc? {
            val tupleArity = tuple.arity()

            return if (tupleArity == 5) {
                val nameArity = NameArity.from(tuple.elementAt(0))
                val line = line(tuple.elementAt(1))
                val kind = Kind.from(tuple.elementAt(2))
                val arguments = arguments(tuple.elementAt(3))
                val doc = doc(tuple.elementAt(4))

                if (nameArity != null && line != null && kind != null && arguments != null) {
                    Doc(nameArity, line, kind, arguments, doc)
                } else {
                    null
                }
            } else {
                logger.error("""
                             :docs element tuple arity ($tupleArity) is not 5.

                             ## tuple

                             ```elixir
                             ${inspect(tuple)}
                             ```
                             """)

                null
            }
        }
    }
}
