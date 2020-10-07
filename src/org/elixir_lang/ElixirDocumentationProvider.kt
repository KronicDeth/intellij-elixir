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
    sealed class FetchedDocs(open val moduleName: String, open val docsMarkdown: String){
        data class MethodDocumentation(override val moduleName: String,
                                     override val docsMarkdown: String,
                                     val methodName: String,
                                     val arguments: List<String>) : FetchedDocs(moduleName, docsMarkdown)

        data class ModuleDocumentation(override val moduleName: String,
                                     override val docsMarkdown: String) : FetchedDocs(moduleName, docsMarkdown)
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return fetchDocs(element)?.let { formatDocs(it) }
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return generateDoc(element, originalElement);
    }

    fun formatDocs(fetchedDocs: FetchedDocs): String {
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(fetchedDocs.docsMarkdown)
        val html = HtmlGenerator(fetchedDocs.docsMarkdown, parsedTree, flavour, false)
                .generateHtml()

        val documentationHtml = StringBuilder()

        val definitionString = when(fetchedDocs){
            is FetchedDocs.MethodDocumentation -> {
                if (fetchedDocs.arguments.isNotEmpty())
                    "<b>${fetchedDocs.moduleName}.${fetchedDocs.methodName}" +
                            "(${fetchedDocs.arguments.joinToString()}) </b>"
                else
                    "<b>${fetchedDocs.moduleName}.${fetchedDocs.methodName}</b>"
            }
            is FetchedDocs.ModuleDocumentation -> {
                "Module: <b>${fetchedDocs.moduleName}</b>"
            }
        }

        documentationHtml.append(DocumentationMarkup.DEFINITION_START)
        documentationHtml.append(definitionString)
        documentationHtml.append(DocumentationMarkup.DEFINITION_END)
        documentationHtml.append(DocumentationMarkup.SECTIONS_START)
        documentationHtml.append(html)
        documentationHtml.append(DocumentationMarkup.SECTIONS_END)
        return documentationHtml.toString()

    }

    fun fetchDocs(element: PsiElement): FetchedDocs? {
        val resolved = element.reference?.resolve()
                ?: (element.reference as? Callable)?.multiResolve(false)?.map { it.element }?.firstOrNull()
                ?: return null

        if (resolved.firstChild?.text == "defmodule"){
            val moduleDoc = (resolved as? ElixirUnmatchedUnqualifiedNoParenthesesCallImpl)
                ?.doBlock
                ?.stab
                ?.stabBody
                ?.unmatchedExpressionList
                ?.filterIsInstance<ElixirUnmatchedAtUnqualifiedNoParenthesesCall>()
                ?.filter { it.atIdentifier.lastChild.text == "moduledoc" }
                ?.mapNotNull { (it.lastChild?.firstChild?.firstChild as? Heredoc)?.children?.toList() }
                ?.flatten()
                ?.joinToString("") { it.text }

            if (moduleDoc != null){
                val moduleName = element.text
                return FetchedDocs.ModuleDocumentation(resolved.canonicalName().orEmpty(), moduleDoc)
            }
        }

        if (element !is Call){
            return null
        }

        val methodName = element.functionName().orEmpty()
        val arityRange = element.resolvedFinalArityRange()
        val arguments = findCall(resolved)?.primaryArguments()?.map { it.text }.orEmpty()
        val firstMethodDocs = findMethodDocs(resolved)
                ?: resolved.prevSiblingSequence()
                        .filterIsInstance<Call>()
                        .filter { it.name == methodName }
                        .sortedByDescending { findCall(it)?.primaryArity() }
                        .filter { call -> findCall(call)?.resolvedFinalArityRange()?.any { arityRange.contains(it) } == true}
                        .mapNotNull { findMethodDocs(it) }
                        .firstOrNull()

        val moduleName = resolved.parent?.getModuleName().orEmpty()
        if (firstMethodDocs != null)
            return FetchedDocs.MethodDocumentation(moduleName, firstMethodDocs, methodName, arguments)
        return null
    }

    private fun findMethodDocs(resolved: PsiElement): String? {
        val methodDocs = resolved
                .prevSiblingSequence()
                .drop(1)
                .takeWhile { !isDefiner(it as? ElixirUnmatchedUnqualifiedNoParenthesesCall) }
                .filterIsInstance<ElixirUnmatchedAtUnqualifiedNoParenthesesCall>()
                .filter { it.firstChild is ElixirAtIdentifier && it.firstChild.text == "@doc" }
                .toList()

        if (methodDocs.isEmpty()){
            return null
        }

        val docsLines = methodDocs
                .mapNotNull { (it.lastChild?.firstChild?.firstChild as? Heredoc)?.children?.toList() }
                .flatten()
                .map{it.text}

        return docsLines.joinToString("") { it }
    }

    private fun findCall(it: PsiElement): ElixirMatchedUnqualifiedParenthesesCall?{
        if (it is ElixirMatchedUnqualifiedParenthesesCall){
            return it
        }
        return it.children.mapNotNull {
            findCall(it)
        }.firstOrNull()
    }

    private fun isDoc(elixirAtIdentifier: ElixirAtIdentifier) : Boolean{
        return elixirAtIdentifier.text == "@doc" || elixirAtIdentifier.text == "@spec"
    }

    private fun isDefiner(elixirAtIdentifier: ElixirUnmatchedUnqualifiedNoParenthesesCall?) : Boolean{
        val firstChild = elixirAtIdentifier?.firstChild?.text
        return firstChild == "def"
                || firstChild == "defp"
                || firstChild == "defmacro"
    }
}