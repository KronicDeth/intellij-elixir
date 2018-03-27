package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, term: OtpErlangTuple): Node(term) {
    private val patterns by lazy {
        (term.elementAt(2) as? OtpErlangList)?.filterIsInstance<OtpErlangTuple>()?.mapNotNull { Node.from(it) }
    }
    private val guards by lazy {
        (term.elementAt(3) as? OtpErlangList)?.filterIsInstance<OtpErlangTuple>()?.mapNotNull { Node.from(it) }
    }
    val head by lazy {
        "${function.name}(${patternsToMacroString(this.patterns)})${guardsToMacroString(this.guards)}"
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

        private fun guardsToMacroString(guards: List<Node>?): String =
                if (guards != null) {
                    if (guards.isEmpty()) {
                        ""
                    } else {
                        " when ${guards.map { it.toMacroString() }.joinToString(" and ")}"
                    }
                } else {
                    " when ?"
                }
        private fun patternsToMacroString(patterns: List<Node>?): String =
                patterns?.joinToString(", ") { pattern -> pattern.toMacroString() } ?: ""
    }
}
