package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Function

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, val term: OtpErlangTuple): Node(term) {
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

        MacroStringDeclaredScope("${function.name}($patternSequenceMacroString)${guardSequenceMacroString()}", patternSequenceDeclaredScope)
    }

    private fun guardSequenceMacroString() =
            org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.guardSequenceMacroString(term)

    private fun patternSequenceMacroStringDeclaredScope() =
            org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.patternSequenceMacroStringDeclaredScope(term, Scope.EMPTY)

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes, function: Function): Clause? =
                ifTag(term, TAG) { Clause(attributes, function, it) }
    }
}
