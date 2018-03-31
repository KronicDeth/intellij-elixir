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

    fun toMacroString(term: OtpErlangObject): String =
            AbstractCodeString.ifToMacroString(term) ?:
            Atom.ifToMacroString(term) ?:
            Bin.ifToMacroString(term) ?:
            BinElement.ifToMacroString(term) ?:
            BitstringComprehension.ifToMacroString(term) ?:
            Call.ifToMacroString(term) ?:
            Case.ifToMacroString(term) ?:
            Char.ifToMacroString(term) ?:
            Cons.ifToMacroString(term) ?:
            Integer.ifToMacroString(term) ?:
            Map.ifToMacroString(term) ?:
            Match.ifToMacroString(term) ?:
            Nil.ifToMacroString(term) ?:
            Op.ifToMacroString(term) ?:
            Record.ifToMacroString(term) ?:
            RecordField.ifToMacroString(term) ?:
            RecordIndex.ifToMacroString(term) ?:
            Remote.ifToMacroString(term) ?:
            Tuple.ifToMacroString(term) ?:
            Var.ifToMacroString(term) ?:
            "unknown_abstract_code"
}
