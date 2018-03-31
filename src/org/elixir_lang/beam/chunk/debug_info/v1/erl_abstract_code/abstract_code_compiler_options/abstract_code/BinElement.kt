package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element.Pattern
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element.Size
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element.TypeSpecifierList

object BinElement {
    fun hasDefaultOptions(term: OtpErlangTuple): Boolean = hasDefaultSize(term) && hasDefaultTypeSpecifierList(term)

    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = AbstractCode.ifTag(term, TAG, ifTrue)
    fun ifToMacroString(term: OtpErlangObject?): String? = ifTo(term) { toMacroString(it) }

    fun toElixirString(term: OtpErlangTuple): OtpErlangBinary? =
            if (hasDefaultOptions(term)) {
                toPattern(term)?.let { Pattern.toElixirString(it) }
            } else {
                null
            }

    fun toMacroString(term: OtpErlangTuple): String {
        val patternMacroString = patternMacroString(term)
        val optionsMacroString = ifOptionsMacroString(term)

        return if (optionsMacroString != null) {
            "$patternMacroString :: $optionsMacroString"
        } else {
            patternMacroString
        }
    }

    fun toPattern(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private const val TAG = "bin_element"

    private fun hasDefaultSize(term: OtpErlangTuple): Boolean = toSize(term)
                    ?.let { Size.isDefault(it)  }
                    ?: false

    private fun hasDefaultTypeSpecifierList(term: OtpErlangTuple): Boolean =
            toTypeSpecifierList(term)
                    ?.let { TypeSpecifierList.isDefault(it) }
                    ?: false

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
            TypeSpecifierList.toMacroString(typeSpecifierList)
        } else {
            "unknown_type_specifier_list"
        }
    }

    private fun patternMacroString(term: OtpErlangTuple): String =
            toPattern(term)
                    ?.let { Pattern.toMacroString(it) } ?:
            "unknown_pattern"

    private fun sizeMacroString(term: OtpErlangTuple): String? {
        val size = toSize(term)

        return if (size != null) {
            Size.toMacroString(size)
        } else {
            "unknown_size"
        }
    }

    private fun toSize(term: OtpErlangTuple) : OtpErlangObject? = term.elementAt(3)
    private fun toTypeSpecifierList(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)
}
