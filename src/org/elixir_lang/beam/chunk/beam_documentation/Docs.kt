package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*

data class FunctionInfo(val kind: String,
                        val name: String,
                        val arity: Int,
                        val signatures: List<String>,
                        val docs: List<Doc>,
                        val metadata: List<FunctionMetadata>)

data class FunctionMetadata(val name: String, val value: String?)

data class Doc(val language: String,
               val documentationText: String)


class Docs(val docsList: OtpErlangList){
    private val functionDocs: List<FunctionInfo> by lazy { getDocs() }

    fun docsForOrSimilar(functionName: String, arity: Int) : FunctionInfo? {
        return functionDocs
                .filter { it.name == functionName }
                .maxBy { it.arity == arity }
    }

    fun getSignatures(functionName: String, arity: Int) : List<String>{
        return functionDocs
                .filter { it.name == functionName }
                .singleOrNull { it.arity == arity }
                ?.signatures ?: listOf()
    }

    fun getFunctionDocs(name: String, arity: Int): List<Doc> {
        return functionDocs
                .filter { it.name == name }
                .filter { it.arity == arity }
                .flatMap { it.docs }
    }

    fun getFunctionDocsOrSimilar(name: String, arity: Int = 0): List<Doc> {
        return functionDocs
                .filter { it.name == name }
                .sortedByDescending { it.arity == arity }
                .flatMap { it.docs }
    }

    fun getFunctionMetadataOrSimilar(name: String, arity: Int = 0): List<FunctionMetadata> {
        return functionDocs
                .filter { it.name == name }
                .sortedByDescending { it.arity == arity }
                .flatMap { it.metadata }
    }

    private fun getDocs(): List<FunctionInfo> {
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
                                Doc(it.key.getBinaryValueString(),
                                        it.value.getBinaryValueString()) }
                            ?.toList() ?: listOf()

                    val metadata = (element.elementAt(4)) as OtpErlangMap
                    val metadataList = metadata.entrySet().map { FunctionMetadata((it.key as OtpErlangAtom).atomValue(), ((it.value as? OtpErlangBinary)?.getBinaryValueString())) }

                    return@map FunctionInfo(kind, name, arity, signatures, docs, metadataList)
                }
    }

    private fun OtpErlangObject.getBinaryValueString(): String {
        return String((this as OtpErlangBinary).binaryValue())
    }

}