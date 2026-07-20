package org.elixir_lang.code_insight.completion

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.code_insight.preferFunctionHeads
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.code_insight.lookup.element.CallDefinitionClause as CallDefinitionClauseLookupElement
import org.elixir_lang.code_insight.lookup.element_renderer.CallDefinitionClause as CallDefinitionClauseRenderer
import org.elixir_lang.psi.CallDefinitionClause as CallDefinitionClausePsi

/**
 * The function-name [LookupElement]s a modular ([scope]) offers when completing a **remote**
 * reference: one entry per public function name, preferring bare function heads. Only public
 * (exported) functions are offered because a remote / MFA dispatch (`Mod.fun(...)`,
 * `apply(Mod, :fun, args)`, `{Mod, :fun, arity}`) can never reach a private function - a private
 * function is callable only through a local unqualified call inside its own module. Handles both
 * source modules ([Call]) and BEAM-decompiled modules ([ModuleImpl]); any other element type yields
 * nothing.
 *
 * @param appendParentheses when `true` (qualified `Mod.<caret>` call completion) the inserted name is
 *   followed by `()`; when `false` (MFA atom `:<caret>` completion) only the bare name is inserted,
 *   because an atom is a name, not a call.
 *
 * Shared by qualified `Mod.<caret>` completion
 * ([org.elixir_lang.code_insight.completion.provider.CallDefinitionClause]) and MFA atom completion
 * ([org.elixir_lang.model.psi.atom.AtomReference.getVariants]).
 */
fun callDefinitionClauseLookupElements(
    scope: PsiElement,
    appendParentheses: Boolean = true
): Iterable<LookupElement> = when (scope) {
    is Call -> callDefinitionClauseLookupElements(scope, appendParentheses)
    is ModuleImpl<*> -> callDefinitionClauseLookupElements(scope, appendParentheses)
    else -> emptyList()
}

/**
 * The remote-completion [LookupElement]s offered by the set of [modulars] a modular name resolved to
 * (via [org.elixir_lang.psi.impl.maybeModularNameToModulars]). Source modules ([Call]) are preferred
 * over BEAM-decompiled stubs ([ModuleImpl]) so a module available in both forms is not offered twice.
 *
 * @see callDefinitionClauseLookupElements for the per-modular contract and [appendParentheses].
 */
fun callDefinitionClauseLookupElements(
    modulars: Collection<PsiElement>,
    appendParentheses: Boolean = true
): List<LookupElement> {
    val sourceModulars = modulars.filterIsInstance<Call>()
    val effectiveModulars = if (sourceModulars.isNotEmpty()) sourceModulars else modulars

    return effectiveModulars.flatMap { callDefinitionClauseLookupElements(it, appendParentheses) }
}

private fun callDefinitionClauseLookupElements(scope: Call, appendParentheses: Boolean): Iterable<LookupElement> {
    val publicClauses = scope
        .macroChildCalls()
        .filter { CallDefinitionClausePsi.`is`(it) }
        .filter { CallDefinitionClausePsi.isPublic(it) }

    return preferFunctionHeads(publicClauses).map { (name, bestClause) ->
        lookupElement(name, bestClause, appendParentheses)
    }
}

private fun callDefinitionClauseLookupElements(moduleImpl: ModuleImpl<*>, appendParentheses: Boolean): Iterable<LookupElement> =
    moduleImpl.callDefinitions()
        .filter { it.isExported }
        .map { callDefinition ->
            lookupElement(callDefinition.exportedName(), callDefinition, appendParentheses)
        }

private fun lookupElement(name: String, element: PsiElement, appendParentheses: Boolean): LookupElement =
    if (appendParentheses) {
        CallDefinitionClauseLookupElement.createWithSmartPointer(name, element)
    } else {
        LookupElementBuilder
            .createWithSmartPointer(name, element)
            .withRenderer(CallDefinitionClauseRenderer(name))
    }
