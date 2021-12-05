package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction

object Record {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            when (term.arity()) {
                4 -> creationToMacroStringDeclaredScope(term, scope)
                5 -> updateToMacroStringDeclaredScope(term, scope)
                else -> MacroStringDeclaredScope.unknown("${TAG}_arity", "${TAG} arity", term)
            }

    fun nameToString(name: OtpErlangAtom): String = inspectAsFunction(name, true)

    internal fun nameToString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> nameToString(term)
                else -> AbstractCode.unknown("${TAG}_name", "${TAG} name", term)
            }

    private const val TAG = "record"

    private fun creationNameString(term: OtpErlangTuple): String =
            toCreationName(term)
                    ?.let { nameToString(it) }
                    ?: AbstractCode.missing("creation_name", "record creation name", term)

    private fun creationToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val nameString = creationNameString(term)
        val (recordFieldsMacroString, recordFieldsDeclaredScope) = creationRecordFieldsMacroStringDeclaredScope(term, scope)

        return MacroStringDeclaredScope(
                "$nameString(${recordFieldsMacroString.string})",
                doBlock = false,
                recordFieldsDeclaredScope
        )
    }

    private fun creationRecordFieldsMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toCreationRecordFields(term)
                    ?.let { Sequence.toCommaSeparatedMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("record_fields", "record creation record fields", term)

    private fun toCreationName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toCreationRecordFields(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toUpdatable(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toUpdateName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toUpdateRecordFields(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)

    private fun updatableString(term: OtpErlangTuple, scope: Scope): String =
            toUpdatable(term)
                    ?.let { AbstractCode.toString(it, scope) }
                    ?: AbstractCode.unknown("updatable", "${TAG} updatable", term)

    private fun updateNameString(term: OtpErlangTuple): String =
            toUpdateName(term)
                    ?.let { nameToString(it) }
                    ?: AbstractCode.missing("update_name", "${TAG} update name", term)

    private fun updateRecordFieldsString(term: OtpErlangTuple, scope: Scope): String =
            toUpdateRecordFields(term)
                    ?.let {  updateRecordFields ->
                        when (updateRecordFields) {
                            is OtpErlangList -> if (updateRecordFields.arity() == 0 && updateRecordFields.isProper) {
                                "[]"
                            } else {
                                Sequence.toCommaSeparatedString(updateRecordFields, scope)
                            }
                            else -> AbstractCode.unknown("update_record_fields", "${TAG} update record fields", updateRecordFields)
                        }
                    }
                    ?: AbstractCode.missing("record_fields", "${TAG} record fields", term)

    private fun updateToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val updatableString = updatableString(term, scope)
        val nameString = updateNameString(term)
        val recordFieldsString = updateRecordFieldsString(term, scope)

        return MacroStringDeclaredScope(
                "$nameString($updatableString, $recordFieldsString)",
                doBlock = false,
                Scope.EMPTY
        )
    }
}
