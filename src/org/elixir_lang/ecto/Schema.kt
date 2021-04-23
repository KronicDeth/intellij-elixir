package org.elixir_lang.ecto

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import org.elixir_lang.NameArity
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.Using
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.resolvesToModularName

object Schema {
    fun `is`(call: Call, state: ResolveState): Boolean =
            call.functionName()?.let { functionName ->
                when (functionName) {
                    SCHEMA_NAME_ARITY.name ->
                        call.resolvedFinalArity() == SCHEMA_NAME_ARITY.arity &&
                                resolvesToEctoSchema(call, state)
                    EMBEDDED_SCHEMA_ARITY.name ->
                        call.resolvedFinalArity() == EMBEDDED_SCHEMA_ARITY.arity &&
                                resolvesToEctoSchema(call, state)
                    else -> false
                }
            } ?: false

    fun treeWalkUp(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
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
                                usingCall = it,
                                useCall = call,
                                resolveState = state,
                                keepProcessing = keepProcessing
                        )
                    }
                }
                ?: true

    private fun resolvesToEctoSchema(call: Call, state: ResolveState): Boolean =
            resolvesToModularName(call, state, "Ecto.Schema")

    private val EMBEDDED_SCHEMA_ARITY = NameArity("embedded_schema", 1)
    private val SCHEMA_NAME_ARITY = NameArity("schema", 2)
}
