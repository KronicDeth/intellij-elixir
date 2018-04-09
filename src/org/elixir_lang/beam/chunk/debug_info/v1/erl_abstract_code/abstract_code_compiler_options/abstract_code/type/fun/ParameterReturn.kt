package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.`fun`

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object ParameterReturn {
    fun toMacroString(parameterReturn: OtpErlangList): MacroString {
        val arity = parameterReturn.arity()

        return when (arity) {
            0 -> "function()"
            2 -> toMacroString(parameterReturn.elementAt(0), parameterReturn.elementAt(1))
            else -> "unknown_parameter_return_arity($arity)"
        }
    }

    fun toMacroString(parameterReturn: OtpErlangList, nameMacroString: MacroString): MacroString {
        val parameterMacroString = parameterMacroString(parameterReturn)
        val returnMacroString = returnMacroString(parameterReturn)

        return "$nameMacroString($parameterMacroString) :: $returnMacroString"
    }

    fun toMacroString(parameter: OtpErlangObject, `return`: OtpErlangObject): MacroString {
        val parameterMacroString = parameterToMacroString(parameter)
        val returnMacroString = returnToMacroString(`return`)

        val fnParameterMacroString = when (parameterMacroString) {
            "" -> "()"
            else -> parameterMacroString
        }

        return "($fnParameterMacroString -> $returnMacroString)"
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
