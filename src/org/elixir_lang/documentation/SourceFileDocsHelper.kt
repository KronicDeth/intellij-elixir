package org.elixir_lang.documentation

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoParenthesesCallImpl
import org.elixir_lang.psi.impl.getModuleName
import org.elixir_lang.psi.impl.prevSiblingSequence

object SourceFileDocsHelper {

    fun fetchDocs(element: PsiElement, resolved: PsiElement): FetchedDocs? {
        // If resolved to a module, then fetch moduledoc from the body
        if (resolved.firstChild?.text == "defmodule") {
            val moduleDoc = (resolved as? ElixirUnmatchedUnqualifiedNoParenthesesCallImpl)
                    ?.doBlock
                    ?.stab
                    ?.stabBody
                    ?.unmatchedExpressionList
                    ?.asSequence()
                    ?.filterIsInstance<ElixirUnmatchedAtUnqualifiedNoParenthesesCall>()
                    ?.filter { it.atIdentifier.lastChild?.text == "moduledoc" }
                    ?.mapNotNull { (it.lastChild?.firstChild?.firstChild as? Heredoc)?.children?.toList() }
                    ?.flatten()
                    ?.joinToString("") { it.text }

            if (!moduleDoc.isNullOrEmpty()) {
                return FetchedDocs.ModuleDocumentation(resolved.canonicalName().orEmpty(), moduleDoc)
            }
        }

        if (element !is Call) {
            return null
        }

        val functionName = (element as? ElixirUnmatchedUnqualifiedNoParenthesesCall)?.canonicalName() ?: element.functionName().orEmpty()
        val arityRange = element.resolvedFinalArityRange()

        val deprecated = findDeprecatedAttribute(resolved)

        val pair =
                resolved.siblings()
                        .filterIsInstance<Call>()
                        .filter { it.name == functionName }
                        .sortedWith(
                                compareByDescending<PsiElement> { call -> findElixirFunction(call)?.resolvedFinalArityRange()?.any { arityRange.contains(it) } }
                                        .thenByDescending{ arityRange.max() == findElixirFunction(it)?.primaryArity() }
                                        .thenBy{ findElixirFunction(it)?.primaryArity() }
                        )
                        .map{Pair(findElixirFunction(it)?.primaryArity(), findMethodDocs(it)) }
                        .filter { it.second != null }
                        .firstOrNull()

        val functionDocsArity = pair?.first ?: arityRange.max()
        val functionDocs = pair?.second


        val elixirFunction = findElixirFunction(resolved)
        // If we are hovering over the function declaration then show arguments for that function
        // not the one we fetched the docs from
        val functionArguments =
                if (elixirFunction != null && elixirFunction == findElixirFunction(element))
                    elixirFunction.primaryArguments().map { it.text }
                else
                    resolved.siblings()
                            .filterIsInstance<Call>()
                            .filter { it.name == functionName }
                            .mapNotNull { findElixirFunction(it) }
                            .sortedWith(
                                    compareByDescending<ElixirMatchedUnqualifiedParenthesesCall>
                                    // Find the arguments with default arguments and  identifiers first
                                    // (i.e. prioritise 'user' over '%{}' argument)
                                    { it.primaryArity() == functionDocsArity }
                                            .thenByDescending { it.primaryArguments().filterIsInstance<ElixirUnmatchedMatchOperation>().count() }
                                            .thenByDescending { it.primaryArguments().filterIsInstance<ElixirUnmatchedUnqualifiedNoArgumentsCall>().count() }
                            )
                            .firstOrNull()?.primaryArguments()
                            ?.map { it.text }

        if (!functionDocs.isNullOrBlank() || !deprecated.isNullOrBlank()){
            val kind = resolved.firstChild?.text.orEmpty().toKind()
            val moduleName = resolved.parent?.getModuleName().orEmpty()
            return FetchedDocs.FunctionOrMacroDocumentation(moduleName, functionDocs.orEmpty(), kind, functionName, deprecated, functionArguments.orEmpty())
        }
        return null
    }

    private fun findDeprecatedAttribute(resolved: PsiElement): String? {
        val deprecatedAttributes = resolved
                .prevSiblingSequence()
                .drop(1)
                .takeWhile { !isDefiner(it as? ElixirUnmatchedUnqualifiedNoParenthesesCall) }
                .filterIsInstance<ElixirUnmatchedAtUnqualifiedNoParenthesesCall>()
                .filter { it.firstChild is ElixirAtIdentifier && it.firstChild.lastChild?.text == "deprecated" }
                .toList()

        if (deprecatedAttributes.isEmpty()) {
            return null
        }
        val docsLines = deprecatedAttributes
                .mapNotNull { it.getAttributeText() }

        return docsLines.joinToString("") { it }
    }

    private fun findElixirFunction(it: PsiElement): ElixirMatchedUnqualifiedParenthesesCall? {
        if (it is ElixirMatchedUnqualifiedParenthesesCall) {
            return it
        }
        return it.children.mapNotNull {
            findElixirFunction(it)
        }.firstOrNull()
    }

    private fun isDefiner(elixirAtIdentifier: ElixirUnmatchedUnqualifiedNoParenthesesCall?): Boolean {
        val firstChildText = elixirAtIdentifier?.firstChild?.text
        return firstChildText == "def"
                || firstChildText == "defp"
                || firstChildText == "defmacro"
    }

    private fun PsiElement.siblings() = this.prevSiblingSequence() + generateSequence(this) { it.nextSibling }

    private fun findMethodDocs(resolved: PsiElement): String? {
        val methodDocs = resolved
                .prevSiblingSequence()
                .drop(1)
                .takeWhile { !isDefiner(it as? ElixirUnmatchedUnqualifiedNoParenthesesCall) }
                .filterIsInstance<ElixirUnmatchedAtUnqualifiedNoParenthesesCall>()
                .filter { it.firstChild is ElixirAtIdentifier && it.firstChild.lastChild?.text == "doc" }
                .toList()

        if (methodDocs.isEmpty()) {
            return null
        }

        val docsLines = methodDocs
                .mapNotNull { it.getAttributeText() }

        return docsLines.joinToString("") { it }
    }

    private fun ElixirUnmatchedAtUnqualifiedNoParenthesesCall.getAttributeText() : String?{
        val element = this.lastChild.firstChild.firstChild
        if (element is Heredoc){
            return element.children.joinToString("") { it.text }
        }
        if (element is ElixirStringLine) {
            return element.body?.text
        }
        return null
    }

    private fun String.toKind(): String {
        if (this == "def")
            return "function"
        if (this == "defmacro")
            return "macro"
        return this
    }

}
