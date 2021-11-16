package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.`fun`.ParameterReturn
import org.elixir_lang.beam.decompiler.MacroNameArity

object Fun {
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifTo(type) { toMacroString(type) }

    fun ifToMacroString(type: OtpErlangTuple,
                        decompiler: MacroNameArity,
                        macroNameArity: org.elixir_lang.beam.MacroNameArity): MacroString? =
            ifTo(type) { toMacroString(type, decompiler, macroNameArity) }

    private const val SUBTYPE = "fun"

    private fun <T> ifTo(type: OtpErlangTuple, ifTrue: (OtpErlangTuple) -> T): T? = ifSubtypeTo(type, SUBTYPE, ifTrue)

    private fun toMacroString(type: OtpErlangTuple) = toMacroString(type) { ParameterReturn.toMacroString(it) }

    private fun toMacroString(type: OtpErlangTuple,
                              decompiler: MacroNameArity,
                              macroNameArity: org.elixir_lang.beam.MacroNameArity) =
            toMacroString(type) { ParameterReturn.toMacroString(it, decompiler, macroNameArity) }

    private fun toMacroString(
            type: OtpErlangTuple,
            parameterReturnToMacroString: (OtpErlangList) -> MacroString
    ): MacroString {
        val parameterReturn = toParameterReturn(type)

        return when (parameterReturn) {
            is OtpErlangList -> parameterReturnToMacroString(parameterReturn)
            else -> "unknown_parameter_return"
        }
    }

    private fun toParameterReturn(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
