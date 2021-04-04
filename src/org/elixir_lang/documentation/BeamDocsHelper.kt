package org.elixir_lang.documentation

import com.intellij.psi.PsiElement
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.psi.Module
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
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
                                val arity = element.exportedArity()

                                beam.documentation()?.docs?.let { docs ->
                                    docs.docsForOrSimilar(name, arity)?.let { functionInfo ->
                                        val signatures = functionInfo.signatures

                                        val deprecated = docs
                                                .getFunctionMetadataOrSimilar(name, arity)
                                                .firstOrNull { it.name == "deprecated" }
                                                ?.let { it.value.orEmpty() }

                                        val functionDoc = docs
                                                .getFunctionDocsOrSimilar(name, arity)
                                                .firstOrNull()
                                                ?.documentationText

                                        if (deprecated != null || functionDoc != null) {
                                            FetchedDocs.FunctionOrMacroDocumentation(
                                                    module,
                                                    deprecated,
                                                    functionDoc,
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
                        else -> TODO()
                    }
                }
            }
}
