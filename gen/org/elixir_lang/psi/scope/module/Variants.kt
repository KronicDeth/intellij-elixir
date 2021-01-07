package org.elixir_lang.psi.scope.module

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil.treeWalkUp
import com.intellij.util.containers.ContainerUtil
import org.elixir_lang.Module.concat
import org.elixir_lang.Module.split
import org.elixir_lang.Reference.indexedNameCollection
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.operation.Normalized
import org.elixir_lang.psi.scope.Module
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.reference.module.UnaliasedName

class Variants : Module() {
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
        lookupElementList.add(
                LookupElementBuilder.createWithSmartPointer(
                        aliasedName,
                        match
                )
        )

        UnaliasedName.unaliasedName(match)?.let { unaliasedName ->
            val project = match.project

            val indexedNameCollection = indexedNameCollection(project)
            val unaliasedNestedNames = ContainerUtil.findAll(
                    indexedNameCollection,
                    org.elixir_lang.Module.IsNestedUnder(unaliasedName)
            )

            if (unaliasedNestedNames.isNotEmpty()) {
                val scope = GlobalSearchScope.allScope(project)

                for (unaliasedNestedName in unaliasedNestedNames) {
                    val unaliasedNestedNamedElementCollection = StubIndex.getElements(
                            AllName.KEY,
                            unaliasedNestedName,
                            project,
                            scope,
                            NamedElement::class.java
                    )

                    if (unaliasedNestedNamedElementCollection.isNotEmpty()) {
                        val unaliasedNestedNamePartList = split(unaliasedNestedName)
                        val unaliasedNamePartList = split(unaliasedName)
                        val aliasedNamePartList = split(aliasedName)
                        val aliasedNestedNamePartList = mutableListOf<String>()

                        aliasedNestedNamePartList.addAll(aliasedNamePartList)

                        for (i in unaliasedNamePartList.size until unaliasedNestedNamePartList.size) {
                            aliasedNestedNamePartList.add(unaliasedNestedNamePartList[i])
                        }

                        val aliasedNestedName = concat(aliasedNestedNamePartList)

                        for (unaliasedNestedNamedElement in unaliasedNestedNamedElementCollection) {
                            lookupElementList.add(
                                    LookupElementBuilder.createWithSmartPointer(
                                            aliasedNestedName,
                                            unaliasedNestedNamedElement
                                    )
                            )
                        }
                    }
                }
            }
        }

        return true
    }

    private var multipleAliases: ElixirMultipleAliases? = null
    private val lookupElementList: MutableList<LookupElement> = mutableListOf()

    private fun projectNameElements(entrance: PsiElement): List<LookupElement> {
        val project = entrance.project
        val prefix = multipleAliases.indexedNamePrefix()
        /* getAllKeys is not the actual keys in the actual project.  They need to be checked.
           See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207930789-StubIndex-persisting-between-test-runs-leading-to-incorrect-completions */
        val prefixedNameCollection = StubIndex.getInstance()
                .getAllKeys(AllName.KEY, project)
                .filter(String::isAlias)
                .filterStartsWithMaybe(prefix)

        val scope = GlobalSearchScope.allScope(project)

        return prefixedNameCollection.flatMap { prefixedName ->
            val lookupName = prefixedName.removeMaybePrefix(prefix)

            StubIndex.getElements(
                    AllName.KEY,
                    prefixedName,
                    project,
                    scope,
                    NamedElement::class.java
            ).map { prefixedNameNamedElement ->
                /* Generalizes over whether the prefixedNameNamedElement is a source element or a compiled element as
                   the navigation element is defined to be always be a source element */
                val navigationElement = prefixedNameNamedElement.navigationElement

                LookupElementBuilder.createWithSmartPointer(
                        lookupName,
                        navigationElement
                )
            }
        }
    }

    private fun execute(match: ElixirMultipleAliases): Boolean {
        multipleAliases = match

        return false
    }

    companion object {
        fun lookupElementList(entrance: PsiElement): List<LookupElement> {
            val variants = Variants()

            treeWalkUp(
                    variants,
                    entrance,
                    entrance.containingFile,
                    ResolveState.initial().put(ENTRANCE, entrance)
            )

            return variants.lookupElementList + variants.projectNameElements(entrance)
        }
    }
}

private fun ElixirMultipleAliases?.indexedNamePrefix(): String? =
        this?.let {
            val children = parent.let { it  as QualifiedMultipleAliases }.
                    children
            val operatorIndex = Normalized.operatorIndex(children)

            return org.elixir_lang.psi.operation.infix.Normalized.leftOperand(
                    children,
                    operatorIndex
            )!!.indexNamePrefix()
        }

private fun List<String>.filterStartsWithMaybe(maybePrefix: String?): List<String> =
        maybePrefix?.let { prefix ->
            filter { element -> element.startsWith(prefix) }
        } ?:
        this

private fun ElixirAccessExpression.indexNamePrefix(): String? =
        children.singleOrNull()?.indexNamePrefix()

private fun PsiElement.indexNamePrefix(): String? =
        when (this) {
            is ElixirAccessExpression -> indexNamePrefix()
            is QualifiableAlias -> indexNamePrefix()
            else -> null
        }

private fun QualifiableAlias.indexNamePrefix(): String? = fullyQualifiedName()?.let { "$it." }

/**
 * Only those names that work as Alias, that is those that start with a capital letter
 */
private fun String?.isAlias(): Boolean = this?.codePointAt(0)?.let { Character.isUpperCase(it) } ?: false

private fun String.removeMaybePrefix(maybePrefix: String?): String =
        maybePrefix?.let { prefix -> this.removePrefix(prefix) } ?:
        this
