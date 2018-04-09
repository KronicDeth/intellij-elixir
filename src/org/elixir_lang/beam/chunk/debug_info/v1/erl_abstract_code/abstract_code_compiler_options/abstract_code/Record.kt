package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction

object Record {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val arity = term.arity()

        return when (arity) {
            4 -> creationToMacroStringDeclaredScope(term, scope)
            5 -> updateToMacroStringDeclaredScope(term, scope)
            else -> MacroStringDeclaredScope("unknown_record_arity", Scope.EMPTY)
        }
    }

    fun nameToMacroString(name: OtpErlangAtom): MacroString {
        val inspectedAsFunction = inspectAsFunction(name)

        return if (inspectedAsFunction.startsWith("\"")) {
            "__MODULE__.$inspectedAsFunction"
        } else {
            inspectedAsFunction
        }
    }

    internal fun nameToMacroString(term: OtpErlangObject): MacroString =
            when (term) {
                is OtpErlangAtom -> nameToMacroString(term)
                else -> "unknown_record_name"
            }

    private const val TAG = "record"

    private fun creationNameMacroString(term: OtpErlangTuple): String =
            toCreationName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_creation_name"

    private fun creationToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val nameMacroString = creationNameMacroString(term)
        val (recordFieldsMacroString, recordFieldsDeclaredScope) = creationRecordFieldsMacroStringDeclaredScope(term, scope)

        return MacroStringDeclaredScope(
                "$nameMacroString($recordFieldsMacroString)",
                recordFieldsDeclaredScope
        )
    }

    private fun creationRecordFieldsMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toCreationRecordFields(term)
                    ?.let { Elements.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_record_fields", Scope.EMPTY)

    private fun toCreationName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toCreationRecordFields(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toUpdatable(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toUpdateName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toUpdateRecordFields(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)

    private fun updatableMacroString(term: OtpErlangTuple, scope: Scope) =
            toUpdatable(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: "unknown_updatable"

    private fun updateNameMacroString(term: OtpErlangTuple) =
            toUpdateName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_update_name"

    private fun updateRecordFieldsMacroString(term: OtpErlangTuple, scope: Scope) =
            toUpdateRecordFields(term)
                    ?.let { Elements.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: "missing_record_fields"

    private fun updateToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val updatableMacroString = updatableMacroString(term, scope)
        val nameMacroString = updateNameMacroString(term)
        val recordFieldsMacroString = updateRecordFieldsMacroString(term, scope)

        return MacroStringDeclaredScope(
                "$nameMacroString($updatableMacroString, $recordFieldsMacroString)",
                Scope.EMPTY
        )
    }
}
