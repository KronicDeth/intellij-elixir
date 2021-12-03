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

    private fun argumentsString(userType: OtpErlangTuple) =
            toArguments(userType)
                    .let { Sequence.toMacroStringDeclaredScope(it, Scope.EMPTY, "", COMMA, "") }
                    .macroString
                    .string

    private fun nameString(userType: OtpErlangTuple) =
            toName(userType)
                    ?.let { nameToString(it) }
                    ?: "missing_name"

    private fun nameToString(name: OtpErlangAtom) = inspectAsFunction(name, true)

    private fun nameToString(name: OtpErlangObject) =
            when (name) {
                is OtpErlangAtom -> nameToString(name)
                else -> "unknown_name"
            }

    private fun toArguments(userType: OtpErlangTuple): OtpErlangObject? = userType.elementAt(3)

    private fun toMacroString(userType: OtpErlangTuple): String {
        val nameString = nameString(userType)
        val argumentsString = argumentsString(userType)

        return "$nameString($argumentsString)"
    }

    private fun toMacroStringDeclaredScope(userType: OtpErlangTuple) =
            toMacroString(userType)
                    .let { MacroStringDeclaredScope(it, doBlock = false, Scope.EMPTY) }

    private fun toName(userType: OtpErlangTuple): OtpErlangObject? = userType.elementAt(2)
}
