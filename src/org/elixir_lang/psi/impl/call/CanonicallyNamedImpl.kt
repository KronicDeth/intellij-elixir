package org.elixir_lang.psi.impl.call

import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.semantic.semantic

object CanonicallyNamedImpl {
    fun getCanonicalName(stubBased: StubBased<*>): String? =
        stubBased.semantic?.let { it as? org.elixir_lang.CanonicallyNamed }?.canonicalName ?: stubBased.name

    fun getCanonicalNameSet(stubBased: StubBased<*>): Set<String> =
        when (val semantic = stubBased.semantic) {
            is org.elixir_lang.CanonicallyNamed -> semantic.canonicalNameSet
            else -> stubBased.name?.let { setOf(it) } ?: emptySet()
        }
}
