package org.elixir_lang.semantic.module_attribute.definition

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.module_attribute.Definition

abstract class ModuleAttribute(val atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) :
    Definition {
    override val enclosingModular: Modular
        get() = TODO("Not yet implemented")
    override val psiElement: PsiElement
        get() = atUnqualifiedNoParenthesesCall
    val moduleAttributeName: String?
        get() = CachedValuesManager.getCachedValue(psiElement, MODULE_ATTRIBUTE_NAME) {
            CachedValueProvider.Result.create(computeModuleAttributeName(this), psiElement)
        }
    val value: PsiElement?
        get() = CachedValuesManager.getCachedValue(psiElement, VALUE) {
            CachedValueProvider.Result.create(computeValue(this), psiElement)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewNodeTextLocation.INSTANCE -> psiElement.text
            UsageViewTypeLocation.INSTANCE -> "module attribute"
            else -> null
        }

    companion object {
        private val MODULE_ATTRIBUTE_NAME: Key<CachedValue<String?>> =
            Key("elixir.semantic.module_attribute.definition.module_attribute_name")

        private fun computeModuleAttributeName(semantic: ModuleAttribute): String? =
            semantic
                .atUnqualifiedNoParenthesesCall
                .atIdentifier
                .text

        private val VALUE: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.module_attribute.definition.value")

        private fun computeValue(semantic: ModuleAttribute): PsiElement? =
            semantic
                .psiElement
                .children.takeIf { it.size >= 2 }
                ?.get(1)?.let { it as? ElixirNoParenthesesOneArgument }
                ?.children
                ?.singleOrNull()
    }
}
