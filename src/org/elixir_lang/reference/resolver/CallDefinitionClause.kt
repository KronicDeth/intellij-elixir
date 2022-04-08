package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.Arity
import org.elixir_lang.Name
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.For
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.Delegation
import org.elixir_lang.semantic.semantic

object CallDefinitionClause : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.CallDefinitionClause> {
    override fun resolve(
        callDefinitionClause: org.elixir_lang.reference.CallDefinitionClause,
        incompleteCode: Boolean
    ): Array<ResolveResult> =
        callDefinitionClause.specification.let { specification ->
            specification
                .enclosingModular
                .psiElement.let { it as? Call }
                ?.macroChildCalls()?.let { siblings ->
                    if (siblings.isNotEmpty()) {
                        val nameArity = specification.nameArity
                        val name = nameArity.name
                        val arity = nameArity.arity

                        siblings
                            .flatMap { call -> callToResolveResults(call, name, arity) }
                            .toTypedArray()
                    } else {
                        null
                    }
                }
                ?: emptyArray()
        }

    private fun callToResolveResults(call: Call, name: Name, arity: Arity): List<ResolveResult> =
        when (val semantic = call.semantic) {
            is Clause -> {
                semantic
                    .nameArityInterval
                    ?.let { nameArityInterval ->
                        nameArityIntervalToResolveResult(
                            call,
                            name,
                            arity,
                            nameArityInterval
                        )
                    }
                    ?.let { listOf(it) }
                    .orEmpty()
            }
            is Delegation -> {
                semantic
                    .nameArityInterval
                    ?.let { nameArityInterval ->
                        nameArityIntervalToResolveResult(
                            call,
                            name,
                            arity,
                            nameArityInterval
                        )
                    }
                    ?.let(::listOf)
                    .orEmpty()
            }
            is org.elixir_lang.semantic.chain.For -> {
                val resolveResultList = mutableListOf<ResolveResult>()

                For.treeWalkDown(call, ResolveState.initial()) { child, _ ->
                    if (child is Call) {
                        resolveResultList.addAll(callToResolveResults(child, name, arity))
                    }

                    true
                }

                resolveResultList
            }
            else -> emptyList()
        }

    private fun nameArityIntervalToResolveResult(
        call: Call,
        name: Name,
        arity: Arity,
        nameArityInterval: NameArityInterval
    ): PsiElementResolveResult? {
        val definerName = nameArityInterval.name

        return if (definerName.startsWith(name)) {
            val definerArityInterval = nameArityInterval.arityInterval
            val validResult = (arity in definerArityInterval) && (definerName == name)

            PsiElementResolveResult(call, validResult)
        } else {
            null
        }
    }
}
