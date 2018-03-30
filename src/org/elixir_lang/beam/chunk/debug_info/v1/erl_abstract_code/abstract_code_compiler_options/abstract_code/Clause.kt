package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.Body
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.GuardSequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.PatternSequence

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, val term: OtpErlangTuple): Node(term) {
    val head by lazy { "${function.name}(${patternSequenceMacroString()})${guardSequenceMacroString()}" }

    private fun guardSequenceMacroString() =
            toGuardSequence()
                    .let { GuardSequence.toMacroString(it) }

    private fun patternSequenceMacroString() =
            toPatternSequence()
                    .let { PatternSequence.toMacroString(it) }

    private fun toBody(): OtpErlangObject? = term.elementAt(4)
    private fun toGuardSequence(): OtpErlangObject? = term.elementAt(3)
    private fun toPatternSequence(): OtpErlangObject? = term.elementAt(2)

    override fun toMacroString(): String {
        val indentedBody =  toBody()
                .let { Body.toMacroString(it) }
                .let { adjustNewLines(it, "\n  ") }

        return "def $head do\n" +
                "  $indentedBody\n" +
                "end"
    }

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes, function: Function): Clause? =
                ifTag(term, TAG) { Clause(attributes, function, it) }
    }
}
