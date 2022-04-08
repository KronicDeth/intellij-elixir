package org.elixir_lang.ecto

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.ModuleWalker
import org.elixir_lang.psi.NameArityRangeWalker
import org.elixir_lang.psi.Using
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic

object Schema : ModuleWalker(
    "Ecto.Schema",
    NameArityRangeWalker("embedded_schema", 1),
    NameArityRangeWalker("schema", 2)
) {
    override fun walkChild(
        call: Call,
        state: ResolveState,
        keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean
    ): Boolean =
        call
            .reference
            ?.let { it as PsiPolyVariantReference }
            ?.multiResolve(false)
            ?.mapNotNull(ResolveResult::getElement)
            ?.mapNotNull(PsiElement::semantic)
            // don't include the `import`s
            ?.filterIsInstance<Clause>()
            ?.let { clauses ->
                whileIn(clauses) { clause ->
                    Using.treeWalkUp(
                        usingCall = clause.psiElement as Call,
                        useCall = call,
                        resolveState = state,
                        keepProcessing = keepProcessing
                    )
                }
            }
            ?: true
}
