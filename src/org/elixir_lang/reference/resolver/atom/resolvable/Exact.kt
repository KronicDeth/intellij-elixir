package org.elixir_lang.reference.resolver.atom.resolvable

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.reference.resolver.atom.Resolvable

class Exact(private val name: String) : Resolvable() {
    override fun resolve(project: Project): Array<ResolveResult> {
        val resolveResultList = mutableListOf<ResolveResult>()

        if (!DumbService.isDumb(project)) {
            val scope = GlobalSearchScope.allScope(project)

            StubIndex.getInstance().processElements(
                AllName.KEY, name, project, scope, NamedElement::class.java
            ) { namedElement ->
                resolveResultList.add(PsiElementResolveResult(namedElement))

                true
            }
        }
        
        return resolveResultList.toTypedArray()
    }
}
