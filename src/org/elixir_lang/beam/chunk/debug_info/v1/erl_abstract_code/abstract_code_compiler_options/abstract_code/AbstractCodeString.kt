package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangString
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.term.inspect
import java.nio.charset.Charset

// not named String because I don't want to delay with the name collision with normal `String`
object AbstractCodeString {
    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = AbstractCode.ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it) }

    fun toElixirString(term: OtpErlangObject): OtpErlangBinary? =
            toErlangString(term)?.let { erlangString ->
                erlangString
                        .stringValue()
                        .let { OtpErlangBinary(it.toByteArray(Charset.forName("UTF-8"))) }
            }

    fun toErlangString(term: OtpErlangObject): OtpErlangString? =
            ifTo(term) {
                toString(it)
                        ?.let { it as? OtpErlangString }
            }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
            MacroStringDeclaredScope(stringMacroString(term), Scope.EMPTY)

    fun toString(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private const val TAG = "string"

    private fun stringMacroString(term: OtpErlangTuple) =
            toString(term)
                    ?.let { stringToMacroString(it) }
                    ?: "missing_string"

    private fun stringToMacroString(term: OtpErlangObject): String =
        when (term) {
            is OtpErlangString -> stringToMacroString(term)
            else -> "unknown_string"
        }

    private fun stringToMacroString(term: OtpErlangString): String = inspect(term)
}
