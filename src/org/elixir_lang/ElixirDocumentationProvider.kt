package org.elixir_lang

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.chunk.BeamDocumentationProvider
import org.elixir_lang.beam.psi.BeamFileImpl
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoParenthesesCallImpl
import org.elixir_lang.psi.impl.getModuleName
import org.elixir_lang.psi.impl.prevSiblingSequence
import org.elixir_lang.reference.Callable
import org.elixir_lang.reference.Module
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
                    "<i>${fetchedDocs.kind} ${fetchedDocs.moduleName}</i>.<b>${fetchedDocs.methodName}</b>" +
                            "(${fetchedDocs.arguments.joinToString()})"
            }
            is FetchedDocs.ModuleDocumentation -> {
                "<i>defmodule</i> <b>${fetchedDocs.moduleName}</b>"
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
            if (element is Call){
                val functionName = element.functionName().orEmpty()
                val arityRange = element.primaryArity()

                val beam = Beam.from(resolved.containingFile.originalFile.virtualFile) ?: return null
                val moduleName = beam.atoms()?.moduleName().orEmpty()

                val docs = beam.beamDocumentation()?.docs
                    ?.docsForOrSimilar(functionName, arityRange ?: 0) ?: return null

                val kind = docs.kind

                val signature = docs.signatures.first()
                val arguments = signature.removePrefix(functionName)
                        .removePrefix("(")
                        .removeSuffix(")")
                        .split(",")

                val resolver = BeamDocumentationProvider()

                val metadata = resolver.getFunctionAttributes(resolved.containingFile.originalFile.virtualFile, functionName, arityRange ?: 0)
                val deprecatedMetadata = metadata?.firstOrNull{it.name == "deprecated"}
                val deprecatedText: String? = if (deprecatedMetadata == null) null else deprecatedMetadata.value.orEmpty()

                val functionDocs = resolver.getFunctionDocs(resolved.containingFile.originalFile.virtualFile, functionName, arityRange ?: 0)
                val docsText = functionDocs?.firstOrNull()?.documentationText
                if (docsText != null || deprecatedMetadata != null)
                    FetchedDocs.FunctionOrMacroDocumentation(moduleName, docsText.orEmpty(), kind,
                    functionName, deprecatedText, arguments )
            }
            else if (element.text.first().isUpperCase()){
                val resolver = BeamDocumentationProvider()
                val moduleDocumentation = resolver.getModuleDocs(resolved.containingFile.originalFile.virtualFile)
                return moduleDocumentation?.let { FetchedDocs.ModuleDocumentation(resolved.getModuleName().orEmpty(), it) }
            }
        }

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

        val functionName = element.functionName().orEmpty()
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

    sealed class FetchedDocs(open val moduleName: String, open val docsMarkdown: String) {
        data class FunctionOrMacroDocumentation(override val moduleName: String,
                                                override val docsMarkdown: String,
                                                val kind: String,
                                                val methodName: String,
                                                val deprecated: String?,
                                                val arguments: List<String>) : FetchedDocs(moduleName, docsMarkdown)

        data class ModuleDocumentation(override val moduleName: String,
                                       override val docsMarkdown: String) : FetchedDocs(moduleName, docsMarkdown)
    }

}

private fun ElixirUnmatchedAtUnqualifiedNoParenthesesCall.getAttributeText() : String?{
    val element = this.lastChild.firstChild.firstChild
    if (element is Heredoc){
        return element.children.joinToString("") { it.text }
    }
    if (element is ElixirStringLine) {
        return element.quoteStringBody?.text
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
