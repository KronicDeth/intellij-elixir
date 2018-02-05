package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.*
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.Keyword
import org.elixir_lang.beam.binaryToTerm
import org.elixir_lang.beam.chunk.elixir_documentation.CallbackDocs
import org.elixir_lang.beam.chunk.elixir_documentation.Docs
import org.elixir_lang.beam.chunk.elixir_documentation.ModuleDoc
import org.elixir_lang.beam.chunk.elixir_documentation.TypeDocs
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.ElixirXValuePresentation.toUtf8String

class ElixirDocumentation(keyword: OtpErlangList) {
    val callbackDocs: CallbackDocs? by lazy { CallbackDocs.from(Keyword.get(keyword, "callback_docs")) }
    val docs: Docs? by lazy { Docs.from(Keyword.get(keyword, "docs")) }
    /**
     * `null` - no `@moduledoc`
     * `ModuleDoc(line, null)` - `@moduledoc false`
     * `ModuleDoc(lin, String)` - `@moduledoc String.t`
     */
    val moduledoc: ModuleDoc? by lazy { ModuleDoc.from(Keyword.get(keyword, "moduledoc")) }
    val typeDocs: TypeDocs? by lazy { TypeDocs.from(Keyword.get(keyword, "type_docs")) }

    companion object {
        val logger = Logger.getInstance(ElixirDocumentation::class.java)

        fun doc(term: OtpErlangObject): Any? =
                when (term) {
                    is OtpErlangBinary -> toUtf8String(term)
                    OtpErlangAtom("false") -> false
                    OtpErlangAtom("nil") -> null
                    else -> {
                        logger.error("""
                                     doc `${term.javaClass}` is neither an `OtpErlangBinary` nor `false`

                                     ```elixir
                                     ${inspect(term)}
                                     ```
                                     """)

                        null
                    }
                }

        fun from(chunk: Chunk): ElixirDocumentation? {
            val data = chunk.data
            var offset = 0

            val (term, termByteCount) = binaryToTerm(data, offset)
            offset += termByteCount

            assert(offset == data.size)

            return from(term)
        }

        // Private Functions

        private fun elixir_docs_v1(versionedData: OtpErlangList): ElixirDocumentation? =
            if (Keyword.isKeyword(versionedData)) {
                ElixirDocumentation(versionedData)
            } else {
                logger.error("""
                             `elixir_docs_v1` list is not `Keyword.t`

                             ## list

                             ```elixir
                             ${inspect(versionedData)}
                             ```
                             """)

                null
            }

        private fun elixir_docs_v1(versionedData: OtpErlangObject): ElixirDocumentation? =
                when (versionedData) {
                    is OtpErlangList -> elixir_docs_v1(versionedData)
                    else -> {
                        logger.error("""
                                     `elixir_docs_v1` data is not a list

                                     ## data

                                     ```elixir
                                     ${inspect(versionedData)}
                                     ```
                                     """.trimIndent())

                        null
                    }
                }

        private fun from(term: OtpErlangObject): ElixirDocumentation? =
            when (term) {
                is OtpErlangTuple -> from(term)
                else -> {
                    logger.error("""
                                 ExDc is not a tuple

                                 # term

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """.trimIndent())

                    null
                }
            }

        private fun from(tuple: OtpErlangTuple): ElixirDocumentation? {
            val arity = tuple.arity()

            return if (arity == 2) {
                val (version, versioned_data) = tuple

                if (version is OtpErlangAtom) {
                    val versionAtomValue = version.atomValue()

                    when (versionAtomValue) {
                        "elixir_docs_v1" -> elixir_docs_v1(versioned_data)
                        else -> throw TODO("Unrecognized ExDc version tag $versionAtomValue")
                    }
                } else {
                    logger.error("""
                                 ExDc 2-tuple is not tagged with an atom

                                 ## tag

                                 ```elixir
                                 ${inspect(version)}
                                 ```
                                 """.trimIndent())

                    null
                }
            } else {
                logger.error("""
                             ExDc tuple arity ($arity) is not 2.

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
