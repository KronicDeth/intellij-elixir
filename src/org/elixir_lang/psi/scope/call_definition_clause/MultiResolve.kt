package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named

import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.nameArityRange

class MultiResolve
private constructor(private val name: String, private val resolvedFinalArity: Int,
                    private val incompleteCode: Boolean) : org.elixir_lang.psi.scope.CallDefinitionClause() {
    private var resolvedSet: MutableSet<PsiElement>? = null

    var resolveResultList: List<ResolveResult>? = null
        private set

    override fun executeOnCallDefinitionClause(element: Call, state: ResolveState): Boolean =
        nameArityRange(element)?.let { nameArityRange ->
            val name = nameArityRange.name

            if (name == this.name) {
                val arityInterval = ArityInterval.arityInterval(nameArityRange, state)

                when {
                    resolvedFinalArity in arityInterval -> addToResolveResultList(element, true, state)
                    incompleteCode -> addToResolveResultList(element, false, state)
                    else -> null
                }
            } else if (incompleteCode && name.startsWith(this.name)) {
                addToResolveResultList(element, false, state)
            } else {
                null
            }
        } ?: true

    override fun keepProcessing(): Boolean =
            org.elixir_lang.psi.scope.MultiResolve.keepProcessing(incompleteCode, resolveResultList)

    private fun addNewToResolveResultList(element: PsiElement, validResult: Boolean) {
        if (resolvedSet == null || !resolvedSet!!.contains(element)) {
            resolveResultList = org.elixir_lang.psi.scope.MultiResolve.addToResolveResultList(
                    resolveResultList, PsiElementResolveResult(element, validResult)
            )

            if (resolvedSet == null) {
                resolvedSet = mutableSetOf()
            }

            resolvedSet!!.add(element)
        }
    }

    private fun addToResolveResultList(call: Call, validResult: Boolean, state: ResolveState): Boolean =
            (call as? Named)?.nameIdentifier?.let { nameIdentifier ->
                if (PsiTreeUtil.isAncestor(state.get(ENTRANCE), nameIdentifier, false)) {
                    addNewToResolveResultList(call, validResult)

                    false
                } else {
                    /* Doesn't use a Map<PsiElement, ResolveSet> so that MultiResolve's helpers that require a
                               List<ResolveResult> can still work */
                    addNewToResolveResultList(call, validResult)

                    state.get<Call>(IMPORT_CALL)?.let { importCall ->
                        addNewToResolveResultList(importCall, validResult)
                    }

                    null
                }
            } ?: true

    companion object {
        @JvmOverloads
        @JvmStatic
        fun resolveResultList(name: String,
                              resolvedFinalArity: Int,
                              incompleteCode: Boolean,
                              entrance: PsiElement,
                              resolveState: ResolveState = ResolveState.initial()): List<ResolveResult>? {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            PsiTreeUtil.treeWalkUp(
                    multiResolve,
                    entrance,
                    entrance.containingFile,
                    resolveState.put(ENTRANCE, entrance)
            )
            return multiResolve.resolveResultList
        }
    }
}
