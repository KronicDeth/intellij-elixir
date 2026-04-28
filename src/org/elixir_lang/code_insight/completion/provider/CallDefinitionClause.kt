package org.elixir_lang.code_insight.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.util.ProcessingContext
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.code_insight.preferFunctionHeads
import org.elixir_lang.navigation.isDecompiled
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.maybeModularNameToModulars

class CallDefinitionClause : CompletionProvider<CompletionParameters>() {
    private fun callDefinitionClauseLookupElements(scope: PsiElement): Iterable<LookupElement> = when (scope) {
        is Call -> callDefinitionClauseLookupElements(scope)
        is ModuleImpl<*> -> callDefinitionClauseLookupElements(scope)
        else -> emptyList()
    }

    private fun callDefinitionClauseLookupElements(scope: Call): Iterable<LookupElement> {
        val callDefinitionClauseList = scope
            .macroChildCalls()
            .filter { org.elixir_lang.psi.CallDefinitionClause.`is`(it) }

        // decompiled private functions can't be made public, so exclude them
        val callable = if (scope.isDecompiled()) {
            callDefinitionClauseList.filter { org.elixir_lang.psi.CallDefinitionClause.isPublic(it) }
        } else {
            callDefinitionClauseList
        }

        return preferFunctionHeads(callable).map { (name, bestClause) ->
            org.elixir_lang.code_insight.lookup.element.CallDefinitionClause.createWithSmartPointer(
                name,
                bestClause
            )
        }
    }

    private fun callDefinitionClauseLookupElements(moduleImpl: ModuleImpl<*>): Iterable<LookupElement> =
        moduleImpl.callDefinitions()
            .filter { it.isExported }
            .map { callDefinition ->
                org.elixir_lang.code_insight.lookup.element.CallDefinitionClause.createWithSmartPointer(
                    callDefinition.exportedName(),
                    callDefinition
                )
            }

    private fun maybeModularName(parameters: CompletionParameters): PsiElement? =
        parameters.originalPosition?.let { originalPosition ->
            originalPosition.parent?.let { originalParent ->
                val grandParent = originalParent.parent

                if (grandParent is org.elixir_lang.psi.qualification.Qualified) {
                    grandParent.qualifier()
                } else if (originalPosition is PsiWhiteSpace) {
                    val originalPositionOffset = originalPosition.textOffset

                    if (originalPositionOffset > 0) {
                        val previousElement = parameters.originalFile.findElementAt(originalPositionOffset - 1)

                        if (previousElement != null && previousElement.node.elementType === ElixirTypes.DOT_OPERATOR) {
                            previousElement.parent.prevSibling
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {
        maybeModularName(parameters)?.let { maybeModularName ->
            maybeModularName.containingFile?.let { containingFile ->
                val modulars = maybeModularName.maybeModularNameToModulars(
                    maxScope = containingFile,
                    useCall = null,
                    incompleteCode = true
                )

                val modularsResultSet = if (resultSet.prefixMatcher.prefix.endsWith(".")) {
                    resultSet.withPrefixMatcher("")
                } else {
                    resultSet
                }

                // Prefer source modulars (Call) over BEAM stubs (ModuleImpl) to avoid duplicate completions
                val sourceModulars = modulars.filterIsInstance<Call>()
                val effectiveModulars = if (sourceModulars.isNotEmpty()) sourceModulars else modulars

                for (modular in effectiveModulars) {
                    modularsResultSet.addAllElements(
                        callDefinitionClauseLookupElements(modular)
                    )
                }
            }
        }
    }
}
