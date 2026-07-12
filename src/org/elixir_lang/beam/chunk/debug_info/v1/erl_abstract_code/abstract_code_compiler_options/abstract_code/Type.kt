package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Fun
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.MapFieldAssociation
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Record

object Type {
    fun <T> ifSubtypeTo(type: OtpErlangTuple, subtype: String, ifTrue: (OtpErlangTuple) -> T): T? =
            ifSubtypeTo(type, setOf(subtype), ifTrue)

    fun <T> ifSubtypeTo(type: OtpErlangTuple, subtypeSet: Set<String>, ifTrue: (OtpErlangTuple) -> T): T? =
        if (subtypeSet.contains(subtypeString(type))) {
            ifTrue(type)
        } else {
            null
        }

    fun <T> ifTo(term: OtpErlangObject, ifTrue: (OtpErlangTuple) -> T): T? = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(
            term: OtpErlangObject
    ): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it) }

    fun subtypeString(type: OtpErlangTuple): String =
            toSubtype(type)
                    ?.let { subtypeToString(it) }
                    ?: AbstractCode.missing("subtype", "${TAG} subtype", type)

    private const val TAG = "type"

    private fun subtypeToString(subtype: OtpErlangAtom) = subtype.atomValue()

    private fun subtypeToString(subtype: OtpErlangObject): String =
            when (subtype) {
                is OtpErlangAtom -> subtypeToString(subtype)
                else -> AbstractCode.unknown("subtype", "${TAG} subtype", subtype)
            }

    private fun toString(type: OtpErlangTuple): String =
        BoundedFun.ifToString(type) ?:
        Builtin.ifToString(type) ?:
        Constraint.ifToString(type) ?:
        FieldType.ifToString(type) ?:
        MapFieldAssociation.ifToString(type) ?:
        MapFieldExact.ifToString(type) ?:
        Fun.ifToString(type) ?:
        Product.ifToString(type) ?:
        Range.ifToString(type) ?:
        Record.ifToString(type) ?:
        Union.ifToString(type) ?:
        "unknown_type"

    private fun toMacroStringDeclaredScope(type: OtpErlangTuple) =
            MacroStringDeclaredScope(toString(type), doBlock = false, Scope.EMPTY)

    private fun toSubtype(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(2)
}

private const val VAR_TAG = "var"
private const val ANONYMOUS_VARIABLE_NAME = "_"

/**
 * Within a type expression the Erlang anonymous type variable `{var, Anno, :_}` means "any type in this
 * position".  Elixir has no anonymous type variable - a bare `_` in a typespec is a `type _/0 undefined`
 * compile error - so rewrite each one to the `any()` built-in type node `{type, Anno, any, []}`, which the
 * existing type renderer emits as `any()`.
 *
 * This is only sound for *type* terms: `@type`/`@typep`/`@opaque` bodies and `@spec` types.  It must never be
 * applied to value/pattern terms (function clauses), where `{var, Anno, :_}` is a legitimate ignore pattern
 * rendered verbatim as `_`.
 */
fun anonymousVariableToAny(term: OtpErlangObject): OtpErlangObject =
        when (term) {
            is OtpErlangTuple ->
                if (isAnonymousVariable(term)) {
                    anyType(term.elementAt(1))
                } else {
                    OtpErlangTuple(Array(term.arity()) { anonymousVariableToAny(term.elementAt(it)) })
                }
            is OtpErlangList ->
                OtpErlangList(Array(term.arity()) { anonymousVariableToAny(term.elementAt(it)) })
            else -> term
        }

private fun isAnonymousVariable(term: OtpErlangTuple): Boolean =
        term.arity() == 3 &&
                (term.elementAt(0) as? OtpErlangAtom)?.atomValue() == VAR_TAG &&
                (term.elementAt(2) as? OtpErlangAtom)?.atomValue() == ANONYMOUS_VARIABLE_NAME

private fun anyType(anno: OtpErlangObject): OtpErlangTuple =
        OtpErlangTuple(arrayOf(OtpErlangAtom("type"), anno, OtpErlangAtom("any"), OtpErlangList()))

