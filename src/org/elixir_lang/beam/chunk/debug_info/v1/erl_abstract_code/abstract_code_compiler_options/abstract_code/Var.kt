package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object Var {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_var"
            }

    fun toMacroString(term: OtpErlangTuple): String = nameMacroString(term)

    private const val TAG = "var"

    private fun nameMacroString(term: OtpErlangTuple): String =
            toName(term)
                    ?.let{ nameToMacroString(it) }
                    ?: "name_missing"

    fun nameToMacroString(name: OtpErlangObject) =
            when (name) {
                is OtpErlangAtom -> nameToMacroString(name)
                else -> "unknown_name"
            }

    private fun nameToMacroString(name: OtpErlangAtom) = name.atomValue().decapitalize()
    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
