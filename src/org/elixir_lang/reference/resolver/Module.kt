package org.elixir_lang.reference.resolver

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.LibraryScopeCache
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.scope.module.MultiResolve
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.reference.Resolver


object Module : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Module> {
    override fun resolve(
            module: org.elixir_lang.reference.Module,
            incompleteCode: Boolean
    ): Array<PsiElementResolveResult> =
            module.element.let { element ->
                element.fullyQualifiedName().let { name ->
                    val sameFileResolveResults =
                            MultiResolve.resolveResults(name, incompleteCode, element)

                    if (sameFileResolveResults.any(PsiElementResolveResult::isValidResult)) {
                        sameFileResolveResults
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
        val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
        val module = ModuleUtil.findModuleForPsiElement(entrance)
        // MUST use `originalFile` to get the PsiFile with a VirtualFile for decompiled elements
        val entranceVirtualFile = entrance.containingFile.originalFile.virtualFile

        val globalSearchScope = if (module != null) {
            val includeTests = entranceVirtualFile?.let { projectFileIndex.isInTestSourceContent(it) } ?: false
            // DOES NOT include the libraries sources, but...
            val moduleWithDependenciesAndLibrariesScope =
                    GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, includeTests)

            entranceVirtualFile?.let {
                // ... we prefer sources compared to decompiled, so use LibraryScope to get the Library source too.
                val orderEntries = projectFileIndex.getOrderEntriesForFile(entranceVirtualFile)
                val libraryScope =
                        LibraryScopeCache
                                .getInstance(project)
                                .getLibraryScope(orderEntries)

                moduleWithDependenciesAndLibrariesScope.uniteWith(libraryScope)
            } ?: moduleWithDependenciesAndLibrariesScope
        } else {
            GlobalSearchScope.allScope(project)
        }

        StubIndex
                .getInstance()
                .processElements(ModularName.KEY, name, project, globalSearchScope, null, NamedElement::class.java) { namedElement ->
                    /* The namedElement may be a ModuleImpl from a .beam.  Using #getNaviationElement() ensures a source
                       (either true source or decompiled) is used. */
                    resolveResultList.add(PsiElementResolveResult(namedElement.navigationElement))
                }

        return Resolver.preferred(entrance, incompleteCode = false, resolveResultList = resolveResultList).toTypedArray()
    }
}
