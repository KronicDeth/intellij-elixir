package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.Usage
import com.intellij.find.usages.api.UsageSearchParameters
import com.intellij.find.usages.api.UsageSearcher
import com.intellij.model.Pointer
import com.intellij.model.search.LeafOccurrence
import com.intellij.model.search.LeafOccurrenceMapper
import com.intellij.model.search.SearchContext
import com.intellij.model.search.SearchService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.ResolveState
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.usages.impl.rules.UsageType
import com.intellij.util.Query
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.callback.BehaviourMembership
import org.elixir_lang.model.psi.callback.Callback
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * Shared [UsageSearcher] for Elixir [ElixirSymbolWithUsages] targets. Registered once over
 * [UsageSearchParameters]; dispatches on the target type.
 *
 * - Every searchable symbol contributes its self-declaration usage.
 * - A [Callback] additionally contributes each implementing `def`/`defmacro` clause of matching
 *   name/arity/kind, where the enclosing module implements the behaviour (see [BehaviourMembership]).
 *
 * Candidates are found by a name-anchored text search (efficient, only visits files containing the
 * name); behaviour membership is resolved via [BehaviourMembership] (shared with the reverse
 * `def` → `@callback` reference so both directions agree).
 */
@Suppress("UnstableApiUsage")
internal class ElixirSymbolUsageSearcher : UsageSearcher {
    override fun collectSearchRequests(parameters: UsageSearchParameters): Collection<Query<out Usage>> {
        val target = parameters.target
        if (target !is ElixirSymbolWithUsages) return emptyList()

        val queries = mutableListOf<Query<out Usage>>()
        queries += ElixirDirectUsageQuery(ElixirPsiUsage.create(target.file, target.range, declaration = true))

        if (target is Callback) {
            queries += implementationQuery(parameters.project, target, parameters.searchScope)
        }
        return queries
    }

    private fun implementationQuery(project: Project, callback: Callback, searchScope: SearchScope): Query<out PsiUsage> =
        SearchService.getInstance()
            .searchWord(project, callback.name)
            .caseSensitive(true) // Elixir function/macro names are case-sensitive
            .inContexts(SearchContext.inCode())
            .inScope(searchScope)
            .buildQuery(ImplementationMapper(callback.createPointer()))

    /**
     * Maps each occurrence of the callback name to an implementing definition clause, if any.
     * The search framework invokes [mapOccurrence] under a read action.
     */
    private class ImplementationMapper(
        private val callbackPointer: Pointer<out Callback>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val callback = callbackPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence

            // Nearest enclosing call-definition clause (def/defp/defmacro/...).
            val defClause = leaf.enclosingCalls().firstOrNull { CallDefinitionClause.`is`(it) } ?: return emptyList()

            // The occurrence must be the clause's own name, not a call in its body.
            val nameIdentifier = CallDefinitionClause.nameIdentifier(defClause) ?: return emptyList()
            if (!PsiTreeUtil.isAncestor(nameIdentifier, leaf, false)) return emptyList()

            val nameArity = CallDefinitionClause.nameArityInterval(defClause, ResolveState.initial()) ?: return emptyList()
            if (nameArity.name != callback.name || callback.arity !in nameArity.arityInterval) return emptyList()

            // `@callback` is implemented by `def`, `@macrocallback` by `defmacro`.
            val kindMatches =
                if (callback.macro) CallDefinitionClause.isMacro(defClause) else CallDefinitionClause.isFunction(defClause)
            if (!kindMatches) return emptyList()

            val implements =
                when (val usingDefiner = defClause.enclosingUsingDefiner()) {
                    // Default implementation: a `def` inside a `__using__` quote whose module is the
                    // behaviour itself, or which injects `@behaviour B`.
                    is Call -> {
                        val definingModule = CallDefinitionClause.enclosingModularMacroCall(usingDefiner)
                        definingModule != null &&
                                (BehaviourMembership.moduleName(definingModule) == callback.moduleName ||
                                        callback.moduleName in BehaviourMembership.namesInjectedByDefiner(usingDefiner, definingModule))
                    }
                    // Concrete implementation: a `def` directly in a module that declares the behaviour.
                    else -> {
                        val modular = CallDefinitionClause.enclosingModularMacroCall(defClause) ?: return emptyList()
                        BehaviourMembership.implements(modular, callback.moduleName)
                    }
                }
            if (!implements) return emptyList()

            return listOf(
                ElixirPsiUsage.create(
                    nameIdentifier,
                    TextRange(0, nameIdentifier.textLength),
                    declaration = false,
                    usageType = IMPLEMENTATION
                )
            )
        }
    }

}

private val IMPLEMENTATION = UsageType { "Implementation" }

private const val USING = "__using__"

private fun PsiElement.enclosingCalls(): Sequence<Call> =
    generateSequence(parent) { it.parent }.takeWhile { it !is PsiFile }.filterIsInstance<Call>()

/** Nearest enclosing `defmacro __using__/1` clause, or `null`. */
@RequiresReadLock
private fun Call.enclosingUsingDefiner(): Call? =
    enclosingCalls().firstOrNull { call ->
        CallDefinitionClause.`is`(call) &&
                CallDefinitionClause.nameArityInterval(call, ResolveState.initial())?.name == USING
    }
