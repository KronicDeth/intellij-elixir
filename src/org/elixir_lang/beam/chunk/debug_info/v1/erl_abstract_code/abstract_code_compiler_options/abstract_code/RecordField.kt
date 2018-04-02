package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsKey

object RecordField {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
        expressionMacroStringDeclaredScope(term, scope).let { (expressionMacroString, expressionDeclaredScope) ->
            val fieldMacroString = fieldMacroString(term)

            MacroStringDeclaredScope("$fieldMacroString $expressionMacroString", expressionDeclaredScope)
        }

    private const val TAG = "record_field"

    private fun expressionMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toExpression(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_expression", Scope.EMPTY)

    private fun fieldMacroString(term: OtpErlangTuple): String =
            toField(term)
                    ?.let { fieldToMacroString(it) }
                    ?: "missing_field:"

    private fun fieldToMacroString(term: OtpErlangObject): String =
            Atom.toElixirAtom(term)
                    ?.let { inspectAsKey(it) }
                    ?: "unknown_field:"

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toField(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
