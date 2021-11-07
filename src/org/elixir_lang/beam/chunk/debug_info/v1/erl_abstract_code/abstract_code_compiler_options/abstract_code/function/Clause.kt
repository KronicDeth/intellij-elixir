package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.toPatternSequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Function
import org.elixir_lang.beam.decompiler.Options

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, val term: OtpErlangTuple) : Node(term) {
    val head by lazy { headMacroStringDeclaredScope.macroString }

    override fun toMacroString(options: Options): String {
        val (headMacroString, headDeclaredScope) = headMacroStringDeclaredScope
        val indentedBody = if (options.decompileBodies) {
            org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.bodyMacroString(term, headDeclaredScope)
        } else {
            "# Body not decompiled due to too many definitions in module"
        }

        return "${function.macroNameArity.macro} $headMacroString do\n" +
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
            toPatternSequence(term)
                    ?.let { Sequence.toMacroStringDeclaredScope(it, function.decompiler, function.macroNameArity) }
                    ?: Sequence.unknown()

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes, function: Function): Clause? =
                ifTag(term, TAG) { Clause(attributes, function, it) }
    }
}
