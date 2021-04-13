package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModulars
import org.elixir_lang.psi.stub.index.AllName

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

    private fun resolveQualified(element: Qualified, name: String, incompleteCode: Boolean): List<ResolveResult> {
        val modulars = element.qualifiedToModulars()

        return if (modulars.isNotEmpty()) {
            modulars.flatMap { modular ->
                val resolvedFinalArity = element.resolvedFinalArity()

                org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                        name,
                        resolvedFinalArity,
                        incompleteCode,
                        modular
                )
            }
        } else {
            val relativeIdentifier = element.relativeIdentifier

            if (relativeIdentifier.children.isEmpty()) {
                val name = relativeIdentifier.text
                val project = element.project
                val keys = mutableListOf<String>()

                StubIndex.getInstance().processAllKeys(AllName.KEY, project) { key ->
                    if ((incompleteCode && key.startsWith(name)) || key == name) {
                        keys.add(key)
                    }

                    true
                }

                val arity = element.resolvedFinalArity()
                val resolveResults = mutableListOf<ResolveResult>()
                // results are never valid because the qualifier is unknown
                val validResult = false

                for (key in keys) {
                    StubIndex.getInstance().processElements(AllName.KEY, key, project, GlobalSearchScope.allScope(project), NamedElement::class.java) { namedElement ->
                        if (namedElement is Call &&
                                org.elixir_lang.psi.CallDefinitionClause.`is`(namedElement) &&
                                (incompleteCode || nameArityRange(namedElement)?.arityRange?.contains(arity) == true)) {
                            resolveResults.add(PsiElementResolveResult(namedElement, validResult))
                        }

                        true
                    }
                }

                resolveResults
            } else {
                Logger.error(Callable::class.java, "Don't know how to calculate name for relativeIdentifier in qualified element", element)

                emptyList()
            }
        }
    }
}
