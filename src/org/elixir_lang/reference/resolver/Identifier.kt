package org.elixir_lang.reference.resolver

import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.reference.Identifier

object Identifier : ResolveCache.PolyVariantResolver<Identifier> {
    override fun resolve(identifier: Identifier, incompleteCode: Boolean): Array<ResolveResult> {
        ApplicationManager.getApplication().assertReadAccessAllowed()
        return resolveElement(identifier.element, incompleteCode).toTypedArray()
    }

    private fun resolveElement(element: ElixirIdentifier, incompleteCode: Boolean): List<ResolveResult> {
        val name = element.text
        val resolveResultList = mutableListOf<ResolveResult>()

        val variableResolveList = org.elixir_lang.psi.scope.variable.MultiResolve.resolveResultList(
            name,
            incompleteCode,
            element
        )

        resolveResultList.addAll(variableResolveList)

        val callDefinitionClauseResolveResultList =
            org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                name,
                0,
                incompleteCode,
                element
            )
        resolveResultList.addAll(callDefinitionClauseResolveResultList)

        return resolveResultList
    }
}
