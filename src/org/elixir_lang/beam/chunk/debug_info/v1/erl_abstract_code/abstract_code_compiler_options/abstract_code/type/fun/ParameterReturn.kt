package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.`fun`

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object ParameterReturn {
    fun toMacroString(parameterReturn: OtpErlangList, nameMacroString: MacroString): MacroString {
        val parameterMacroString = parameterMacroString(parameterReturn)
        val returnMacroString = returnMacroString(parameterReturn)

        return "$nameMacroString($parameterMacroString) :: $returnMacroString"
    }

    private fun parameterMacroString(parameterReturn: OtpErlangList) =
            toParameter(parameterReturn)
                    ?.let { parameterToMacroString(it) }
                    ?: "missing_parameter"

    private fun parameterToMacroString(parameter: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(parameter, Scope.EMPTY).macroString

    private fun returnMacroString(parameterReturn: OtpErlangList) =
            toReturn(parameterReturn)
                    ?.let { returnToMacroString(it) }
                    ?: "missing_return"

    private fun returnToMacroString(return_: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(return_, Scope.EMPTY).macroString

    private fun toParameter(parameterReturn: OtpErlangList): OtpErlangObject? = parameterReturn.elementAt(0)
    private fun toReturn(parameterReturn: OtpErlangList): OtpErlangObject? = parameterReturn.elementAt(1)
}
