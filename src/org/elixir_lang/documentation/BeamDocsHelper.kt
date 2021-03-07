package org.elixir_lang.documentation

import com.intellij.psi.PsiElement
import org.elixir_lang.beam.Beam
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.getModuleName

object BeamDocsHelper {
    fun fetchDocs(element: PsiElement) : FetchedDocs? = Beam
            .from(element.containingFile.originalFile.virtualFile)
            ?.let { beam ->
                if (element.firstChild.text == "defmodule"){
                    val moduleDocumentation = beam.documentation()?.moduleDocs?.englishDocs
                    moduleDocumentation?.let { FetchedDocs.ModuleDocumentation(element.getModuleName().orEmpty(), it) }
                } else if (element is Call){
                    val functionName = element.name.orEmpty()
                    val arityRange = element.primaryArity()

                    val moduleName = beam.atoms()?.moduleName().orEmpty()

                    val docs = beam.documentation()?.docs
                            ?.docsForOrSimilar(functionName, arityRange ?: 0) ?: return null

                    val signatures = docs.signatures

                    val metadata = beam.documentation()?.docs?.getFunctionMetadataOrSimilar(functionName, arityRange ?: 0)
                    val deprecatedMetadata = metadata?.firstOrNull{it.name == "deprecated"}

                    val deprecatedText: String? = when (deprecatedMetadata){
                        null -> null
                        else -> deprecatedMetadata.value.orEmpty()
                    }

                    val functionDocs =
                            beam.documentation()?.docs?.getFunctionDocsOrSimilar(functionName, arityRange ?: 0)

                    val docsText = functionDocs?.firstOrNull()?.documentationText
                    if (docsText != null || deprecatedMetadata != null) {
                        FetchedDocs.FunctionOrMacroDocumentation(moduleName, deprecatedText, docsText.orEmpty(), impls = emptyList(), specs = emptyList(), heads = signatures)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
}
