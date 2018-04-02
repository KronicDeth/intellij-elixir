package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction


object RecordIndex {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
            toMacroString(term)
                    .let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private const val TAG = "record_index"

    private fun fieldMacroString(term: OtpErlangTuple): MacroString =
            toField(term)
                    ?.let { fieldToMacroString(it) }
                    ?: "missing_field"

    private fun fieldToMacroString(term: OtpErlangObject): MacroString =
            AbstractCode.toMacroStringDeclaredScope(term, Scope.EMPTY).macroString

    private fun nameMacroString(term: OtpErlangTuple): MacroString =
            toName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_name"

    private fun nameToMacroString(term: OtpErlangObject): MacroString =
            when (term) {
                is OtpErlangAtom -> inspectAsFunction(term)
                else -> "unknown_name"
            }

    private fun toMacroString(term: OtpErlangTuple): MacroString {
        val nameMacroString = nameMacroString(term)
        val fieldMacroString = fieldMacroString(term)

        return "$nameMacroString($fieldMacroString)"
    }

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toField(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
