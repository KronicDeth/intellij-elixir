package org.elixir_lang.reference.resolver.atom.resolvable

import com.intellij.openapi.project.Project
import org.elixir_lang.reference.resolver.atom.Resolvable
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import org.elixir_lang.semantic.Alias

class Exact(private val name: String) : Resolvable() {
    override fun resolve(project: Project): Array<ResolveResult> =
            Alias.modulars(project, name).map { PsiElementResolveResult(it.psiElement) }.toTypedArray()
}
