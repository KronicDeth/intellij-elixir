package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction

object UserType {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    private const val TAG = "user_type"

    private fun argumentsMacroString(userType: OtpErlangTuple) =
            toArguments(userType)
                    .let { Sequence.toMacroStringDeclaredScope(it, Scope.EMPTY, "", ", ", "") }
                    .macroString

    private fun nameMacroString(userType: OtpErlangTuple) =
            toName(userType)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_name"

    private fun nameToMacroString(name: OtpErlangAtom) = inspectAsFunction(name)

    private fun nameToMacroString(name: OtpErlangObject) =
            when (name) {
                is OtpErlangAtom -> nameToMacroString(name)
                else -> "unknown_name"
            }

    private fun toArguments(userType: OtpErlangTuple): OtpErlangObject? = userType.elementAt(3)

    private fun toMacroString(userType: OtpErlangTuple): MacroString {
        val nameMacroString = nameMacroString(userType)
        val argumentsMacroString = argumentsMacroString(userType)

        return "$nameMacroString($argumentsMacroString)"
    }

    private fun toMacroStringDeclaredScope(userType: OtpErlangTuple) =
            toMacroString(userType)
                    .let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private fun toName(userType: OtpErlangTuple): OtpErlangObject? = userType.elementAt(2)
}
