package org.elixir_lang.ecto

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ModuleWalker
import org.elixir_lang.psi.NameArityRangeWalker
import org.elixir_lang.psi.Using
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.scope.WhileIn.whileIn

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
            ?.mapNotNull { it.element as? Call }
            // don't include the `import`s
            ?.filter { CallDefinitionClause.`is`(it) }
            ?.let { definitions ->
                whileIn(definitions) {
                    Using.treeWalkUp(
                        using = it,
                        use = call,
                        resolveState = state,
                        keepProcessing = keepProcessing
                    )
                }
            }
            ?: true
}
