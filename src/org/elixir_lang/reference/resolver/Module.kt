package org.elixir_lang.reference.resolver

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.Reference.forEachNavigationElement
import org.elixir_lang.psi.scope.module.MultiResolve
import org.elixir_lang.reference.module.ResolvableName.resolvableName
import java.util.*

object Module : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Module> {
    override fun resolve(module: org.elixir_lang.reference.Module, incompleteCode: Boolean): Array<ResolveResult> {
        val element = module.element

        val resolveResultList = resolvableName(element)?.let { name ->
            val sameFileResolveResultList =
                    MultiResolve.resolveResultList(name, incompleteCode, element, module.maxScope)

            if (sameFileResolveResultList != null && sameFileResolveResultList.isNotEmpty()) {
                sameFileResolveResultList
            }
            else  {
                multiResolveProject(
                        element.project,
                        name
                )
            }
        }

        return resolveResultList?.toTypedArray() ?: emptyArray()
    }

    private fun multiResolveProject(project: Project,
                                    name: String): List<ResolveResult> {
        val results = ArrayList<ResolveResult>()

        forEachNavigationElement(
                project,
                name
        ) { navigationElement ->
            results.add(PsiElementResolveResult(navigationElement))

            true
        }

        return results
    }

}
