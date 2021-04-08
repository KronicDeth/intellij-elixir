package org.elixir_lang.reference.resolver

import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModulars

object Callable : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Callable> {
    override fun resolve(callable: org.elixir_lang.reference.Callable, incompleteCode: Boolean): Array<ResolveResult> {
        val element = callable.element

        /* DO NOT use `getName()` as it will return the NameIdentifier's text, which for `defmodule` is the Alias, not
           `defmodule` */
        val resolveResultList = element.functionName()?.let { name ->
            try {
                if (element is Qualified) {
                    resolveQualified(element, name, incompleteCode)
                } else {
                    resolveUnqualified(element, name, incompleteCode)
                }
            } catch (stackOverflowError: StackOverflowError) {
                Logger.error(Callable::class.java, "StackOverflowError when annotating Call", element)

                null
            }
        } ?: emptyList()


        return org.elixir_lang.reference.Resolver.preferred(element, incompleteCode, resolveResultList).toTypedArray()
    }

    private fun resolveUnqualified(element: Call, name: String, incompleteCode: Boolean): List<ResolveResult> {
        val resolvedFinalArity = element.resolvedFinalArity()
        val resolveResultList = mutableListOf<ResolveResult>()

        // UnqualifiedNoArgumentsCall prevents `foo()` from being treated as a variable.
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

        return resolveResultList
    }

    private fun resolveQualified(element: Qualified, name: String, incompleteCode: Boolean): List<ResolveResult> =
            element.qualifiedToModulars().flatMap { modular ->
                val resolvedFinalArity = element.resolvedFinalArity()

                org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                        name,
                        resolvedFinalArity,
                        incompleteCode,
                        modular
                )
            }
}
