package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.toPatternSequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Function

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, val term: OtpErlangTuple) : Node(term) {
    val head by lazy { headMacroStringDeclaredScope.macroString }

    override fun toMacroString(): String {
        val (headMacroString, headDeclaredScope) = headMacroStringDeclaredScope
        val indentedBody = org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.bodyMacroString(term, headDeclaredScope)

        return "def $headMacroString do\n" +
                "  $indentedBody\n" +
                "end"
    }

    private val headMacroStringDeclaredScope by lazy {
        val (patternSequenceMacroString, patternSequenceDeclaredScope) = patternSequenceMacroStringDeclaredScope()

        MacroStringDeclaredScope("${patternSequenceMacroString}${guardSequenceMacroString()}", patternSequenceDeclaredScope)
    }

    private fun guardSequenceMacroString() =
            org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.guardSequenceMacroString(term)

    private fun patternSequenceMacroStringDeclaredScope() =
        when (val patternSequence = toPatternSequence(term)) {
            is OtpErlangList -> patternSequenceMacroStringDeclaredScope(patternSequence)
            else -> MacroStringDeclaredScope("unknown_sequence", Scope.EMPTY)
        }

    private fun patternSequenceMacroStringDeclaredScope(term: OtpErlangList): MacroStringDeclaredScope =
            Sequence.toMacroStringDeclaredScope(term, Scope.EMPTY.copy(pinning = true)) { macroStringList ->
                val macroStringBuilder = StringBuilder()
                val macroNameArity = function.macroNameArity

                function.decompiler.appendSignature(macroStringBuilder, macroNameArity, macroNameArity.name, macroStringList.toTypedArray())

                macroStringBuilder.toString()
            }

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes, function: Function): Clause? =
                ifTag(term, TAG) { Clause(attributes, function, it) }
    }
}
