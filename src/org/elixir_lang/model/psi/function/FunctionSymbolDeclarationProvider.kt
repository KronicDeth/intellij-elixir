package org.elixir_lang.model.psi.function

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * Exposes the name of a `def`/`defp`/`defmacro`/`defmacrop` clause as a declaration of the
 * [FunctionSymbol] symbol(s), so the platform's "Declaration or Usages" flow treats the caret as a
 * declaration (→ Show Usages) rather than navigating.
 *
 * Clauses inside a `defprotocol` are owned by [org.elixir_lang.model.psi.protocol.ProtocolFunction] (guarded in [FunctionSymbol.fromClause]).
 * The declaration is anchored on the `CallDefinitionClause` element with the name-identifier range.
 *
 * A clause with default arguments (`def foo(a, b \\ 1)`) yields several [FunctionSymbol]s (one per
 * arity in the clause's interval) that all share the **same** name range. Exposing each as its own
 * declaration would make the platform offer an arity chooser on Shift+F6 (and break in-place rename
 * headlessly). They are the same textual name, so a single representative declaration is exposed;
 * the usage searcher already renames every arity's call sites via family/`Callable` resolution.
 */
@Suppress("UnstableApiUsage")
internal class FunctionSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
    @RequiresReadLock
    override fun getDeclarations(element: PsiElement, offsetInElement: Int): Collection<PsiSymbolDeclaration> {
        val clause = generateSequence(element) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
            ?: return emptyList()
        val symbols = FunctionSymbol.fromClause(clause)
        if (symbols.isEmpty()) return emptyList()
        val nameId = CallDefinitionClause.nameIdentifier(clause) ?: return emptyList()
        val rangeInDeclaringElement = nameId.textRange.shiftLeft(clause.textRange.startOffset)

        // All symbols from one clause share the same name range (they differ only by arity from
        // default arguments), so collapse to a single declaration to avoid an arity chooser.
        val symbol = symbols.first()
        return listOf(
            object : PsiSymbolDeclaration {
                override fun getDeclaringElement(): PsiElement = clause
                override fun getRangeInDeclaringElement(): TextRange = rangeInDeclaringElement
                override fun getSymbol(): Symbol = symbol
            }
        )
    }
}
