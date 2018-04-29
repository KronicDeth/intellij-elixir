package org.elixir_lang

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.rules.UsageType
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.reference.Callable
import org.elixir_lang.structure_view.element.CallDefinitionClause
import org.elixir_lang.structure_view.element.Use

class UsageTypeProvider : com.intellij.usages.impl.rules.UsageTypeProviderEx {
    override fun getUsageType(element: PsiElement?, targets: Array<out UsageTarget>): UsageType? =
            when (element) {
                is QualifiableAlias -> getUsageType(element, targets)
                is Call -> getUsageType(element, targets)
                else -> null
            }

    override fun getUsageType(element: PsiElement?): UsageType? = getUsageType(element, emptyArray())

    private fun getUsageType(targets: Array<out UsageTarget>): UsageType? =
            targets.map { getUsageType(it) }.reduce { acc, targetUsageType ->
                when {
                    acc == targetUsageType ->
                        acc
                    (acc == FUNCTION_DEFINITION_CLAUSE && targetUsageType == MACRO_DEFINITION_CLAUSE) ||
                            (acc == MACRO_DEFINITION_CLAUSE && targetUsageType == FUNCTION_DEFINITION_CLAUSE) ||
                            (acc == CALL_DEFINITION_CLAUSE && (targetUsageType == FUNCTION_DEFINITION_CLAUSE || targetUsageType == MACRO_DEFINITION_CLAUSE)) ->
                        CALL_DEFINITION_CLAUSE
                    else ->
                        TODO()
                }
            }

    private fun getUsageType(usageTarget: UsageTarget): UsageType? =
            when (usageTarget) {
                is PsiElement2UsageTargetAdapter ->
                    getUsageType(usageTarget.element).let { usageType ->
                        when (usageType) {
                            FUNCTION_CALL -> FUNCTION_DEFINITION_CLAUSE
                            MACRO_CALL -> MACRO_DEFINITION_CLAUSE
                            else -> usageType
                        }
                    }
                else -> null
            }

    private fun getUsageType(call: Call, targets: Array<out UsageTarget>): UsageType? =
        when (ReadWriteAccessDetector.getExpressionAccess(call)) {
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Read -> getReadUsageType(call, targets)
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.Write -> getWriteUsageType(call)
            com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.ReadWrite -> null
        }

    private fun getUsageType(qualifiableAlias: QualifiableAlias, targets: Array<out UsageTarget>): UsageType? =
            qualifiableAlias.parent?.let { it as? ElixirAccessExpression }?.let { accessExpression ->
                val accessExpressionParent = accessExpression.parent

                when (accessExpressionParent) {
                    is Qualified -> if (accessExpressionParent.qualifier().isEquivalentTo(accessExpression)) {
                        REMOTE_CALL
                    } else {
                        null
                    }
                    is ElixirNoParenthesesOneArgument -> {
                        if (accessExpressionParent.parent?.let { it as? Call }?.let { Use.`is`(it) } == true) {
                            USE
                        } else {
                            null
                        }
                    }
                    else -> null
                }
            }

    private fun getReadUsageType(call: Call, targets: Array<out UsageTarget>): UsageType {
        return when {
            CallDefinitionClause.isFunction(call) -> FUNCTION_CALL
            CallDefinitionClause.isMacro(call) -> MACRO_CALL
            else -> {
                val targetsUsageType = getUsageType(targets)
                val parameter = Parameter(call).let { Parameter.putParameterized(it) }

                if (parameter.isCallDefinitionClauseName) {
                     val parameterized = parameter.parameterized

                    if (parameterized != null && targets.anyEquivalentElement(parameterized)) {
                        targetsUsageType
                    } else {
                        CALL_DEFINITION_CLAUSE
                    }
                } else {
                    when (targetsUsageType) {
                        CALL_DEFINITION_CLAUSE -> CALL
                        FUNCTION_DEFINITION_CLAUSE -> FUNCTION_CALL
                        MACRO_DEFINITION_CLAUSE -> MACRO_CALL
                        else -> null
                    }
                } ?: UsageType.READ
            }
        }
    }

    private fun getWriteUsageType(call: Call): UsageType =
        if (Callable.isParameter(call) || Callable.isParameterWithDefault(call)) {
            FUNCTION_PARAMETER
        } else {
            UsageType.WRITE
        }
}

private val CALL = UsageType("Call")
private val CALL_DEFINITION_CLAUSE = UsageType("Call definition clause")
private val FUNCTION_DEFINITION_CLAUSE = UsageType("Function definition clause")
private val FUNCTION_CALL = UsageType("Function call")
private val FUNCTION_PARAMETER = UsageType("Parameter declaration")
private val MACRO_CALL = UsageType("Macro call")
private val MACRO_DEFINITION_CLAUSE = UsageType("Macro definition clause")
private val REMOTE_CALL = UsageType("Remote call")
private val USE = UsageType("Use")

private fun Array<out UsageTarget>.anyEquivalentElement(element: PsiElement): Boolean =
    element.manager.let { manager ->
        this.any {
            manager.areElementsEquivalent(element, (it as? PsiElement2UsageTargetAdapter)?.element)
        }
    }
