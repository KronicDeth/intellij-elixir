package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.Usage
import com.intellij.model.Pointer
import com.intellij.model.psi.PsiSymbolReferenceService
import com.intellij.model.search.LeafOccurrence
import com.intellij.model.search.LeafOccurrenceMapper
import com.intellij.model.search.SearchContext
import com.intellij.model.search.SearchService
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.ResolveState
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.usages.impl.rules.UsageType
import com.intellij.util.AbstractQuery
import com.intellij.util.Processor
import com.intellij.util.Query
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.model.psi.atom.AtomReference
import org.elixir_lang.model.psi.atom.AtomSymbol
import org.elixir_lang.model.psi.callback.BehaviourMembership
import org.elixir_lang.model.psi.callback.Callback
import org.elixir_lang.model.psi.function.FunctionArityKeywordPairReference
import org.elixir_lang.model.psi.function.FunctionSymbol
import org.elixir_lang.model.psi.module.ModuleSymbol
import org.elixir_lang.model.psi.module_attribute.ModuleAttributeReference
import org.elixir_lang.model.psi.module_attribute.ModuleAttributeSymbol
import org.elixir_lang.model.psi.protocol.ProtocolFunction
import org.elixir_lang.model.psi.type.TypeReference
import org.elixir_lang.model.psi.type.TypeSymbol
import org.elixir_lang.model.psi.type.TypeVariableSymbol
import org.elixir_lang.model.psi.variable.VariableSymbol
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.moduleAttributeName
import org.elixir_lang.psi.impl.identifierTextRange
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.scope.ancestorTypeSpec
import org.elixir_lang.reference.Callable
import org.elixir_lang.reference.CaptureNameArity
import org.elixir_lang.psi.operation.capture.NonNumeric as CaptureNonNumeric
import org.elixir_lang.structure_view.element.CallDefinitionSpecification
import java.util.concurrent.Callable as JCallable

/**
 * Builds the usage queries for an [ElixirSymbolWithUsages] target; dispatches on the target type.
 * Free-standing (not part of any `Searcher`) so that [ElixirSymbolUsageSearcher] and
 * [ElixirRenameUsageSearcher] can share it: `Searcher` is `@ApiStatus.OverrideOnly`, so neither
 * searcher may invoke the other's interface methods - only the platform may call a `Searcher`.
 *
 * - Every searchable symbol contributes its self-declaration usage.
 * - A [Callback] additionally contributes each implementing `def`/`defmacro` clause of matching
 *   name/arity/kind, where the enclosing module implements the behaviour (see [BehaviourMembership]).
 * - A [ProtocolFunction] additionally contributes each **call site** that dispatches to it
 *   (`Protocol.function(args)` of matching name/arity) and each implementing `def`/`defmacro`
 *   clause inside a `defimpl` of the same protocol (so rename keeps every implementation in sync;
 *   for Find Usages the implementations are also reachable via "Go To Implementation").
 * - A [FunctionSymbol] additionally contributes each **call site** that dispatches to it.
 *   Qualified calls (`Module.function(args)`) are matched via [Call.isCalling]; unqualified calls
 *   require scope resolution and are resolved by delegating to the legacy [Callable] walker.
 *
 * Candidates are found by a name-anchored text search (efficient, only visits files containing the
 * name); behaviour membership is resolved via [BehaviourMembership]; call-site dispatch is resolved
 * via [Call.isCalling].
 */
