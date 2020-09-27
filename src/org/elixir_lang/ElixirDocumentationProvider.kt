package org.elixir_lang

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.SharedPsiElementImplUtil.getPrevSibling
import org.elixir_lang.jps.sdk_type.Elixir
import org.elixir_lang.navigation.item_presentation.Implementation
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.ElixirStringHeredocImpl
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoParenthesesCallImpl
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModular
import org.elixir_lang.psi.impl.isOutermostQualifiableAlias
import org.elixir_lang.psi.impl.prevSiblingSequence
import org.elixir_lang.reference.Callable
import org.elixir_lang.sdk.elixir.Type
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.stream.Collectors


class ElixirDocumentationProvider : DocumentationProvider {
    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val docs = fetchDocs(element)
        if (docs != null){
            return formatDocs(element.reference?.canonicalText.orEmpty(), docs)
        }
        return null
    }

    fun formatDocs(docsForName: String, moduleDoc: String): String {
        val doc = moduleDoc
                .removePrefix("~S")
                .removePrefix("~s")
                .removePrefix("\"\"\"")
                .removeSuffix("\"\"\"")
//                .lineSequence()
//                .dropWhile { it.isBlank() }
//                .takeWhile { it.isNotBlank() }
//                .joinToString()

            val flavour = GFMFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(doc)
            val html = HtmlGenerator(doc, parsedTree, flavour, false).generateHtml()

            return DocumentationMarkup.DEFINITION_START +
                    docsForName +
                    DocumentationMarkup.DEFINITION_END +
                    DocumentationMarkup.CONTENT_START +
                    html +
                    DocumentationMarkup.CONTENT_END
    }

    fun fetchDocs(element: PsiElement): String? {
        val moduleDoc = (element.reference?.resolve() as? ElixirUnmatchedUnqualifiedNoParenthesesCallImpl)
                ?.doBlock?.stab?.stabBody
                ?.unmatchedExpressionList
                ?.filterIsInstance<ElixirUnmatchedAtUnqualifiedNoParenthesesCall>()
                ?.filter { it.atIdentifier.text == "@moduledoc" }
                ?.map { it.lastChild.text }
                ?.firstOrNull()
        if (moduleDoc != null)
            return moduleDoc

        var resolved = element.reference?.resolve()
        if (resolved == null)
            resolved =  (element.reference as? Callable)?.multiResolve(false)?.map { it.element }?.firstOrNull()

        if (resolved != null){
            val prev = resolved.prevSiblingSequence().toList()
            val methodDocs = prev
                    //TODO: Until next method
                    .filter { it is ElixirUnmatchedAtUnqualifiedNoParenthesesCall }
                    .filter { it.firstChild is ElixirAtIdentifier && it.firstChild.text == "@doc" }
                    .firstOrNull()
//            return methodDocs?.lastChild?.text
            val docsLines = (methodDocs?.lastChild?.firstChild?.firstChild as? ElixirStringHeredoc)?.children?.map { it.text }
            if (docsLines != null){
                return docsLines.joinToString("\n") { it }
            }

            //For macros
//            (methodDocs.lastChild.firstChild.firstChild as ElixirStringHeredocImpl).children.map { it.text }
        }

        return null

    }

    fun getDocCommentText(element: PsiElement): String? {
        val text = StringBuilder()
        var doc: PsiElement? = getPrevSibling(element)
        while (doc != null) {
            if (doc is PsiWhiteSpace) {
                doc = doc.getPrevSibling()
            }
            if (doc is PsiComment) {
                if (text.length != 0) text.insert(0, "\n")
                val comment = doc.getText()
                text.insert(0, comment)
            } else {
                break
            }
            doc = doc.getPrevSibling()
        }
        return if (text.length == 0) null else text.toString()
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val docs = fetchDocs(element)
        if (docs != null){
            return formatDocs(element.reference?.canonicalText.orEmpty(), docs)
        }
        return null

        var namedElement = TargetElementUtil.getInstance().getNamedElement(element, 0)
        var test1 = getDocCommentText(element)
        var test2 = getDocCommentText(element.reference!!.resolve()!!.navigationElement)
//        TargetElementEvaluator.getElementByReference(element.reference, 0)
//        if (element is Reference)
//            Resolver.resolve(element, false)

        val resolved = (element.reference as? org.elixir_lang.reference.Module)?.multiResolve(false)

        //WIP TODO: Check this
        //((element.reference.resolve().prevSiblingSequence().toList()[3]) as ElixirUnmatchedAtUnqualifiedNoParenthesesCallImpl).atIdentifier


        val evaluator = TargetElementEvaluator()
        val test = element.reference?.let { evaluator.getElementByReference(it, 0) }
//        eement.qualifiedToModular()?.let {
//            Implementation.getLocationString(it)
//        }
//        return getDoc(element)
//        return element?.reference?.resolve()?.text
        return null
    }
}