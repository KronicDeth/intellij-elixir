package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.call.Call


private fun ElixirAtom.computeReference(): PsiReference = org.elixir_lang.reference.Atom(this)

fun getReference(atom: ElixirAtom): PsiReference? =
        getCachedValue(atom) { CachedValueProvider.Result.create(atom.computeReference(), atom) }

fun ElixirAtom.maybeModularNameToModulars(incompleteCode: Boolean): Set<Call> =
    reference?.maybeModularNameToModulars(incompleteCode) ?: emptySet()

fun PsiReference.maybeModularNameToModulars(incompleteCode: Boolean): Set<Call> {
    val resolveResults = when (this) {
        is PsiPolyVariantReference -> multiResolve(incompleteCode)
        else -> resolve()
                ?.let { arrayOf(PsiElementResolveResult(it, true)) }
                ?: emptyArray()
    }

    return resolveResults.mapNotNull { resolveResult -> resolveResult.element as? Call  }.toSet()
}
