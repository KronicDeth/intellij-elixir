package org.elixir_lang.semantic.type.definition.source

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic
import org.elixir_lang.semantic.type.definition.Source

class Specification(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) :
    Source(atUnqualifiedNoParenthesesCall) {
    val callDefinitions: Set<Definition>?
        get() = CachedValuesManager.getCachedValue(atUnqualifiedNoParenthesesCall, CALL_DEFINITIONS) {
            CachedValueProvider.Result.create(computeCallDefinitions(head?.type), atUnqualifiedNoParenthesesCall)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "specification"
            else -> null
        }

    companion object {
        private val CALL_DEFINITIONS: Key<CachedValue<Set<Definition>?>> =
            Key("elixir.semantic.type.definition.source.call_definitions")

        private fun computeCallDefinitions(type: Call?): Set<Definition> =
            type
                ?.reference?.let { it as PsiPolyVariantReference }
                ?.multiResolve(false)
                ?.mapNotNull { resolveResult ->
                    when (val semantic = resolveResult.element?.semantic) {
                        is Clause -> semantic.definition
                        else -> null
                    }
                }
                ?.toSet()
                .orEmpty()
    }
}
