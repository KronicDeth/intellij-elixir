package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named

import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.maxScope

class MultiResolve
private constructor(private val name: String,
                    private val resolvedFinalArity: Int,
                    private val incompleteCode: Boolean) : org.elixir_lang.psi.scope.CallDefinitionClause() {
    override fun executeOnCallDefinitionClause(element: Call, state: ResolveState): Boolean =
        nameArityRange(element)?.let { nameArityRange ->
            val name = nameArityRange.name

            if (name.startsWith(this.name)) {
                val arityInterval = ArityInterval.arityInterval(nameArityRange, state)
                val validResult = (resolvedFinalArity in arityInterval) && name == this.name

                addToResolveResults(element, validResult, state)
            } else {
                null
            }
        } ?: true

    override fun keepProcessing(): Boolean = resolveResultOrderedSet.keepProcessing(incompleteCode)
    fun resolveResults(): Array<PsiElementResolveResult> = resolveResultOrderedSet.toTypedArray()

    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    private fun addToResolveResults(call: Call, validResult: Boolean, state: ResolveState): Boolean =
            (call as? Named)?.nameIdentifier?.let { nameIdentifier ->
                if (PsiTreeUtil.isAncestor(state.get(ENTRANCE), nameIdentifier, false)) {
                    resolveResultOrderedSet.add(call, validResult)

                    false
                } else {
                    resolveResultOrderedSet.add(call, validResult)

                    state.get<Call>(IMPORT_CALL)?.let { importCall ->
                        resolveResultOrderedSet.add(importCall, validResult)
                    }

                    null
                }
            } ?: true

    companion object {
        @JvmOverloads
        @JvmStatic
        fun resolveResults(name: String,
                           resolvedFinalArity: Int,
                           incompleteCode: Boolean,
                           entrance: PsiElement,
                           resolveState: ResolveState = ResolveState.initial()): Array<PsiElementResolveResult> {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            val maxScope = maxScope(entrance)

            PsiTreeUtil.treeWalkUp(
                    multiResolve,
                    entrance,
                    maxScope,
                    resolveState.put(ENTRANCE, entrance).putInitialVisitedElement(entrance)
            )

            return multiResolve.resolveResults()
        }

    }
}
