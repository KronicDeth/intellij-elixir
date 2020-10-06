package org.elixir_lang

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.SharedPsiElementImplUtil.getPrevSibling
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoParenthesesCallImpl
import org.elixir_lang.psi.impl.getModuleName
import org.elixir_lang.psi.impl.prevSiblingSequence
import org.elixir_lang.reference.Callable
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser


class ElixirDocumentationProvider : DocumentationProvider {
    data class FetchedDocs(val elementFullName: String, val docs: String?, val specDocs: String?)

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val docs = fetchDocs(element)
        if (docs != null){
            return formatDocs(element.reference?.canonicalText.orEmpty(), docs)
        }
        return null
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return generateDoc(element, originalElement);
    }

    fun formatDocs(docsForName: String, moduleDoc: FetchedDocs?): String {
        val doc = moduleDoc?.docs.orEmpty()

        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(doc)
        val html = HtmlGenerator(doc, parsedTree, flavour, false).generateHtml()

        val elementName =
                if (moduleDoc?.elementFullName?.isNotEmpty() == true)
                    "${moduleDoc.elementFullName}.${docsForName}"
                else
                    docsForName

            return DocumentationMarkup.DEFINITION_START +
                    "<b>" + (elementName) + "</b>" +
                    DocumentationMarkup.DEFINITION_END +
                    DocumentationMarkup.CONTENT_START +
                    html +
                    DocumentationMarkup.CONTENT_END
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
                return FetchedDocs(moduleName, moduleDoc, null)
            }
        }

        val methodName = (element as? Call)?.functionName()
        val prev = resolved.prevSiblingSequence().toList()
        val relatedElements = prev
                .drop(1)
                .takeWhile {
                    !(isDefiner(it as? ElixirUnmatchedUnqualifiedNoParenthesesCall)
                        && it.children.elementAtOrNull(1)?.firstChild?.firstChild?.text != methodName)
                }
                .filterIsInstance<ElixirUnmatchedAtUnqualifiedNoParenthesesCall>()

        val methodDocs = relatedElements
                .filter { it.firstChild is ElixirAtIdentifier && isDoc(it.firstChild as ElixirAtIdentifier) }
                .toList()

        val specDocs = relatedElements
                .firstOrNull { it.firstChild is ElixirAtIdentifier && isSpecDoc(it.firstChild as ElixirAtIdentifier) }
                ?.lastChild?.text

        val docsLines = methodDocs
                .mapNotNull { (it.lastChild?.firstChild?.firstChild as? Heredoc)?.children?.toList() }
                .flatten()
                .map{it.text}

//        val moduleName = (element.reference?.resolve() as? ElixirUnmatchedUnqualifiedNoParenthesesCall)?.resolvedModuleName()
        val moduleName = resolved.parent?.getModuleName()
        return FetchedDocs(moduleName.orEmpty(), docsLines.joinToString("") { it }, specDocs)
    }

    private fun isSpecDoc(elixirAtIdentifier: ElixirAtIdentifier) : Boolean{
        return elixirAtIdentifier.text == "@spec"
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