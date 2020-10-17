package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*

data class BeamFunctionInfo(val kind: String,
                            val name: String,
                            val arity: Int,
                            val signatures: List<String>,
                            val docs: List<BeamDoc>,
                            val metadata: List<BeamFunctionMetadata>)

data class BeamFunctionMetadata(val name: String, val value: String?)

data class BeamDoc(val language: String,
                   val format: String,
                   val documentationText: String)

class BeamDocs(val docsList: OtpErlangList){
    val functionDocs: List<BeamFunctionInfo> by lazy { fetchDocs() }

    fun docsForOrSimilar(functionName: String, arity: Int) : BeamFunctionInfo? {
        return functionDocs
                .filter { it.name == functionName }
                .sortedBy { it.arity == arity }
                .firstOrNull()
    }

    fun getSignatures(functionName: String, arity: Int) : List<String>{
        return functionDocs
                .filter { it.name == functionName }
                .singleOrNull { it.arity == arity }
                ?.signatures ?: listOf()
    }

    fun getFunctionDocs(name: String, arity: Int): List<BeamDoc> {
        return functionDocs
                .filter { it.name == name }
                .filter { it.arity == arity }
                .flatMap { it.docs }
    }

    fun getFunctionDocsOrSimilar(name: String, arity: Int = 0): List<BeamDoc> {
        return functionDocs
                .filter { it.name == name }
                .sortedByDescending { it.arity == arity }
                .flatMap { it.docs }
    }

    fun getFunctionMetadataOrSimilar(name: String, arity: Int = 0): List<BeamFunctionMetadata> {
        return functionDocs
                .filter { it.name == name }
                .sortedByDescending { it.arity == arity }
                .flatMap { it.metadata }
    }



    /** Get only markdown formatted docs */
    fun getMarkdownFunctionDocs(name: String, arity: Int): List<BeamDoc> {
        return functionDocs
                .filter { it.name == name }
                //TODO: filter by format
                .filter { it.arity == arity }
                .flatMap { it.docs }
    }


    private fun fetchDocs(): List<BeamFunctionInfo> {
        return docsList.elements()
                .filterIsInstance<OtpErlangTuple>()
                .map { element ->
                    val firstTuple = element.elementAt(0) as OtpErlangTuple
                    val kind = ((firstTuple).elementAt(0) as OtpErlangAtom).atomValue()
                    val name = ((firstTuple).elementAt(1) as OtpErlangAtom).atomValue()
                    val arity = ((firstTuple).elementAt(2) as OtpErlangLong).uIntValue()

                    val signatures = (element.elementAt(2) as OtpErlangList)
                            .filterIsInstance<OtpErlangBinary>()
                            .map { String(it.binaryValue()) }
                            .toList()
                    val docs = (element.elementAt(3) as? OtpErlangMap)
                            ?.entrySet()
                            ?.map {
                                BeamDoc(it.key.getBinaryValueString(),
                                        "markdown",
                                        it.value.getBinaryValueString()) }
                            ?.toList() ?: listOf()

                    val metadata = (element.elementAt(4)) as OtpErlangMap
                    val metadataList = metadata.entrySet().map { BeamFunctionMetadata((it.key as OtpErlangAtom).atomValue(), ((it.value as? OtpErlangBinary)?.getBinaryValueString())) }

                    return@map BeamFunctionInfo(kind, name, arity, signatures, docs, metadataList)
                }
    }

    private fun OtpErlangObject.getBinaryValueString(): String {
        return String((this as OtpErlangBinary).binaryValue())
    }

}