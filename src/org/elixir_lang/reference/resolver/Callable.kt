package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AccumulatorContinue
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModular

object Callable : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Callable> {
    override fun resolve(callable: org.elixir_lang.reference.Callable, incompleteCode: Boolean): Array<ResolveResult> {
        val element = callable.element

        return try {
            if (element is org.elixir_lang.psi.call.qualification.Qualified) {
                resolveElement(element)
            } else {
                resolveElement(element, incompleteCode)
            }.toTypedArray()
        } catch (stackOverflowError: StackOverflowError) {
            Logger.error(Callable::class.java, "StackOverflowError when annotating Call", element)

            emptyArray()
        }
    }

    private fun resolveElement(element: Call, incompleteCode: Boolean): List<ResolveResult> =
            /* DO NOT use `getName()` as it will return the NameIdentifier's text, which for `defmodule` is the Alias, not
              `defmodule` */
            element.functionName()?.let { name ->
                val resolvedFinalArity = element.resolvedFinalArity()
                val resolveResultList = mutableListOf<ResolveResult>()

                // UnqualifiedNorArgumentsCall prevents `foo()` from being treated as a variable.
                // resolvedFinalArity prevents `|> foo` from being counted as 0-arity
                if (element is UnqualifiedNoArgumentsCall<*> && resolvedFinalArity == 0) {
                    val variableResolveList = org.elixir_lang.psi.scope.variable.MultiResolve.resolveResultList(
                            name,
                            incompleteCode,
                            element
                    )

                    if (variableResolveList != null) {
                        resolveResultList.addAll(variableResolveList)
                    }
                }

                val callDefinitionClauseResolveResultList = org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                        name,
                        resolvedFinalArity,
                        incompleteCode,
                        element
                )

                resolveResultList.addAll(callDefinitionClauseResolveResultList)

                org.elixir_lang.reference.Resolver.preferred(element, incompleteCode, resolveResultList)
            } ?: emptyList()

    private fun resolveElement(element: Qualified): List<ResolveResult> =
            element.qualifiedToModular()?.let { modular ->
                element.functionName()?.let { name ->
                    val resolvedFinalArity = element.resolvedFinalArity()

                    Modular.callDefinitionClauseCallFoldWhile(
                            modular,
                            name,
                            mutableListOf<ResolveResult>()
                    ) { callDefinitionClauseCall, _, arityRange, acc ->
                        acc.add(PsiElementResolveResult(callDefinitionClauseCall, arityRange.contains(resolvedFinalArity)))
                        AccumulatorContinue(acc, true)
                    }.accumulator
                }
            } ?: emptyList()
}
