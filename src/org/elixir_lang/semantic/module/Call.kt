package org.elixir_lang.semantic.module

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.Arity
import org.elixir_lang.Presentable
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Module
import org.jetbrains.annotations.Contract

class Call(
    enclosingModular: Modular?,
    call: org.elixir_lang.psi.call.Call
) : org.elixir_lang.semantic.modular.Call(enclosingModular, call), Module, Presentable {
    override val name: String?
        get() = CachedValuesManager.getCachedValue(call, NAME) {
            CachedValueProvider.Result.create(computeName(this), call)
        }
    val nameIdentifier: PsiElement?
        get() = CachedValuesManager.getCachedValue(call, NAME_IDENTIFIER) {
            CachedValueProvider.Result.create(computeNameIdentifier(call), call)
        }
    override val moduleDocs: List<org.elixir_lang.semantic.documentation.Module>
        get() = TODO("Not yet implemented")
    override val structureViewTreeElement: org.elixir_lang.structure_view.element.modular.Module
        get() = TODO("Not yet implemented")
    override val locationString: String
        get() = TODO("Not yet implemented")
    override val canonicalNameSet: Set<String>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    override val decompiled: Set<Modular>
        get() = TODO("Not yet implemented")
    override val presentation: org.elixir_lang.navigation.item_presentation.modular.Module
        get() = CachedValuesManager.getCachedValue(psiElement, PRESENTATION) {
            CachedValueProvider.Result.create(computePresentation(this), psiElement)
        }

    companion object {
        const val MODULE = org.elixir_lang.psi.call.name.Module.KERNEL
        const val FUNCTION = Function.DEFMODULE
        const val ARITY = 2

        @Contract(pure = true)
        fun name(call: org.elixir_lang.psi.call.Call): String = call.primaryArguments()!!.first()!!.text

        fun from(enclosingModular: Modular?, call: org.elixir_lang.psi.call.Call, arity: Arity): Module? =
            if (arity == ARITY) {
                defmodule(enclosingModular, call)
            } else {
                null
            }

        fun defmodule(enclosingModular: Modular?, call: org.elixir_lang.psi.call.Call) = Call(enclosingModular, call)

        private val NAME: Key<CachedValue<String?>> =
            Key("elixir.semantic.module.call.name")

        private fun computeName(semantic: Call): String? = semantic.nameIdentifier?.text

        private val NAME_IDENTIFIER: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.module.call.name_identifier")

        private fun computeNameIdentifier(call: org.elixir_lang.psi.call.Call): PsiElement? =
            call.primaryArguments()?.firstOrNull()?.stripAccessExpression()

        private val PRESENTATION: Key<CachedValue<org.elixir_lang.navigation.item_presentation.modular.Module>> =
            Key("elixir.semantic.module.call.presentation")

        private fun computePresentation(call: Call): org.elixir_lang.navigation.item_presentation.modular.Module {
            val location =
                call.enclosingModular?.presentation?.let { it as? Parent }?.locatedPresentableText
                    ?: call.locationString

            return org.elixir_lang.navigation.item_presentation.modular.Module(location, call)
        }
    }
}
