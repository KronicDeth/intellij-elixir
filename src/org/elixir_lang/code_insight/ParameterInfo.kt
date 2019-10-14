package org.elixir_lang.code_insight

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.parameterInfo.*
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.Arguments
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.structure_view.element.CallDefinitionHead
import java.util.Collections.singletonList

class ParameterInfo : ParameterInfoHandler<Arguments, Any> {
    override fun couldShowInLookup(): Boolean = true

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): Arguments? =
            findArguments(context)

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): Arguments? =
            findArguments(context)

    override fun showParameterInfo(element: Arguments, context: CreateParameterInfoContext) {
        PsiTreeUtil.getParentOfType(element, Call::class.java)?.let { call ->
            val itemToShowList = call.references.flatMap { reference ->
                if (reference is PsiPolyVariantReference) {
                    reference.multiResolve(true).flatMap { resolveResult ->
                        resolveResult.element?.let { singletonList(it) } ?: emptyList()
                    }
                } else {
                    reference.resolve()?.let { resolvedElement ->
                        singletonList(resolvedElement)
                    } ?: emptyList<Any>()
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

            if (p is Call) {
                if (CallDefinitionClause.`is`(p)) {
                    CallDefinitionClause.head(p)?.let { callDefinitionClauseHead ->
                        val stripped = CallDefinitionHead.strip(callDefinitionClauseHead)

                        if (stripped is Call) {
                            stripped.finalArguments()?.let { finalArguments ->
                                finalArguments.forEachIndexed { index, psiElement ->
                                    if (index != 0) {
                                        stringBuilder.append(", ")
                                    }

                                    if (index == currentParameterIndex) {
                                        start = stringBuilder.length
                                    }

                                    stringBuilder.append(psiElement.text)

                                    if (index == currentParameterIndex) {
                                        end = stringBuilder.length
                                    }
                                }

                                disabled = finalArguments.size <= currentParameterIndex
                            }
                        } else {
                            TODO()
                        }
                    }
                }
            } else {
                TODO()
            }

            if (stringBuilder.isEmpty()) {
                stringBuilder.append("<no parameters>")
            }

            context.setupUIComponentPresentation(stringBuilder.toString(), start, end, disabled, false, true,
                    context.defaultParameterColor)
        }
    }


    override fun getParametersForLookup(item: LookupElement?, context: ParameterInfoContext?): Array<Any>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun findArguments(context: ParameterInfoContext): Arguments? {
        val elementAtOffset = context.file.findElementAt(context.offset)

        return PsiTreeUtil.getParentOfType<Arguments>(elementAtOffset, Arguments::class.java)
    }
}
