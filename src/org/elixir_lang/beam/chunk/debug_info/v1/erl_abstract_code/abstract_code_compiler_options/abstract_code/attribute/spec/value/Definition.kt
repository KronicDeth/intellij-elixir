package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.value

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
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
            else -> AbstractCode.error("unknown_type() :: unknown_definition", "Attribute spec value definition is unknown", definition)
        }

    private fun toString(definition: OtpErlangTuple,
                         decompiler: MacroNameArity,
                         macroNameArity: org.elixir_lang.beam.MacroNameArity) =
            Type.ifTo(definition) {
                BoundedFun.ifToString(definition, decompiler, macroNameArity) ?:
                Fun.ifToString(definition, decompiler, macroNameArity) ?:
                fallback(definition, decompiler, macroNameArity, "subtype")
            } ?: fallback(definition, decompiler, macroNameArity, "definition")

    private fun fallback(definition: OtpErlangObject,
                         decompiler: MacroNameArity,
                         macroNameArity: org.elixir_lang.beam.MacroNameArity,
                         definitionSuffix: String): String {
        val definitionString = AbstractCode.unknown(definitionSuffix, "Attribute spec value definition $definitionSuffix", definition)

        return StringBuilder()
                .let { ParameterReturn.fallback(it, decompiler, macroNameArity) }
                .append(" :: ")
                .append(definitionString)
                .toString()
    }
}
