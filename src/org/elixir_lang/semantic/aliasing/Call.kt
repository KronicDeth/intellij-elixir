package org.elixir_lang.semantic.aliasing

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.semantic.Alias
import org.elixir_lang.semantic.Aliasing
import org.elixir_lang.semantic.MultipleAliases
import org.elixir_lang.semantic.semantic

class Call(override val psiElement: Call) : Aliasing {
    override val aliases: List<Alias>
        get() = CachedValuesManager.getCachedValue(psiElement, ALIASES) {
            CachedValueProvider.Result.create(computeAliases(psiElement), psiElement)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val ALIASES: Key<CachedValue<List<Alias>>> = Key("elixir.aliasing.aliases")

        private fun computeAliases(call: Call): List<Alias> =
            call
                .finalArguments()
                ?.let { arguments ->
                    when (arguments.size) {
                        2 -> {
                            TODO()
                        }
                        1 -> {
                            when (val semantic = arguments[0].semantic) {
                                is Alias -> semantic.suffix?.let { listOf(it) } ?: emptyList()
                                is MultipleAliases -> semantic.suffixes
                                else -> null
                            }
                        }
                        else -> null
                    }
                }
                ?: emptyList()
    }
}
