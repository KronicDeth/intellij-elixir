package org.elixir_lang.psi.scope

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.siblings
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named

import org.elixir_lang.psi.call.name.Function.ALIAS
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.keywordArgument
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.stub.type.call.Stub.isModular

abstract class Module : PsiScopeProcessor {
    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here or [.executeOnModularName]
     *
     * @return `true` to keep processing; `false` to stop processing.
     */
    protected abstract fun executeOnAliasedName(match: PsiNamedElement,
                                                aliasedName: String,
                                                state: ResolveState): Boolean

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here or [.executeOnAliasedName]
     *
     * @return `true` to keep processing; `false` to stop processing.
     */
    protected abstract fun executeOnModularName(modular: Named, modularName: String, state: ResolveState): Boolean

    protected fun execute(match: Named, state: ResolveState): Boolean =
            when {
                isModular(match) -> executeOnModular(match, state)
                Use.`is`(match) -> Use.treeWalkUp(match, state, ::execute)
                match.isCalling(KERNEL, ALIAS) -> executeOnAliasCall(match, state)
                else -> true
            }

    private fun aliasedName(element: ElixirAlias): String? = element.name

    protected fun aliasedName(element: PsiElement): String? =
            when (element) {
                is ElixirAlias -> aliasedName(element)
                is QualifiedAlias -> aliasedName(element)
                else -> null
            }

    private fun aliasedName(element: QualifiedAlias): String? {
        val children = element.children
        val operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children)
        val unqualified = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(children, operatorIndex)

        return (unqualified as? PsiNamedElement)?.name
    }

    private fun executeOnAliasCall(aliasCall: Named, state: ResolveState): Boolean {
        val asKeywordValue = aliasCall.keywordArgument("as")

        return if (asKeywordValue != null) {
            asKeywordValue
                    .stripAccessExpression()
                    .let { executeOnAs(it, state.put(ALIAS_CALL, aliasCall)) }
        } else {
            val finalArguments = aliasCall.finalArguments()

            if (finalArguments != null && finalArguments.isNotEmpty()) {
                executeOnAliasCallArgument(finalArguments[0], state)
            } else {
                true
            }
        }
    }

    private fun executeOnAliasCallArgument(accessExpression: ElixirAccessExpression,
                                           state: ResolveState): Boolean =
            executeOnAliasCallArgument(accessExpression.children, state)

    private fun executeOnAliasCallArgument(multipleAliases: ElixirMultipleAliases,
                                           state: ResolveState): Boolean =
            executeOnMultipleAliasChildren(multipleAliases.children, state)


    protected fun executeOnAliasCallArgument(element: PsiElement?, state: ResolveState): Boolean =
            when (element) {
                is ElixirAccessExpression -> executeOnAliasCallArgument(element, state)
                is QualifiableAlias -> executeOnAliasCallArgument(element, state)
                is QualifiedMultipleAliases -> executeOnAliasCallArgument(element, state)
                else -> true
            }

    private fun executeOnAliasCallArgument(children: Array<PsiElement>, state: ResolveState): Boolean {
        var keepProcessing = true

        for (child in children) {
            keepProcessing = executeOnAliasCallArgument(child, state)

            if (!keepProcessing) {
                break
            }
        }

        return keepProcessing
    }

    private fun executeOnMultipleAliasChild(child: ElixirAccessExpression, state: ResolveState): Boolean =
        child.children.let { executeOnMultipleAliasChildren(it, state) }

    private fun executeOnMultipleAliasChild(child: PsiElement, state: ResolveState): Boolean =
            when (child) {
                is ElixirAccessExpression -> executeOnMultipleAliasChild(child, state)
                is QualifiableAlias -> executeOnMultipleAliasChild(child, state)
                else -> true
            }

    private fun executeOnMultipleAliasChild(element: QualifiableAlias, state: ResolveState): Boolean =
            executeOnMaybeAliasedName(element, aliasedName(element), state)

    private fun executeOnMultipleAliasChildren(elements: Array<PsiElement>, state: ResolveState): Boolean {
        var keepProcessing = true

        for (element in elements) {
            keepProcessing = executeOnMultipleAliasChild(element, state)

            if (!keepProcessing) {
                break
            }
        }

        return keepProcessing
    }

    private fun executeOnAliasCallArgument(qualifiableAlias: QualifiableAlias,
                                           state: ResolveState): Boolean =
            executeOnMaybeAliasedName(qualifiableAlias, aliasedName(qualifiableAlias), state)

    private fun executeOnAliasCallArgument(qualifiedMultipleAliases: QualifiedMultipleAliases,
                                           state: ResolveState): Boolean {
        val children = qualifiedMultipleAliases.children
        val operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children)
        val unqualified = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(children, operatorIndex)

        return if (unqualified is ElixirMultipleAliases) {
            executeOnAliasCallArgument(unqualified, state)
        } else {
            true
        }
    }

    private fun executeOnAs(asKeywordValue: PsiElement, state: ResolveState): Boolean =
            when (asKeywordValue) {
                is PsiNamedElement -> executeOnMaybeAliasedName(
                        asKeywordValue,
                        asKeywordValue.name,
                        state
                )
                else -> true
            }

    private fun executeOnMaybeAliasedName(named: PsiNamedElement,
                                          aliasedName: String?,
                                          state: ResolveState): Boolean =
            if (aliasedName != null) {
                executeOnAliasedName(named, aliasedName, state)
            } else {
                true
            }


    private fun executeOnModular(match: Named, state: ResolveState): Boolean =
            if (state.get(ENTRANCE).containingFile.context == match) {
                executeOnViewModular(match, state)
            } else {
                match.name?.let {
                    executeOnModularName(match, it, state)
                } ?: true
            }

    private fun executeOnViewModular(match: Named, state: ResolveState): Boolean =
        match
                .doBlock
                ?.stab
                ?.stabBody
                ?.let { executeOnViewModularExpression(it, state) }
                ?: true

    private fun executeOnViewModularExpression(expression: PsiElement, state: ResolveState): Boolean =
            when (expression) {
                is ElixirStabBody -> expression
                        .lastChild
                        .siblings(forward = false, withSelf = true)
                        .filter { it.node is CompositeElement }
                        .let { childSequence ->
                            var keepProcessing = true

                            for (child in childSequence) {
                                keepProcessing = executeOnViewModularExpression(child, state)

                                if (!keepProcessing) {
                                    break
                                }
                            }

                            keepProcessing
                        }
                else -> execute(expression, state)
            }

    companion object {
        @JvmStatic
        val ALIAS_CALL = Key<Call>("ALIAS_CALL")
    }
}
