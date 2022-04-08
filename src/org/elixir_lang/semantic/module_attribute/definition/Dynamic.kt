package org.elixir_lang.semantic.module_attribute.definition

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Named

abstract class Dynamic(val call: Call) : org.elixir_lang.semantic.module_attribute.Definition, Named {
    override val enclosingModular: Modular
        get() = TODO("Not yet implemented")
    override val psiElement: PsiElement
        get() = call
    override val name: String?
        get() = CachedValuesManager.getCachedValue(psiElement, NAME) {
            CachedValueProvider.Result.create(computeName(this), psiElement)
        }
    val nameIdentifier: PsiElement?
        get() = CachedValuesManager.getCachedValue(psiElement, NAME_IDENTIFIER) {
            CachedValueProvider.Result.create(computeNameIdentifier(call), psiElement)
        }

    companion object {
        private val NAME: Key<CachedValue<String?>> =
            Key.create("elixir.semantic.module_attribute.definition.register.name")

        fun computeName(register: Dynamic): String? =
            when (val nameIdentifier = register.nameIdentifier) {
                is ElixirAtom -> "@${nameIdentifier.name}"
                else -> null
            }

        private val NAME_IDENTIFIER: Key<CachedValue<PsiElement?>> =
            Key.create("elixir.semantic.module_attribute.definition.register.name_identifier")

        private fun computeNameIdentifier(call: Call): PsiElement? =
            call
                .finalArguments()
                ?.let { arguments ->
                    arguments[arguments.lastIndex - 1]
                }
                ?.stripAccessExpression()
    }
}
