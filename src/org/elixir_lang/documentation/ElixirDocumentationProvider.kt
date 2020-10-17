package org.elixir_lang.documentation

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import org.elixir_lang.beam.psi.BeamFileImpl
import org.elixir_lang.psi.ElixirUnmatchedExpression
import org.elixir_lang.reference.Callable
import org.elixir_lang.reference.Module
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser


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

        val moduleName = fetchedDocs.moduleName.removePrefix("Elixir.")

        val definitionString = when (fetchedDocs) {
            is FetchedDocs.FunctionOrMacroDocumentation -> {
                    "<i>${fetchedDocs.kind} ${moduleName}</i>.<b>${fetchedDocs.methodName}</b>" +
                            "(${fetchedDocs.arguments.joinToString()})"
            }
            is FetchedDocs.ModuleDocumentation -> {
                "<i>module</i> <b>${moduleName}</b>"
            }
        }

        documentationHtml.append(DocumentationMarkup.DEFINITION_START)
        documentationHtml.append(definitionString)
        documentationHtml.append(DocumentationMarkup.DEFINITION_END)
        documentationHtml.append(DocumentationMarkup.CONTENT_START)
        if (fetchedDocs is FetchedDocs.FunctionOrMacroDocumentation && fetchedDocs.deprecated != null){
            documentationHtml.append("<b>deprecated</b> ")
            documentationHtml.append(fetchedDocs.deprecated)
            documentationHtml.append("\n")
        }
        documentationHtml.append(html)
        documentationHtml.append(DocumentationMarkup.CONTENT_END)
        return documentationHtml.toString()

    }

    fun fetchDocs(element: PsiElement): FetchedDocs? {
        val resolved = element.reference?.resolve()
                ?: ((element.reference as? Callable)?.multiResolve(false) ?: (element.reference as? Module)?.multiResolve(false))
                        ?.map { it.element }
                        ?.filterIsInstance<ElixirUnmatchedExpression>()
                        ?.firstOrNull()
                ?: return null

        // If resolves to .beam file then fetch docs from the decompiled docs
        if (resolved.containingFile.originalFile is BeamFileImpl){
            return BeamDocsHelper.fetchDocs(element, resolved)
        }
        return SourceFileDocsHelper.fetchDocs(element, resolved)
    }
}

