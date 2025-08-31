package org.elixir_lang.beam.chunk.elixir_documentation

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.NameArity
import org.elixir_lang.beam.chunk.ElixirDocumentation.Companion.doc
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.beam.term.line

data class CallbackDoc(val nameArity: NameArity, val line: Int, val kind: Kind, val doc: Any?) {
    enum class Kind {
        CALLBACK,
        MACROCALLBACK;

        val attributeName: String by lazy { name.lowercase() }
        val attribute: String by lazy { "@$attributeName" }

        companion object {
            private val KIND_BY_ATTRIBUTE_NAME =
                Kind.values().associateBy { it.attributeName }

            fun from(term: OtpErlangObject): Kind? =
                when (term) {
                    is OtpErlangAtom ->
                        from(term)

                    else -> {
                        logger.error(
                            """
                                     Kind `${term.javaClass}` is not an `OtpErlangAtom`

                                     ## kind

                                     ```elixir
                                     ${inspect(term)}
                                     ```
                                     """
                        )

                        null
                    }
                }

            // Private Functions

            private fun from(atom: OtpErlangAtom): Kind? = KIND_BY_ATTRIBUTE_NAME[atom.atomValue()]
        }
    }

    companion object {
        private val logger = Logger.getInstance(CallbackDoc::class.java)

        fun from(term: OtpErlangObject): CallbackDoc? =
            when (term) {
                is OtpErlangTuple -> from(term)
                else -> {
                    logger.error(
                        """
                                 :callback_docs element is not a tuple

                                 ## element

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """.trimIndent()
                    )

                    null
                }
            }

        private fun from(tuple: OtpErlangTuple): CallbackDoc? {
            val arity = tuple.arity()

            return if (arity == 4) {
                val nameArity = NameArity.from(tuple.elementAt(0))
                val line = line(tuple.elementAt(1))
                val kind = Kind.from(tuple.elementAt(2))
                val doc = doc(tuple.elementAt(3))

                if (nameArity != null && line != null && kind != null) {
                    CallbackDoc(nameArity, line, kind, doc)
                } else {
                    null
                }
            } else {
                logger.error(
                    """
                             :callback_docs element tuple arity ($arity) is not 4

                             ```elixir
                             ${inspect(tuple)}
                             ```
                             """.trimIndent()
                )

                null
            }
        }


    }
}
