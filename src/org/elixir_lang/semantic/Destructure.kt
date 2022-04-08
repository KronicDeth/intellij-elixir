package org.elixir_lang.semantic

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments

class Destructure(val call: Call) : Semantic {
    override val psiElement: PsiElement
        get() = call
    val output: Semantic?
        get() = CachedValuesManager.getCachedValue(call, OUTPUT) {
            val computed = call.finalArguments()?.first()?.semantic
            CachedValueProvider.Result.create(computed, listOf(call))
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val OUTPUT: Key<CachedValue<Semantic?>> = Key("elixir.semantic.destructure.output")
    }
}
