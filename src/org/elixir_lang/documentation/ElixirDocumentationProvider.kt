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
        val documentationHtml = StringBuilder()

        documentationHtml.append(DocumentationMarkup.DEFINITION_START)

        documentationHtml.append("<i>module</i> <b>").append(fetchedDocs.module).append("</b>\n")

        if (fetchedDocs is FetchedDocs.FunctionOrMacroDocumentation) {
            for (head in fetchedDocs.heads) {
                documentationHtml.append(head).append("\n")
            }
        }

        documentationHtml.append(DocumentationMarkup.DEFINITION_END)


        when (fetchedDocs) {
            is FetchedDocs.ModuleDocumentation -> {
                fetchedDocs.moduledoc?.let { moduledoc ->
                    documentationHtml
                            .append(DocumentationMarkup.CONTENT_START)
                            .append(html(moduledoc))
                            .append(DocumentationMarkup.CONTENT_END)
                }
            }
            is FetchedDocs.FunctionOrMacroDocumentation -> {
                fetchedDocs.doc?.let { doc ->
                    documentationHtml
                            .append(DocumentationMarkup.CONTENT_START)
                            .append(html(doc))
                            .append(DocumentationMarkup.CONTENT_END)
                }

                val deprecated = fetchedDocs.deprecated
                val impls = fetchedDocs.impls
                val implsIsNotEmpty = impls.isNotEmpty()
                val specs = fetchedDocs.specs
                val specsIsNotEmpty = specs.isNotEmpty()

                if (deprecated != null || implsIsNotEmpty || specsIsNotEmpty) {
                    documentationHtml.append(DocumentationMarkup.SECTIONS_START)

                    if (deprecated != null) {
                        documentationHtml
                                .append(DocumentationMarkup.SECTION_HEADER_START)
                                .append("Deprecated")
                                .append(DocumentationMarkup.SECTION_SEPARATOR)
                                .append(html(deprecated))
                                .append(DocumentationMarkup.SECTION_END)
                    }

                    if (implsIsNotEmpty) {
                        documentationHtml
                                .append(DocumentationMarkup.SECTION_HEADER_START)
                                .append("Behaviors Implemented")
                                .append(DocumentationMarkup.SECTION_SEPARATOR)
                                .append("<ul>")

                        for (impl in impls) {
                            documentationHtml
                                    .append("<pre>")
                                    .append(impl)
                                    .append("</pre>")
                        }

                        documentationHtml
                                .append("</ul>")
                                .append(DocumentationMarkup.SECTION_END)
                    }

                    if (specsIsNotEmpty) {
                        documentationHtml
                                .append(DocumentationMarkup.SECTION_HEADER_START)
                                .append("Specifications")
                                .append(DocumentationMarkup.SECTION_SEPARATOR)

                        for (spec in specs) {
                            documentationHtml.append(spec).append("<br />\n")
                        }

                        documentationHtml.append(DocumentationMarkup.SECTION_END)
                    }

                    documentationHtml.append(DocumentationMarkup.SECTIONS_END)
                }
            }
        }


        return documentationHtml.toString()
    }

    private fun html(markdownText: String): String {
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdownText)

        return HtmlGenerator(markdownText, parsedTree, flavour, false)
                .generateHtml()
    }

    private fun fetchDocs(element: PsiElement): FetchedDocs? =
            // If resolves to .beam file then fetch docs from the decompiled docs
            if (element.containingFile.originalFile is BeamFileImpl){
                BeamDocsHelper.fetchDocs(element)
            } else {
                SourceFileDocsHelper.fetchDocs(element)
            }
}
