package org.elixir_lang.code_insight.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.elixir_lang.psi.isModuleAttributeNameElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.scope.module_attribute.Variants

/**
 * Completion for a `@<prefix><caret>` module-attribute reference: offers the in-scope, user-defined
 * module attributes (bare name, no `@`, since the `@` is already typed).
 *
 * A dedicated provider is required because [org.elixir_lang.model.psi.module_attribute.ModuleAttributeReference]
 * is a Symbol-API `PsiSymbolReference`, which - unlike a classic `PsiReference` - has no `getVariants`
 * hook for the platform's reference-completion path.
 */
class ModuleAttribute : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {
        val usage = PsiTreeUtil.getParentOfType(parameters.position, Call::class.java, false)
            ?.takeIf { it.isModuleAttributeNameElement() }
            ?: return

        resultSet.addAllElements(Variants.lookupElementList(usage))
    }
}
