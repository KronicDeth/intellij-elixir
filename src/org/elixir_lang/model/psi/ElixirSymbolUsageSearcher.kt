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
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.ResolveState
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.usages.impl.rules.UsageType
import com.intellij.util.Query
import com.intellij.util.concurrency.annotations.RequiresReadLock
import com.intellij.openapi.progress.ProcessCanceledException
import org.elixir_lang.model.psi.callback.BehaviourMembership
import org.elixir_lang.model.psi.callback.Callback
import org.elixir_lang.model.psi.function.FunctionSymbol
import org.elixir_lang.model.psi.protocol.ProtocolFunction
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable
import org.elixir_lang.reference.MfaFunctionReference

/**
 * Shared [UsageSearcher] for Elixir [ElixirSymbolWithUsages] targets. Registered once over
 * [UsageSearchParameters]; dispatches on the target type.
 *
 * - Every searchable symbol contributes its self-declaration usage.
 * - A [Callback] additionally contributes each implementing `def`/`defmacro` clause of matching
 *   name/arity/kind, where the enclosing module implements the behaviour (see [BehaviourMembership]).
 * - A [ProtocolFunction] additionally contributes each **call site** that dispatches to it
 *   (`Protocol.function(args)` of matching name/arity). Implementations are intentionally *not*
 *   usages - they are reached via "Go To Implementation" (`Ctrl+Alt+B`) / the gutter marker.
 * - A [FunctionSymbol] additionally contributes each **call site** that dispatches to it.
 *   Qualified calls (`Module.function(args)`) are matched via [Call.isCalling]; unqualified calls
 *   require scope resolution and are resolved by delegating to the legacy [Callable] walker.
 *
 * Candidates are found by a name-anchored text search (efficient, only visits files containing the
 * name); behaviour membership is resolved via [BehaviourMembership]; call-site dispatch is resolved
 * via [Call.isCalling].
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
        } else if (target is ProtocolFunction) {
            queries += protocolCallSiteQuery(parameters.project, target, parameters.searchScope)
        } else if (target is FunctionSymbol) {
            queries += functionDeclarationFamilyQuery(parameters.project, target, parameters.searchScope)
            queries += functionCallSiteQuery(parameters.project, target, parameters.searchScope)
        }
        return queries
    }

    private fun implementationQuery(
        project: Project,
        callback: Callback,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
            SearchService.getInstance()
                .searchWord(project, callback.name)
                .caseSensitive(true) // Elixir function/macro names are case-sensitive
                .inContexts(SearchContext.inCode())
                .inScope(searchScope)
                .buildQuery(ImplementationMapper(callback.createPointer()))

    private fun protocolCallSiteQuery(
        project: Project,
        pf: ProtocolFunction,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
            SearchService.getInstance()
                .searchWord(project, pf.name)
                .caseSensitive(true)
                .inContexts(SearchContext.inCode())
                .inScope(searchScope)
                .buildQuery(ProtocolCallSiteMapper(pf.createPointer()))

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

            val nameArity =
                    CallDefinitionClause.nameArityInterval(defClause, ResolveState.initial()) ?: return emptyList()
            if (nameArity.name != callback.name || callback.arity !in nameArity.arityInterval) return emptyList()

            // `@callback` is implemented by `def`, `@macrocallback` by `defmacro`.
            val kindMatches =
                    if (callback.macro) CallDefinitionClause.isMacro(defClause) else CallDefinitionClause.isFunction(
                        defClause
                    )
            if (!kindMatches) return emptyList()

            val implements =
                    when (val usingDefiner = defClause.enclosingUsingDefiner()) {
                        // Default implementation: a `def` inside a `__using__` quote whose module is the
                        // behaviour itself, or which injects `@behaviour B`.
                        is Call -> {
                            val definingModule = CallDefinitionClause.enclosingModularMacroCall(usingDefiner)
                            definingModule != null &&
                                    (BehaviourMembership.moduleName(definingModule) == callback.moduleName ||
                                            callback.moduleName in BehaviourMembership.namesInjectedByDefiner(
                                        usingDefiner,
                                        definingModule
                                    ))
                        }
                        // Concrete implementation: a `def` directly in a module that declares the behaviour.
                        else -> {
                            val modular =
                                    CallDefinitionClause.enclosingModularMacroCall(defClause) ?: return emptyList()
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

    /**
     * Maps each occurrence of a protocol function name to a **call site** that dispatches to it, if any.
     * A call site is a qualified call `Protocol.function(args)` (of matching name/arity) whose resolved
     * module is the protocol. The search framework invokes [mapOccurrence] under a read action.
     */
    private class ProtocolCallSiteMapper(
        private val protocolFunctionPointer: Pointer<out ProtocolFunction>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val protocolFunction = protocolFunctionPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence

            // Nearest enclosing call. A call site is an invocation, not a definition clause.
            val call = leaf.enclosingCalls().firstOrNull() ?: return emptyList()
            if (CallDefinitionClause.`is`(call)) return emptyList()

            // The occurrence must be the call's own function name, not one of its arguments.
            val nameElement = call.functionNameElement() ?: return emptyList()
            if (!PsiTreeUtil.isAncestor(nameElement, leaf, false)) return emptyList()

            // The call must dispatch to this protocol function: `<protocolName>.<name>/<arity>`.
            if (!call.isCalling(protocolFunction.protocolName, protocolFunction.name, protocolFunction.arity)) {
                return emptyList()
            }

            return listOf(
                ElixirPsiUsage.create(
                    nameElement,
                    TextRange(0, nameElement.textLength),
                    declaration = false,
                    usageType = CALL
                )
            )
        }
    }

    /**
     * Builds a word-index query that finds every source token whose text matches the function name,
     * then hands each occurrence to [FunctionCallSiteMapper].
     */
    private fun functionCallSiteQuery(
        project: Project,
        symbol: FunctionSymbol,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
        SearchService.getInstance()
            .searchWord(project, symbol.name)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inScope(searchScope)
            .buildQuery(FunctionCallSiteMapper(symbol.createPointer()))

    /**
     * Finds additional declaration clauses for the same logical function family
     * (`module.name/arity`) as [symbol].
     */
    private fun functionDeclarationFamilyQuery(
        project: Project,
        symbol: FunctionSymbol,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
        SearchService.getInstance()
            .searchWord(project, symbol.name)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inScope(searchScope)
            .buildQuery(FunctionDeclarationFamilyMapper(symbol.createPointer()))

    /**
     * Maps each occurrence of a function name to a matching declaration clause in the same
     * logical function family (`module.name/arity`).
     */
    private class FunctionDeclarationFamilyMapper(
        private val symbolPointer: Pointer<out FunctionSymbol>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val symbol = symbolPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence

            val defClause = leaf.enclosingCalls().firstOrNull { CallDefinitionClause.`is`(it) } ?: return emptyList()
            val nameIdentifier = CallDefinitionClause.nameIdentifier(defClause) ?: return emptyList()
            if (!PsiTreeUtil.isAncestor(nameIdentifier, leaf, false)) return emptyList()
            if (!defClause.matchesFunctionFamily(symbol)) return emptyList()

            // Direct usage query already contributes the symbol's own declaration.
            if (defClause.containingFile.virtualFile == symbol.file.virtualFile &&
                nameIdentifier.textRange == symbol.range
            ) {
                return emptyList()
            }

            return listOf(
                ElixirPsiUsage.create(
                    nameIdentifier,
                    TextRange(0, nameIdentifier.textLength),
                    declaration = true
                )
            )
        }
    }

    /**
     * Maps each occurrence of a function name to a **call site** for the given [FunctionSymbol].
     *
     * Qualified calls (`Module.function(args)`) are matched by name/arity without scope resolution.
     * Unqualified calls are resolved via the legacy [Callable] scope-walker.
     */
    private class FunctionCallSiteMapper(
        private val symbolPointer: Pointer<out FunctionSymbol>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val symbol = symbolPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence

            mfaTupleUsage(leaf, symbol)?.let { return listOf(it) }

            val call = leaf.enclosingCalls().firstOrNull() ?: return emptyList()
            // Skip definition heads/clauses - they are declarations, not call sites.
            if (CallDefinitionClause.isHead(call) || CallDefinitionClause.`is`(call)) return emptyList()

            val nameElement = call.functionNameElement() ?: return emptyList()
            if (!PsiTreeUtil.isAncestor(nameElement, leaf, false)) return emptyList()

            // Fast path: qualified call (`ModuleName.functionName/arity`).
            val matched = if (call.isCalling(symbol.moduleName, symbol.name, symbol.arity)) {
                true
            } else {
                // Unqualified call: resolve via legacy scope-walker and check against this symbol.
                val resolved = Callable(call).multiResolve(false)
                    .filter { it.isValidResult }
                    .mapNotNull { it.element as? Call }
                    .filter { CallDefinitionClause.`is`(it) }
                    .flatMap { FunctionSymbol.fromClause(it) }
                resolved.any { it == symbol }
            }

            if (!matched) return emptyList()

            return listOf(
                ElixirPsiUsage.create(
                    nameElement,
                    TextRange(0, nameElement.textLength),
                    declaration = false,
                    usageType = CALL
                )
            )
        }

        @RequiresReadLock
        private fun mfaTupleUsage(leaf: PsiElement, symbol: FunctionSymbol): PsiUsage? {
            val atom = PsiTreeUtil.getParentOfType(leaf, ElixirAtom::class.java, false) ?: return null
            val references = PsiReferenceService.getService()
                .getReferences(atom, PsiReferenceService.Hints.NO_HINTS)
                .filterIsInstance<MfaFunctionReference>()
            if (references.isEmpty()) return null

            val matches = references.any { reference ->
                reference.multiResolve(false)
                    .filter { it.isValidResult }
                    .mapNotNull { it.element as? Call }
                    .flatMap { FunctionSymbol.fromClause(it) }
                    .any { it == symbol }
            }
            if (!matches) return null

            return ElixirPsiUsage.create(
                leaf,
                TextRange(0, leaf.textLength),
                declaration = false,
                usageType = CALL
            )
        }
    }
}

