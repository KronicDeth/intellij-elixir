package org.elixir_lang.psi.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall

fun AtUnqualifiedNoParenthesesCall<*>.getReference(): PsiReference? =
        CachedValuesManager.getCachedValue(this) {
            org.elixir_lang.reference.ModuleAttribute(this)
                    .let { CachedValueProvider.Result.create(it, this) }
        }

