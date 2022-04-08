package org.elixir_lang.reference.resolver

import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.Arity
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModulars
import org.elixir_lang.psi.scope.VisitedElementSetResolveResult
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.semantic.Unquote
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic

object Callable : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Callable> {
    override fun resolve(callable: org.elixir_lang.reference.Callable, incompleteCode: Boolean): Array<ResolveResult> {
        val element = callable.element
        val resolvedPrimaryArity = element.resolvedPrimaryArity() ?: 0

        return resolve(element, resolvedPrimaryArity, incompleteCode)
    }

    fun resolve(call: Call, resolvedPrimaryArity: Arity, incompleteCode: Boolean): Array<ResolveResult> {
        val preferred = resolvePreferred(call, resolvedPrimaryArity, incompleteCode)
        val expanded = expand(preferred)

        return expanded.toTypedArray()
    }

    private fun expand(visitedElementSetResolveResultList: List<VisitedElementSetResolveResult>): List<PsiElementResolveResult> =
        visitedElementSetResolveResultList
            .flatMap { visitedElementSetResolveResult ->
                val visitedElementSet = visitedElementSetResolveResult.visitedElementSet
                val validResult = visitedElementSetResolveResult.isValidResult

                val pathResolveResultList =
                    visitedElementSet
                        .filter { visitedElement ->
                            when (visitedElement.semantic) {
                                is org.elixir_lang.semantic.call.definition.Delegation,
                                is org.elixir_lang.semantic.Import,
                                is org.elixir_lang.semantic.Use -> true
                                else -> false
                            }
                        }
                        .map { PsiElementResolveResult(it, validResult) }

                val terminalResolveResult = PsiElementResolveResult(
                    visitedElementSetResolveResult.element,
                    visitedElementSetResolveResult.isValidResult
                )

                listOf(terminalResolveResult) + pathResolveResultList
            }
            // deduplicate shared `defdelegate`, `import`, or `use`
            .groupBy { it.element }
            .map { (_element, resolveResults) -> resolveResults.first() }

    private fun resolvePreferred(
        element: Call,
        resolvedPrimaryArity: Arity,
        incompleteCode: Boolean
    ): List<VisitedElementSetResolveResult> {
        val all = resolveAll(element, resolvedPrimaryArity, incompleteCode)

        return org.elixir_lang.reference.Resolver.preferred(element, incompleteCode, all)
    }

    private fun resolveAll(element: Call, resolvedPrimaryArity: Arity, incompleteCode: Boolean) =
        /* DO NOT use `getName()` as it will return the NameIdentifier's text, which for `defmodule` is the Alias,
           not `defmodule` */
        element
            .functionName()
            ?.let { name -> resolve(element, name, resolvedPrimaryArity, incompleteCode) }
            ?: emptyList()

    private fun resolve(element: Call, name: String, resolvedPrimaryArity: Arity, incompleteCode: Boolean) =
        resolveInScope(element, name, resolvedPrimaryArity, incompleteCode)
            .takeIf { set -> set.any(ResolveResult::isValidResult) }
            ?: nameArityInAnyModule(element, name, resolvedPrimaryArity, incompleteCode)

    private fun resolveInScope(
        element: Call,
        name: String,
        resolvedPrimaryArity: Arity,
        incompleteCode: Boolean
    ): List<VisitedElementSetResolveResult> =
        try {
            if (element is Qualified) {
                resolveQualified(element, name, resolvedPrimaryArity, incompleteCode)
            } else {
                resolveUnqualified(element, name, resolvedPrimaryArity, incompleteCode)
            }
        } catch (stackOverflowError: StackOverflowError) {
            Logger.error(Callable::class.java, "StackOverflowError when annotating Call", element)

            emptyList()
        }

    private fun resolveUnqualified(
        element: Call,
        name: String,
        resolvedPrimaryArity: Arity,
        incompleteCode: Boolean
    ): List<VisitedElementSetResolveResult> {
        val resolveResultList = mutableListOf<VisitedElementSetResolveResult>()

        // UnqualifiedNoArgumentsCall prevents `foo()` from being treated as a variable.
        // resolvedFinalArity prevents `|> foo` from being counted as 0-arity
        if (element is UnqualifiedNoArgumentsCall<*> && resolvedPrimaryArity == 0) {
            val variableResolveList = org.elixir_lang.psi.scope.variable.MultiResolve.resolveResultList(
                name,
                incompleteCode,
                element
            )

            resolveResultList.addAll(variableResolveList)
        }

        val callDefinitionClauseResolveResultList =
            org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                name,
                resolvedPrimaryArity,
                incompleteCode,
                element
            )

        resolveResultList.addAll(callDefinitionClauseResolveResultList)

        return resolveResultList
    }

    private fun resolveQualified(
        element: Qualified,
        name: String,
        arity: Arity,
        incompleteCode: Boolean
    ): List<VisitedElementSetResolveResult> {
        val modulars = element.qualifiedToModulars()

        return if (modulars.isNotEmpty()) {
            val resolvableName = name.takeUnless { Unquote.isQualified(element, it) }

            modulars.flatMap { modular ->
                org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                    resolvableName,
                    arity,
                    incompleteCode,
                    modular
                )
            }
        } else {
            emptyList()
        }
    }

    private fun nameArityInAnyModule(
        element: Call,
        name: String,
        arity: Arity,
        incompleteCode: Boolean
    ): List<VisitedElementSetResolveResult> {
        val project = element.project
        val resolveResults = mutableListOf<VisitedElementSetResolveResult>()

        if (!DumbService.isDumb(project)) {
            val keys = mutableListOf<String>()
            val stubIndex = StubIndex.getInstance()

            stubIndex.processAllKeys(AllName.KEY, project) { key ->
                if ((incompleteCode && key.startsWith(name)) || key == name) {
                    keys.add(key)
                }

                true
            }

            val scope = GlobalSearchScope.allScope(project)
            // results are never valid because the qualifier is unknown
            val validResult = false

            for (key in keys) {
                stubIndex
                    .processElements(AllName.KEY, key, project, scope, NamedElement::class.java) { namedElement ->
                        when (val semantic = namedElement.semantic) {
                            is Clause -> {
                                if (incompleteCode ||
                                    semantic.nameArityInterval?.arityInterval?.contains(arity) == true
                                ) {
                                    resolveResults.add(
                                        VisitedElementSetResolveResult(
                                            namedElement,
                                            validResult,
                                            emptySet()
                                        )
                                    )
                                }
                            }
                            is org.elixir_lang.semantic.type.definition.source.Callback ->
                                if (incompleteCode ||
                                    semantic.nameArity.arity == arity
                                ) {
                                    resolveResults.add(
                                        VisitedElementSetResolveResult(
                                            namedElement,
                                            validResult,
                                            emptySet()
                                        )
                                    )
                                }
                        }

                        true
                    }
            }
        }

        return resolveResults
    }
}
