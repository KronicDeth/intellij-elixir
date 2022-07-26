package org.elixir_lang.documentation

import com.intellij.codeInsight.documentation.DocumentationManagerProtocol
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.index.ModularName
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.html.URI
import org.intellij.markdown.parser.LinkMap
import java.util.regex.Pattern

class MarkdownFlavourDescriptor(private val project: Project) : GFMFlavourDescriptor() {
    override fun createHtmlGeneratingProviders(linkMap: LinkMap, baseURI: URI?): Map<IElementType, GeneratingProvider> =
        super
            .createHtmlGeneratingProviders(linkMap, baseURI)
            .toMutableMap()
            .apply {
                compute(MarkdownElementTypes.CODE_SPAN) { _, gfmHtmlGeneratingProvider ->
                    object : GeneratingProvider {
                        override fun processNode(
                            visitor: HtmlGenerator.HtmlGeneratingVisitor,
                            text: String,
                            node: ASTNode
                        ) {
                            if (node.children.size == 3) {
                                val nameChild = node.children[1]
                                val name = nameChild.getTextInNode(text).toString()
                                val moduleFunctionArityMatcher = MODULE_FUNCTION_ARITY_PATTERN.matcher(name)

                                if (moduleFunctionArityMatcher.matches()) {
                                    val module = moduleFunctionArityMatcher.group("module")
                                    val function = moduleFunctionArityMatcher.group("function")
                                    val arity = moduleFunctionArityMatcher.group("arity").toInt()

                                    val functionCount =
                                        module
                                            .let { modulars(project, it) }
                                            .flatMap { modular ->
                                                org.elixir_lang.psi.scope.call_definition_clause.MultiResolve.resolveResults(
                                                    function,
                                                    arity,
                                                    false,
                                                    modular
                                                )
                                            }
                                            .count { it.isValidResult }

                                    if (functionCount > 0) {


                                        visitor.consumeTagOpen(
                                            node,
                                            "a",
                                            "href=\"${DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL}${name}\""
                                        )
                                        gfmHtmlGeneratingProvider!!.processNode(visitor, text, node)
                                        visitor.consumeTagClose("a")
                                    }
                                } else {
                                    val modulars = modulars(project, name)

                                    if (modulars.isNotEmpty()) {
                                        visitor.consumeTagOpen(
                                            node,
                                            "a",
                                            "href=\"${DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL}${name}\""
                                        )
                                        gfmHtmlGeneratingProvider!!.processNode(visitor, text, node)
                                        visitor.consumeTagClose("a")
                                    }
                                }
                            }
                        }
                    }
                }
            }

    companion object {
        val MODULE_FUNCTION_ARITY_PATTERN: Pattern = Pattern.compile("(?<module>.+)\\.(?<function>.+)/(?<arity>\\d+)")

        fun modulars(project: Project, name: String): Collection<PsiElement> {
            val globalSearchScope = GlobalSearchScope.allScope(project)

            return StubIndex
                .getElements(
                    ModularName.KEY,
                    name,
                    project,
                    globalSearchScope,
                    null,
                    NamedElement::class.java
                )
        }
    }
}
