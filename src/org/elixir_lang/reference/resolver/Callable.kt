package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.Arity
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.CallDefinitionClause.enclosingModularMacroCall
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.UNQUOTE
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModulars
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.structure_view.element.CallDefinitionHead
import org.elixir_lang.structure_view.element.Callback

object Callable : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Callable> {
    override fun resolve(callable: org.elixir_lang.reference.Callable, incompleteCode: Boolean): Array<ResolveResult> {
        val element = callable.element

        return resolvePreferred(element, incompleteCode).toTypedArray()
    }

    private fun resolvePreferred(element: Call, incompleteCode: Boolean): List<ResolveResult> {
        val all = resolveAll(element, incompleteCode)

        return org.elixir_lang.reference.Resolver.preferred(element, incompleteCode, all)
    }

    private fun resolveAll(element: Call, incompleteCode: Boolean) =
            /* DO NOT use `getName()` as it will return the NameIdentifier's text, which for `defmodule` is the Alias,
               not `defmodule` */
            element
                    .functionName()
                    ?.let { name -> resolve(element, name, incompleteCode) }
                    ?: emptyList()

    private fun resolve(element: Call, name: String, incompleteCode: Boolean) =
        resolveInScope(element, name, incompleteCode)
                .takeIf(Collection<ResolveResult>::isNotEmpty)
                ?: nameArityInAnyModule(element, name, incompleteCode)

    private fun resolveInScope(element: Call, name: String, incompleteCode: Boolean): List<ResolveResult> =
        try {
            if (element is Qualified) {
                resolveQualified(element, name, incompleteCode)
            } else {
                resolveUnqualified(element, name, incompleteCode)
            }
        } catch (stackOverflowError: StackOverflowError) {
            Logger.error(Callable::class.java, "StackOverflowError when annotating Call", element)

            emptyList()
        }

    private fun resolveUnqualified(element: Call, name: String, incompleteCode: Boolean): List<ResolveResult> {
        val resolvedPrimaryArity = element.resolvedPrimaryArity() ?: 0
        val resolveResultList = mutableListOf<ResolveResult>()

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

        val callDefinitionClauseResolveResultList = org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                name,
                resolvedPrimaryArity,
                incompleteCode,
                element
        )

        resolveResultList.addAll(callDefinitionClauseResolveResultList)

        return resolveResultList
    }

    private fun resolveQualified(element: Qualified, name: String, incompleteCode: Boolean): List<ResolveResult> {
        val modulars = element.qualifiedToModulars()

        return if (modulars.isNotEmpty()) {
            val resolvedFinalArity = element.resolvedFinalArity()
            val resolvableName = name.takeUnless { Unquote.isQualified(element, it) }

            modulars.flatMap { modular ->
                org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                        resolvableName,
                        resolvedFinalArity,
                        incompleteCode,
                        modular
                )
            }
        } else {
            emptyList()
        }
    }

    private fun arityInModulars(arity: Arity, modulars: Set<Call>): List<ResolveResult> =
       modulars.flatMap { modular -> arityInModular(arity, modular) }

    private fun arityInModular(arity: Arity, modular: Call): List<ResolveResult> {

        TODO("Not yet implemented")
    }

    private fun nameArityInAnyModule(element: Call, name: String, incompleteCode: Boolean): List<ResolveResult> {
        val project = element.project
        val keys = mutableListOf<String>()
        val stubIndex = StubIndex.getInstance()

        stubIndex.processAllKeys(AllName.KEY, project) { key ->
            if ((incompleteCode && key.startsWith(name)) || key == name) {
                keys.add(key)
            }

            true
        }

        val scope = GlobalSearchScope.allScope(project)
        val arity = element.resolvedFinalArity()
        val resolveResults = mutableListOf<ResolveResult>()
        // results are never valid because the qualifier is unknown
        val validResult = false

        for (key in keys) {
            stubIndex
                    .processElements(AllName.KEY, key, project, scope, NamedElement::class.java) { namedElement ->
                        if (namedElement is Call) {
                            val state = ResolveState.initial()

                            if (CallDefinitionClause.`is`(namedElement)) {
                                if (incompleteCode ||
                                        nameArityInterval(namedElement, state)
                                                ?.arityInterval?.contains(arity) == true) {
                                    resolveResults.add(PsiElementResolveResult(namedElement, validResult))
                                }
                            } else if (Callback.`is`(namedElement)) {
                                if (incompleteCode ||
                                        Callback
                                                .headCall(namedElement as AtUnqualifiedNoParenthesesCall<*>)
                                                ?.let { CallDefinitionHead.nameArityInterval(it, state) }
                                                ?.arityInterval?.contains(arity) == true) {
                                    resolveResults.add(PsiElementResolveResult(namedElement, validResult))
                                }
                            }
                        }

                true
            }
        }

        return resolveResults
    }
}
