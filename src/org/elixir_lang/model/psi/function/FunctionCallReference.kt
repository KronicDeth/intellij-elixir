package org.elixir_lang.model.psi.function

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.model.psi.protocol.ProtocolFunction
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable

/**
 * Symbol reference from a call-site expression to the [FunctionSymbol] (or [ProtocolFunction]) it
 * invokes - powers "Go To Declaration" (Ctrl+Click) from a call site to the matching `def`/`defmacro`.
 *
 * Resolution delegates to the existing [Callable] scope-walking infrastructure (which handles
 * qualified calls, unqualified calls, captures, etc.) and wraps each resolved
 * `CallDefinitionClause` as a [FunctionSymbol] via [FunctionSymbol.fromClause], or - when the clause
 * is directly inside a `defprotocol` - as a [ProtocolFunction] via [ProtocolFunction.fromClause].
 *
 * This delegation is the intended, permanent design, not a stopgap: it mirrors the platform's own
 * [com.intellij.model.psi.PsiSymbolService] / `Psi2SymbolReference` bridge, which likewise resolves
 * a Symbol reference by calling `multiResolve` on a classic resolver and wrapping the results. We do
 * not route through `PsiSymbolService` because this class adds arity filtering, default-argument
 * expansion, and `defprotocol` routing, and yields rich domain symbols ([FunctionSymbol] /
 * [ProtocolFunction]) whose semantic `equals`/`hashCode` and navigation/rename/search behaviour the
 * generic `Psi2Symbol` wrapper cannot provide. The only migration debt here is the *package
 * location* of the shared scope-walker ([Callable] lives under `reference/`); it is relocated, not
 * replaced, by the final cleanup phase.
 */
@Suppress("UnstableApiUsage")
class FunctionCallReference(
    private val call: Call,
    private val rangeInElement: TextRange
) : PsiSymbolReference {
    override fun getElement(): PsiElement = call

    override fun getRangeInElement(): TextRange = rangeInElement

    @RequiresReadLock
    override fun resolveReference(): Collection<Symbol> {
        // Delegate to the legacy Callable scope-walker, which understands qualified calls,
        // unqualified calls within the lexical scope, captures, imports, etc.
        val callArity = call.resolvedFinalArity()
        val clauses = Callable(call).multiResolve(false)
            .filter { it.isValidResult }
            .mapNotNull { result ->
                when (val element = result.element) {
                    // A source `def`/`defmacro` clause is already a `Call`, while a decompiled beam function exposes
                    // the equivalent clause as its navigation element (the `.beam` mirror), so both flow through the
                    // same `FunctionSymbol.fromClause` pipeline and compare equal by module/name/arity/macro.
                    is Call -> element
                    is CallDefinitionImpl<*> -> element.navigationElement as? Call
                    else -> null
                }
            }
            .filter { CallDefinitionClause.`is`(it) }

        // Only navigate to the clause(s) whose arity matches this specific call site.
        // fromClause() expands multi-arity defs (via default args); without this filter,
        // Ctrl+Click on `foo(x)` would offer both `foo/1` and `foo/2` as targets.
        val functionSymbols = clauses
            .flatMap { FunctionSymbol.fromClause(it) }
            .filter { it.arity == callArity }
        if (functionSymbols.isNotEmpty()) return functionSymbols

        // Clauses directly inside a `defprotocol` are owned by ProtocolFunction, not FunctionSymbol
        // (FunctionSymbol.fromClause returns empty for them). A qualified protocol call
        // `Protocol.function(args)` therefore resolves here.
        return clauses
            .flatMap { ProtocolFunction.fromClause(it) }
            .filter { it.arity == callArity }
    }
}
