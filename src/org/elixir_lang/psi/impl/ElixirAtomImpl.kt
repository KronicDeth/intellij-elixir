package org.elixir_lang.psi.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.call.Call


private fun ElixirAtom.computeReference(): PsiReference = org.elixir_lang.reference.Atom(this)

fun getReference(atom: ElixirAtom): PsiReference? =
        getCachedValue(atom) { CachedValueProvider.Result.create(atom.computeReference(), atom) }

fun ElixirAtom.maybeModularNameToModular(): Call? =
    reference?.let { reference ->
        (reference.resolve() as? Call)?.let { resolved ->
            if (org.elixir_lang.psi.stub.type.call.Stub.isModular(resolved)) {
                resolved
            } else {
                null
            }
        }
    }
