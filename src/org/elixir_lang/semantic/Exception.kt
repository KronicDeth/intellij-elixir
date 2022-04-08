package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.semantic.exception.call.Definition
import org.elixir_lang.semantic.modular.Enclosed

class Exception(override val enclosingModular: Modular, val call: org.elixir_lang.psi.call.Call) : Enclosed, Semantic {
    override val psiElement: PsiElement
        get() = call
    val structure: org.elixir_lang.semantic.structure.Definition by lazy {
        org.elixir_lang.semantic.structure.Definition(enclosingModular, call)
    }
    val definitions: kotlin.collections.List<Definition> by lazy {
        NAME_ARITY_INTERVALS.map { nameArityInterval ->
            Definition(this, nameArityInterval)
        }
    }

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "exception"
            else -> null
        }

    companion object {
        val ONE_ONE = ArityInterval(1, 1)
        val EXCEPTION = NameArityInterval("exception", ONE_ONE)
        val MESSAGE = NameArityInterval("message", ONE_ONE)

        val NAME_ARITY_INTERVALS = listOf(
            EXCEPTION,
            MESSAGE
        )
    }
}
