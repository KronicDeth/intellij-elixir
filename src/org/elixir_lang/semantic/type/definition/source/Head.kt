package org.elixir_lang.semantic.type.definition.source

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.NameArity
import org.elixir_lang.psi.ElixirMatchedWhenOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.semantic.type.definition.Source
import org.elixir_lang.semantic.type.definition.head.Parameter

class Head(val source: Source, val call: Call) : org.elixir_lang.semantic.type.definition.Head {
    val nameIdentifier: PsiElement?
        get() = CachedValuesManager.getCachedValue(call, NAME_IDENTIFIER) {
            CachedValueProvider.Result.create(computeNameIdentifier(call), call)
        }
    override val psiElement: PsiElement
        get() = call
    val nameArity: NameArity
        get() = CachedValuesManager.getCachedValue(call, NAME_ARITY) {
            CachedValueProvider.Result.create(computeNameArity(type), call)
        }
    val type: Call?
        get() = CachedValuesManager.getCachedValue(call, TYPE) {
            CachedValueProvider.Result.create(computeType(call), call)
        }
    override val parameters: List<Parameter>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val NAME_IDENTIFIER: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.type.definition.specification.head.name_identifier")

        private fun computeNameIdentifier(type: Call?): PsiElement? = type?.functionNameElement()

        private val NAME_ARITY: Key<CachedValue<NameArity>> =
            Key("elixir.semantic.type.definition.specification.head.name_arity")

        private fun computeNameArity(type: Call?): NameArity? =
            if (type != null) {
                type.functionName()?.let { name ->
                    val arity = type.resolvedFinalArity()

                    NameArity(name, arity)
                }
            } else {
                null
            }

        private val TYPE: Key<CachedValue<Call?>> =
            Key("elixir.semantic.type.definition.specification.head.type")

        private fun computeType(call: Call): Call? =
            when (call) {
                is Type -> call.leftOperand() as? Call
                is ElixirMatchedWhenOperation -> (call.leftOperand() as? Type)?.let { (it.leftOperand() as? Call) }
                else -> null
            }


    }
}
