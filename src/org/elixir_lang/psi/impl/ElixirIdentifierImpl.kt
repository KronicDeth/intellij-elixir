package org.elixir_lang.psi.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.psi.UnqualifiedBracketOperation

fun getReference(identifier: ElixirIdentifier): PsiReference? =
        getCachedValue(identifier) { CachedValueProvider.Result.create(identifier.computeReference(), identifier) }

private fun ElixirIdentifier.computeReference(): PsiReference? =
        if (parent is UnqualifiedBracketOperation) {
            org.elixir_lang.reference.Identifier(this)
        } else {
            null
        }
