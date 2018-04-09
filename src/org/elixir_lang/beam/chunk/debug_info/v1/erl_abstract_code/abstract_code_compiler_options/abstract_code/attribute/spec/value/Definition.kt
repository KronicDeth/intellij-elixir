package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.value

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.BoundedFun
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Fun

object Definition {
    fun toMacroString(definition: OtpErlangObject, nameMacroString: MacroString): MacroString =
        when (definition) {
            is OtpErlangTuple -> toMacroString(definition, nameMacroString)
            else -> "$nameMacroString() :: unknown_definition"
        }

    private fun toMacroString(definition: OtpErlangTuple, nameMacroString: MacroString) =
            Type.ifTo(definition) {
                BoundedFun.ifToMacroString(definition, nameMacroString) ?:
                Fun.ifToMacroString(definition, nameMacroString) ?:
                "$nameMacroString() :: unknown_subtype"
            } ?: "$nameMacroString() :: unknown_definition"
}
