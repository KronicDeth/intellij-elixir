package org.elixir_lang

import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.rules.UsageType
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable
import org.elixir_lang.structure_view.element.CallDefinitionClause

class UsageTypeProvider : com.intellij.usages.impl.rules.UsageTypeProviderEx {
    override fun getUsageType(element: PsiElement?, targets: Array<out UsageTarget>): UsageType? =
            when (element) {
                is Call -> getUsageType(element)
                else -> null
            }

    override fun getUsageType(element: PsiElement?): UsageType? = getUsageType(element, emptyArray())

    private val FUNCTION_CALL = UsageType("Function call")
    private val FUNCTION_PARAMETER = UsageType("Parameter declaration")
    private val MACRO_CALL = UsageType("Macro call")

    private fun getUsageType(call: Call): UsageType? =
        when (ReadWriteAccessDetector.getExpressionAccess(call)) {
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Read -> getReadUsageType(call)
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Write -> getWriteUsageType(call)
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.ReadWrite -> null
        }

    private fun getReadUsageType(call: Call): UsageType =
            when {
                CallDefinitionClause.isFunction(call) -> FUNCTION_CALL
                CallDefinitionClause.isMacro(call) -> MACRO_CALL
                else -> UsageType.READ
            }

    private fun getWriteUsageType(call: Call): UsageType =
        if (Callable.isParameter(call) || Callable.isParameterWithDefault(call)) {
            FUNCTION_PARAMETER
        } else {
            UsageType.WRITE
        }
}
