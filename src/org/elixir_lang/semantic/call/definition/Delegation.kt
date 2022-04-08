package org.elixir_lang.semantic.call.definition

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Named
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.call.definition.delegation.Head
import org.elixir_lang.semantic.documentation.CallDefinition
import org.elixir_lang.semantic.modular.Enclosed

class Delegation(override val enclosingModular: Modular, val call: Call) : Named, Enclosed, Semantic, Definition {
    override val psiElement: PsiElement
        get() = call
    val nameIdentifier: PsiElement?
        get() = CachedValuesManager.getCachedValue(call, NAME_IDENTIFIER) {
            CachedValueProvider.Result.create(computeNameIdentifier(call), call)
        }
    val head: Head by lazy {
        TODO()
    }
    override val nameArityInterval: NameArityInterval? by lazy {
        head.nameArityInterval
    }
    override val docs: List<CallDefinition>
        get() = TODO("Not yet implemented")
    override val clauses: List<Clause>
        get() = TODO("Not yet implemented")
    val delegates: List<Clause>
        get() = TODO()
    override val decompiled: List<Definition>
        get() = TODO("Not yet implemented")
    override val visibility: Visibility
        get() = TODO("Not yet implemented")
    override val name: String?
        get() = TODO("Not yet implemented")
    override val presentation: org.elixir_lang.navigation.item_presentation.NameArityInterval
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        if (location === UsageViewTypeLocation.INSTANCE) {
            "delegation"
        } else {
            null
        }

    override val time: Time
        get() = TODO("Not yet implemented")

    companion object {
        private val NAME_IDENTIFIER: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.call.definition.delegation.name_identifier")

        fun computeNameIdentifier(call: Call): PsiElement? =
            call.finalArguments()?.get(0)?.let { it as? Call }?.functionNameElement()
    }
}
