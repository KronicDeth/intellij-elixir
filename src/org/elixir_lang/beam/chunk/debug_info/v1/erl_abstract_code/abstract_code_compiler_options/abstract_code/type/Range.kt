package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo

object Range {
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifSubtypeTo(type, SUBTYPE) { toMacroString(type) }

    private const val SUBTYPE = "range"

    private fun firstLastToFirst(firstLast: OtpErlangList): OtpErlangObject? = firstLast.elementAt(0)
    private fun firstLastToLast(firstLast: OtpErlangList): OtpErlangObject? = firstLast.elementAt(1)

    private fun firstLastToMacroString(firstLast: OtpErlangList): MacroString {
        val firstMacroString = firstMacroString(firstLast)
        val lastMacroString = lastMacroString(firstLast)

        return "$firstMacroString..$lastMacroString"
    }

    private fun firstLastToMacroString(term: OtpErlangObject) =
            when (term) {
                is OtpErlangList -> firstLastToMacroString(term)
                else -> "unknown_first_last"
            }

    private fun firstMacroString(firstLast: OtpErlangList) =
            firstLastToFirst(firstLast)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString }
                    ?: "missing_first"

    private fun lastMacroString(firstLast: OtpErlangList) =
            firstLastToLast(firstLast)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString }
                    ?: "missing_last"

    private fun toFirstLast(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun toMacroString(type: OtpErlangTuple) =
            toFirstLast(type)
                    ?.let { firstLastToMacroString(it) }
                    ?: "missing_first_last"
}
