package org.elixir_lang.reference.resolver

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.navigation.isDecompiled
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.scope.module.MultiResolve
import org.elixir_lang.psi.stub.index.ModularName


object Module : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Module> {
    override fun resolve(
            module: org.elixir_lang.reference.Module,
            incompleteCode: Boolean
    ): Array<PsiElementResolveResult> =
            module.element.let { element ->
                element.fullyQualifiedName().let { name ->
                    val sameFileResolveResultList =
                            MultiResolve.resolveResults(name, incompleteCode, element)

                    if (sameFileResolveResultList.any(PsiElementResolveResult::isValidResult)) {
                        sameFileResolveResultList
                    } else {
                        multiResolveProject(
                                element,
                                name
                        )
                    }
                }
            }

    private fun multiResolveProject(entrance: PsiElement,
                                    name: String): Array<PsiElementResolveResult> {
        val resolveResultList = mutableListOf<PsiElementResolveResult>()

        val project = entrance.project
        val globalSearchScope = ModuleUtil
                .findModuleForPsiElement(entrance)
                ?.let { module ->
                    val includeTests = ProjectRootManager.getInstance(project).fileIndex.isInTestSourceContent(entrance.containingFile.virtualFile)
                    GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, includeTests)
                }
                ?:
                GlobalSearchScope.allScope(project)

        StubIndex
                .getInstance()
                .processElements(ModularName.KEY, name, project, globalSearchScope, null, NamedElement::class.java) { namedElement ->
                    /* The namedElement may be a ModuleImpl from a .beam.  Using #getNaviationElement() ensures a source
                       (either true source or decompiled) is used. */
                    resolveResultList.add(PsiElementResolveResult(namedElement.navigationElement))
                }

        val sourceResolveResultList = resolveResultList.filter { !it.element.isDecompiled() }

        return if (sourceResolveResultList.isNotEmpty()) {
            sourceResolveResultList.toTypedArray()
        } else {
            resolveResultList.toTypedArray()
        }
    }
}
