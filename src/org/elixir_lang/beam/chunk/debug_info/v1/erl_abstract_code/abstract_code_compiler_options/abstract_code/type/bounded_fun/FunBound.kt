package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.bounded_fun

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Fun

object FunBound {
    fun toMacroString(funBound: OtpErlangList, nameMacroString: MacroString): MacroString {
        val funMacroString = funMacroString(funBound, nameMacroString)
        val boundMacroString = boundMacroString(funBound)

        return "$funMacroString when $boundMacroString"
    }

    private fun boundMacroString(funBound: OtpErlangList) =
            toBound(funBound)
                    ?.let { boundToMacroString(it) }
                    ?: "missing_bound"

    private fun boundToMacroString(bound: OtpErlangList) =
            bound.joinToString(", ") {
                AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString
            }

    private fun boundToMacroString(bound: OtpErlangObject) =
            when (bound) {
                is OtpErlangList -> boundToMacroString(bound)
                else -> "unknown_bound"
            }

    private fun funMacroString(funBound: OtpErlangList, nameMacroString: MacroString) =
            toFun(funBound)
                    ?.let { funToMacroString(it, nameMacroString) }
                    ?: "missing_fun"

    private fun funToMacroString(`fun`: OtpErlangObject, nameMacroString: MacroString) =
        Type.ifTo(`fun`) {
            Fun.ifToMacroString(it, nameMacroString)
            ?: "unknown_fun"
        } ?: "unknown_fun"

    private fun toBound(funBound: OtpErlangList): OtpErlangObject? = funBound.elementAt(1)
    private fun toFun(funBound: OtpErlangList): OtpErlangObject? = funBound.elementAt(0)
}
