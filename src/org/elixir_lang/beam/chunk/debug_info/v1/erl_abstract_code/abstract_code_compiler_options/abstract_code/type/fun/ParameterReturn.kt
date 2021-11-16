package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.`fun`

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Product
import org.elixir_lang.beam.decompiler.Default
import org.elixir_lang.beam.decompiler.MacroNameArity

object ParameterReturn {
    fun toMacroString(parameterReturn: OtpErlangList): MacroString {
        val arity = parameterReturn.arity()

        return when (arity) {
            0 -> "function()"
            2 -> toMacroString(parameterReturn.elementAt(0), parameterReturn.elementAt(1))
            else -> "unknown_parameter_return_arity($arity)"
        }
    }

    fun toMacroString(parameterReturn: OtpErlangList,
                      decompiler: MacroNameArity,
                      macroNameArity: org.elixir_lang.beam.MacroNameArity): MacroString {
        val nameParameterString = parameterMacroString(parameterReturn, decompiler, macroNameArity)
        val returnMacroString = returnMacroString(parameterReturn)

        return "$nameParameterString :: $returnMacroString"
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

    private fun parameterMacroString(parameterReturn: OtpErlangList,
                                     decompiler: MacroNameArity,
                                     macroNameArity: org.elixir_lang.beam.MacroNameArity) =
            toParameter(parameterReturn)
                    ?.let { parameterToMacroString(it, decompiler, macroNameArity) }
                    ?: "missing_parameter"

    private fun parameterToMacroString(parameter: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(parameter, Scope.EMPTY).macroString

    private fun parameterToMacroString(parameter: OtpErlangObject,
                                       decompiler: MacroNameArity,
                                       macroNameArity: org.elixir_lang.beam.MacroNameArity): MacroString =
        Type.ifTo(parameter) { type ->
            Product.ifTo(type) {
                Product.toOperands(type)
                        ?.let { Sequence.toMacroStringDeclaredScope(it, decompiler, macroNameArity) }
                        ?.macroString
            }
        } ?: fallback(decompiler, macroNameArity)

    private fun returnMacroString(parameterReturn: OtpErlangList) =
            toReturn(parameterReturn)
                    ?.let { returnToMacroString(it) }
                    ?: "missing_return"

    private fun returnToMacroString(return_: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(return_, Scope.EMPTY).macroString

    private fun toParameter(parameterReturn: OtpErlangList): OtpErlangObject? = parameterReturn.elementAt(0)
    private fun toReturn(parameterReturn: OtpErlangList): OtpErlangObject? = parameterReturn.elementAt(1)

    private fun fallback(decompiler: MacroNameArity, macroNameArity: org.elixir_lang.beam.MacroNameArity): String =
        StringBuilder().let { fallback(it, decompiler, macroNameArity) }.toString()

    fun fallback(macroStringBuilder: StringBuilder, decompiler: MacroNameArity, macroNameArity: org.elixir_lang.beam.MacroNameArity): StringBuilder {
        val parameters = Default.INSTANCE.parameters(macroNameArity)
        decompiler.appendSignature(macroStringBuilder, macroNameArity, macroNameArity.name, parameters)

        return macroStringBuilder
    }
}