private val IMPLEMENTATION = UsageType { "Implementation" }

private val CALL = UsageType { "Function call" }

private const val USING = "__using__"

private fun PsiElement.enclosingCalls(): Sequence<Call> =
        generateSequence(parent) { it.parent }.takeWhile { it !is PsiFile }.filterIsInstance<Call>()

@RequiresReadLock
private fun Call.matchesFunctionFamily(symbol: FunctionSymbol): Boolean {
    val enclosingModular = CallDefinitionClause.enclosingModularMacroCall(this) ?: return false
    val moduleName = runCatching { org.elixir_lang.psi.Module.name(enclosingModular) }
        .getOrElse { if (it is ProcessCanceledException) throw it else null }
        ?: return false
    if (moduleName != symbol.moduleName) return false

    val nameArity = CallDefinitionClause.nameArityInterval(this, ResolveState.initial()) ?: return false
    if (nameArity.name != symbol.name || symbol.arity !in nameArity.arityInterval) return false

    val clauseIsMacro = CallDefinitionClause.isMacro(this)
    return clauseIsMacro == symbol.macro
}

/** Nearest enclosing `defmacro __using__/1` clause, or `null`. */
@RequiresReadLock
private fun Call.enclosingUsingDefiner(): Call? =
        enclosingCalls().firstOrNull { call ->
            CallDefinitionClause.`is`(call) &&
                    CallDefinitionClause.nameArityInterval(call, ResolveState.initial())?.name == USING
        }
