package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.term.inspect

object Atom {

    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it) }

    fun toAtom(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    fun toElixirAtom(term: OtpErlangObject): OtpErlangAtom? =
            Atom.ifTo(term) {
                Atom.toAtom(it)?.let { atom ->
                    atom as? OtpErlangAtom
                }
            }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope {
        val macroString = toMacroString(term)

        return MacroStringDeclaredScope(macroString, Scope.EMPTY)
    }

    private const val TAG = "atom"

    private fun toMacroString(term: OtpErlangTuple): String =
            toAtom(term)
                    ?.let { inspect(it) }
                    ?: ":unknown_atom"
}
