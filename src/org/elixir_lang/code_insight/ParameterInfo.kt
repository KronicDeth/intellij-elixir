package org.elixir_lang.code_insight

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.parameterInfo.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.Arguments
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.semantic
import java.util.Collections.singletonList

class ParameterInfo : ParameterInfoHandler<Arguments, Any> {
    override fun findElementForParameterInfo(context: CreateParameterInfoContext): Arguments? =
            findArguments(context)

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): Arguments? =
            findArguments(context)

    override fun showParameterInfo(element: Arguments, context: CreateParameterInfoContext) {
        PsiTreeUtil.getParentOfType(element, Call::class.java)?.let { call ->
            val itemToShowList = call.references.flatMap { reference ->
                if (reference is PsiPolyVariantReference) {
                    reference.multiResolve(true).flatMap { resolveResult ->
                        resolveResult
                                .element
                                ?.semantic
                                ?.let { it as? Clause }
                                ?.let { listOf(it) }
                                .orEmpty()
                    }
                } else {
                    reference
                            .resolve()
                            ?.semantic
                            ?.let { it as? Clause }
                            ?.let { listOf(it) }
                            .orEmpty()
                }
            }

            if (itemToShowList.isNotEmpty()) {
                context.itemsToShow = itemToShowList.toTypedArray()
                context.showHint(element, element.textRange.startOffset, this)
            }
        }
    }

    override fun updateParameterInfo(parameterOwner: Arguments, context: UpdateParameterInfoContext) {
        context.setCurrentParameter(ParameterInfoUtils.getCurrentParameterIndex(parameterOwner.node, context.offset, ElixirTypes.COMMA))
    }

    override fun updateUI(p: Any?, context: ParameterInfoUIContext) {
        if (p == null) {
            context.isUIComponentEnabled = false
        } else {
            val currentParameterIndex = context.currentParameterIndex

            val stringBuilder = StringBuilder()
            var disabled = false
            var start = 0
            var end = 0

            if (p is PsiElement) {
                when (val semantic = p.semantic) {
                    is org.elixir_lang.semantic.call.definition.clause.Call -> {
                        val parameters = semantic.head?.parameters.orEmpty()

                        parameters.forEachIndexed { index, parameter ->
                            if (index != 0) {
                                stringBuilder.append(", ")
                            }

                            if (index == currentParameterIndex) {
                                start = stringBuilder.length
                            }

                            stringBuilder.append(parameter.psiElement.text)

                            if (index == currentParameterIndex) {
                                end = stringBuilder.length
                            }
                        }

                        disabled = parameters.size <= currentParameterIndex
                    }
                }
            }

            if (stringBuilder.isEmpty()) {
                stringBuilder.append("<no parameters>")
            }

            context.setupUIComponentPresentation(stringBuilder.toString(), start, end, disabled, false, true,
                    context.defaultParameterColor)
        }
    }

    private fun findArguments(context: ParameterInfoContext): Arguments? {
        val elementAtOffset = context.file.findElementAt(context.offset)

        return PsiTreeUtil.getParentOfType<Arguments>(elementAtOffset, Arguments::class.java)
    }
}
