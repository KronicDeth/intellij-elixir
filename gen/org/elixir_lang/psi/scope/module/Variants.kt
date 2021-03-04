package org.elixir_lang.psi.scope.module

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.operation.Normalized
import org.elixir_lang.psi.scope.LookupElementByLookupName
import org.elixir_lang.psi.scope.Module
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.psi.stub.type.call.Stub
import org.elixir_lang.reference.module.UnaliasedName

class Variants(private val entrance: PsiElement) : Module() {
    override fun execute(match: PsiElement, state: ResolveState): Boolean =
            when (match) {
                is ElixirMultipleAliases -> execute(match)
                is Named -> execute(match, state)
                else -> true
            }

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here.
     *
     * @param match
     * @param aliasedName
     * @param state
     * @return `true` to keep processing; `false` to stop processing.
     */
    override fun executeOnAliasedName(match: PsiNamedElement, aliasedName: String, state: ResolveState): Boolean {
        lookupElementByLookupName.put(aliasedName, match)

        val splitPrefix = org.elixir_lang.Module.split(aliasedName)
        putNestedAliased(lookupElementByLookupName, splitPrefix, match)

        return true
    }

    private var multipleAliases: ElixirMultipleAliases? = null
    private val lookupElementByLookupName: LookupElementByLookupName = LookupElementByLookupName()

    private fun execute(match: ElixirMultipleAliases): Boolean {
        multipleAliases = match

        return false
    }

    /**
     * Puts all `aliases` in scope from `entrance` and any modules nested under those modules in
     * `lookupElementByLookupName`.
     */
    private fun putAliases(): Variants {
        PsiTreeUtil.treeWalkUp(
                this,
                entrance,
                entrance.containingFile,
                ResolveState.initial().put(ElixirPsiImplUtil.ENTRANCE, entrance)
        )

        return this
    }

    /**
     * Puts all project `Alias`es.
     */
    private fun putProject(): Variants {
        val project = entrance.project
        val scope = GlobalSearchScope.allScope(project)

        val stubIndex = StubIndex.getInstance()
        stubIndex.processAllKeys(ModularName.KEY, project) { name ->
            if (!lookupElementByLookupName.contains(name)) {
                stubIndex.processElements(AllName.KEY, name, project, scope, NamedElement::class.java) { named_element ->
                    lookupElementByLookupName.put(name, named_element.navigationElement)

                    // just use the first element
                    false
                }
            }

            true
        }

        return this
    }

    private fun lookupElements(): Collection<LookupElement> = lookupElementByLookupName.lookupElements()

    companion object {
        fun lookupElements(entrance: QualifiableAlias): Collection<LookupElement> =
                entrance
                        .qualifier()
                        // if there is a qualifier, then it is only modules nested under `qualifier`'s alias or it's fully
                        // qualified name if unaliased that are valid variants because the completions are after a `.`
                        ?.let { qualifier ->
                            filteredLookupElements(qualifier as PsiNamedElement)
                        }
                        ?:
                        // if there is no qualifier then all aliases in the file and all project names are valid
                        unfilteredLookupElements(entrance)

        /**
         * Any modules nested under `qualifier`
         */
        private fun filteredLookupElements(qualifier: PsiNamedElement): Collection<LookupElement> =
                qualifier
                        .reference
                        ?.let { qualifierReference ->
                            when (qualifierReference) {
                                is PsiPolyVariantReference -> {
                                    val lookupElementByLookupName = LookupElementByLookupName()

                                    val resolvedElements = qualifierReference
                                            .multiResolve(false)
                                            .filter(ResolveResult::isValidResult)
                                            .mapNotNull(ResolveResult::getElement)

                                    val resolvedModulars = resolvedElements.filterIsInstance<Call>().filter { Stub.isModular(it) }

                                    val resolveds = if (resolvedModulars.isNotEmpty()) {
                                        resolvedModulars
                                    } else {
                                        resolvedElements

                                    }

                                    for (resolved in resolveds) {
                                        putNestedAliased(lookupElementByLookupName, emptyList(), resolved as PsiNamedElement)
                                    }

                                    lookupElementByLookupName.lookupElements()
                                }
                                else -> {
                                    val resolved = qualifierReference.resolve()
                                    TODO()
                                }
                            }
                        }
                        ?:
                        // if the qualifier can't be resolved to an `alias` or a modular, then we can't find the nested
                        // modulars.
                        emptyList()

        private fun putNested(lookupElementByLookupName: LookupElementByLookupName, project: Project) {

        }

        private fun putNestedAliased(lookupElementByLookupName: LookupElementByLookupName, splitPrefix: List<String>, aliasedElement: PsiNamedElement) {
            UnaliasedName.unaliasedName(aliasedElement)?.let { unaliasedName ->
                val splitUnaliasedName = org.elixir_lang.Module.split(unaliasedName)
                val project = aliasedElement.project
                val scope = GlobalSearchScope.allScope(project)

                val stubIndex = StubIndex.getInstance()
                stubIndex.processAllKeys(ModularName.KEY, project) { name ->
                    val splitRelativeName = org.elixir_lang.Module.relative(ancestors = splitUnaliasedName, descendant = name)

                    if (splitRelativeName.isNotEmpty()) {
                        val aliasedNestedName = org.elixir_lang.Module.concat(splitPrefix + splitRelativeName)

                        if (!lookupElementByLookupName.contains(aliasedNestedName)) {
                            stubIndex.processElements(AllName.KEY, name, project, scope, NamedElement::class.java) { named_element ->
                                lookupElementByLookupName.put(aliasedNestedName, named_element.navigationElement)

                                // only take the first element
                                false
                            }
                        }
                    }

                    true
                }
            }
        }

        /**
         * * All `alias`es in scope from `entrance` and any modules nested under those modules.
         * * All project `Alias`es.
         */
        private fun unfilteredLookupElements(entrance: PsiElement): Collection<LookupElement> =
                Variants(entrance)
                        .putAliases()
                        .putProject()
                        .lookupElements()
    }
}
