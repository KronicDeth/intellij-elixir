package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element.Pattern
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element.Size
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element.TypeSpecifierList

object BinElement {
    fun hasDefaultOptions(term: OtpErlangTuple): Boolean = hasDefaultSize(term) && hasDefaultTypeSpecifierList(term)

    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it, scope) }

    fun toElixirString(term: OtpErlangTuple): OtpErlangBinary? =
            if (hasDefaultOptions(term)) {
                toPattern(term)?.let { Pattern.toElixirString(it) }
            } else {
                null
            }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (patternMacroString, patternDeclaredScope) = patternMacroStringDeclaredScope(term, scope)
        val patternString = patternMacroString.string
        val optionsString = ifOptionsString(term)

        val string = if (optionsString != null) {
            "$patternString :: $optionsString"
        } else {
            patternString
        }

        return MacroStringDeclaredScope(string, doBlock = false, patternDeclaredScope)
    }

    private const val TAG = "bin_element"

    private fun hasDefaultSize(term: OtpErlangTuple): Boolean = toSize(term)
                    ?.let { Size.isDefault(it)  }
                    ?: false

    private fun hasDefaultTypeSpecifierList(term: OtpErlangTuple): Boolean =
            toTypeSpecifierList(term)
                    ?.let { TypeSpecifierList.isDefault(it) }
                    ?: false

    private fun ifOptionsString(term: OtpErlangTuple): String? {
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

    private fun toPattern(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun typeSpecifierListMacroString(term: OtpErlangTuple): String? {
        val typeSpecifierList = toTypeSpecifierList(term)

        return if (typeSpecifierList != null) {
            TypeSpecifierList.toMacroString(typeSpecifierList)
        } else {
            AbstractCode.unknown("type_specifier_list", "Bin element type specifier list", term)
        }
    }

    private fun patternMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toPattern(term)
                    ?.let { Pattern.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.unknown("pattern", "bin element pattern", term)

    private fun sizeMacroString(term: OtpErlangTuple): String? {
        val size = toSize(term)

        return if (size != null) {
            Size.toString(size)
        } else {
            AbstractCode.unknown("size", "bin element size", term)
        }
    }

    private fun toSize(term: OtpErlangTuple) : OtpErlangObject? = term.elementAt(3)
    private fun toTypeSpecifierList(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)
}
