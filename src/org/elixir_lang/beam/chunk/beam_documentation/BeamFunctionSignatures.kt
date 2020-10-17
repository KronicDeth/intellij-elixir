package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*

data class BeamFunctionSignature(val functionName: String, val arity: Int, val signatures: List<String>)

class BeamFunctionSignatures(val keyword: OtpErlangList){
//    private fun fetchSignatures() : Iterable<BeamFunctionSignature> {
////        val signatures = (keyword.elements()
////                .filterIsInstance<OtpErlangTuple>()
//////                .filter{((it.elements().first() as? OtpErlangTuple)?.elements()?.get(1) as? OtpErlangAtom)?.atomValue() == functionName}
////                .flatMap { (it.elements()[2]as OtpErlangList).elements().
////                filterIsInstance<OtpErlangBinary>().map { String(it.binaryValue()) } }
//
//        val signatures = (keyword.elements()
//                .filterIsInstance<OtpErlangTuple>()
//                .map {
//                    val functionName = ((it.elements().first() as? OtpErlangTuple)?.elements()?.get(1) as? OtpErlangAtom)?.atomValue()
//                    val
//                }
////                .filter{((it.elements().first() as? OtpErlangTuple)?.elements()?.get(1) as? OtpErlangAtom)?.atomValue() == functionName}
//                .flatMap { (it.elements()[2]as OtpErlangList).elements().
//                filterIsInstance<OtpErlangBinary>().map { String(it.binaryValue()) } }
//    }

}