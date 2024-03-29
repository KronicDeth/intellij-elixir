package org.elixir_lang.beam.decompiler

import org.elixir_lang.NameArity
import java.lang.StringBuilder

/**
 * For macro/name/arity that don't need be handled by [Unquoted], but won't map to Code correctly if the signature
 * in the `Docs` chunk is used.
 */
object SignatureOverride : Default() {
    /**
     * Whether the decompiler accepts the `macroNameArity`
     *
     * @return `true`
     */
    override fun accept(beamLanguage: String, nameArity: NameArity): Boolean = nameArity.name == "__struct__"

    /**
     * Append the decompiled source for `macroNameArity` to `decompiled`.
     *
     * @param decompiled the decompiled source so far
     */
    override fun append(decompiled: StringBuilder, macroNameArity: org.elixir_lang.beam.MacroNameArity) {
        when (macroNameArity.arity) {
            1 -> {
                appendSignature(decompiled, macroNameArity, "__struct__", arrayOf("kv"))
                appendBody(decompiled)
            }
            else -> super.append(decompiled, macroNameArity)
        }
    }

    override fun appendSignature(decompiled: StringBuilder,
                                 macroNameArity: org.elixir_lang.beam.MacroNameArity,
                                 name: String,
                                 parameters: Array<String>) {
        val (nameOverride, argumentsOverride) = when (macroNameArity.arity) {
            1 -> {
                Pair("__struct__", arrayOf("kv"))
            }
            else -> Pair(name, parameters)
        }

        super.appendSignature(decompiled, macroNameArity, nameOverride, argumentsOverride)
    }
}
