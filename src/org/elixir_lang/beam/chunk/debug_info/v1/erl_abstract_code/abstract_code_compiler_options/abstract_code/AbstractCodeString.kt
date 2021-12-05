package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.*
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
            MacroStringDeclaredScope(stringString(term), doBlock = false, Scope.EMPTY)

    fun toString(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private const val TAG = "string"

    private fun stringString(term: OtpErlangTuple) =
            toString(term)
                    ?.let { stringToString(it) }
                    ?: AbstractCode.missing("string", "string string", term)

    private fun stringToString(term: OtpErlangObject): String =
        when (term) {
            is OtpErlangString -> stringToString(term)
            is OtpErlangList -> stringToString(term)
            else -> AbstractCode.unknown("string_string", "string string", term)
        }

    private fun stringToString(term: OtpErlangString): String = inspect(term)

    private fun stringToString(term: OtpErlangList): String =
            if (term.arity() == 0 && term.isProper) {
                "\"\""
            } else {
                AbstractCode.error("non_empty_list_as_string", "string list is non-empty", term)
            }
}
