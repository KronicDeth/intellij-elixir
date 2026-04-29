package org.elixir_lang.documentation

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.psi.Module
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.Definition
import org.elixir_lang.psi.call.MaybeExported
import org.elixir_lang.utils.ElixirModulesUtil.erlangModuleNameToElixir

object BeamDocsHelper {
    fun fetchDocs(element: PsiElement): FetchedDocs? = Beam
        .from(element.containingFile.originalFile.virtualFile)
        ?.let { beam ->
            beam.atoms()?.moduleName()?.let { erlangModuleNameToElixir(it) }?.let { module ->
                when (element) {
                    is Module -> beam.documentation()?.moduleDocs?.englishDocs?.let { moduleDoc ->
                        FetchedDocs.ModuleDocumentation(module, moduleDoc)
                    }
                    is MaybeExported -> {
                        element.exportedName()?.let { name ->
                            val arity = element.exportedArity(ResolveState.initial())
                            val kind = kindForElement(element)

                            beam.documentation()?.docs?.let { docs ->
                                // Try exact arity first, then fall back to other arities for the same name.
                                // In Elixir, default-argument stubs (e.g. info/1) delegate to the full-arity
                                // definition (e.g. info/2) which is where the @doc lives.
                                val documented = kind.firstNotNullOfOrNull { k -> docs.documented(k, name, arity) }
                                    ?: kind.firstNotNullOfOrNull { k -> docs.documentedByNameFallback(k, name, arity) }

                                documented?.let {
                                    val signatures = it.signatures
                                    val deprecated = it.deprecated()
                                    val doc = it.doc

                                    if (deprecated != null || doc != null) {
                                        FetchedDocs.FunctionOrMacroDocumentation(
                                            module,
                                            deprecated,
                                            doc,
                                            impls = emptyList(),
                                            specs = emptyList(),
                                            heads = signatures
                                        )
                                    } else {
                                        null
                                    }
                                }
                            }
                        }
                    }
                    // types are only generated for builtins, so no docs
                    is AtUnqualifiedNoParenthesesCall<*> -> null
                    else -> {
                        Logger.error(BeamDocsHelper.javaClass, "Don't know how to fetch docs", element)

                        null
                    }
                }
            }
        }

    /**
     * Returns the list of BEAM documentation "kind" strings to try when looking up docs for [element].
     *
     * For [CallDefinitionImpl] elements, the stub's [Definition] tells us whether this is a function or macro.
     * The preferred kind is tried first, but we fall back to the other kind in case the BEAM docs
     * use a different categorization than expected (e.g. guards stored as macros).
     */
    private fun kindForElement(element: MaybeExported): List<String> =
        when (element) {
            is CallDefinitionImpl<*> -> when (element.stub.definition) {
                Definition.PUBLIC_MACRO, Definition.PRIVATE_MACRO -> listOf("macro", "function")
                Definition.PUBLIC_FUNCTION, Definition.PRIVATE_FUNCTION -> listOf("function", "macro")
                Definition.PUBLIC_GUARD, Definition.PRIVATE_GUARD -> listOf("macro", "function")
                else -> listOf("function", "macro")
            }
            else -> listOf("function", "macro")
        }
}
