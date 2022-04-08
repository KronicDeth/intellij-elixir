package org.elixir_lang.semantic

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.reference.module.UnaliasedName

/**
 * An Elixir alias, such a [org.elixir_lang.semantic.Modular]'s name.
 */
interface Alias : Semantic {
    val name: String
    val unaliasedName: String?
        get() = CachedValuesManager.getCachedValue(psiElement, UNALIASED_NAME) {
            CachedValueProvider.Result.create(computeUnaliasedName(psiElement), psiElement)
        }
    val modulars: Set<Modular>
        get() = CachedValuesManager.getCachedValue(psiElement, MODULARS) {
            CachedValueProvider.Result.create(modulars(psiElement.project, unaliasedName), psiElement)
        }
    val suffix: Alias?
    val nested: Set<Alias>

    companion object {
        private val UNALIASED_NAME: Key<CachedValue<String?>> = Key("elixir.alias.unaliased_name")

        private fun computeUnaliasedName(psiElement: PsiElement): String? =
            psiElement.let { it as? PsiNamedElement }?.let { UnaliasedName.unaliasedName(it) }

        private val MODULARS: Key<CachedValue<Set<Modular>>> = Key("elixir.alias.modulars")

        fun modulars(project: Project, unaliasedName: String?): Set<Modular> =
            if (unaliasedName != null) {
                modulars(project, setOf(unaliasedName))
            } else {
                emptySet()
            }

        fun modulars(project: Project, unaliasedNames: Set<String>): Set<Modular> =
            if (!DumbService.isDumb(project)) {
                val modulars = mutableSetOf<Modular>()

                for (unaliasedName in unaliasedNames) {
                    StubIndex
                        .getInstance()
                        .processElements(
                            ModularName.KEY,
                            unaliasedName,
                            project,
                            GlobalSearchScope.allScope(project),
                            NamedElement::class.java
                        ) { namedElement ->
                            val semantic = namedElement.semantic

                            if (semantic is Modular) {
                                modulars.add(semantic)
                            }

                            true
                        }
                }

                modulars
            } else {
                emptySet()
            }

        fun nestedModularNames(project: Project, unaliasedName: String): Set<String> {
            val nestedModularNames = mutableSetOf<String>()

            if (!DumbService.isDumb(project)) {
                val splitAncestorName = org.elixir_lang.Module.split(unaliasedName)

                StubIndex.getInstance().processAllKeys(ModularName.KEY, project) { modularName ->
                    val splitModularName = org.elixir_lang.Module.split(modularName)

                    if (splitModularName.startsWith(splitAncestorName)) {
                        nestedModularNames.add(modularName)
                    }

                    true
                }
            }

            return nestedModularNames
        }
    }
}

private fun kotlin.collections.List<String>.startsWith(prefix: kotlin.collections.List<String>): Boolean =
    if (this.isNotEmpty() && prefix.isNotEmpty() && this.size > prefix.size) {
        this.zip(prefix).all(String::equals)
    } else {
        false
    }
