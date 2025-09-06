package org.elixir_lang.code_insight.line_marker_provider

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.SeparatorPlacement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.*
import org.elixir_lang.psi.impl.siblingExpression
import org.elixir_lang.structure_view.element.CallDefinitionSpecification.Companion.moduleAttributeNameArity
import org.elixir_lang.structure_view.element.CallDefinitionSpecification.Companion.specification
import org.elixir_lang.structure_view.element.CallDefinitionSpecification.Companion.specificationType

class CallDefinition : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? =
        if (daemonCodeAnalyzerSettings.SHOW_METHOD_SEPARATORS) {
            when (element) {
                is AtUnqualifiedNoParenthesesCall<*> -> getLineMarkerInfo(element)
                is Call -> getLineMarkerInfo(element)
                else -> null
            }
        } else {
            null
        }

    private val daemonCodeAnalyzerSettings: DaemonCodeAnalyzerSettings = DaemonCodeAnalyzerSettings.getInstance()
    private val editorColorsManager: EditorColorsManager = EditorColorsManager.getInstance()

    private fun callDefinitionSeparator(
        atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>
    ): LineMarkerInfo<*> {
        val leafPsiElement = atUnqualifiedNoParenthesesCall
            .atIdentifier
            .node
            .findChildByType(ElixirTypes.IDENTIFIER_TOKEN) as LeafPsiElement?
            ?: error(
                "AtUnqualifiedNoParenthesesCall (" +
                        atUnqualifiedNoParenthesesCall.text +
                        ") does not have an Tokenizer token"
            )

        return callDefinitionSeparator(leafPsiElement)
    }

    private fun callDefinitionSeparator(call: Call): LineMarkerInfo<*> {
        // Find the leaf element (identifier token) within the Call
        val leafPsiElement = call
            .functionNameElement()
            ?.node
            ?.findChildByType(ElixirTypes.IDENTIFIER_TOKEN) as LeafPsiElement?
            ?: call
                .node
                .findChildByType(ElixirTypes.IDENTIFIER_TOKEN) as LeafPsiElement?
            ?: error(
                "Call (${call.text}) does not have an IDENTIFIER_TOKEN"
            )

        return callDefinitionSeparator(leafPsiElement)
    }

    private fun callDefinitionSeparator(psiElement: PsiElement): LineMarkerInfo<*> =
        LineMarkerInfo(
            psiElement,
            psiElement.textRange
        ).apply {
            separatorColor = editorColorsManager.globalScheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR)
            separatorPlacement = SeparatorPlacement.TOP
        }

    private fun getLineMarkerInfo(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): LineMarkerInfo<*>? {
        var lineMarkerInfo: LineMarkerInfo<*>? = null

        val moduleAttributeName = moduleAttributeName(atUnqualifiedNoParenthesesCall)

        if (moduleAttributeName == "@doc") {
            val firstInGroup =
                atUnqualifiedNoParenthesesCall
                    .siblingExpression(PREVIOUS_SIBLING)
                    .let { it as? AtUnqualifiedNoParenthesesCall<*> }
                    ?.let { previousModuleAttribute ->
                        val previousModuleAttributeName = moduleAttributeName(previousModuleAttribute)

                        if (previousModuleAttributeName == "@spec") {
                            moduleAttributeNameArity(previousModuleAttribute)?.let { moduleAttributeNameArity ->
                                siblingCallDefinitionClause(
                                    atUnqualifiedNoParenthesesCall,
                                    NEXT_SIBLING
                                )?.let { nextSiblingCallDefinitionClause ->
                                    nameArityInterval(
                                        nextSiblingCallDefinitionClause,
                                        ResolveState.initial()
                                    )?.let { nameArityInterval ->
                                        val arityInterval = nameArityInterval.arityInterval

                                        if (moduleAttributeNameArity.arity in arityInterval) {
                                            // the previous spec is part of the group
                                            false
                                        } else {
                                            null
                                        }
                                    }
                                }
                            }
                        } else {
                            null
                        }
                    }
                    ?: true

            if (firstInGroup) {
                lineMarkerInfo = callDefinitionSeparator(atUnqualifiedNoParenthesesCall)
            }
        } else if (moduleAttributeName == "@spec") {
            val previousExpression = atUnqualifiedNoParenthesesCall.siblingExpression(PREVIOUS_SIBLING)
            var firstInGroup = true

            if (previousExpression is AtUnqualifiedNoParenthesesCall<*>) {
                val previousModuleAttribute = previousExpression as AtUnqualifiedNoParenthesesCall<*>?
                val previousModuleAttributeName = moduleAttributeName(previousModuleAttribute!!)

                if (previousModuleAttributeName == "@doc") {
                    firstInGroup = false
                } else if (previousModuleAttributeName == "@spec") {
                    val moduleAttributeNameArity = moduleAttributeNameArity(atUnqualifiedNoParenthesesCall)

                    if (moduleAttributeNameArity != null) {
                        moduleAttributeNameArity(previousModuleAttribute)?.let { previousModuleAttributeNameArity ->
                            // name match, now check if the arities match.
                            if (moduleAttributeNameArity.arity == previousModuleAttributeNameArity.arity) {
                                val moduleAttributeArity = moduleAttributeNameArity.arity
                                val previousModuleAttributeArity = previousModuleAttributeNameArity.arity

                                if (moduleAttributeArity == previousModuleAttributeArity) {
                                    /* same arity with different pattern is same function, so the previous @spec should
                                       check if it is first because this one isn't */
                                    firstInGroup = false
                                } else {
                                    /* same name, but different arity needs to determine if the call definition has an
                                       arity range. */
                                    specification(atUnqualifiedNoParenthesesCall)?.let { specificationType(it) }?.reference?.let { reference ->
                                        val resolvedList = if (reference is PsiPolyVariantReference) {
                                            val resolveResults = reference.multiResolve(false)

                                            if (resolveResults.isNotEmpty()) {
                                                resolveResults.map { it.element!! }
                                            } else {
                                                null
                                            }
                                        } else {
                                            val resolved = reference.resolve()

                                            if (resolved != null) {
                                                listOf(resolved)
                                            } else {
                                                null
                                            }
                                        }

                                        if (resolvedList != null && resolvedList.isNotEmpty()) {
                                            firstInGroup = resolvedList.filterIsInstance<Call>().none { resolved ->
                                                nameArityInterval(
                                                    resolved,
                                                    ResolveState.initial()
                                                )?.let { (_, resolvedArityRange) ->
                                                    // the current @spec and the previous @spec apply to the same call definition clause
                                                    moduleAttributeArity in resolvedArityRange && previousModuleAttributeArity in resolvedArityRange
                                                } ?: false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val type = specification(atUnqualifiedNoParenthesesCall)?.let { specificationType(it) }

                type?.reference?.let { it as? PsiPolyVariantReference }?.let { reference ->
                    val resolveResults = reference.multiResolve(false)
                    val containingFile = type.containingFile

                    firstInGroup = resolveResults.mapNotNull { it.element }.none { element ->
                        element.containingFile == containingFile && element.textOffset < type.textOffset
                    }
                }
            }

            if (firstInGroup) {
                lineMarkerInfo = callDefinitionSeparator(atUnqualifiedNoParenthesesCall)
            }
        }

        return lineMarkerInfo
    }

    private fun getLineMarkerInfo(call: Call): LineMarkerInfo<*>? {
        var lineMarkerInfo: LineMarkerInfo<*>? = null

        if (CallDefinitionClause.`is`(call)) {
            val previousCallDefinitionClause = siblingCallDefinitionClause(call, PREVIOUS_SIBLING)
            var firstClause: Boolean
            val state = ResolveState.initial()

            firstClause = if (previousCallDefinitionClause == null) {
                true
            } else {
                val callNameArityRange = nameArityInterval(call, state)

                if (callNameArityRange != null) {
                    val previousNameArityRange = nameArityInterval(previousCallDefinitionClause, state)

                    previousNameArityRange == null || previousNameArityRange != callNameArityRange
                } else {
                    true
                }
            }

            if (firstClause) {
                val previousExpression = previousSiblingExpression(call)

                if (previousExpression is AtUnqualifiedNoParenthesesCall<*>) {
                    val previousModuleAttributeDefinition = previousExpression as AtUnqualifiedNoParenthesesCall<*>?
                    val moduleAttributeName = moduleAttributeName(previousModuleAttributeDefinition!!)

                    if (moduleAttributeName == "@doc") {
                        firstClause = false
                    } else if (moduleAttributeName == "@spec") {
                        val callNameArityRange = nameArityInterval(call, state)

                        if (callNameArityRange != null) {
                            val specNameArity = moduleAttributeNameArity(previousModuleAttributeDefinition)

                            if (specNameArity != null) {
                                val specArity = specNameArity.arity
                                val callArityInterval = callNameArityRange.arityInterval

                                firstClause = specArity !in callArityInterval
                            }
                        }
                    }
                }
            }

            if (firstClause) {
                lineMarkerInfo = callDefinitionSeparator(call)
            }
        }

        return lineMarkerInfo
    }

    private fun siblingCallDefinitionClause(
        element: PsiElement,
        function: Function1<PsiElement, PsiElement>
    ): Call? {
        var expression: PsiElement? = element
        var siblingCallDefinitionClause: Call? = null

        while (expression != null) {
            expression = expression.siblingExpression(function)

            if (expression is Call && CallDefinitionClause.`is`(expression)) {
                siblingCallDefinitionClause = expression
                break
            }
        }

        return siblingCallDefinitionClause
    }
}
