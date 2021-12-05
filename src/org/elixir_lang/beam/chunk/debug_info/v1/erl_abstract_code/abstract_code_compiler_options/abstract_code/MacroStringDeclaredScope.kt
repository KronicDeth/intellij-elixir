package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

typealias DeclaredScope = Scope

data class MacroString(val string: String, val doBlock: Boolean) {
    fun group(): MacroString =
            if (doBlock) {
                MacroString("($string)", doBlock = false)
            } else {
                this
            }

    override fun toString(): String {
        return string
    }

    companion object {
        fun unknown(stringSuffix: String, titlePrefix: String, term: OtpErlangObject) =
                error("unknown_$stringSuffix", "$titlePrefix is unknown", term)

        fun error(default: String, title: String, term: OtpErlangObject): MacroString {
            val string = AbstractCode.error(default, title, term)

            return MacroString(string, doBlock = false)
        }
    }
}

data class MacroStringDeclaredScope(val macroString: MacroString, val declaredScope: DeclaredScope) {
    constructor(string: String, doBlock: Boolean, declaredScope: DeclaredScope):
            this(MacroString(string, doBlock), declaredScope)

    companion object {
        fun missing(defaultSuffix: String,
                    titlePrefix: String,
                    term: OtpErlangObject): MacroStringDeclaredScope =
                missing(defaultSuffix, Scope.EMPTY, titlePrefix, term)

        fun missing(defaultSuffix: String,
                    scope: Scope,
                    titlePrefix: String,
                    term: OtpErlangObject): MacroStringDeclaredScope {
            val default = AbstractCode.missing(defaultSuffix, titlePrefix, term)

            return error(default, scope)
        }

        fun unknown(defaultSuffix: String, titlePrefix: String, term: OtpErlangObject): MacroStringDeclaredScope {
            val default = AbstractCode.unknown(defaultSuffix, titlePrefix, term)

            return error(default)
        }

        fun error(string: String, scope: Scope = Scope.EMPTY): MacroStringDeclaredScope =
                MacroStringDeclaredScope(MacroString(string, doBlock = false), scope)
    }
}
