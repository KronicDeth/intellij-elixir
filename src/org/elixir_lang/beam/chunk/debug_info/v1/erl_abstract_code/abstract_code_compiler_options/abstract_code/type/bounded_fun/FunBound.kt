package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.bounded_fun

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.COMMA
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Fun
import org.elixir_lang.beam.decompiler.MacroNameArity

object FunBound {
    fun toString(funBound: OtpErlangList, decompiler: MacroNameArity, macroNameArity: org.elixir_lang.beam.MacroNameArity): String {
        val funString = funString(funBound, decompiler, macroNameArity)
        val boundString = boundString(funBound)

        return "$funString when $boundString"
    }

    private fun boundString(funBound: OtpErlangList): String =
            toBound(funBound)
                    ?.let { boundToString(it) }
                    ?: "missing_bound"

    private fun boundToString(bound: OtpErlangList) = Sequence.toCommaSeparatedString(bound)

    private fun boundToString(bound: OtpErlangObject): String =
            when (bound) {
                is OtpErlangList -> boundToString(bound)
                else -> "unknown_bound"
            }

    private fun funString(funBound: OtpErlangList,
                          decompiler: MacroNameArity,
                          macroNameArity: org.elixir_lang.beam.MacroNameArity): String =
            toFun(funBound)
                    ?.let { funToMacroString(it, decompiler, macroNameArity) }
                    ?: "missing_fun"

    private fun funToMacroString(`fun`: OtpErlangObject,
                                 decompiler: MacroNameArity,
                                 macroNameArity: org.elixir_lang.beam.MacroNameArity) =
        Type.ifTo(`fun`) {
            Fun.ifToString(it, decompiler, macroNameArity)
            ?: "unknown_fun"
        } ?: "unknown_fun"

    private fun toBound(funBound: OtpErlangList): OtpErlangObject? = funBound.elementAt(1)
    private fun toFun(funBound: OtpErlangList): OtpErlangObject? = funBound.elementAt(0)
}
