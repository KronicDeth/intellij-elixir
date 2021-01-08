package org.elixir_lang.documentation

import com.intellij.psi.PsiElement
import org.elixir_lang.beam.Beam
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.getModuleName

object BeamDocsHelper {
    fun fetchDocs(element: PsiElement, resolved: PsiElement, ignoreDefiner: Boolean) : FetchedDocs? {
        val beam = Beam.from(resolved.containingFile.originalFile.virtualFile)
                ?: Beam.from(element.containingFile.originalFile.virtualFile)
                ?: return null

        if (resolved.firstChild.text == "defmodule"){
            val moduleDocumentation = beam.documentation()?.moduleDocs?.englishDocs
            return moduleDocumentation?.let { FetchedDocs.ModuleDocumentation(resolved.getModuleName().orEmpty(), it) }
        } else if (element is Call){
            val functionName = if (ignoreDefiner) element.name.orEmpty() else element.functionName().orEmpty()
            val arityRange = element.primaryArity()

            val moduleName = beam.atoms()?.moduleName().orEmpty()

            val docs = beam.documentation()?.docs
                    ?.docsForOrSimilar(functionName, arityRange ?: 0) ?: return null

            val kind = docs.kind

            val signature = docs.signatures.first()
            val arguments = signature.removePrefix(functionName)
                    .removePrefix("(")
                    .removeSuffix(")")
                    .split(",")
                    .map { it.trim() }

            val metadata = beam.documentation()?.docs?.getFunctionMetadataOrSimilar(functionName, arityRange ?: 0)
            val deprecatedMetadata = metadata?.firstOrNull{it.name == "deprecated"}

            val deprecatedText: String? = when (deprecatedMetadata){
                null -> null
                else -> deprecatedMetadata.value.orEmpty()
            }

            val functionDocs =
                    beam.documentation()?.docs?.getFunctionDocsOrSimilar(functionName, arityRange ?: 0)

            val docsText = functionDocs?.firstOrNull()?.documentationText
            if (docsText != null || deprecatedMetadata != null)
                return FetchedDocs.FunctionOrMacroDocumentation(moduleName, docsText.orEmpty(), kind,
                        functionName, deprecatedText, arguments)
        }

        return null
    }
}
