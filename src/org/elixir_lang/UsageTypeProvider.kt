package org.elixir_lang

import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.rules.UsageType
import org.elixir_lang.psi.call.Call

class UsageTypeProvider : com.intellij.usages.impl.rules.UsageTypeProviderEx {
    override fun getUsageType(element: PsiElement?, targets: Array<out UsageTarget>): UsageType? =
            when (element) {
                is Call -> getUsageType(element, targets)
                else -> null
            }

    override fun getUsageType(element: PsiElement?): UsageType? = getUsageType(element, emptyArray())

    private fun getUsageType(call: Call, targets: Array<out UsageTarget>): UsageType? =
        when (ReadWriteAccessDetector.getExpressionAccess(call)) {
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Read -> UsageType.READ
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Write -> UsageType.WRITE
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.ReadWrite -> null
        }
}
