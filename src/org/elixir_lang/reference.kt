package org.elixir_lang

import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.isAncestor
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.ElixirPsiImplUtil

fun safeMultiResolve(reference: PsiPolyVariantReference, incompleteCode: Boolean): Array<ResolveResult> =
    try {
        reference.multiResolve(incompleteCode)
    } catch (stackOverflowError: StackOverflowError) {
        Logger.error(PsiPolyVariantReference::class.java, "StackOverflow resolving reference", reference.element)

        emptyArray()
    }

fun resolvesToModularName(call: Call, state: ResolveState, modularName: String): Boolean =
        // it is not safe to call `multiResolve` on the call's reference if that `call` is currently being resolved.
        if (!isBeingResolved(call, state)) {
            val reference = call.reference as PsiPolyVariantReference

            safeMultiResolve(reference, false).any { resolveResult ->
                if (resolveResult.isValidResult) {
                    resolveResult.element?.let { it as? Call }?.let { resolved ->
                        CallDefinitionClause.isMacro(resolved) &&
                                org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall(resolved)?.name  == modularName
                    } ?: false
                } else {
                    false
                }
            }
        } else {
            false
        }

private fun isBeingResolved(call: Call, state: ResolveState): Boolean =
        call.isEquivalentTo(state.get(ElixirPsiImplUtil.ENTRANCE)) || qualifierIsBeingResolved(call, state)

private fun qualifierIsBeingResolved(call: Call, state: ResolveState): Boolean =
        if (call is Qualified) {
            call.qualifier().isAncestor(state.get(ElixirPsiImplUtil.ENTRANCE), strict = false)
        } else {
            false
        }
