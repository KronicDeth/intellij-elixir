package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo

object Range {
    fun ifToString(type: OtpErlangTuple): String? = ifSubtypeTo(type, SUBTYPE) { toString(type) }

    private const val SUBTYPE = "range"

    private fun firstLastToFirst(firstLast: OtpErlangList): OtpErlangObject? = firstLast.elementAt(0)
    private fun firstLastToLast(firstLast: OtpErlangList): OtpErlangObject? = firstLast.elementAt(1)

    private fun firstLastToString(firstLast: OtpErlangList): String {
        val firstString = firstString(firstLast)
        val lastString = lastString(firstLast)

        return "$firstString..$lastString"
    }

    private fun firstLastToString(term: OtpErlangObject) =
            when (term) {
                is OtpErlangList -> firstLastToString(term)
                else -> "unknown_first_last"
            }

    private fun firstString(firstLast: OtpErlangList): String =
            firstLastToFirst(firstLast)
                    ?.let { AbstractCode.toString(it) }
                    ?: "missing_first"

    private fun lastString(firstLast: OtpErlangList): String =
            firstLastToLast(firstLast)
                    ?.let { AbstractCode.toString(it) }
                    ?: "missing_last"

    private fun toFirstLast(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun toString(type: OtpErlangTuple) =
            toFirstLast(type)
                    ?.let { firstLastToString(it) }
                    ?: "missing_first_last"
}
