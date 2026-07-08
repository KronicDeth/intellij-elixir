package org.elixir_lang.psi.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.psi.AtOperation

private fun AtOperation.computeReference(): PsiReference? =
        when (containingFile.virtualFile?.fileType) {
            org.elixir_lang.leex.file.Type.INSTANCE -> org.elixir_lang.leex.reference.Assign(this)
            else -> null
        }

fun AtOperation.cachedReference(): PsiReference? =
        getCachedValue(this) { CachedValueProvider.Result.create(computeReference(), this) }
