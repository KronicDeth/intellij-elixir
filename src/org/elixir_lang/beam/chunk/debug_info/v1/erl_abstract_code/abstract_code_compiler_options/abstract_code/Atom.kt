package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.term.inspect

private const val TAG = "atom"

class Atom(term: OtpErlangTuple): Node(term) {
    val atom by lazy { (term.elementAt(2) as? OtpErlangAtom) }

    override fun toMacroString(): String = atom?.let { inspect(it) } ?: "?"

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Atom)
    }
}
