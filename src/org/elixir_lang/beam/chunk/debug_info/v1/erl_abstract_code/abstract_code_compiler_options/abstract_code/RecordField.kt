package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsKey

object RecordField {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val arity = term.arity()

        return when (arity) {
            4 -> setToMacroStringDeclaredScope(term, scope)
            5 -> getToMacroStringDeclaredScope(term)
            else -> MacroStringDeclaredScope("unknown_record_field_arity($arity)", Scope.EMPTY)
        }
    }

    private const val TAG = "record_field"

    private fun fieldToMacroString(term: OtpErlangObject): String =
            Atom.toElixirAtom(term)
                    ?.let { inspectAsKey(it) }
                    ?: "unknown_field:"

    private fun getExpressionMacroString(term: OtpErlangTuple): String =
            getToExpression(term).let { AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString }

    private fun getFieldMacroString(term: OtpErlangTuple): String =
            getToField(term).let{ AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString }

    private fun getNameMacroString(term: OtpErlangTuple): String =
            getToName(term).let { Record.nameToMacroString(it) }

    private fun getToExpression(term: OtpErlangTuple): OtpErlangObject = term.elementAt(2)
    private fun getToField(term: OtpErlangTuple): OtpErlangObject = term.elementAt(4)

    private fun getToMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope {
        val nameMacroString = getNameMacroString(term)
        val expressionMacroString = getExpressionMacroString(term)
        val fieldMacroString = getFieldMacroString(term)

        return MacroStringDeclaredScope(
                "$nameMacroString($expressionMacroString, $fieldMacroString)",
                Scope.EMPTY
        )
    }

    private fun getToName(term: OtpErlangTuple): OtpErlangObject = term.elementAt(3)

    private fun setFieldMacroString(term: OtpErlangTuple): String = setToField(term).let { fieldToMacroString(it) }

    private fun setToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            setValueMacroStringDeclaredScope(term, scope).let { (valueMacroString, valueDeclaredScope) ->
                val fieldMacroString = setFieldMacroString(term)

                MacroStringDeclaredScope("$fieldMacroString $valueMacroString", valueDeclaredScope)
            }

    private fun setToValue(term: OtpErlangTuple): OtpErlangObject = term.elementAt(3)
    private fun setToField(term: OtpErlangTuple): OtpErlangObject = term.elementAt(2)

    private fun setValueMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            setToValue(term).let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
}
