package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.UNQUOTE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.scope.WhileIn.whileIn

object Unquote {
    @RequiresReadLock
    fun treeWalkUp(unquoteCall: Call, resolveState: ResolveState, keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        val unquoteCallResolveState = resolveState.putVisitedElement(unquoteCall)

        return unquoteCall
                .finalArguments()
                ?.singleOrNull()
                ?.let { it as? Call }
                ?.takeUnlessHasBeenVisited(unquoteCallResolveState)
                ?.reference
                ?.let { it as PsiPolyVariantReference }
                ?.let { reference -> treeWalkUp(reference, unquoteCallResolveState, keepProcessing) }
                ?: true
    }

    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, UNQUOTE)

    @RequiresReadLock
    fun isQualified(qualified: Qualified): Boolean =
            isQualified(qualified, qualified.functionName())

    @RequiresReadLock
    fun isQualified(qualified: Qualified, name: String?): Boolean =
            name == UNQUOTE &&
                    qualified.resolvedPrimaryArity() == 1 &&
                    CallDefinitionClause.enclosingModularMacroCall(qualified)?.let { QuoteMacro.`is`(it) } == true

    fun ancestorUnquote(descendent: PsiElement): Call? =
            descendent.parent?.parent?.parent?.let { it as? Call }?.takeIf { `is`(it) }

    private fun treeWalkUp(reference: PsiPolyVariantReference,
                           resolveState: ResolveState,
                           keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean =
            reference
                    .multiResolve(false)
                    .filter(ResolveResult::isValidResult)
                    .mapNotNull(ResolveResult::getElement)
                    .filterIsInstance<Call>()
                    .filter { !resolveState.hasBeenVisited(it) }
                    .let { resolveds -> treeWalkUpUnquoted(resolveds, resolveState, keepProcessing) }

    private fun treeWalkUpUnquoted(unquotedList: List<Call>,
                                   resolveState: ResolveState,
                                   keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean =
            whileIn(unquotedList) { unquoted ->
                val unquotedResolveState = resolveState.putVisitedElement(unquoted)

                if (CallDefinitionClause.`is`(unquoted)) {
                    Using.treeWalkUp(unquoted, null, unquotedResolveState, keepProcessing)
                } else {
                    treeWalkUpUnquotedVariable(unquoted, unquotedResolveState, keepProcessing)
                    // a variable

                    true
                }
            }

    private tailrec fun treeWalkUpUnquotedVariable(unquoted: PsiElement,
                                                   resolveState: ResolveState,
                                                   keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        // a detached element binds nothing above
        val parent = unquoted.parent ?: return true

        return when (UnquotedVariableWalk.classify(parent)) {
            UnquotedVariableWalk.Bucket.MATCH -> {
                val match = parent as Match

                // variable = ...
                if (match.leftOperand() == unquoted) {
                    match.rightOperand()?.let { value ->
                        treeWalkUpValue(value, resolveState, keepProcessing)
                    } ?: true
                }
                // ... = variable: a use, which binds nothing further up
                else {
                    true
                }
            }
            UnquotedVariableWalk.Bucket.RECURSE -> treeWalkUpUnquotedVariable(parent, resolveState, keepProcessing)
            UnquotedVariableWalk.Bucket.STOP, UnquotedVariableWalk.Bucket.UNFOLLOWED, UnquotedVariableWalk.Bucket.LEAF -> true
        }
    }

    private fun treeWalkUpValue(value: PsiElement,
                                resolveState: ResolveState,
                                keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean =
            when (value) {
                is Call -> treeWalkUpValue(value, resolveState, keepProcessing)
                // A literal or container value is not walked into, so a quote destructured out of one is not found
                else -> true
            }

    private fun treeWalkUpValue(value: Call,
                                resolveState: ResolveState,
                                keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean =
            when {
                QuoteMacro.`is`(value) -> QuoteMacro.treeWalkUp(value, resolveState, keepProcessing)
                Case.`is`(value) -> Case.treeWalkUp(value, resolveState, keepProcessing)
                else -> {
                    value.reference?.let { it as PsiPolyVariantReference }?.let { reference ->
                        treeWalkUp(reference, resolveState.putVisitedElement(value), keepProcessing)
                    } ?: true
                }
            }
}
