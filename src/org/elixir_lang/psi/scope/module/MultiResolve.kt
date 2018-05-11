package org.elixir_lang.psi.scope.module

import com.intellij.openapi.project.DumbService
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.Module.concat
import org.elixir_lang.Module.split
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.scope.Module
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.reference.module.UnaliasedName
import java.util.*

class MultiResolve internal constructor(private val name: String, private val incompleteCode: Boolean) : Module() {
    var resolveResultList: List<ResolveResult>? = null
        private set

    override fun execute(match: PsiElement, state: ResolveState): Boolean =
            if (match is Named) {
                execute(match, state)
            } else {
                true
            }

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here.
     *
     * @return `true` to keep processing; `false` to stop processing.
     */
    override fun executeOnAliasedName(match: PsiNamedElement,
                                      aliasedName: String,
                                      state: ResolveState): Boolean {
        if (aliasedName == name) {
            val namePartList = split(name)

            // adds `Foo.SSH` in `alias Foo.SSH`
            addToResolveResultList(match, true)

            // adds `defmodule Foo.SSH` for `alias Foo.SSH`
            addUnaliasedNamedElementsToResolveResultList(match, namePartList)
        } else {
            val namePartList = split(name)
            val firstNamePart = namePartList[0]

            // alias Foo.SSH, then SSH.Key is name
            if (aliasedName == firstNamePart) {
                addToResolveResultList(match, true)

                addUnaliasedNamedElementsToResolveResultList(match, namePartList)
            } else if (incompleteCode && aliasedName.startsWith(name)) {
                addToResolveResultList(match, false)
            }
        }

        return org.elixir_lang.psi.scope.MultiResolve.keepProcessing(incompleteCode, resolveResultList)
    }

    private fun addToResolveResultList(element: PsiElement, validResult: Boolean) {
        resolveResultList = org.elixir_lang.psi.scope.MultiResolve.addToResolveResultList(
                resolveResultList, PsiElementResolveResult(element, validResult)
        )
    }

    private fun addUnaliasedNamedElementsToResolveResultList(match: PsiNamedElement, namePartList: List<String>) {
        unaliasedName(match, namePartList)
                .let { indexedNamedElements(match, it) }
                .forEach { addToResolveResultList(it, true) }
    }

    companion object {
        fun resolveResultList(name: String,
                              incompleteCode: Boolean,
                              entrance: PsiElement,
                              maxScope: PsiElement): List<ResolveResult>? =
                resolveResultList(name, incompleteCode, entrance, maxScope, ResolveState.initial())

        private fun indexedNamedElements(match: PsiNamedElement, unaliasedName: String): Collection<NamedElement> {
            val project = match.project

            return if (DumbService.isDumb(project)) {
                emptyList()
            } else {
                StubIndex.getElements(
                        AllName.KEY,
                        unaliasedName,
                        project,
                        GlobalSearchScope.allScope(project),
                        NamedElement::class.java
                )
            }
        }

        private fun resolveResultList(name: String,
                                      incompleteCode: Boolean,
                                      entrance: PsiElement,
                                      maxScope: PsiElement,
                                      state: ResolveState): List<ResolveResult>? {
            val multiResolve = MultiResolve(name, incompleteCode)

            PsiTreeUtil.treeWalkUp(
                    multiResolve,
                    entrance,
                    maxScope,
                    state.put(ENTRANCE, entrance)
            )

            return multiResolve.resolveResultList
        }

        private fun unaliasedName(match: PsiNamedElement, namePartList: List<String>): String {
            val matchUnaliasedName = UnaliasedName.unaliasedName(match)

            val unaliasedNamePartList = ArrayList<String>(namePartList.size)
            unaliasedNamePartList.add(matchUnaliasedName!!)

            for (i in 1 until namePartList.size) {
                unaliasedNamePartList.add(namePartList[i])
            }

            return concat(unaliasedNamePartList)
        }
    }
}
