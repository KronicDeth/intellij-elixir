package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "record"

class Record(term: OtpErlangTuple): Node(term) {
    val name by lazy { term.elementAt(2)?.let { Node.from(it) } }
    val recordFields by lazy { (term.elementAt(3) as? OtpErlangList)?.mapNotNull { RecordField.from(it) } }

    override fun toMacroString(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Record)
    }
}
