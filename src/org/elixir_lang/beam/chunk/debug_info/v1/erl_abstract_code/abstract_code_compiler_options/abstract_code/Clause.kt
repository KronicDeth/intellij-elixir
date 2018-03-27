package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, val term: OtpErlangTuple): Node(term) {
    private val patterns by lazy {
        (term.elementAt(2) as? OtpErlangList)?.filterIsInstance<OtpErlangTuple>()?.mapNotNull { Node.from(it) }
    }
    private val guardSequence by lazy {
        (term.elementAt(3) as? OtpErlangList)?.filterIsInstance<OtpErlangList>()?.mapNotNull { it.mapNotNull { Node.from(it) } }
    }
    val head by lazy {
        "${function.name}(${patternsToMacroString(this.patterns)})${guardSequenceToMacroString(this.guardSequence)}"
    }
    val body by lazy {
        (term.elementAt(4) as? OtpErlangList)?.filterIsInstance<OtpErlangTuple>()?.mapNotNull { Node.from(it) } ?:
        emptyList()
    }

    override fun toMacroString(): String =
            "def $head do\n" +
                    "  ${adjustNewLines(body.joinToString("\n") { it.toMacroString() }, "\n  ")}\n" +
                    "end"

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes, function: Function): Clause? =
                ifTag(term, TAG) { Clause(attributes, function, it) }

        private fun guardSequenceToMacroString(guardSequence: List<List<Node>>?): String =
                if (guardSequence != null) {
                    if (guardSequence.isEmpty()) {
                        ""
                    } else {
                        " when ${guardSequence.joinToString(" or ") { guard ->
                            guardToMacroString(guard)
                        }}"
                    }
                } else {
                    " when ?"
                }

        private fun guardToMacroString(guard: List<Node>): String =
                guard.joinToString(" and ") { it.toMacroString() }

        private fun patternsToMacroString(patterns: List<Node>?): String =
                patterns?.joinToString(", ") { pattern -> pattern.toMacroString() } ?: ""
    }
}
