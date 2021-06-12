package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.psi.ResolveState
import org.elixir_lang.Arity
import org.elixir_lang.ArityRange
import org.elixir_lang.NameArityRange
import org.elixir_lang.ecto.query.API.ECTO_QUERY_API
import org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS
import org.elixir_lang.psi.scope.CallDefinitionClause.Companion.MODULAR_CANONICAL_NAME

/**
 * While an arity range for a normal function or macro is represented as an
 * [org.apache.commons.lang.math.IntRange], some special forms have no fixed arity when used because although
 * defined in `Kernel.SpecialForms` as 1-arity, that one argument is effectively a splat of all the arguments, so
 * there needs to be a way to represent that half-open interval.
 */
internal class ArityInterval {
    private val minimum: Arity
    private val maximum: Arity?

    /**
     * Unlike [org.apache.commons.lang.math.IntRange.IntRange]}, where the argument becomes the minimum AND
     * the maximum, here the argument is ONLY the minimum and the interval has an infinite maximum.
     *
     * @param minimum minimum arity
     */
    private constructor(minimum: Arity) {
        this.minimum = minimum
        this.maximum = null
    }

    private constructor(minimum: Arity, maximum: Arity) {
        this.minimum = minimum
        this.maximum = maximum
    }

    private constructor(arityRange: ArityRange) {
        this.minimum = arityRange.first
        this.maximum = arityRange.last
    }

    operator fun contains(candidate: Arity): Boolean {
        return minimum <= candidate && (maximum == null || candidate <= maximum)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder("ArityInterval(").append(minimum)

        if (maximum != null) {
            stringBuilder.append(", ").append(maximum)
        }

        stringBuilder.append(")")

        return stringBuilder.toString()
    }

    companion object {
        private val ZERO = ArityInterval(0)
        private val ONE = ArityInterval(1)
        private val ONE_TWO = ArityInterval(1, 2)
        private val ARITY_INTERVAL_BY_NAME_BY_MODULAR_CANONICAL_NAME =
                mapOf(
                        ECTO_QUERY_API to mapOf(
                                "fragment" to ZERO
                        ),
                        KERNEL_SPECIAL_FORMS to
                                mapOf(
                                        "__aliases__" to ONE,
                                        "__block__" to ONE,
                                        "alias" to ONE_TWO,
                                        "for" to ONE,
                                        "import" to ONE_TWO,
                                        "quote" to ONE_TWO,
                                        "require" to ONE_TWO,
                                        "super" to ZERO,
                                        "with" to ONE
                                )
                )

        @JvmStatic
        fun arityInterval(nameArityRange: NameArityRange, resolveState: ResolveState): ArityInterval =
                resolveState.get(MODULAR_CANONICAL_NAME)
                        ?.let { modularCanonicalName ->
                            ARITY_INTERVAL_BY_NAME_BY_MODULAR_CANONICAL_NAME[modularCanonicalName]!![nameArityRange.name]
                        }
                        ?: ArityInterval(nameArityRange.arityRange)
    }
}
