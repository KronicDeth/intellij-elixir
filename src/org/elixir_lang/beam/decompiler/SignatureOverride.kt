package org.elixir_lang.beam.decompiler

import java.lang.StringBuilder

/**
 * For macro/name/arity that don't need be handled by [Unquoted], but won't map to Code correctly if the signature
 * in the `Docs` chunk is used.
 */
class SignatureOverride : MacroNameArity() {
    /**
     * Whether the decompiler accepts the `macroNameArity`
     *
     * @return `true`
     */
    override fun accept(macroNameArity: org.elixir_lang.beam.MacroNameArity): Boolean {
        return macroNameArity.name == "__struct__"
    }

    /**
     * Append the decompiled source for `macroNameArity` to `decompiled`.
     *
     * @param decompiled the decompiled source so far
     */
    override fun append(decompiled: StringBuilder, macroNameArity: org.elixir_lang.beam.MacroNameArity) {
        when (macroNameArity.arity) {
            1 -> {
                decompiled
                        .append("  ")
                        .append(macroNameArity.macro)
                        .append(" __struct__(kv)")
                appendBody(decompiled)
            }
            else -> Default.INSTANCE.append(decompiled, macroNameArity)
        }
    }

    override fun appendName(decompiled: StringBuilder, name: String) {
        decompiled.append(name)
    }

    companion object {
        @JvmField
        val INSTANCE: MacroNameArity = SignatureOverride()
    }
}
