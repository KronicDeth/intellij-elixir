package org.elixir_lang.semantic.module_attribute.definition

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.semantic.Named

class Literal(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) :
    ModuleAttribute(atUnqualifiedNoParenthesesCall), Named {
    override val name: String?
        get() = CachedValuesManager.getCachedValue(psiElement, NAME) {
            CachedValueProvider.Result.create(computeName(this), psiElement)
        }
    val nameIdentifier: PsiElement
        get() = atUnqualifiedNoParenthesesCall.atIdentifier

    companion object {
        private val NAME: Key<CachedValue<String?>> =
            Key.create("elixir.semantic.module_attribute.definition.register.name")

        fun computeName(semantic: Literal): String? =
            semantic.nameIdentifier.text
    }
}
