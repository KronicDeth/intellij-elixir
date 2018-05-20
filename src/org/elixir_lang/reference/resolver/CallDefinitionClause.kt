package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.Arity
import org.elixir_lang.Name
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall
import org.elixir_lang.structure_view.element.CallDefinitionSpecification.Companion.typeNameArity

object CallDefinitionClause : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.CallDefinitionClause> {
    override fun resolve(callDefinitionClause: org.elixir_lang.reference.CallDefinitionClause,
                         incompleteCode: Boolean): Array<ResolveResult> =
            enclosingModularMacroCall(callDefinitionClause.moduleAttribute)?.macroChildCalls()?.let { siblings ->
                if (siblings.isNotEmpty()) {
                    val nameArity = typeNameArity(callDefinitionClause.element)
                    val name = nameArity.name
                    val arity = nameArity.arity

                    siblings
                            .filter { org.elixir_lang.structure_view.element.CallDefinitionClause.`is`(it) }
                            .mapNotNull { call -> callToResolveResult(call, incompleteCode, name, arity) }
                            .toTypedArray()
                } else {
                    null
                }
            } ?: emptyArray()

    private fun callToResolveResult(call: Call, incompleteCode: Boolean, name: Name, arity: Arity): ResolveResult? =
            org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange(call)
                    ?.let { callNameArityRange ->
                        val callName = callNameArityRange.name

                        if (callName == name) {
                            val callArityRange = callNameArityRange.arityRange

                            when {
                                arity in callArityRange -> PsiElementResolveResult(call, true)
                                arity < callArityRange.endInclusive -> PsiElementResolveResult(call, false)
                                else -> null
                            }
                        } else if (incompleteCode && callName.startsWith(name)) {
                            val callArityRange = callNameArityRange.arityRange

                            when {
                                arity in callArityRange -> PsiElementResolveResult(call, false)
                                arity < callArityRange.endInclusive -> PsiElementResolveResult(call, false)
                                else -> null
                            }
                        } else {
                            null
                        }
                    }
}
