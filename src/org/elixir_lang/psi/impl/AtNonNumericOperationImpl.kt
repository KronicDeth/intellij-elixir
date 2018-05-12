package org.elixir_lang.psi.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.reference.ModuleAttribute
import org.elixir_lang.reference.ModuleAttribute.Companion.isNonReferencing

private fun AtNonNumericOperation.computeReference(): PsiReference? =
        if (!isNonReferencing(this)) {
            ModuleAttribute(this)
        } else {
            null
        }

fun AtNonNumericOperation.getReference(): PsiReference? =
        getCachedValue(this) { CachedValueProvider.Result.create(computeReference(), this) }

