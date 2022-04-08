package org.elixir_lang.code_insight.line_marker_provider

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.SeparatorPlacement
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.*
import org.elixir_lang.psi.impl.siblingExpression
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Head.Companion.nameArityInterval
import org.elixir_lang.semantic.semantic
import org.elixir_lang.semantic.type.definition.source.Specification

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

    private fun callDefinitionSeparator(psiElement: PsiElement): LineMarkerInfo<*> =
        LineMarkerInfo(
            psiElement,
            psiElement.textRange,
            null,
            null,
            null,
            GutterIconRenderer.Alignment.RIGHT
        ).apply {
            separatorColor = editorColorsManager.globalScheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR)
            separatorPlacement = SeparatorPlacement.TOP
        }

    private fun getLineMarkerInfo(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): LineMarkerInfo<*>? {
        var lineMarkerInfo: LineMarkerInfo<*>? = null

        when (val semantic = atUnqualifiedNoParenthesesCall.semantic) {
            is org.elixir_lang.semantic.documentation.CallDefinition -> {
                val firstInGroup =
                    atUnqualifiedNoParenthesesCall
                        .siblingExpression(PREVIOUS_SIBLING)
                        .semantic
                        ?.let { it as? Specification }
                        ?.let { specification ->
                            specification.nameArity.let { moduleAttributeNameArity ->
                                siblingCallDefinitionClause(
                                    atUnqualifiedNoParenthesesCall,
                                    NEXT_SIBLING
                                )?.let { nextSiblingCallDefinitionClause ->
                                    nameArityInterval(nextSiblingCallDefinitionClause, null)?.let { nameArityInterval ->
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
                        }
                        ?: true

                if (firstInGroup) {
                    lineMarkerInfo = callDefinitionSeparator(atUnqualifiedNoParenthesesCall)
                }
            }
            is Specification -> {
                val previousExpression = atUnqualifiedNoParenthesesCall.siblingExpression(PREVIOUS_SIBLING)
                var firstInGroup = true

                if (previousExpression is AtUnqualifiedNoParenthesesCall<*>) {
                    when (val previousSemantic = previousExpression.semantic) {
                        is org.elixir_lang.semantic.documentation.CallDefinition ->
                            firstInGroup = false
                        is Specification -> {
                            val nameArity = semantic.nameArity
                            val arity = nameArity.arity
                            val previousNameArity = previousSemantic.nameArity
                            val previousArity = previousNameArity.arity

                            // name match, now check if the arities match.
                            if (arity == previousArity) {
                                /* same arity with different pattern is same function, so the previous @spec should
                                   check if it is first because this one isn't */
                                firstInGroup = false
                            } else {
                                /* same name, but different arity needs to determine if the call definition has an
                                   arity range. */
                                val callDefinitions = semantic.callDefinitions

                                if (callDefinitions != null) {
                                    firstInGroup = callDefinitions.none { callDefinition ->
                                        callDefinition.nameArityInterval?.arityInterval?.let { arityInterval ->
                                            arityInterval.contains(arity) && arityInterval.contains(previousArity)
                                        } ?: false
                                    }
                                }
                            }
                        }
                        else -> Unit
                    }
                } else {
                    val callDefinitions = semantic.callDefinitions

                    if (callDefinitions != null) {
                        val containingFile = atUnqualifiedNoParenthesesCall.containingFile
                        val textOffset = atUnqualifiedNoParenthesesCall.textOffset

                        firstInGroup =
                            callDefinitions.flatMap { it.clauses }.map { it.psiElement }.none { element ->
                                element.containingFile == containingFile && element.textOffset < textOffset
                            }
                    }
                }

                if (firstInGroup) {
                    lineMarkerInfo = callDefinitionSeparator(atUnqualifiedNoParenthesesCall)
                }
            }
        }

        return lineMarkerInfo
    }

    private fun getLineMarkerInfo(call: Call): LineMarkerInfo<*>? =
        call.semantic?.let { it as? Clause }?.let { clause ->
            val containingFile = call.containingFile
            val textOffset = call.textOffset

            var firstClause = clause.definition.clauses.none { clause ->
                clause.psiElement.let { it.containingFile == containingFile && it.textOffset < textOffset }
            }

            if (firstClause) {
                val previousExpression = previousSiblingExpression(call)

                when (val previousSemantic = previousExpression?.semantic) {
                    is org.elixir_lang.semantic.documentation.CallDefinition ->
                        firstClause = false
                    is Specification -> {
                        val clauseNameArityInterval = clause.nameArityInterval

                        if (clauseNameArityInterval != null) {
                            val previousNameArity = previousSemantic.nameArity

                            val specArity = previousNameArity.arity
                            val callArityInterval = clauseNameArityInterval.arityInterval

                            firstClause = specArity !in callArityInterval
                        }
                    }
                }
            }

            if (firstClause) {
                callDefinitionSeparator(call)
            } else {
                null
            }
        }

    private fun siblingCallDefinitionClause(
        element: PsiElement,
        function: Function1<PsiElement, PsiElement>
    ): Call? {
        var expression: PsiElement? = element
        var siblingCallDefinitionClause: Call? = null

        while (expression != null) {
            expression = expression.siblingExpression(function)
            val clause = expression.semantic as? Clause

            if (clause != null) {
                siblingCallDefinitionClause = clause.psiElement as? Call
                break
            }
        }

        return siblingCallDefinitionClause
    }
}
