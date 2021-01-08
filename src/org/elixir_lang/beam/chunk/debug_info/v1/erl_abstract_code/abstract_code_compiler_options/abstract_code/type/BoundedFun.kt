package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.bounded_fun.FunBound

object BoundedFun {
    fun funBound(definition: OtpErlangTuple): OtpErlangObject? = definition.elementAt(3)

    fun <T> ifTo(type: OtpErlangTuple, ifTrue: (OtpErlangTuple) -> T): T? = ifSubtypeTo(type, SUBTYPE, ifTrue)
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifTo(type) { toMacroString(type) }

    fun ifToMacroString(type: OtpErlangTuple, nameMacroString: MacroString): MacroString? =
            ifTo(type) { toMacroString(type, nameMacroString) }

    private const val SUBTYPE = "bounded_fun"

    private fun toMacroString(@Suppress("UNUSED_PARAMETER") type: OtpErlangTuple): MacroString {
        TODO()
    }

    private fun toMacroString(type: OtpErlangTuple, nameMacroString: MacroString): MacroString {
        val funBound = BoundedFun.funBound(type)

        return when (funBound) {
            is OtpErlangList -> FunBound.toMacroString(funBound, nameMacroString)
            else -> "unknown_fun_bound"
        }
    }
}
