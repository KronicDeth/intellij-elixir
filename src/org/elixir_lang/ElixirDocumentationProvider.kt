package org.elixir_lang

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoParenthesesCallImpl
import org.elixir_lang.psi.impl.getModuleName
import org.elixir_lang.psi.impl.prevSiblingSequence
import org.elixir_lang.reference.Callable
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import kotlin.collections.List


class ElixirDocumentationProvider : DocumentationProvider {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return fetchDocs(element)?.let { formatDocs(it) }
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return generateDoc(element, originalElement)
    }

    fun formatDocs(fetchedDocs: FetchedDocs): String {
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(fetchedDocs.docsMarkdown)
        val html = HtmlGenerator(fetchedDocs.docsMarkdown, parsedTree, flavour, false)
                .generateHtml()

        val documentationHtml = StringBuilder()

        val definitionString = when (fetchedDocs) {
            is FetchedDocs.FunctionOrMacroDocumentation -> {
                if (fetchedDocs.arguments.isNotEmpty())
                    "<i>${fetchedDocs.definer} ${fetchedDocs.moduleName}</i>.<b>${fetchedDocs.methodName}</b>" +
                            "(${fetchedDocs.arguments.joinToString()})"
                else
                    "<i>${fetchedDocs.definer} ${fetchedDocs.moduleName}</i>.<b>${fetchedDocs.methodName}</b>()"
            }
            is FetchedDocs.ModuleDocumentation -> {
                "<i>defmodule</i> <b>${fetchedDocs.moduleName}</b>"
            }
        }

        documentationHtml.append(DocumentationMarkup.DEFINITION_START)
        documentationHtml.append(definitionString)
        documentationHtml.append(DocumentationMarkup.DEFINITION_END)
        documentationHtml.append(DocumentationMarkup.CONTENT_START)
        documentationHtml.append(html)
        documentationHtml.append(DocumentationMarkup.CONTENT_END)
        return documentationHtml.toString()

    }

    fun fetchDocs(element: PsiElement): FetchedDocs? {
        val resolved = element.reference?.resolve()
                ?: (element.reference as? Callable)?.multiResolve(false)?.map { it.element }?.firstOrNull()
                ?: return null

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

            if (moduleDoc != null) {
                return FetchedDocs.ModuleDocumentation(resolved.canonicalName().orEmpty(), moduleDoc)
            }
        }

        if (element !is Call) {
            return null
        }

        val functionName = element.functionName().orEmpty()
        val arityRange = element.resolvedFinalArityRange()

        val (functionDocsArity, functionDocs) =
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
                        ?: return null



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

        if (!functionDocs.isNullOrBlank()){
            val definer = resolved.firstChild?.text.orEmpty()
            val moduleName = resolved.parent?.getModuleName().orEmpty()
            return FetchedDocs.FunctionOrMacroDocumentation(moduleName, functionDocs, definer, functionName, functionArguments.orEmpty())
        }
        return null
    }

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
                .mapNotNull { (it.lastChild?.firstChild?.firstChild as? Heredoc)?.children?.toList() }
                .flatten()
                .map { it.text }

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

    sealed class FetchedDocs(open val moduleName: String, open val docsMarkdown: String) {
        data class FunctionOrMacroDocumentation(override val moduleName: String,
                                                override val docsMarkdown: String,
                                                val definer: String,
                                                val methodName: String,
                                                val arguments: List<String>) : FetchedDocs(moduleName, docsMarkdown)

        data class ModuleDocumentation(override val moduleName: String,
                                       override val docsMarkdown: String) : FetchedDocs(moduleName, docsMarkdown)
    }

}