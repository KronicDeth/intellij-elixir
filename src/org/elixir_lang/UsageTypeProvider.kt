package org.elixir_lang

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.rules.UsageType
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.reference.Callable
import org.elixir_lang.semantic.Aliasing
import org.elixir_lang.semantic.Module
import org.elixir_lang.semantic.Use
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.semantic

class UsageTypeProvider : com.intellij.usages.impl.rules.UsageTypeProviderEx {
    override fun getUsageType(element: PsiElement?, targets: Array<out UsageTarget>): UsageType? =
        when (element) {
            is AtUnqualifiedNoParenthesesCall<*> -> MODULE_ATTRIBUTE_ACCUMULATE_OR_OVERRIDE
            is AtOperation -> MODULE_ATTRIBUTE_READ
            is QualifiableAlias -> qualifiableAliasUsageType(element, entrance = element)
            is Call -> getUsageType(element, targets)
            else -> null
        }

    override fun getUsageType(element: PsiElement): UsageType? = getUsageType(element, emptyArray())

    private fun getUsageType(targets: Array<out UsageTarget>): UsageType? =
        targets.map { getUsageType(it) }.fold(null as UsageType?) { acc, targetUsageType ->
            when {
                acc == targetUsageType ->
                    acc
                (acc == FUNCTION_DEFINITION_CLAUSE && targetUsageType == MACRO_DEFINITION_CLAUSE) ||
                        (acc == MACRO_DEFINITION_CLAUSE && targetUsageType == FUNCTION_DEFINITION_CLAUSE) ||
                        (acc == CALL_DEFINITION_CLAUSE && (targetUsageType == FUNCTION_DEFINITION_CLAUSE || targetUsageType == MACRO_DEFINITION_CLAUSE)) ->
                    CALL_DEFINITION_CLAUSE
                acc == null ->
                    targetUsageType
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

    private tailrec fun qualifiableAliasUsageType(psiElement: PsiElement, entrance: QualifiableAlias): UsageType? =
        when (psiElement) {
            is ElixirAccessExpression,
            is ElixirMatchedQualifiedMultipleAliases,
            is ElixirMultipleAliases,
            is ElixirNoParenthesesOneArgument,
            is ElixirUnmatchedQualifiedMultipleAliases,
            is QualifiableAlias ->
                qualifiableAliasUsageType(psiElement.parent, entrance)
            is Qualified ->
                if (psiElement.qualifier().isEquivalentTo(entrance)) {
                    REMOTE_CALL
                } else {
                    null
                }
            is Call ->
                when (psiElement.semantic) {
                    is Use -> USE
                    is Module -> MODULE_DEFINITION
                    is Aliasing -> ALIAS
                    else -> null
                }
            else ->
                null
        }

    private fun getReadUsageType(call: Call, targets: Array<out UsageTarget>): UsageType =
        call.semantic?.let { semantic ->
            when (semantic) {
                is Clause -> when (semantic.definition.time) {
                    Time.RUN -> FUNCTION_CALL
                    Time.GUARD -> GUARD_CALL
                    Time.COMPILE -> MACRO_CALL
                }
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
        } ?: UsageType.READ


    private fun getWriteUsageType(call: Call): UsageType =
        if (Callable.isParameter(call) || Callable.isParameterWithDefault(call)) {
            FUNCTION_PARAMETER
        } else {
            UsageType.WRITE
        }

    companion object {
        val ALIAS = UsageType { "Alias" }
        val CALL = UsageType { "Call" }
        val CALL_DEFINITION_CLAUSE = UsageType { "Call definition clause" }
        val FUNCTION_PARAMETER = UsageType { "Parameter declaration" }
        val MODULE_DEFINITION = UsageType { "Module definition" }
        val MODULE_ATTRIBUTE_ACCUMULATE_OR_OVERRIDE = UsageType { "Module attribute accumulate or override" }
        val MODULE_ATTRIBUTE_READ = UsageType { "Module attribute read" }
    }
}

private val FUNCTION_DEFINITION_CLAUSE = UsageType { "Function definition clause" }
private val FUNCTION_CALL = UsageType { "Function call" }
private val GUARD_CALL = UsageType { "Guard call" }
private val MACRO_CALL = UsageType { "Macro call" }
private val MACRO_DEFINITION_CLAUSE = UsageType { "Macro definition clause" }
private val REMOTE_CALL = UsageType { "Remote call" }
private val USE = UsageType { "Use" }

private fun Array<out UsageTarget>.anyEquivalentElement(element: PsiElement): Boolean =
    element.manager.let { manager ->
        this.any {
            manager.areElementsEquivalent(element, (it as? PsiElement2UsageTargetAdapter)?.element)
        }
    }
