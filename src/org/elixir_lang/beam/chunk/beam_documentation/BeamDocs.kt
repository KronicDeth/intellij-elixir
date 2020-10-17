package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*

data class BeamFunctionInfo(val kind: String,
                            val name: String,
                            val arity: Int,
                            val signatures: List<String>,
                            val docs: List<BeamDoc>)

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

                    return@map BeamFunctionInfo(kind, name, arity, signatures, docs)
                }
    }

    private fun OtpErlangObject.getBinaryValueString(): String {
        return String((this as OtpErlangBinary).binaryValue())
    }

}