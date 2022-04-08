package org.elixir_lang.documentation

import com.intellij.psi.PsiElement
import org.elixir_lang.semantic.Documentation
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.semantic

object SourceFileDocsHelper {
    fun fetchDocs(element: PsiElement): FetchedDocs? = when (val semantic = element.semantic) {
        is Modular ->
            semantic
                .moduleDocs
                .map(Documentation::text)
                .joinToString("")
                .takeIf { it.isNotEmpty() }?.let { moduleDoc ->
                    FetchedDocs.ModuleDocumentation(semantic.canonicalName!!, moduleDoc)
                }
        is Clause ->
            semantic
                .definition
                .let { FetchedDocs.FunctionOrMacroDocumentation.fromCallDefinition(it) }
        else -> null
    }
}

