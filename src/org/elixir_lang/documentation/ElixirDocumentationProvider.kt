package org.elixir_lang.documentation

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.beam.psi.BeamFileImpl
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.stub.type.call.Stub
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser


class ElixirDocumentationProvider : DocumentationProvider {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? =
            fetchDocs(element)?.let { formatDocs(it) }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? =
            generateDoc(element, originalElement)

    override fun getCustomDocumentationElement(editor: Editor, file: PsiFile, contextElement: PsiElement?, targetOffset: Int): PsiElement? {
        return contextElement?.let(::getCustomDocumentationElement)
    }

    private tailrec fun getCustomDocumentationElement(contextElement: PsiElement): PsiElement? = when (contextElement) {
        is LeafPsiElement, is ElixirIdentifier -> getCustomDocumentationElement(contextElement.parent)
        is Call -> {
            contextElement
                    .getReference()
                    ?.let { it as PsiPolyVariantReference }
                    ?.let { reference ->
                        reference
                                .multiResolve(false)
                                .filter(ResolveResult::isValidResult)
                                .mapNotNull(ResolveResult::getElement)
                                .filterIsInstance<Call>()
                                .singleOrNull { CallDefinitionClause.`is`(it) }
                    }
        }
        is QualifiableAlias -> {
            val reference = contextElement.getReference()

            if (reference != null) {
                reference
                        .let { it as PsiPolyVariantReference }
                        .multiResolve(false)
                        .filter(ResolveResult::isValidResult)
                        .mapNotNull(ResolveResult::getElement)
                        .filterIsInstance<Call>()
                        .singleOrNull { Stub.isModular(it) }
            } else {
                getCustomDocumentationElement(contextElement.parent)
            }
        }
        else -> null
    }

    private fun formatDocs(fetchedDocs: FetchedDocs): String {
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

    private fun fetchDocs(element: PsiElement): FetchedDocs? =
            // If resolves to .beam file then fetch docs from the decompiled docs
            if (element.containingFile.originalFile is BeamFileImpl){
                BeamDocsHelper.fetchDocs(element)
            } else {
                SourceFileDocsHelper.fetchDocs(element)
            }
}
