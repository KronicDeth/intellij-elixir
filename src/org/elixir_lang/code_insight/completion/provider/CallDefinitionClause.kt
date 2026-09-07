package org.elixir_lang.code_insight.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.elixir_lang.code_insight.completion.callDefinitionClauseLookupElements
import org.elixir_lang.psi.ElixirStructOperation
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.qualifier
import org.elixir_lang.psi.impl.maybeModularNameToModulars

class CallDefinitionClause : CompletionProvider<CompletionParameters>() {
    /** The copy is read first: its dummy identifier terminates the qualified name. */
    private fun maybeModularName(parameters: CompletionParameters): PsiElement? =
        (maybeModularNameAt(parameters.position) ?: maybeModularNameAt(parameters.originalPosition))
            ?.takeIf(::callIsValidAt)

    private fun maybeModularNameAt(position: PsiElement?): PsiElement? =
        position?.parent?.parent?.let { qualifiedName ->
            // Bare, the copy's dummy makes `Mod.` a qualified alias; with a prefix typed it is a call.
            when (qualifiedName) {
                is org.elixir_lang.psi.qualification.Qualified -> qualifiedName.qualifier()
                is QualifiableAlias -> qualifiedName.qualifier()
                else -> null
            }
        }

    private fun callIsValidAt(qualifiedAlias: PsiElement): Boolean =
        generateSequence(qualifiedAlias.parent) { it.parent }
            .takeWhile { it !is PsiFile }
            .none { ancestor ->
                when (ancestor) {
                    is ElixirStructOperation -> !isInsideStructArguments(ancestor, qualifiedAlias)
                    is Call -> namesModuleDirective(ancestor, qualifiedAlias)
                    else -> false
                }
            }

    /**
     * `structOperation ::= mapPrefixOperator mapExpression eolStar mapArguments`, so a field value has the
     * struct operation as an ancestor too and only the name part must be refused.
     */
    private fun isInsideStructArguments(
        structOperation: ElixirStructOperation,
        qualifiedAlias: PsiElement
    ): Boolean = PsiTreeUtil.isAncestor(structOperation.mapArguments, qualifiedAlias, false)

    /**
     * `defmodule` needs listing even though `QualifiableAliasImpl.isDefmoduleDeclarationName` nulls the
     * reference for one: that recognises a `defmodule` by its `do` block, which a name still being typed
     * has not got yet.
     */
    private fun namesModuleDirective(call: Call, qualifiedAlias: PsiElement): Boolean =
        call.functionName() in MODULE_NAMING_FUNCTION_NAMES &&
            call.resolvedModuleName() == Module.KERNEL &&
            call.primaryArguments()?.firstOrNull()
                ?.let { PsiTreeUtil.isAncestor(it, qualifiedAlias, false) } == true

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

                modularsResultSet.addAllElements(
                    callDefinitionClauseLookupElements(modulars)
                )
            }
        }
    }

    companion object {
        /**
         * `Kernel` calls whose first argument is a module name rather than an expression.
         *
         * The other module-name positions this does not reach are listed in
         * [#4051](https://github.com/KronicDeth/intellij-elixir/issues/4051).
         */
        private val MODULE_NAMING_FUNCTION_NAMES = setOf(
            Function.ALIAS,
            Function.DEFIMPL,
            Function.DEFMODULE,
            Function.DEFPROTOCOL,
            Function.IMPORT,
            Function.REQUIRE,
            Function.USE
        )
    }
}
