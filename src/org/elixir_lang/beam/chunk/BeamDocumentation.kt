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
import org.elixir_lang.debugger.stack_frame.value.Presentation.toUtf8String

/***
 * Standardised way to store code docs in Beam files
 */
class BeamDocumentation(val keyword: OtpErlangList) {
    val callbackDocs: CallbackDocs? by lazy { CallbackDocs.from(Keyword.get(keyword, "callback_docs")) }
    val docs: Docs? by lazy { Docs.from(Keyword.get(keyword, "docs")) }
    /**
     * `null` - no `@moduledoc`
     * `ModuleDoc(line, null)` - `@moduledoc false`
     * `ModuleDoc(lin, String)` - `@moduledoc String.t`
     */
    val moduledoc: String? by lazy { fetchModuleDocs() }
    val typeDocs: TypeDocs? by lazy { TypeDocs.from(Keyword.get(keyword, "type_docs")) }

    private fun fetchModuleDocs() : String? {
        val moduleDoc = keyword.elements()[4] as OtpErlangMap
        val moduleDocBinary = moduleDoc.values()?.first() as? OtpErlangBinary
        return moduleDocBinary?.binaryValue()?.let { String(it) }
    }

    companion object {
        val logger = Logger.getInstance(BeamDocumentation::class.java)

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

        fun from(chunk: Chunk): BeamDocumentation? {
            val data = chunk.data
            var offset = 0

            val (term, termByteCount) = binaryToTerm(data, offset)
            offset += termByteCount

//            assert(offset == data.size)
            if (term is OtpErlangTuple) {
                val firstAtom = term.elements().firstOrNull() as? OtpErlangAtom ?: return null
                if (firstAtom.atomValue() != "docs_v1"){
                    return null
                }

                val list = OtpErlangList(term.elements())
                return BeamDocumentation(list)
            }
            return null
        }

    }
}
