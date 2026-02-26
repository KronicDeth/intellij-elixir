package org.elixir_lang.reference.resolver

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.LibraryScopeCache
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.Alias
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.Require
import org.elixir_lang.psi.Use
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.scope.VisitedElementSetResolveResult
import org.elixir_lang.psi.scope.module.MultiResolve
import org.elixir_lang.psi.stub.index.ModularName


object Module : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Module> {
    override fun resolve(
        module: org.elixir_lang.reference.Module,
        incompleteCode: Boolean
    ): Array<ResolveResult> {
        ApplicationManager.getApplication().assertReadAccessAllowed()
        val element = module.element
        val name = element.fullyQualifiedName()

        return resolve(element, name, incompleteCode)
    }

    fun resolve(element: PsiElement, name: String, incompleteCode: Boolean): Array<ResolveResult> {
        val preferred = resolvePreferred(element, name, incompleteCode)
        val expanded = expand(preferred)

        return expanded.toTypedArray()
    }

    private fun expand(visitedElementSetResolveResultList: List<VisitedElementSetResolveResult>): List<PsiElementResolveResult> =
        visitedElementSetResolveResultList
            .flatMap { visitedElementSetResolveResult ->
                val visitedElementSet = visitedElementSetResolveResult.visitedElementSet
                val validResult = visitedElementSetResolveResult.isValidResult

                val pathResolveResultList =
                    visitedElementSet
                        .filter { visitedElement ->
                            visitedElement.let { it as? Call }?.let { visitedCall ->
                                Alias.`is`(visitedCall) || Require.`is`(visitedCall) || Use.`is`(visitedCall)
                            } ?: false
                        }
                        .map { PsiElementResolveResult(it, validResult) }

                val terminalResolveResult = PsiElementResolveResult(
                    visitedElementSetResolveResult.element,
                    visitedElementSetResolveResult.isValidResult
                )

                listOf(terminalResolveResult) + pathResolveResultList
            }
            // deduplicate shared `defdelegate`, `import`, or `use`
            .groupBy { it.element }
            .map { (_, resolveResults) -> resolveResults.first() }

    private fun resolvePreferred(
        element: PsiElement,
        name: String,
        incompleteCode: Boolean
    ): List<VisitedElementSetResolveResult> {
        val all = resolveAll(element, name, incompleteCode)

        return org.elixir_lang.reference.Resolver.preferred(element, incompleteCode, all)
    }

    private fun resolveAll(element: PsiElement, name: String, incompleteCode: Boolean) =
        resolveInScope(element, name, incompleteCode)
            .takeIf { set -> set.any(ResolveResult::isValidResult) }
            ?: multiResolveProject(element, name)

    private fun resolveInScope(element: PsiElement, name: String, incompleteCode: Boolean) =
        MultiResolve.resolveResults(name, incompleteCode, element)

    private fun multiResolveProject(
        entrance: PsiElement,
        name: String
    ): List<VisitedElementSetResolveResult> {
        val resolveResultList = mutableListOf<VisitedElementSetResolveResult>()
        val project = entrance.project

        if (!DumbService.isDumb(project)) {
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
                .processElements(
                    ModularName.KEY,
                    name,
                    project,
                    globalSearchScope,
                    null,
                    NamedElement::class.java
                ) { namedElement ->
                    /**
                     * Don't use `namedElement.navigationElement` as it triggers decompiling of the whole module,
                     * which will parse the whole Module text, which is overkill to get the module's name that is
                     * available in the `ModuleImpl` alone.
                     */
                    resolveResultList.add(VisitedElementSetResolveResult(namedElement))
                }
        }

        return resolveResultList
    }
}