@Suppress("UnstableApiUsage")
internal object ElixirUsageQueries {
    @RequiresReadLock
    fun searchRequests(
        project: Project,
        target: ElixirSymbolWithUsages,
        searchScope: SearchScope
    ): Collection<Query<out Usage>> {
        val queries = mutableListOf<Query<out Usage>>()
        queries += ElixirDirectUsageQuery(ElixirPsiUsage.create(target.file, target.range, declaration = true))

        queries += usageQueries(project, target, searchScope)

        // A symbol declared inside a decompiled file (e.g. a `.beam`) has usages that live in the SAME
        // decompiled file - for a BEAM type, every `@spec`/`@type` reference to it, plus the right-hand
        // side of its own `@type`. Those are invisible to the queries above: Find Usages is driven by
        // SearchService.searchWord over a GlobalSearchScope, which only visits word-indexed files, and a
        // `.beam` file's indexed content is its binary bytes - the decompiled text is never scanned. Re-run
        // the same per-symbol queries against a LocalSearchScope over the decompiled mirror: a
        // LocalSearchScope makes searchWord scan the given PSI text directly (no index), so the in-memory
        // mirror's occurrences are found. The global pass above cannot see these, so there is no overlap.
        //
        // `target.file` can arrive in either of two shapes: the navigable compiled file itself (e.g. when
        // the target is built directly from the caret), or - crucially, in the real Find Usages pipeline,
        // which dereferences the target through its `Pointer` on a background thread - the in-memory mirror
        // `ElixirFile`, because `TypeSymbol.createPointer()` restores `file` from the mirror element's
        // `containingFile`. A mirror is recognised by its `originalFile` being the compiled file. Handle both.
        val declarationFile = target.file
        val compiledFile: PsiCompiledFile? = when {
            declarationFile is PsiCompiledFile -> declarationFile
            declarationFile.originalFile is PsiCompiledFile -> declarationFile.originalFile as PsiCompiledFile
            else -> null
        }
        if (compiledFile != null) {
            // The decompiled mirror to scan (the same cached instance whether `target.file` arrived as the
            // compiled file or as the mirror restored through the pointer - `getMirror()` caches it).
            val mirror = compiledFile.decompiledPsiFile
            // The per-symbol mappers anchor each usage to the mirror element's `containingFile` - the
            // in-memory mirror `ElixirFile`, which has no editor document/VirtualFile. The usage view cannot
            // map such usages to real lines, so they all collapse onto a single line. Re-anchor them to the
            // navigable compiled BEAM file (the mirror's `originalFile`): the mirror text is byte-for-byte the
            // decompiled editor's document text, so the absolute offsets are identical and now resolve to the
            // correct decompiled-editor lines - exactly like the declaration usage and Go To Declaration.
            usageQueries(project, target, LocalSearchScope(mirror)).mapTo(queries) { query ->
                query.mapping { usage: Usage ->
                    if (usage is ElixirPsiUsage && usage.file !== compiledFile) {
                        ElixirPsiUsage(compiledFile, usage.range, usage.declaration, usage.usageType, usage.usageTextByName)
                    } else {
                        usage
                    }
                }
            }
        }

        return queries
    }

