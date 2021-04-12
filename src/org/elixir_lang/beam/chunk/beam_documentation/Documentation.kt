package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*
import org.elixir_lang.beam.binaryToTerm
import org.elixir_lang.beam.chunk.Chunk

/**
 * Standardised way to store code docs for a given module, available in Elixir since 1.7.
 * For more information lookup EEP-48.
 */
class Documentation(private val docsList: OtpErlangList) {
    /** Beam Language such as 'elixir', 'erlang' */
    val beamLanguage: String by lazy { (docsList.elementAt(2) as OtpErlangAtom).atomValue() }

    /** format the docs are stored in i.e. 'text/markdown' */
    val format: String? by lazy { (docsList.elementAt(3) as? OtpErlangBinary)?.let{ String(it.binaryValue())} }

    /**  List of documentation for other entities (such as functions and types) in the module.*/
    val docs: Docs? by lazy { (docsList.elementAt(6) as? OtpErlangList)?.let { Docs.from(it)} }

    val moduleDocs: ModuleDocs? by lazy { (docsList.elementAt(4) as? OtpErlangMap)?. let { ModuleDocs(it) } }

    companion object {
        fun from(chunk: Chunk): Documentation? {
            val data = chunk.data
            var offset = 0

            val (term, termByteCount) = binaryToTerm(data, offset)
            offset += termByteCount

            if (term is OtpErlangTuple) {
                val firstAtom = term.elements().firstOrNull() as? OtpErlangAtom ?: return null
                if (firstAtom.atomValue() != "docs_v1"){
                    return null
                }

                val list = OtpErlangList(term.elements())
                return Documentation(list)
            }
            return null
        }

    }
}
