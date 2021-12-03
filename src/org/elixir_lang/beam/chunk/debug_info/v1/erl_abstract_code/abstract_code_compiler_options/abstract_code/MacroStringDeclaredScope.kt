package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

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
        fun error(string: String): MacroString =
                MacroString(string, doBlock = false)
    }
}

data class MacroStringDeclaredScope(val macroString: MacroString, val declaredScope: DeclaredScope) {
    constructor(string: String, doBlock: Boolean, declaredScope: DeclaredScope):
            this(MacroString(string, doBlock), declaredScope)

    companion object {
        fun error(string: String): MacroStringDeclaredScope =
                MacroStringDeclaredScope(MacroString.error(string), Scope.EMPTY)
    }
}
