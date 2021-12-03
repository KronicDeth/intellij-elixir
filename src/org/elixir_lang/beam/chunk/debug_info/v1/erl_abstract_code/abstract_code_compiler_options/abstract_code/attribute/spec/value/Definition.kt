package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.value

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.BoundedFun
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Fun
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.`fun`.ParameterReturn
import org.elixir_lang.beam.decompiler.MacroNameArity

object Definition {
    fun toString(definition: OtpErlangObject,
                 decompiler: MacroNameArity,
                 macroNameArity: org.elixir_lang.beam.MacroNameArity): String =
        when (definition) {
            is OtpErlangTuple -> toString(definition, decompiler, macroNameArity)
            else -> "unknown_type() :: unknown_definition"
        }

    private fun toString(definition: OtpErlangTuple,
                         decompiler: MacroNameArity,
                         macroNameArity: org.elixir_lang.beam.MacroNameArity) =
            Type.ifTo(definition) {
                BoundedFun.ifToString(definition, decompiler, macroNameArity) ?:
                Fun.ifToString(definition, decompiler, macroNameArity) ?:
                fallback(decompiler, macroNameArity, "unknown_subtype")
            } ?: fallback(decompiler, macroNameArity, "unknown_definition")

    private fun fallback(decompiler: MacroNameArity,
                         macroNameArity: org.elixir_lang.beam.MacroNameArity,
                         definitionString: String): String =
        StringBuilder()
                .let { ParameterReturn.fallback(it, decompiler, macroNameArity) }
                .append(" :: ")
                .append(definitionString)
                .toString()
}
