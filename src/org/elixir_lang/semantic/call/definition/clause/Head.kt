package org.elixir_lang.semantic.call.definition.clause

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.psi.ElixirMatchedAtOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.PsiNamedElementImpl
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.Semantic.Companion.semantic
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.Head.strip
import org.elixir_lang.semantic.call.definition.clause.head.Parameter

class Head(val clause: Clause, val call: Call) : Semantic {
    val nameIdentifier: PsiElement?
        get() = CachedValuesManager.getCachedValue(call, NAME_IDENTIFIER) {
            CachedValueProvider.Result.create(computeNameIdentifier(call), call)
        }
    override val psiElement: PsiElement
        get() = call
    val nameArityInterval: NameArityInterval? by lazy {
        TODO()
    }
    val parameters: List<Parameter> by lazy {
        TODO()
    }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val NAME_IDENTIFIER: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.call.definition.clause.head.name_identifier")

        private fun computeNameIdentifier(call: Call): PsiElement? =
            if (call is ElixirMatchedAtOperation) {
                call.operator()
            } else {
                val stripped = strip(call)

                if (stripped is Call) {
                    stripped.functionNameElement()
                } else {
                    stripped
                }
            }

        fun get(clause: Clause, psiElement: Call): Head = semantic(psiElement) {
            Head(clause, psiElement)
        }

        fun nameArityInterval(head: PsiElement, modularCanonicalName: String?): NameArityInterval? =
            if (head is ElixirMatchedAtOperation) {
                val name = head.operator().text.trim { it <= ' ' }
                val arityInterval = ArityInterval(1, 1)

                NameArityInterval(name, arityInterval)
            } else {
                (strip(head) as? Call)?.let { stripped ->
                    val functionName = stripped.functionName()

                    if (functionName != null) {
                        val name = PsiNamedElementImpl.unquoteName(stripped, functionName)
                        val arityInterval = stripped.resolvedFinalArityInterval()

                        NameArityInterval(name, arityInterval).adjusted(modularCanonicalName)
                    } else {
                        null
                    }
                }
            }
    }
}
