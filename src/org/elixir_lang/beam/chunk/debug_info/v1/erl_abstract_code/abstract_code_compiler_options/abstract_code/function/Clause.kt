package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.toPatternSequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Function
import org.elixir_lang.beam.decompiler.Options

private const val TAG = "clause"

class Clause(val attributes: Attributes, val function: Function, val term: OtpErlangTuple) : Node(term) {
    val head by lazy { headMacroStringDeclaredScope.macroString.string }

    override fun toMacroString(options: Options): String {
        val (headMacroString, headDeclaredScope) = headMacroStringDeclaredScope
        val prefix = "${function.macroNameArity.macro} ${headMacroString.string}"

        return if (options.decompileBodies) {
            val indentedBody = org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.bodyString(term, headDeclaredScope)

            if (options.truncateDecompiledBody(indentedBody)) {
                "$prefix, do: ..."
            } else {
                if (indentedBody.contains("\n")) {
                    "$prefix do\n" +
                            "  $indentedBody\n" +
                            "end"
                } else {
                    "$prefix, do: ${indentedBody.trimIndent()}"
                }
            }
        } else {
            "$prefix, do: ..."
        }
    }

    private val headMacroStringDeclaredScope by lazy {
        val (patternSequenceMacroString, patternSequenceDeclaredScope) = patternSequenceMacroStringDeclaredScope()

        MacroStringDeclaredScope("${patternSequenceMacroString.string}${guardSequenceString()}", doBlock = false, patternSequenceDeclaredScope)
    }

    private fun guardSequenceString(): String =
            org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Clause.guardSequenceString(term)

    private fun patternSequenceMacroStringDeclaredScope() =
            toPatternSequence(term)
                    ?.let { Sequence.toMacroStringDeclaredScope(it, function.decompiler, function.macroNameArity) }
                    ?: Sequence.unknown()

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes, function: Function): Clause? =
                ifTag(term, TAG) { Clause(attributes, function, it) }
    }
}
