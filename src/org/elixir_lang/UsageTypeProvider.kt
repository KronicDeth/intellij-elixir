package org.elixir_lang

import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.rules.UsageType
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable

class UsageTypeProvider : com.intellij.usages.impl.rules.UsageTypeProviderEx {
    override fun getUsageType(element: PsiElement?, targets: Array<out UsageTarget>): UsageType? =
            when (element) {
                is Call -> getUsageType(element)
                else -> null
            }

    override fun getUsageType(element: PsiElement?): UsageType? = getUsageType(element, emptyArray())

    private val FUNCTION_PARAMETER = UsageType("Function parameter declaration")

    private fun getUsageType(call: Call): UsageType? =
        when (ReadWriteAccessDetector.getExpressionAccess(call)) {
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Read -> UsageType.READ
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Write -> getWriteUsageType(call)
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.ReadWrite -> null
        }

    private fun getWriteUsageType(call: Call): UsageType =
        if (Callable.isParameter(call) || Callable.isParameterWithDefault(call)) {
            FUNCTION_PARAMETER
        } else {
            UsageType.WRITE
        }
}
