package org.elixir_lang.psi.scope

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import org.elixir_lang.psi.ElixirStabBody
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.whileInChildExpressions
import org.elixir_lang.psi.putVisitedElement
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.semantic.*

abstract class Module : PsiScopeProcessor {
    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    override fun execute(match: PsiElement, state: ResolveState): Boolean =
        match
            .semantic?.let { execute(it, state) }
            ?: true

    private fun execute(semantic: Semantic, state: ResolveState): Boolean = when (semantic) {
        is Modular -> execute(semantic, state)
        is org.elixir_lang.semantic.Use -> execute(semantic, state)
        is Aliasing -> execute(semantic, state)
        is Branching -> execute(semantic, state)
        else -> true
    }

    private fun execute(semantic: Modular, state: ResolveState): Boolean =
        if (state.get(ENTRANCE).containingFile.context == semantic.psiElement) {
            executeOnViewModular(semantic, state)
        } else {
            val keepProcessing = semantic.name?.let {
                executeOnModularName(semantic.psiElement, it, state)
            } ?: true

            // descend in modular to check for nested modulars in case their relative name is being used
            keepProcessing && executeOnNestedModulars(semantic, state)
        }

    private fun executeOnViewModular(semantic: Modular, state: ResolveState): Boolean =
        semantic
            .psiElement
            .let { it as? Call }
            ?.doBlock
            ?.stab
            ?.stabBody
            ?.let { executeOnViewModularExpression(it, state) }
            ?: true

    private fun executeOnViewModularExpression(expression: PsiElement, state: ResolveState): Boolean =
        when (expression) {
            is ElixirStabBody -> expression
                .whileInChildExpressions(forward = false) { child ->
                    executeOnViewModularExpression(child, state)
                }
            else -> execute(expression, state)
        }

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here or [.executeOnAliasedName]
     *
     * @return `true` to keep processing; `false` to stop processing.
     */
    protected abstract fun executeOnModularName(modular: PsiElement, modularName: String, state: ResolveState): Boolean

    private fun execute(semantic: org.elixir_lang.semantic.Use, state: ResolveState): Boolean =
        whileIn(semantic.modulars) {
            execute(it, state)
        }

    private fun execute(aliasing: Aliasing, state: ResolveState): Boolean {
        val aliasPsiElementState = state.putVisitedElement(aliasing.psiElement)

        return whileIn(aliasing.aliases) { alias ->
            execute(alias, aliasPsiElementState)
        }
    }

    private fun execute(semantic: Branching, state: ResolveState): Boolean =
        whileIn(semantic.branches) {
            execute(it, state)
        }

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here or [.executeOnModularName]
     *
     * @return `true` to keep processing; `false` to stop processing.
     */
    protected abstract fun executeOnAliasedName(
        match: PsiElement,
        aliasedName: String,
        state: ResolveState
    ): Boolean

    protected abstract fun execute(alias: Alias, state: ResolveState): Boolean

    private fun executeOnNestedModulars(parent: Modular, state: ResolveState): Boolean {
        var accumlatedKeepProcessing = true

        for (nested in parent.nested) {
            accumlatedKeepProcessing = execute(nested, state)

            if (!accumlatedKeepProcessing) {
                break
            }
        }

        return accumlatedKeepProcessing
    }


    companion object {
        @JvmStatic
        val ALIAS_CALL = Key<Call>("ALIAS_CALL")
        val MULTIPLE_ALIASES_QUALIFIER = Key<PsiNamedElement>("MULTIPLE_ALIASES_QUALIFIER")
        val REQUIRE_CALL = Key<Call>("REQUIRE_CALL")
    }
}
