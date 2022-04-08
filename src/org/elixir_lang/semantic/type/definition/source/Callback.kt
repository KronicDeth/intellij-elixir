package org.elixir_lang.semantic.type.definition.source

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArity
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirMatchedWhenOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.module_attribute.definition.ModuleAttribute
import org.elixir_lang.semantic.type.Definition
import org.elixir_lang.semantic.type.definition.Body
import org.elixir_lang.semantic.type.definition.Guard
import org.elixir_lang.structure_view.element.call.definition.delegation.Head
import org.jetbrains.annotations.Contract

class Callback(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>, identifierName: String) :
    ModuleAttribute(atUnqualifiedNoParenthesesCall), Definition {
    val time: Time = when (identifierName) {
        "callback" -> Time.RUN
        "macrocallback" -> Time.COMPILE
        else -> Time.RUN
    }
    override val head: org.elixir_lang.semantic.type.definition.Head?
        get() = TODO("Not yet implemented")
    val nameIdentifier: PsiElement?
        get() = CachedValuesManager.getCachedValue(atUnqualifiedNoParenthesesCall, NAME_IDENTIFIER) {
            CachedValueProvider.Result.create(
                computeNameIdentifier(atUnqualifiedNoParenthesesCall),
                atUnqualifiedNoParenthesesCall
            )
        }
    override val body: Body?
        get() = TODO("Not yet implemented")
    override val nameArity: NameArity
        get() = TODO("Not yet implemented")
    override val guard: Guard?
        get() = TODO("Not yet implemented")
    val callDefinition: org.elixir_lang.semantic.call.Definition
        get() = org.elixir_lang.semantic.type.definition.source.callback.call.Definition(this)

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "callback"
            else -> null
        }

    override val enclosingModular: Modular
        get() = TODO("Not yet implemented")

    companion object {
        private val NAME_IDENTIFIER: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.type.definition.callback.name_identifier")

        private fun computeNameIdentifier(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>):
                PsiElement? =
            headCall(atUnqualifiedNoParenthesesCall)?.let { headCall ->
                Head.nameIdentifier(headCall)
            }

        @Contract(pure = true)
        private fun headCall(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): Call? =
            atUnqualifiedNoParenthesesCall
                .noParenthesesOneArgument
                .children
                .singleOrNull()
                ?.let { specificationHeadCall(it) }

        private fun specificationHeadCall(specification: PsiElement): Call? =
            when (specification) {
                is Type -> typeHeadCall(specification)
                is ElixirMatchedWhenOperation -> parameterizedTypeHeadCall(specification)
                else -> null
            }

        private fun typeHeadCall(typeOperation: Type): Call? = typeOperation.leftOperand() as? Call

        private fun parameterizedTypeHeadCall(whenOperation: ElixirMatchedWhenOperation): Call? =
            (whenOperation.leftOperand() as? Type)?.let { type ->
                typeHeadCall(type)
            }
    }
}
