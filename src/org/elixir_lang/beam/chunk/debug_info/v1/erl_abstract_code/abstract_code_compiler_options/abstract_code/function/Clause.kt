package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attributes
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Function
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Node

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, val term: OtpErlangTuple): Node(term) {
    val head by lazy { "${function.name}(${patternSequenceMacroString()})${guardSequenceMacroString()}" }

    private fun guardSequenceMacroString() =
            org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.guardSequenceMacroString(term)

    private fun patternSequenceMacroString() =
            org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.patternSequenceMacroString(term)

    override fun toMacroString(): String {
        val indentedBody = org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.bodyMacroString(term)

        return "def $head do\n" +
                "  $indentedBody\n" +
                "end"
    }

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes, function: Function): Clause? =
                ifTag(term, TAG) { Clause(attributes, function, it) }
    }
}
