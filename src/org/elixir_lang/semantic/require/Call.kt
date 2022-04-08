package org.elixir_lang.semantic.require

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Alias
import org.elixir_lang.semantic.Require

class Call(val call: Call) : Require {
    override val psiElement: PsiElement
        get() = call
    override val aliases: List<Alias>
        get() = CachedValuesManager.getCachedValue(call, ALIASES) {
            CachedValueProvider.Result.create(computeAliases(call), call)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val ALIASES: Key<CachedValue<List<Alias>>> = Key("elixir.require.aliases")

        private fun computeAliases(call: Call): List<Alias> =
            when (call.resolvedFinalArity()) {
                2 -> {
                    TODO()
                }
                else -> emptyList()
            }
    }
}
