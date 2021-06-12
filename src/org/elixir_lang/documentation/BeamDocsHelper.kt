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
                                    docs.documented("function", name, arity)?.let { documented ->

                                        val signatures = documented.signatures
                                        val deprecated = documented.deprecated()
                                        val doc = documented.doc

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
                        else -> TODO()
                    }
                }
            }
}
