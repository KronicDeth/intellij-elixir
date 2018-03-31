package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.Body
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.GuardSequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.PatternSequence

object Clause {
    fun bodyMacroString(clause: OtpErlangTuple): String =
            toBody(clause)
                    .let { Body.toMacroString(it) }
                    .let { adjustNewLines(it, "\n  ") }

    fun guardSequenceMacroString(clause: OtpErlangTuple): String =
            toGuardSequence(clause)
                    .let { GuardSequence.toMacroString(it) }

    fun ifToMacroString(term: OtpErlangObject): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(clause: OtpErlangTuple): String {
        val patternSequenceMacroString = patternSequenceMacroString(clause)
        val guardSequenceMacroString = guardSequenceMacroString(clause)
        val bodyMacroString = bodyMacroString(clause)

        return "$patternSequenceMacroString$guardSequenceMacroString ->\n" +
                "  $bodyMacroString"
    }

    fun patternSequenceMacroString(clause: OtpErlangTuple): String =
            toPatternSequence(clause)
                    .let { PatternSequence.toMacroString(it) }

    private const val TAG = "clause"

    private fun toBody(clause: OtpErlangTuple): OtpErlangObject? = clause.elementAt(4)
    private fun toGuardSequence(clause: OtpErlangTuple): OtpErlangObject? = clause.elementAt(3)
    private fun toPatternSequence(clause: OtpErlangTuple): OtpErlangObject? = clause.elementAt(2)
}
