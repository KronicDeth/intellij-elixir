package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.code.Identifier.inspectAsFunction

private const val TAG = "remote"

class Remote(term: OtpErlangTuple): Node(term) {
    val module by lazy { (term.elementAt(2) as? OtpErlangTuple)?.let { Node.from(it) } }
    val name by lazy { (term.elementAt(3) as? OtpErlangTuple)?.let { Node.from(it) } }

    override fun toMacroString(): String = "${moduleMacroString()}.${nameMacroString()}"

    private fun moduleMacroString(): String = (module as Atom).atom?.let { inspect(it) } ?: "?"
    private fun nameMacroString(): String = (name as Atom).atom?.let { inspectAsFunction(it) } ?: "?"

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Remote)
    }
}