    private fun usageQueries(
        project: Project,
        target: ElixirSymbolWithUsages,
        searchScope: SearchScope
    ): List<Query<out Usage>> {
        val queries = mutableListOf<Query<out Usage>>()
        when (target) {
            is Callback -> {
                queries += implementationQuery(project, target, searchScope)
            }

            is ProtocolFunction -> {
                queries += protocolCallSiteQuery(project, target, searchScope)
                queries += protocolImplementationQuery(project, target, searchScope)
            }

            is FunctionSymbol -> {
                queries += functionDeclarationFamilyQuery(project, target, searchScope)
                queries += functionCallSiteQuery(project, target, searchScope)
            }

            is AtomSymbol -> {
                queries += atomUsageQuery(project, target.name, searchScope) { atomSymbol ->
                    atomSymbol == target
                }
            }

            is ModuleSymbol -> {
                queries += moduleUsageQuery(project, target, searchScope)
            }

            is TypeSymbol -> {
                queries += typeUsageQuery(project, target, searchScope)
            }

            is TypeVariableSymbol -> {
                queries += typeVariableUsageQuery(project, target, searchScope)
            }

            is ModuleAttributeSymbol -> {
                queries += moduleAttributeReadUsageQuery(project, target, searchScope)
                queries += moduleAttributeWriteUsageQuery(target, searchScope)
            }

            is VariableSymbol -> {
                queries += variableUsageQuery(project, target, searchScope)
            }
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

    private fun protocolImplementationQuery(
        project: Project,
        pf: ProtocolFunction,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
            SearchService.getInstance()
                .searchWord(project, pf.name)
                .caseSensitive(true)
                .inContexts(SearchContext.inCode())
                .inScope(searchScope)
                .buildQuery(ProtocolImplementationMapper(pf.createPointer()))

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

            // A `defoverridable name: arity` entry that names this callback (resolved through the
            // behaviour in scope) - keeps the overridable entry renaming with the callback.
            defoverridableKeyUsage(leaf, callback)?.let { return listOf(it) }

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

        /**
         * If [leaf] is inside the key of a `defoverridable name: arity` entry that resolves to
         * [callback], the keyword-key occurrence, else `null`. Resolution reuses the shared
         * [FunctionArityKeywordPairReference] attached to the `defoverridable` host so behaviour-scope
         * logic stays in one place.
         */
        @RequiresReadLock
        private fun defoverridableKeyUsage(leaf: PsiElement, callback: Callback): PsiUsage? {
            val occurrence = FunctionArityKeywordPair.at(leaf) ?: return null
            if (occurrence.host != FunctionArityKeywordPair.Host.DEFOVERRIDABLE) return null
            if (occurrence.name != callback.name || occurrence.arity != callback.arity) return null

            val matches = PsiSymbolReferenceService.getService()
                .getReferences(occurrence.hostCall)
                .filterIsInstance<FunctionArityKeywordPairReference>()
                .filter { it.absoluteRange.containsOffset(leaf.textRange.startOffset) }
                .flatMap { it.resolveReference() }
                .filterIsInstance<Callback>()
                .any { it == callback }
            if (!matches) return null

            val key = occurrence.pair.keywordKey
            return ElixirPsiUsage.create(
                key,
                TextRange(0, key.textLength),
                declaration = false,
                usageType = IMPLEMENTATION
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
     * Maps each occurrence of a protocol function name to an implementing `def`/`defmacro` clause
     * inside a `defimpl` of the same protocol, if any. Unlike Find Usages (where implementations are
     * reached via "Go To Implementation"), rename MUST update every `defimpl` clause so the protocol
     * member and all its concrete implementations stay in sync. The search framework invokes
     * [mapOccurrence] under a read action.
     */
    private class ProtocolImplementationMapper(
        private val protocolFunctionPointer: Pointer<out ProtocolFunction>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val protocolFunction = protocolFunctionPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence

            // Nearest enclosing call-definition clause (def/defmacro).
            val defClause = leaf.enclosingCalls().firstOrNull { CallDefinitionClause.`is`(it) } ?: return emptyList()

            // The occurrence must be the clause's own name, not a call in its body.
            val nameIdentifier = CallDefinitionClause.nameIdentifier(defClause) ?: return emptyList()
            if (!PsiTreeUtil.isAncestor(nameIdentifier, leaf, false)) return emptyList()

            val nameArity =
                    CallDefinitionClause.nameArityInterval(defClause, ResolveState.initial()) ?: return emptyList()
            if (nameArity.name != protocolFunction.name || protocolFunction.arity !in nameArity.arityInterval) {
                return emptyList()
            }

            // `def` implements a function protocol member; `defmacro` a macro member.
            val kindMatches =
                    if (protocolFunction.macro) CallDefinitionClause.isMacro(defClause)
                    else CallDefinitionClause.isFunction(defClause)
            if (!kindMatches) return emptyList()

            // The clause must live directly inside a `defimpl` for this protocol.
            val defimpl = CallDefinitionClause.enclosingModularMacroCall(defClause) ?: return emptyList()
            if (!Implementation.`is`(defimpl)) return emptyList()
            if (Implementation.protocolName(defimpl) != protocolFunction.protocolName) return emptyList()

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

    private fun atomUsageQuery(
        project: Project,
        atomText: String,
        searchScope: SearchScope,
        atomMatches: (AtomSymbol) -> Boolean
    ): Query<out PsiUsage> =
            SearchService.getInstance()
                .searchWord(project, atomText)
                .caseSensitive(true)
                .inContexts(SearchContext.inCode())
                .inScope(searchScope)
                .buildQuery(AtomUsageMapper(atomMatches))

    private fun typeUsageQuery(
        project: Project,
        symbol: TypeSymbol,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
        SearchService.getInstance()
            .searchWord(project, symbol.searchText)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inScope(searchScope)
            .buildQuery(TypeUsageMapper(symbol.createPointer()))

    private fun typeVariableUsageQuery(
        project: Project,
        symbol: TypeVariableSymbol,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
        SearchService.getInstance()
            .searchWord(project, symbol.searchText)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inScope(symbol.maximalSearchScope?.intersectWith(searchScope) ?: searchScope)
            .buildQuery(TypeVariableUsageMapper(symbol.createPointer()))

    private fun variableUsageQuery(
        project: Project,
        symbol: VariableSymbol,
        searchScope: SearchScope
    ): Query<out PsiUsage> =        SearchService.getInstance()
            .searchWord(project, symbol.searchText)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inScope(symbol.maximalSearchScope?.intersectWith(searchScope) ?: searchScope)
            .buildQuery(VariableUsageMapper(symbol.createPointer()))

    private fun moduleAttributeReadUsageQuery(
        project: Project,
        symbol: ModuleAttributeSymbol,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
        SearchService.getInstance()
            .searchWord(project, symbol.searchText)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inScope(symbol.maximalSearchScope?.intersectWith(searchScope) ?: searchScope)
            .buildQuery(ModuleAttributeReadUsageMapper(symbol.createPointer()))

    private fun moduleAttributeWriteUsageQuery(
        symbol: ModuleAttributeSymbol,
        @Suppress("UNUSED_PARAMETER") searchScope: SearchScope
    ): Query<out PsiUsage> = ModuleAttributeWriteUsageQuery(symbol.createPointer())

    /**
     * Finds every reference to this module using the word index (efficient) and then resolves
     * each occurrence via the existing PSI reference infrastructure. This handles all forms:
     * - `alias MyApp.Module` / `use MyApp.Module` / `import MyApp.Module`
     * - `alias MyApp.{Module, AnotherModule}` (multi-alias)
     * - Bare references in code: `Supervisor.init(\[MyApp.Module])`
     * - Aliased short-name references: `Module.function()` where `alias MyApp.Module` is in scope
     */
    private fun moduleUsageQuery(
        project: Project,
        symbol: ModuleSymbol,
        searchScope: SearchScope
    ): Query<out PsiUsage> =
        SearchService.getInstance()
            .searchWord(project, symbol.searchText)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inScope(searchScope)
            .buildQuery(ModuleUsageMapper(symbol.createPointer()))

    private class ModuleUsageMapper(
        private val symbolPointer: Pointer<out ModuleSymbol>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val symbol = symbolPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence

            // Walk up to the outermost QualifiableAlias containing this leaf.
            val alias = generateSequence(leaf) { it.parent }
                .filterIsInstance<QualifiableAlias>()
                .lastOrNull()
                ?: return emptyList()

            // Skip defmodule declaration names - null reference by convention.
            // The declaration is already contributed by ElixirDirectUsageQuery.
            if (alias.reference == null) return emptyList()

            // Primary: pure structural match via fullyQualifiedName(). Works for:
            //   alias MyApp.Module, use MyApp.Module, MyApp.{Module, Other}, MyApp.Module in code
            val fqn = alias.fullyQualifiedName().removeElixirPrefix()
            if (fqn == symbol.moduleName) {
                // When the matched node spells only a SUFFIX of the FQN - a multi-alias group
                // member like `Renamee` in `alias Grouped.{Renamee, Sibling}` - a rename must
                // write the new name RELATIVE to the surrounding qualifier: writing the full new
                // name into the member slot would produce `Grouped.{Grouped.Fresh, Sibling}`.
                // The qualifier prefix is recoverable without further PSI inspection: it is the
                // FQN minus the node's own text. When the node spells the whole FQN the prefix is
                // empty and the new name is written as-is. A rename that moves the module OUT of
                // the qualifier (prefix no longer matches the new name) cannot be expressed
                // inside the group; the full name is written then - imperfect, but it never
                // corrupts the qualifier-preserving case.
                val qualifierPrefix = fqn.removeSuffix(alias.text)
                val usageTextByName: ((String) -> String)? =
                    if (qualifierPrefix.isNotEmpty() && qualifierPrefix != fqn) {
                        { newName -> if (newName.startsWith(qualifierPrefix)) newName.removePrefix(qualifierPrefix) else newName }
                    } else {
                        null
                    }
                return listOf(
                    ElixirPsiUsage.create(
                        alias,
                        TextRange(0, alias.textLength),
                        declaration = false,
                        usageType = MODULE_REFERENCE,
                        usageTextByName = usageTextByName
                    )
                )
            }

            // Secondary: bare short-name reference where the FQN is just the last segment.
            // e.g. `Module` in code where `alias MyApp.Module` is in lexical scope.
            // `alias MyApp.Module` is simultaneously a usage of `MyApp.Module` AND a declaration
            // of `Module` in the enclosing scope - bare `Module` references that declaration
            // transitively. Check structurally: is there an alias/use/import of symbol.moduleName
            // in the enclosing defmodule? No index or scope resolution required.
            if (fqn == symbol.searchText && hasEnclosingModuleAlias(alias, symbol.moduleName)) {
                return listOf(
                    ElixirPsiUsage.create(
                        alias,
                        TextRange(0, alias.textLength),
                        declaration = false,
                        usageType = MODULE_REFERENCE,
                        // A bare reference stays bare: the `alias` line it rides on is renamed in
                        // the same pass, so only the new name's last segment belongs here.
                        usageTextByName = { newName -> newName.substringAfterLast('.') }
                    )
                )
            }

            return emptyList()
        }

        private fun hasEnclosingModuleAlias(element: PsiElement, targetFqn: String): Boolean {
            val enclosingModule = generateSequence(element) { it.parent }
                .filterIsInstance<Call>()
                .firstOrNull { Module.`is`(it) }
                ?: return false

            return PsiTreeUtil.findChildrenOfType(enclosingModule, Call::class.java).any { call ->
                (call.isCalling(KERNEL, ALIAS) ||
                    call.isCalling(KERNEL, USE) ||
                    call.isCalling(KERNEL, IMPORT)) &&
                    call.finalArguments()
                        ?.firstOrNull()
                        ?.stripAccessExpression()
                        ?.let { argument -> argumentAliasFqns(argument).any { it == targetFqn } } == true
            }
        }

        /**
         * The fully-qualified names an alias/use/import argument brings into scope. A plain
         * `alias Grouped.Renamee` argument IS a [QualifiableAlias]; a multi-alias group
         * `alias Grouped.{Renamee, Sibling}` is not - its MEMBERS are the [QualifiableAlias]es,
         * each of which computes its FQN through the group qualifier. Enumerating the argument
         * itself plus its descendants covers both shapes (nested nodes resolve to their full
         * FQN too, so extras are harmless duplicates, never wrong names).
         */
        private fun argumentAliasFqns(argument: PsiElement): Sequence<String> {
            val self = (argument as? QualifiableAlias)?.let { sequenceOf(it) } ?: emptySequence()
            val descendants = PsiTreeUtil.findChildrenOfType(argument, QualifiableAlias::class.java).asSequence()
            return (self + descendants).map { it.fullyQualifiedName().removeElixirPrefix() }
        }

        private fun String.removeElixirPrefix(): String =
            if (startsWith("Elixir.")) removePrefix("Elixir.") else this
    }

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

            atomUsage(leaf, symbol)?.let { return listOf(it) }

            keywordKeyUsage(leaf, symbol)?.let { return listOf(it) }

            captureUsage(leaf, symbol)?.let { return listOf(it) }

            val call = leaf.enclosingCalls().firstOrNull() ?: return emptyList()
            // Skip definition heads/clauses - they are declarations, not call sites.
            if (CallDefinitionClause.isHead(call) || CallDefinitionClause.`is`(call)) return emptyList()

            val nameElement = call.functionNameElement() ?: return emptyList()
            if (!PsiTreeUtil.isAncestor(nameElement, leaf, false)) return emptyList()

            specUsage(call, symbol)?.let { return listOf(it) }

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

        /**
         * If [leaf] is the captured NAME of a `&name/arity` or `&Mod.name/arity` capture that
         * resolves to [symbol], the name occurrence, else `null`. The bare name inside a capture
         * classifies as a variable to the [Callable] scope-walker (so the generic call-site path
         * below cannot resolve it); the capture-aware resolution lives on the capture element's
         * own [CaptureNameArity] reference, which this reuses.
         */
        @RequiresReadLock
        private fun captureUsage(leaf: PsiElement, symbol: FunctionSymbol): PsiUsage? {
            val capture = generateSequence(leaf) { it.parent }
                .takeWhile { it !is PsiFile }
                .filterIsInstance<CaptureNonNumeric>()
                .firstOrNull() ?: return null
            val reference = capture.reference as? CaptureNameArity ?: return null

            // Only the captured name is a usage - not the `/arity` digits, and for a qualified
            // capture not the `Mod.` qualifier (CaptureNameArity's range is the name alone).
            val absoluteNameRange = reference.rangeInElement.shiftRight(capture.textRange.startOffset)
            if (!absoluteNameRange.contains(leaf.textRange)) return null
            if (reference.arity != symbol.arity) return null

            val matches = reference.multiResolve(false)
                .filter { it.isValidResult }
                .mapNotNull { it.element as? Call }
                .filter { CallDefinitionClause.`is`(it) }
                .flatMap { FunctionSymbol.fromClause(it) }
                .any { it == symbol }
            if (!matches) return null

            return ElixirPsiUsage.create(
                capture,
                reference.rangeInElement,
                declaration = false,
                usageType = CALL
            )
        }

        @RequiresReadLock
        private fun specUsage(call: Call, symbol: FunctionSymbol): PsiUsage? {
            call.enclosingSpecAttributeIfHead() ?: return null
            val matches = PsiSymbolReferenceService.getService()
                .getReferences(call)
                .flatMap { it.resolveReference() }
                .filterIsInstance<FunctionSymbol>()
                .any { it == symbol }
            if (!matches) return null

            val nameElement = call.functionNameElement() ?: return null
            return ElixirPsiUsage.create(
                nameElement,
                TextRange(0, nameElement.textLength),
                declaration = false,
                usageType = SPECIFICATION
            )
        }

        @RequiresReadLock
        private fun keywordKeyUsage(leaf: PsiElement, symbol: FunctionSymbol): PsiUsage? {
            val occurrence = FunctionArityKeywordPair.at(leaf) ?: return null
            // `defoverridable` keys resolve to a Callback, handled via the Callback search path.
            if (occurrence.host == FunctionArityKeywordPair.Host.DEFOVERRIDABLE) return null
            if (occurrence.name != symbol.name || occurrence.arity != symbol.arity) return null

            val matches = PsiSymbolReferenceService.getService()
                .getReferences(occurrence.hostCall)
                .filterIsInstance<FunctionArityKeywordPairReference>()
                .filter { it.absoluteRange.containsOffset(leaf.textRange.startOffset) }
                .flatMap { it.resolveReference() }
                .filterIsInstance<FunctionSymbol>()
                .any { it == symbol }
            if (!matches) return null

            val key = occurrence.pair.keywordKey
            return ElixirPsiUsage.create(
                key,
                TextRange(0, key.textLength),
                declaration = false,
                usageType = CALL
            )
        }

        @RequiresReadLock
        private fun atomUsage(leaf: PsiElement, symbol: FunctionSymbol): PsiUsage? {
            val atom = PsiTreeUtil.getParentOfType(leaf, ElixirAtom::class.java, false) ?: return null
            val references = PsiSymbolReferenceService.getService()
                .getReferences(atom)
                .filterIsInstance<AtomReference>()
            if (references.isEmpty()) return null

            val matches = references.any { reference ->
                reference.resolveReference()
                    .filterIsInstance<AtomSymbol>()
                    .any {
                        it.moduleName == symbol.moduleName &&
                            it.name == symbol.name &&
                            it.arity == symbol.arity &&
                            it.macro == symbol.macro
                    }
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

    private class AtomUsageMapper(
        private val atomMatches: (AtomSymbol) -> Boolean
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val (_, leaf, _) = occurrence
            val atom = PsiTreeUtil.getParentOfType(leaf, ElixirAtom::class.java, false) ?: return emptyList()
            val references = PsiSymbolReferenceService.getService()
                .getReferences(atom)
                .filterIsInstance<AtomReference>()
            if (references.isEmpty()) return emptyList()

            val matches = references.any { reference ->
                reference.resolveReference()
                    .filterIsInstance<AtomSymbol>()
                    .any(atomMatches)
            }
            if (!matches) return emptyList()

            return listOf(
                ElixirPsiUsage.create(
                    leaf,
                    TextRange(0, leaf.textLength),
                    declaration = false,
                    usageType = CALL
                )
            )
        }
    }

    private class TypeUsageMapper(
        private val symbolPointer: Pointer<out TypeSymbol>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val symbol = symbolPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence
            val call = leaf.enclosingCalls().firstOrNull() ?: return emptyList()
            // Type usages are only valid inside type/spec syntax, never in executable code
            // (for example, variable bindings in function heads).
            if (call.ancestorTypeSpec() == null) return emptyList()
            val nameElement = call.functionNameElement() ?: return emptyList()
            if (!PsiTreeUtil.isAncestor(nameElement, leaf, false)) return emptyList()
            if (call.enclosingTypeDeclarationIfHead() != null) return emptyList()
            if (call.enclosingSpecAttributeIfHead() != null) return emptyList()

            val resolved = TypeReference.resolveSymbols(call)
            if (resolved.none { it == symbol }) return emptyList()

            return listOf(
                ElixirPsiUsage.create(
                    nameElement,
                    TextRange(0, nameElement.textLength),
                    declaration = false,
                    usageType = SPECIFICATION
                )
            )
        }
    }

    private class TypeVariableUsageMapper(
        private val symbolPointer: Pointer<out TypeVariableSymbol>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val symbol = symbolPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence
            // The search scope is the single enclosing `@type`/`@spec` attribute, so any same-named
            // occurrence here is either this variable's declaration or one of its usages.
            val call = generateSequence(leaf) { it.parent }
                .takeWhile { it !is PsiFile }
                .filterIsInstance<Call>()
                .firstOrNull { candidate ->
                    val nameElement = candidate.functionNameElement()
                    nameElement != null &&
                        PsiTreeUtil.isAncestor(nameElement, leaf, false) &&
                        candidate.functionName() == symbol.name
                }
                ?: return emptyList()
            val nameElement = call.functionNameElement() ?: return emptyList()
            // Skip the declaration's own occurrence; it is contributed separately as declaration = true.
            if (nameElement.containingFile.virtualFile == symbol.file.virtualFile &&
                nameElement.textRange == symbol.range
            ) {
                return emptyList()
            }
            if (TypeReference.resolveTypeVariableSymbols(call).none { it == symbol }) return emptyList()

            return listOf(
                ElixirPsiUsage.create(
                    nameElement,
                    TextRange(0, nameElement.textLength),
                    declaration = false,
                    usageType = SPECIFICATION
                )
            )
        }
    }

    private class VariableUsageMapper(
        private val symbolPointer: Pointer<out VariableSymbol>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val symbol = symbolPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence
            for (candidate in generateSequence(leaf) { it.parent }.takeWhile { it !is PsiFile }) {
                ProgressManager.checkCanceled()
                if (VariableSymbol.variableName(candidate) != symbol.name) continue
                val nameElement = VariableSymbol.nameIdentifierElement(candidate) ?: continue
                if (!PsiTreeUtil.isAncestor(nameElement, leaf, false)) continue
                if (nameElement.containingFile.virtualFile == symbol.file.virtualFile &&
                    nameElement.textRange == symbol.range
                ) {
                    continue
                }
                // NOTE: the right-hand read of a rebinding (`x` in `x = x + 1`) semantically reads
                // the PREVIOUS binding, but a rebinding chain is one user-facing variable (the
                // symbol's search scope starts at the chain root - see
                // VariableSymbol.maximalSearchScope), so it is a usage regardless of which
                // binding anchors the symbol.
                return listOf(
                    ElixirPsiUsage.create(
                        nameElement,
                        TextRange(0, nameElement.textLength),
                        declaration = false,
                        usageType = if (VariableSymbol.isDeclaration(candidate)) VALUE_WRITE else VALUE_READ
                    )
                )
            }
            return emptyList()
        }
    }

    private class ModuleAttributeReadUsageMapper(
        private val symbolPointer: Pointer<out ModuleAttributeSymbol>
    ) : LeafOccurrenceMapper<PsiUsage> {
        @RequiresReadLock
        override fun mapOccurrence(occurrence: LeafOccurrence): Collection<PsiUsage> {
            val symbol = symbolPointer.dereference() ?: return emptyList()
            val (_, leaf, _) = occurrence

            for (candidate in generateSequence(leaf) { it.parent }.takeWhile { it !is PsiFile }) {
                ProgressManager.checkCanceled()
                if (candidate !is Call) continue
                if (!candidate.isModuleAttributeNameElement()) continue
                if (candidate.functionName() != symbol.name) continue
                val nameElement = candidate.functionNameElement() ?: continue
                if (!PsiTreeUtil.isAncestor(nameElement, leaf, false)) continue
                // Same name + same module means the same logical attribute: a re-declared
                // (accumulated/overridden) attribute has several declaration sites, and a read
                // resolves only to the nearest preceding one - identity comparison would make a
                // read invisible when the rename starts from any of the other declarations.
                if (ModuleAttributeReference.resolveSymbols(candidate)
                        .none { it.name == symbol.name && it.moduleName == symbol.moduleName }
                ) {
                    continue
                }
                return listOf(
                    ElixirPsiUsage.create(
                        nameElement,
                        TextRange(0, nameElement.textLength),
                        declaration = false,
                        usageType = MODULE_ATTRIBUTE_READ
                    )
                )
            }

            return emptyList()
        }
    }

    private class ModuleAttributeWriteUsageQuery(
        private val symbolPointer: Pointer<out ModuleAttributeSymbol>
    ) : AbstractQuery<PsiUsage>() {
        override fun processResults(consumer: Processor<in PsiUsage>): Boolean =
            ReadAction.nonBlocking(JCallable<Boolean> {
                val symbol = symbolPointer.dereference() ?: return@JCallable true
                val declaration = generateSequence(symbol.file.findElementAt(symbol.range.startOffset)) { it.parent }
                    .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
                    .firstOrNull { it.atIdentifier.identifierTextRange() == symbol.range }
                    ?: return@JCallable true
                val seen = mutableSetOf<TextRange>(declaration.atIdentifier.textRange)

                // Walk ALL of the declaration's siblings, not just the following ones: the rename
                // can start from ANY of a re-declared attribute's declaration sites (or from a
                // read, which resolves to the nearest preceding one), and the earlier declarations
                // are just as much part of the logical attribute as the later ones.
                var sibling: PsiElement? = declaration.parent?.firstChild ?: declaration
                while (sibling != null) {
                    ProgressManager.checkCanceled()
                    val declarationCandidate = sibling as? AtUnqualifiedNoParenthesesCall<*>
                    if (declarationCandidate != null) {
                        ProgressManager.checkCanceled()
                        val candidateSymbol = ModuleAttributeSymbol.fromDeclaration(declarationCandidate)
                        if (candidateSymbol != null) {
                            if (candidateSymbol.name != symbol.name || candidateSymbol.moduleName != symbol.moduleName) {
                                sibling = sibling.nextSibling
                                continue
                            }
                            if (!seen.add(declarationCandidate.atIdentifier.textRange)) {
                                sibling = sibling.nextSibling
                                continue
                            }

                            val atIdentifier = declarationCandidate.atIdentifier
                            val usage = ElixirPsiUsage.create(
                                atIdentifier,
                                atIdentifier.identifierTextRange().shiftLeft(atIdentifier.textRange.startOffset),
                                declaration = false,
                                usageType = MODULE_ATTRIBUTE_WRITE
                            )
                            if (!consumer.process(usage)) return@JCallable false
                        }
                    }

                    sibling = sibling.nextSibling
                }

                true
            }).executeSynchronously()
    }
}

private val IMPLEMENTATION = UsageType { "Implementation" }

private val CALL = UsageType { "Function call" }

private val MODULE_REFERENCE = UsageType { "Module reference" }

private val SPECIFICATION = UsageType { "Specification" }

private val MODULE_ATTRIBUTE_READ = UsageType { "Module attribute read" }

private val MODULE_ATTRIBUTE_WRITE = UsageType { "Module attribute accumulate or override" }

private val VALUE_READ = UsageType { "Value read" }

private val VALUE_WRITE = UsageType { "Value write" }

private const val USING = "__using__"

private fun PsiElement.enclosingCalls(): Sequence<Call> =
        generateSequence(parent) { it.parent }.takeWhile { it !is PsiFile }.filterIsInstance<Call>()

@RequiresReadLock
private fun Call.matchesFunctionFamily(symbol: FunctionSymbol): Boolean {
    val enclosingModular = CallDefinitionClause.enclosingModularMacroCall(this) ?: return false
    val moduleName = runCatching { Module.name(enclosingModular) }
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

@RequiresReadLock
private fun Call.enclosingSpecAttributeIfHead(): AtUnqualifiedNoParenthesesCall<*>? {
    val moduleAttribute = generateSequence(parent) { it.parent }
        .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
        .firstOrNull()
        ?: return null
    if (moduleAttributeName(moduleAttribute) != "@spec") return null
    val specification = CallDefinitionSpecification.specification(moduleAttribute) ?: return null
    val specHead = CallDefinitionSpecification.specificationType(specification) ?: return null
    return if (specHead.isEquivalentTo(this)) moduleAttribute else null
}

@RequiresReadLock
private fun Call.enclosingTypeDeclarationIfHead(): AtUnqualifiedNoParenthesesCall<*>? {
    val moduleAttribute = generateSequence(parent) { it.parent }
        .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
        .firstOrNull { org.elixir_lang.structure_view.element.Type.`is`(it) }
        ?: return null
    val specification = CallDefinitionSpecification.specification(moduleAttribute) ?: return null
    val typeHead = CallDefinitionSpecification.specificationType(specification) ?: return null
    return if (typeHead.isEquivalentTo(this)) moduleAttribute else null
}
