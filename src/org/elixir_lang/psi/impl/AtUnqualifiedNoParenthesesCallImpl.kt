package org.elixir_lang.psi.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.impl.call.computeReference

private fun AtUnqualifiedNoParenthesesCall<*>.computeReference(): PsiReference? =
        org.elixir_lang.reference.ModuleAttribute(this)

fun AtUnqualifiedNoParenthesesCall<*>.getReference(): PsiReference? =
        CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(computeReference(), this)
        }

