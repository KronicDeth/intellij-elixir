package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Char
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Map


object AbstractCode {
    inline fun <T> ifTag(term: OtpErlangObject?, tag: String, ifTrue: (OtpErlangTuple) -> T?): T? =
            when (term) {
                is OtpErlangTuple -> ifTag(term, tag, ifTrue)
                else -> null
            }

    inline fun <T> ifTag(term: OtpErlangTuple, tag: String, ifTrue: (OtpErlangTuple) -> T?): T? =
            (term.elementAt(0) as? OtpErlangAtom)?.let { actualTag ->
                if (actualTag.atomValue() == tag) {
                    ifTrue(term)
                } else {
                    null
                }
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            AbstractCodeString.ifToMacroStringDeclaredScope(term) ?:
            AnnotatedType.ifToMacroStringDeclaredScope(term) ?:
            Atom.ifToMacroStringDeclaredScope(term) ?:
            Bin.ifToMacroStringDeclaredScope(term, scope) ?:
            BinElement.ifToMacroStringDeclaredScope(term, scope) ?:
            Call.ifToMacroStringDeclaredScope(term) ?:
            Case.ifToMacroStringDeclaredScope(term, scope) ?:
            Catch.ifToMacroStringDeclaredScope(term, scope) ?:
            Char.ifToMacroStringDeclaredScope(term) ?:
            Comprehension.ifToMacroStringDeclaredScope(term, scope) ?:
            Cons.ifToMacroStringDeclaredScope(term, scope) ?:
            Fun.ifToMacroStringDeclaredScope(term, scope) ?:
            If.ifToMacroStringDeclaredScope(term, scope) ?:
            Integer.ifToMacroStringDeclaredScope(term) ?:
            Map.ifToMacroStringDeclaredScope(term, scope) ?:
            Match.ifToMacroStringDeclaredScope(term, scope) ?:
            Nil.ifToMacroStringDeclaredScope(term) ?:
            Op.ifToMacroStringDeclaredScope(term, scope) ?:
            Receive.ifToMacroStringDeclaredScope(term, scope) ?:
            Record.ifToMacroStringDeclaredScope(term, scope) ?:
            RecordField.ifToMacroStringDeclaredScope(term, scope) ?:
            RecordIndex.ifToMacroStringDeclaredScope(term) ?:
            Remote.ifToMacroStringDeclaredScope(term) ?:
            RemoteType.ifToMacroStringDeclaredScope(term) ?:
            Try.ifToMacroStringDeclaredScope(term, scope) ?:
            Tuple.ifToMacroStringDeclaredScope(term, scope) ?:
            Type.ifToMacroStringDeclaredScope(term) ?:
            UserType.ifToMacroStringDeclaredScope(term) ?:
            Var.ifToMacroStringDeclaredScope(term, scope) ?:
            MacroStringDeclaredScope("unknown_abstract_code", Scope.EMPTY)
}
