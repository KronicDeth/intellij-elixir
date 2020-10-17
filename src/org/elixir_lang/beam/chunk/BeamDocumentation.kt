package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.*
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.Keyword
import org.elixir_lang.beam.binaryToTerm
import org.elixir_lang.beam.chunk.BeamDocumentation.Companion.from
import org.elixir_lang.beam.chunk.beam_documentation.BeamDocs
import org.elixir_lang.beam.chunk.beam_documentation.BeamDoc
import org.elixir_lang.beam.chunk.beam_documentation.BeamFunctionInfo
import org.elixir_lang.beam.chunk.beam_documentation.BeamModuleDocs
import org.elixir_lang.beam.chunk.elixir_documentation.CallbackDocs
import org.elixir_lang.beam.chunk.elixir_documentation.Docs
import org.elixir_lang.beam.chunk.elixir_documentation.TypeDocs
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.stack_frame.value.Presentation.toUtf8String

/**
 * {docs_v1,
 * Anno :: erl_anno:anno(),
 * BeamLanguage :: atom(),
 * Format :: binary(),
 * ModuleDoc :: #{DocLanguage := DocValue} | none | hidden,
 * Metadata :: map(),
 * Docs ::
 * [{{Kind, Name, Arity},
 * Anno :: erl_anno:anno(),
 * Signature :: [binary()],
 * Doc :: #{DocLanguage := DocValue} | none | hidden,
 * Metadata :: map()
 * }]} when DocLanguage :: binary(),
 * DocValue :: binary() | term()
 */

/***
 * Standardised way to store code docs in Beam files, available in Elixir since 1.11
 * Represents docs for a given module
 */

class BeamDocumentation(val keyword: OtpErlangList) {

    /** Beam Language such as Elixir, Erlang */
    val beamLanguage: String by lazy { (keyword.elementAt(2) as OtpErlangAtom).atomValue() }

    /**  List of documentation for other entities (such as functions and types) in the module.*/
    val docs: BeamDocs? by lazy { (keyword.elements()[6] as? OtpErlangList)?.let { BeamDocs(it)} }

    val moduleDocs: BeamModuleDocs? by lazy { (keyword.elementAt(4) as? OtpErlangMap)?. let { BeamModuleDocs(it) } }
    val typeDocs: TypeDocs? by lazy { TypeDocs.from(Keyword.get(keyword, "type_docs")) }

    fun fetchFunctionDocsClosest(functionName: String, arity: Int) : String? {
        val docTuple = (keyword.elements()[6] as OtpErlangList)
                .elements()
                .filterIsInstance<OtpErlangTuple>()
                .sortedByDescending{((it.elements().first() as? OtpErlangTuple)?.elements()?.get(2) as? OtpErlangLong)?.uIntValue() == arity}
                .firstOrNull{((it.elements().first() as? OtpErlangTuple)?.elements()?.get(1) as? OtpErlangAtom)?.atomValue() == functionName}
                ?: return null

        val functionDocsMap = docTuple.elements()[3] as? OtpErlangMap ?: return null
        val docsBinary = functionDocsMap.values().first() as OtpErlangBinary
        return String(docsBinary.binaryValue())
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
