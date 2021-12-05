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
            toString(term)
                    .let { MacroStringDeclaredScope(it, doBlock = false, Scope.EMPTY) }

    private const val TAG = "record_index"

    private fun fieldString(term: OtpErlangTuple): String =
            toField(term)
                    ?.let { fieldToString(it) }
                    ?: AbstractCode.missing("field", "${TAG} field", term)

    private fun fieldToString(term: OtpErlangObject): String = AbstractCode.toString(term)

    private fun nameString(term: OtpErlangTuple): String =
            toName(term)
                    ?.let { nameToString(it) }
                    ?: AbstractCode.missing("name", "${TAG} name", term)

    private fun nameToString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> inspectAsFunction(term, true)
                else -> AbstractCode.unknown("name", "${TAG} name", term)
            }

    private fun toString(term: OtpErlangTuple): String {
        val nameString = nameString(term)
        val fieldString = fieldString(term)

        return "$nameString($fieldString)"
    }

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toField(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
