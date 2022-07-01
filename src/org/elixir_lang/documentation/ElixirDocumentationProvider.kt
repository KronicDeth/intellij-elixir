package org.elixir_lang.documentation

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.beam.chunk.beam_documentation.docs.documented.Hidden
import org.elixir_lang.beam.chunk.beam_documentation.docs.documented.MarkdownByLanguage
import org.elixir_lang.beam.chunk.beam_documentation.docs.documented.None
import org.elixir_lang.beam.psi.BeamFileImpl
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.stub.type.call.Stub
import org.elixir_lang.psi.stub.type.call.Stub.isModular
import org.elixir_lang.reference.ModuleAttribute.Companion.isDocumentationName
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.function.Consumer


class ElixirDocumentationProvider : DocumentationProvider {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? =
        fetchDocs(element)?.let { formatDocs(it) }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? =
        generateDoc(element, originalElement)

    override fun generateRenderedDoc(comment: PsiDocCommentBase): String? =
        comment
            .let(::markdown)
            ?.let(::html)

    private fun markdown(comment: PsiDocCommentBase): String? =
        when (comment) {
            is Comment -> {
                comment.moduleAttribute.moduleAttributeValue()?.let { quote ->
                    when (quote) {
                        is Heredoc -> {
                            val prefixLength = quote.heredocPrefix.textLength

                            quote.heredocLineList.joinToString("") { heredocLine ->
                                val text = heredocLine.text
                                val textLengthWithoutNewline = text.length - 1
                                val startIndex = min(max(textLengthWithoutNewline, 0), prefixLength)

                                heredocLine.text.substring(startIndex)
                            }
                        }
                        is ElixirLine -> quote.body?.text
                        else -> null
                    }
                }
            }
            else -> null
        }


    override fun collectDocComments(file: PsiFile, sink: Consumer<in PsiDocCommentBase>) {
        file.let { it as? ElixirFile }?.let { collectDocComments(it, sink) }
    }

    private fun collectDocComments(file: ElixirFile, sink: Consumer<in PsiDocCommentBase>) {
        file.childExpressions().forEach { collectDocComments(it, sink) }
    }

    private fun collectDocComments(element: PsiElement, sink: Consumer<in PsiDocCommentBase>) {
        when (element) {
            is Call -> collectDocComments(element, sink)
            else -> {
                Logger.error(javaClass, "Don't know how to collect doc comments", element)
            }
        }
    }

    private fun collectDocComments(element: AtUnqualifiedNoParenthesesCall<*>, sink: Consumer<in PsiDocCommentBase>) {
        val identifierName = element.atIdentifier.identifierName()

        if (isDocumentationName(identifierName)) {
            sink.accept(Comment(element))
        }
    }

    private fun collectDocComments(call: Call, sink: Consumer<in PsiDocCommentBase>) {
        when {
            call is AtUnqualifiedNoParenthesesCall<*> -> collectDocComments(call, sink)
            isModular(call) -> {
                call.macroChildCallSequence().forEach { child ->
                    collectDocComments(child, sink)
                }
            }
        }
    }

    override fun findDocComment(file: PsiFile, range: TextRange): PsiDocCommentBase? {
        val moduleAttribute = PsiTreeUtil.getParentOfType(
            file.findElementAt(range.startOffset),
            AtUnqualifiedNoParenthesesCall::class.java,
            false
        )
        return if (moduleAttribute == null || range != moduleAttribute.textRange) null else Comment(moduleAttribute)
    }

    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager?,
        `object`: Any?,
        element: PsiElement?
    ): PsiElement? {
        return super.getDocumentationElementForLookupItem(psiManager, `object`, element)
    }

    override fun getDocumentationElementForLink(
        psiManager: PsiManager?,
        link: String?,
        context: PsiElement?
    ): PsiElement? {
        return super.getDocumentationElementForLink(psiManager, link, context)
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
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
                fetchedDocs.moduledoc.let { moduledoc ->
                    documentationHtml
                        .append(DocumentationMarkup.CONTENT_START)
                        .append(html(moduledoc))
                        .append(DocumentationMarkup.CONTENT_END)
                }
            }
            is FetchedDocs.FunctionOrMacroDocumentation -> {
                fetchedDocs.doc?.let { doc ->
                    when (doc) {
                        is None -> Unit
                        is Hidden ->
                            documentationHtml
                                .append(DocumentationMarkup.CONTENT_START)
                                .append(false)
                                .append(DocumentationMarkup.CONTENT_END)
                        is MarkdownByLanguage ->
                            doc.formattedByLanguage.values.map { formatted ->
                                documentationHtml
                                    .append(DocumentationMarkup.CONTENT_START)
                                    .append(html(formatted))
                                    .append(DocumentationMarkup.CONTENT_END)
                            }
                    }
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

    private fun html(otpErlangObject: OtpErlangObject): String =
        when (otpErlangObject) {
            is OtpErlangBinary ->
                otpErlangObject
                    .binaryValue()
                    .let { String(it, Charsets.UTF_8) }
                    .let { html(it) }
            else -> {
                Logger.error(javaClass, "Don't know how to render deprecated metadata", otpErlangObject)

                ""
            }
        }

    private fun html(markdownText: String): String {
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdownText)

        return HtmlGenerator(markdownText, parsedTree, flavour, false)
            .generateHtml()
    }

    private fun fetchDocs(element: PsiElement): FetchedDocs? =
        // If resolves to .beam file then fetch docs from the decompiled docs
        if (element.containingFile?.originalFile is BeamFileImpl) {
            BeamDocsHelper.fetchDocs(element)
        } else {
            SourceFileDocsHelper.fetchDocs(element)
        }
}
