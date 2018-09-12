package org.elixir_lang.beam

import com.intellij.navigation.GotoRelatedItem
import com.intellij.navigation.GotoRelatedProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.overlaps
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.Definition
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.definition
import org.elixir_lang.psi.stub.index.AllName

/**
 * Go To Related from source to decompiled version of the same function
 */
class GotoDecompiledProvider : GotoRelatedProvider() {
    override tailrec fun getItems(psiElement: PsiElement): List<GotoRelatedItem> {
        val definitionItems = if (psiElement is Call) {
            val definition = definition(psiElement)

            if (definition != null) {
                definitionItems(definition, psiElement)
            } else {
                null
            }
        } else {
            null
        }

        return if (definitionItems == null) {
            val parent = psiElement.parent

            if (parent != null && parent !is PsiFile) {
                getItems(parent)
            } else {
                emptyList()
            }
        } else {
            definitionItems
        }
    }

    private fun definitionItems(definition: Definition, definer: Call): List<GotoRelatedItem> =
        definitionDecompiledSet(definition, definer).map { GotoRelatedItem(it, "Decompiled BEAM") }

    private fun definitionDecompiledSet(definition: Definition, definer: Call): Set<Call> =
            if (definition.type == Definition.Type.CALLABLE) {
                callableDefinerToDecompiledSet(definer)
            } else {
                modularDefinerToDecompiledSet(definer)
            }

    private fun callableDefinerToDecompiledSet(definer: Call) =
            callableDefinerToModularDefiner(definer)
                    ?.let { modularDefiner ->
                        nameArityRange(definer)?.let { nameArityRange ->
                            modularDefinerToDecompiledSet(modularDefiner)
                                    .flatMap { decompiledModularDefiner ->
                                        Modular
                                                .callDefinitionClauseCallSequence(decompiledModularDefiner)
                                                .mapNotNull { decompiledDefiner ->
                                                    nameArityRange(decompiledDefiner)?.let { decompiledNameArityRange ->
                                                        if (nameArityRange.name == decompiledNameArityRange.name && nameArityRange.arityRange.overlaps(decompiledNameArityRange.arityRange)) {
                                                            decompiledDefiner
                                                        } else {
                                                            null
                                                        }
                                                    }
                                                }.asIterable()
                                    }
                                    .toSet()
                        }
                    } ?: emptySet()

    private tailrec fun callableDefinerToModularDefiner(ancestor: PsiElement): Call? {
        return if (ancestor is Call && definition(ancestor)?.type == Definition.Type.MODULAR) {
            ancestor
        } else if (ancestor is PsiFile) {
            null
        } else {
            callableDefinerToModularDefiner(ancestor.parent)
        }
    }

    private fun modularDefinerToDecompiledSet(modularDefiner: Call): Set<Call> {
        val project = modularDefiner.project
        val scope = GlobalSearchScope.projectScope(project)

        return if (modularDefiner is StubBased<*>) {
            modularDefiner
                    .canonicalNameSet()
                    .let { decompiledSet(project, scope, it) }
        } else {
            emptySet()
        }
    }

    private fun decompiledSet(
            project: Project,
            scope: GlobalSearchScope,
            canonicalNameIterable: Iterable<String>
    ): Set<Call> =
            canonicalNameIterable.flatMapTo(mutableSetOf()) { decompiledSet(project, scope, it) }

    private fun decompiledSet(project: Project, scope: GlobalSearchScope, canonicalName: String): Set<Call> =
        StubIndex.getElements(
                AllName.KEY,
                canonicalName,
                project,
                scope,
                NamedElement::class.java
        ).mapNotNull { namedElement ->
            (namedElement as? ModuleImpl<*>)
                    ?.navigationElement
                    ?.let { it as Call }
        }.toSet()
}
