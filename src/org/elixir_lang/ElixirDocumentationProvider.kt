package org.elixir_lang

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import org.elixir_lang.jps.sdk_type.Elixir
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.getModuleName
import org.elixir_lang.psi.impl.isOutermostQualifiableAlias
import org.elixir_lang.sdk.elixir.Type
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.stream.Collectors


class ElixirDocumentationProvider : DocumentationProvider {
    private data class HelpArguments(val moduleName: String, val functionName: String? = null, val arity: Int? = null)

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return null
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val helpArguments = getHelpArguments(element)
        if (helpArguments == null || helpArguments.moduleName.isBlank()){
            return null
        }

        val sdkPath = Type.getSdkPath(element.project).toString()
        val iexExecutable = Elixir.getIExExecutable(sdkPath).absolutePath
        val projectBasePath = element.project.basePath ?: return null
        return fetchDocumentation(projectBasePath, iexExecutable, helpArguments.moduleName, helpArguments.functionName, helpArguments.arity)
    }

    private fun getHelpArguments(element: PsiElement) : HelpArguments?{
        when (element) {
            is QualifiableAlias -> {
                if (element.isOutermostQualifiableAlias()){
                    return element.fullyQualifiedName()?.let { HelpArguments(it) }
                }else{
                    val parent = element.parent
                    if (parent is ElixirMatchedQualifiedAlias){
                        return parent.fullyQualifiedName()?.let { HelpArguments(it) }
                    }
                    return null
                }
            }
            is ElixirUnmatchedQualifiedParenthesesCall -> {
                return HelpArguments(element.resolvedModuleName(), element.name, element.resolvedPrimaryArity())
            }
            is ElixirMatchedQualifiedNoParenthesesCall -> {
                return HelpArguments(element.resolvedModuleName(), element.name, element.resolvedPrimaryArity())
            }
            is ElixirMatchedQualifiedNoArgumentsCall -> {
                return element.getModuleName()?.let { HelpArguments(it, element.name, element.resolvedPrimaryArity()) }
            }
            is ElixirUnmatchedQualifiedNoArgumentsCall -> {
                return element.getModuleName()?.let { HelpArguments(it, element.name, element.resolvedPrimaryArity()) }
            }
            is ElixirMatchedUnqualifiedParenthesesCall -> {
                return element.getModuleName()?.let { HelpArguments(it, element.name, element.resolvedPrimaryArity()) }
            }
            is ElixirUnmatchedUnqualifiedParenthesesCall -> {
                return element.getModuleName()?.let { HelpArguments(it, element.name, element.resolvedPrimaryArity()) }
            }
            else -> return null
        }
    }


    private fun fetchDocumentation(projectBasePath: String, iexExecutablePath: String, moduleName: String, functionName: String?, arity: Int?): String? {
        val helpArgumentBuilder = StringBuilder()
                .append(moduleName)

        if (functionName != null){
            helpArgumentBuilder.append(".$functionName")
            if (arity != null){
                helpArgumentBuilder.append("/${arity}")
            }
        }

        val startDocumentationMarker = "START_DOCUMENTATION"
        val endDocumentationMarker = "END_DOCUMENTATION"

        val iexCommand = "import IEx.Helpers;" +
                "IO.puts \"$startDocumentationMarker\";" +
                "h $helpArgumentBuilder;" +
                "IO.puts \"$endDocumentationMarker\";" +
                "System.halt;"

        val cmdArray = arrayOf(iexExecutablePath, "--no-start", "--no-compile", "-S", "mix", "run", "-e", iexCommand)
        val process = Runtime.getRuntime().exec(cmdArray, null, File(projectBasePath))

        val stdInput = BufferedReader(InputStreamReader(process.inputStream))

        val stdInputString = stdInput.lines().collect(Collectors.joining("\n"))

        val reg = Regex("$startDocumentationMarker([\\s\\S]*)$endDocumentationMarker")
        val src = reg.find(stdInputString) ?: return null
        if (src.groups.size < 2)
            return null

        val iexDocumentation = src.groups[1]?.value.orEmpty()
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(iexDocumentation)
        return HtmlGenerator(iexDocumentation, parsedTree, flavour, false).generateHtml()
    }
}