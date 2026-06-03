package org.elixir_lang.reference.resolver.atom.resolvable

import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.reference.resolver.atom.Resolvable
import org.elixir_lang.reference.resolver.narrowedScope
import com.intellij.psi.stubs.StubIndex

class Exact(private val name: String) : Resolvable() {
    override fun resolve(element: ElixirAtom): Array<ResolveResult> {
        val resolveResultList = mutableListOf<ResolveResult>()
        val project = element.project

        if (!DumbService.isDumb(project)) {
            val scope = narrowedScope(element, project)

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
