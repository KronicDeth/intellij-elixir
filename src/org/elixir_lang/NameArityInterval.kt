package org.elixir_lang

import org.elixir_lang.ecto.query.API
import org.elixir_lang.ecto.query.WindowAPI
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.psi.call.name.Module

data class NameArityInterval(val name: Name, val arityInterval: ArityInterval) : Comparable<NameArityInterval> {
    fun adjusted(modularCanonicalName: String?): NameArityInterval =
        if (modularCanonicalName != null) {
            ARITY_INTERVAL_BY_NAME_BY_MODULAR_CANONICAL_NAME[modularCanonicalName]?.get(name)?.let {
                copy(arityInterval = it)
            } ?: this
        } else {
            this
        }

    override fun compareTo(other: NameArityInterval): Int =
        compareValuesBy(this, other, NameArityInterval::name, NameArityInterval::arityInterval)

    fun contains(nameArity: NameArity): Boolean = name == nameArity.name && arityInterval.contains(nameArity.arity)
    fun contains(other: NameArityInterval): Boolean = name == other.name && arityInterval.contains(other.arityInterval)

    override fun toString(): String {
        return super.toString()
    }

    companion object {
        fun nameIsAdjusted(name: String): Boolean = name in NAMES

        private val ZERO = ArityInterval(0)
        private val ONE = ArityInterval(1)
        private val ONE_TWO = ArityInterval(1, 2)
        private val ARITY_INTERVAL_BY_NAME_BY_MODULAR_CANONICAL_NAME =
            mapOf(
                API.name to mapOf(
                    "fragment" to ZERO
                ),
                WindowAPI.name to mapOf(
                    "over" to ONE_TWO
                ),
                Module.KERNEL_SPECIAL_FORMS to
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
        private val NAMES: Set<String> =
            ARITY_INTERVAL_BY_NAME_BY_MODULAR_CANONICAL_NAME.values.flatMap { it.keys }.toSet()
    }
}
