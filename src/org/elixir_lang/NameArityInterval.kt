package org.elixir_lang

import com.intellij.psi.ResolveState
import org.elixir_lang.ecto.query.API
import org.elixir_lang.ecto.query.WindowAPI
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.scope.CallDefinitionClause

data class NameArityInterval(val name: Name, val arityInterval: ArityInterval) {
    fun adjusted(resolveState: ResolveState): NameArityInterval =
            resolveState.get(CallDefinitionClause.MODULAR_CANONICAL_NAME)
                    ?.let { modularCanonicalName ->
                        ARITY_INTERVAL_BY_NAME_BY_MODULAR_CANONICAL_NAME[modularCanonicalName]?.get(name)?.let {
                            copy(arityInterval = it)
                        }
                    }
                    ?: this

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
