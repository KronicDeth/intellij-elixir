package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.Body
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.GuardSequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause.PatternSequence

object Clause {
    fun bodyString(clause: OtpErlangTuple, scope: Scope): String =
            toBody(clause)
                    .let { Body.toMacroStringDeclaredScope(it, scope).macroString.string }
                    .let { adjustNewLines(it, "\n  ") }

    fun guardsString(clause: OtpErlangTuple): String =
            toGuardSequence(clause)
                    ?.let { GuardSequence.guardsMacroString(it) }
                    ?: "missing_guard_sequence"

    fun guardSequenceString(clause: OtpErlangTuple): String =
            toGuardSequence(clause)
                    .let { GuardSequence.toMacroString(it) }

    fun <T> ifTo(term: OtpErlangObject, ifTrue: (OtpErlangTuple) -> T): T? = ifTag(term, TAG, ifTrue)
    fun ifToString(term: OtpErlangObject, scope: Scope): String? = ifTo(term) { toString(it, scope) }

    fun toString(clause: OtpErlangTuple, scope: Scope): String {
        val (patternSequenceMacroString, patternSequenceDeclaredScope) = patternSequenceMacroStringDeclaredScope(clause, scope)
        val guardSequenceString = guardSequenceString(clause)
        val bodyString = bodyString(clause, patternSequenceDeclaredScope)

        return "${patternSequenceMacroString.string}$guardSequenceString ->\n" +
                "  $bodyString"
    }

    private fun patternSequenceMacroStringDeclaredScope(clause: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toPatternSequence(clause)
                    .let { PatternSequence.toMacroStringDeclaredScope(it, scope) }

    private const val TAG = "clause"

    private fun toBody(clause: OtpErlangTuple): OtpErlangObject? = clause.elementAt(4)
    private fun toGuardSequence(clause: OtpErlangTuple): OtpErlangObject? = clause.elementAt(3)
    fun toPatternSequence(clause: OtpErlangTuple): OtpErlangObject? = clause.elementAt(2)
}
