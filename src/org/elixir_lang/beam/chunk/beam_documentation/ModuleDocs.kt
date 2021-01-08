package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangMap
import com.ericsson.otp.erlang.OtpErlangObject

data class BeamModuleDoc(val language: String, val text: String)

class ModuleDocs(private val docsMap: OtpErlangMap) {
    val docsByLanguage: Iterable<BeamModuleDoc> by lazy { fetchModuleDocs() }
    val englishDocs: String? by lazy {
        docsByLanguage
            .filter { it.language == "en" }
                .map{ it.text }
                .firstOrNull ()
    }

    private fun fetchModuleDocs() : Iterable<BeamModuleDoc> {
        return docsMap
                .entrySet()
                .map { BeamModuleDoc(it.key.getBinaryValueString(), it.value.getBinaryValueString()) }
    }

    companion object{
        fun from(term: OtpErlangMap?): ModuleDocs? =
                when (term) {
                    null -> null
                    is OtpErlangList -> ModuleDocs(term)
                    else -> TODO("Module Docs term is not OtpErlangList")
                }
        }

    private fun OtpErlangObject.getBinaryValueString(): String {
        return String((this as OtpErlangBinary).binaryValue())
    }

}
