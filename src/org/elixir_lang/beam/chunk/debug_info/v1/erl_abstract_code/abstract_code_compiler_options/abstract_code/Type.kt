package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Fun
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.MapFieldAssociation
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Record

object Type {
    fun <T> ifSubtypeTo(type: OtpErlangTuple, subtype: String, ifTrue: (OtpErlangTuple) -> T): T? =
            ifSubtypeTo(type, setOf(subtype), ifTrue)

    fun <T> ifSubtypeTo(type: OtpErlangTuple, subtypeSet: Set<String>, ifTrue: (OtpErlangTuple) -> T): T? =
        if (subtypeSet.contains(subtypeMacroString(type))) {
            ifTrue(type)
        } else {
            null
        }

    fun <T> ifTo(term: OtpErlangObject, ifTrue: (OtpErlangTuple) -> T): T? = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(
            term: OtpErlangObject
    ): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it) }

    fun subtypeMacroString(type: OtpErlangTuple): MacroString =
            toSubtype(type)
                    ?.let { subtypeToMacroString(it) }
                    ?: "missing_subtype"


    private const val TAG = "type"

    private fun subtypeToMacroString(subtype: OtpErlangAtom) = subtype.atomValue()

    private fun subtypeToMacroString(subtype: OtpErlangObject) =
            when (subtype) {
                is OtpErlangAtom -> subtypeToMacroString(subtype)
                else -> "unknown_subtype"
            }

    private fun toMacroString(type: OtpErlangTuple) =
        BoundedFun.ifToMacroString(type) ?:
        Builtin.ifToMacroString(type) ?:
        Constraint.ifToMacroString(type) ?:
        FieldType.ifToMacroString(type) ?:
        MapFieldAssociation.ifToMacroString(type) ?:
        MapFieldExact.ifToMacroString(type) ?:
        Fun.ifToMacroString(type) ?:
        Product.ifToMacroString(type) ?:
        Range.ifToMacroString(type) ?:
        Record.ifToMacroString(type) ?:
        Union.ifToMacroString(type) ?:
        "unknown_type"

    private fun toMacroStringDeclaredScope(type: OtpErlangTuple) =
            MacroStringDeclaredScope(toMacroString(type), Scope.EMPTY)

    private fun toSubtype(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(2)
}

