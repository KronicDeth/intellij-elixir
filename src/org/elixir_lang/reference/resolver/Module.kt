package org.elixir_lang.reference.resolver

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.LibraryScopeCache
import com.intellij.openapi.vfs.VfsUtilCore
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
        val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
        val module = ModuleUtil.findModuleForPsiElement(entrance)
        val entranceVirtualFile = entrance.containingFile.virtualFile

        val globalSearchScope = if (module != null) {
            val includeTests = projectFileIndex.isInTestSourceContent(entranceVirtualFile)
            // DOES NOT include the libraries sources, but...
            val moduleWithDependenciesAndLibrariesScope =
                    GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, includeTests)

            // ... we prefer sources compared to decompiled, so use LibraryScope to get the Library source too.
            val orderEntries = projectFileIndex.getOrderEntriesForFile(entranceVirtualFile)
            val libraryScope =
                    LibraryScopeCache
                            .getInstance(project)
                            .getLibraryScope(orderEntries)

            moduleWithDependenciesAndLibrariesScope.uniteWith(libraryScope)
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

        val nearResolveResultList = if (module != null) {
            val contentRootSet = ModuleRootManager.getInstance(module).contentRoots.toSet()
            resolveResultList.filter { resolveResult ->
                // The `Module` of a Library source will be `null`, so have to check for a library in `deps` of module
                // using path instead.
                resolveResult
                        .element
                        .containingFile
                        .virtualFile
                        ?.let { virtualFile -> VfsUtilCore.isUnder(virtualFile, contentRootSet) }
                        ?:
                        false
            }
        } else {
            resolveResultList
        }

        val sourceResolveResultList = nearResolveResultList.filter { !it.element.isDecompiled() }

        return if (sourceResolveResultList.isNotEmpty()) {
            sourceResolveResultList.toTypedArray()
        } else {
            nearResolveResultList.toTypedArray()
        }
    }
}
