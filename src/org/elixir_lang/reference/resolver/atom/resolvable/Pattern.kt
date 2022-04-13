package org.elixir_lang.reference.resolver.atom.resolvable

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.reference.resolver.atom.Resolvable
import java.util.function.Predicate
import java.util.regex.Pattern

class Pattern(private val predicate: Predicate<String>) : Resolvable() {
    constructor(regex: String) : this(Pattern.compile(":$regex")) {}
    constructor(pattern: Pattern) : this(pattern.asPredicate()) {}

    override fun resolve(project: Project): Array<ResolveResult> {
        val stubIndex = StubIndex.getInstance()
        val scope = GlobalSearchScope.allScope(project)
        val resolveResults = mutableListOf<ResolveResult>()

        stubIndex
            .processAllKeys(AllName.KEY, project) { name ->
                if (predicate.test(name)) {
                    stubIndex.processElements(
                        AllName.KEY,
                        name,
                        project,
                        scope,
                        NamedElement::class.java
                    ) {
                        resolveResults.add(PsiElementResolveResult(it, false))

                        true
                    }
                } else {
                    true
                }
            }

        return resolveResults.toTypedArray()
    }
}
