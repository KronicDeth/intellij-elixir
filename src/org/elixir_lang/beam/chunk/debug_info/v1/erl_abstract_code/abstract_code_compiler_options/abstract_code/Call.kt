package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.code.Identifier.inspectAsFunction

private const val TAG = "call"

class Call(term: OtpErlangTuple): Node(term) {
    val name by lazy { (term.elementAt(2) as? OtpErlangTuple)?.let { Node.from(it) } }
    val arguments by lazy {
        (term.elementAt(3) as? OtpErlangList)?.mapNotNull { Node.from(it) } ?: emptyList()
    }

    override fun toMacroString(): String =
        "${nameToMacroString()}(${arguments.joinToString(", ") { it.toMacroString() }})"

    private fun nameToMacroString(): String {
        val name = this.name

        return when (name) {
            is Atom -> name.atom?.let { inspectAsFunction(it) } ?: "?"
            else -> name.toMacroString()
        }
    }

    companion object {
        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Call)
    }
}
