package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.*
import org.elixir_lang.beam.chunk.component1
import org.elixir_lang.beam.chunk.component2
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.term.inspect
import java.nio.charset.Charset

object BinElement {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val patternMacroString = patternMacroString(term)
        val optionsMacroString = ifOptionsMacroString(term)

        return if (optionsMacroString != null) {
            "$patternMacroString :: $optionsMacroString"
        } else {
            patternMacroString
        }
    }

    fun toMacroString(term: OtpErlangObject?): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_bin_element"
            }

    private const val TAG = "bin_element"

    private fun ifOptionsMacroString(term: OtpErlangTuple): String? {
        val sizeMacroString = sizeMacroString(term)
        val typeSpecifierListMacroString = typeSpecifierListMacroString(term)

        return if (sizeMacroString != null) {
            if (typeSpecifierListMacroString != null) {
                "$sizeMacroString-$typeSpecifierListMacroString"
            } else {
                sizeMacroString
            }
        } else {
            @Suppress("IfThenToSafeAccess")
            if (typeSpecifierListMacroString != null) {
                typeSpecifierListMacroString
            } else {
                null
            }
        }
    }

    private fun typeSpecifierListMacroString(term: OtpErlangTuple): String? {
        val typeSpecifierList = toTypeSpecifierList(term)

        return if (typeSpecifierList != null) {
            typeSpecifierListToMacroString(typeSpecifierList)
        } else {
            "unknown_type_specifier_list"
        }
    }

    private fun patternMacroString(term: OtpErlangTuple): String =
            toPattern(term)
                    ?.let { patternToMacroString(it) } ?:
            "unknown_pattern"

    /**
     * Elixir does not have `<<'charlist'>>`, but `<<"string">`
     */
    private fun patternToMacroString(term: OtpErlangObject): String =
        AbstractCodeString.ifTo(term) {
            AbstractCodeString.toString(it)?.let { string ->
                if (string is OtpErlangString) {
                    string
                            .stringValue()
                            .let { OtpErlangBinary(it.toByteArray(Charset.forName("UTF-8"))) }
                            .let { inspect(it) }
                } else {
                    null
                }
            }
        } ?: AbstractCode.toMacroString(term)

    private fun sizeMacroString(term: OtpErlangTuple): String? {
        val size = toSize(term)

        return if (size != null) {
            sizeToMacroString(size)
        } else {
            "unknown_size"
        }
    }

    private fun sizeToMacroString(term: OtpErlangAtom): String? =
            if (term.atomValue() == "default") {
                null
            } else {
                "unknown_size_atom"
            }

    private fun sizeToMacroString(term: OtpErlangObject): String? =
        when (term) {
            is OtpErlangAtom -> sizeToMacroString(term)
            else -> AbstractCode.toMacroString(term)
        }

    private fun toPattern(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toSize(term: OtpErlangTuple) : OtpErlangObject? = term.elementAt(3)
    private fun toTypeSpecifierList(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)

    private fun typeSpecifierListToMacroString(term: OtpErlangAtom): String? =
            if (term.atomValue() == "default") {
                null
            } else {
                "unknown_type_specifier_list_atom"
            }

    private fun typeSpecifierListToMacroString(term: OtpErlangList): String =
            term.joinToString("-") { typeSpecifierToMacroString(it) }

    private fun typeSpecifierListToMacroString(term: OtpErlangObject): String? =
            when (term) {
                is OtpErlangAtom -> typeSpecifierListToMacroString(term)
                is OtpErlangList -> typeSpecifierListToMacroString(term)
                else -> "unknown_type_specifier_list"
            }

    private fun typeSpecifierToMacroString(term: OtpErlangAtom): String = term.atomValue()

    private fun typeSpecifierToMacroString(term: OtpErlangTuple): String =
           if (term.arity() == 2) {
               val (name, value) = term

               if (name is OtpErlangAtom && value is OtpErlangLong) {
                   "${name.atomValue()}(${value.longValue()})"
               } else {
                   "unknown_type_specifier_name_value_pair"
               }
           } else {
               "unknown_type_specifier_tuple"
           }

    private fun typeSpecifierToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> typeSpecifierToMacroString(term)
                is OtpErlangTuple -> typeSpecifierToMacroString(term)
                else -> "unknown_type_specifier"
            }
}
