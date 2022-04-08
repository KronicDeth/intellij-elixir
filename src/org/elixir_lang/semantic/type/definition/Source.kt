package org.elixir_lang.semantic.type.definition

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArity
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.module_attribute.definition.ModuleAttribute
import org.elixir_lang.semantic.type.Definition
import org.elixir_lang.semantic.type.definition.source.Head

open class Source(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) :
    ModuleAttribute(atUnqualifiedNoParenthesesCall), Definition {
    val nameIdentifier: PsiElement?
        get() = head?.nameIdentifier
    override val nameArity: NameArity by lazy {
        head!!.nameArity
    }

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> super<Definition>.elementDescription(location)
            UsageViewNodeTextLocation.INSTANCE -> super<ModuleAttribute>.elementDescription(location)
            else -> null
        }

    override val head: Head?
        get() = CachedValuesManager.getCachedValue(atUnqualifiedNoParenthesesCall, HEAD) {
            CachedValueProvider.Result.create(
                computeHead(this),
                atUnqualifiedNoParenthesesCall
            )
        }
    override val body: Body?
        get() = TODO("Not yet implemented")
    override val guard: Guard?
        get() = TODO("Not yet implemented")

    companion object {
        private val HEAD: Key<CachedValue<Head?>> =
            Key("elixir.semantic.type.definition.source.head")

        private fun computeHead(source: Source): Head? =
            source
                .value
                ?.let { it as? Call }
                ?.let { Head(source, it) }
    }
}
