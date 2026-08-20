package org.elixir_lang.beam.decompiler

import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.NameArity

private val logger = Logger.getInstance("org.elixir_lang.beam.decompiler.Decompilers")

private val MACRO_NAME_ARITY_DECOMPILER_LIST: List<MacroNameArity> = listOf(
    InfixOperator,
    PrefixOperator,
    Unquoted,
    SignatureOverride,
    Default.INSTANCE
)

/**
 * Selects the [MacroNameArity] decompiler that accepts [nameArity] for [beamLanguage], or logs an
 * error and returns `null` when none match.
 *
 * @param beamLanguage BEAM language, such as `"elixir"` or `"erlang"`.  Matches the format used in
 * [org.elixir_lang.beam.chunk.beam_documentation.Documentation.beamLanguage].
 * @param nameArity name and arity of the definition to decompile.
 */
fun decompiler(beamLanguage: String, nameArity: NameArity): MacroNameArity? {
    for (decompiler in MACRO_NAME_ARITY_DECOMPILER_LIST) {
        if (decompiler.accept(beamLanguage, nameArity)) {
            return decompiler
        }
    }

    logger.error("No decompiler for MacroNameArity ($nameArity)")

    return null
}
