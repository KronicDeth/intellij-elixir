package org.elixir_lang.navigation

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import org.elixir_lang.beam.psi.BeamFileImpl
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.modular.Modular
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Quote

fun <E> List<E>.isDecompiled(): Boolean = all { it!!.isDecompiled() }
fun PsiElement.isDecompiled(): Boolean = containingFile.originalFile is BeamFileImpl

/**
 * Navigation items for [GotoSymbolContributor.getItemsByName] that will only return Source version if both Source and Decompiled version are added.
 */
class SourcePreferredItems  {
    fun add(callback: Callback) {
        // currently we don't decompile callbacks, so no need to check
        callbackList.add(callback)
    }

    fun add(callDefinition: CallDefinition) {
        val modularName = callDefinition.modularName()
        val callDefinitionByArityByName = callDefinitionListByArityByNameByModularName.computeIfAbsent(modularName) { mutableMapOf() }
        val callDefinitionListByArity = callDefinitionByArityByName.computeIfAbsent(callDefinition.name()) { mutableMapOf() }

        callDefinitionListByArity.compute(callDefinition.arity) { _, currentCallDefinitionList ->
            if (currentCallDefinitionList != null) {
                if (currentCallDefinitionList.isDecompiled()) {
                    if (callDefinition.isDecompiled()) {
                        // collect all decompiled for all environments
                        currentCallDefinitionList.add(callDefinition)
                        currentCallDefinitionList
                    } else {
                        // prefer source over all decompiled
                       mutableListOf(callDefinition)
                    }
                } else {
                    if (callDefinition.isDecompiled()) {
                        // ignore decompiled when there is source
                        currentCallDefinitionList
                    } else {
                        // collect all source
                        currentCallDefinitionList.add(callDefinition)
                        currentCallDefinitionList
                    }
                }
            } else {
                mutableListOf(callDefinition)
            }
        }
    }

    fun add(callDefinitionHead: CallDefinitionHead) {
        val callDefinition = callDefinitionHead.callDefinition
        val modularName = callDefinition.modularName()
        val callDefinitionHeadListByArityByName = callDefinitionHeadListByArityByNameByModularName.computeIfAbsent(modularName) { mutableMapOf() }
        val callDefinitionHeadListByArity = callDefinitionHeadListByArityByName.computeIfAbsent(callDefinition.name()) { mutableMapOf() }

        callDefinitionHeadListByArity.compute(callDefinition.arity) { _, currentCallDefinitionHeadList ->
            if (currentCallDefinitionHeadList != null) {
                if (currentCallDefinitionHeadList.isDecompiled()) {
                    if (callDefinitionHead.isDecompiled()) {
                        // collect all decompiled for all environments
                        currentCallDefinitionHeadList.add(callDefinitionHead)
                        currentCallDefinitionHeadList
                    } else {
                        // prefer source over all decompiled
                        mutableListOf(callDefinitionHead)
                    }
                } else {
                    if (callDefinitionHead.isDecompiled()) {
                        // ignore decompiled when there is source
                        currentCallDefinitionHeadList
                    } else {
                        // collect all source
                        currentCallDefinitionHeadList.add(callDefinitionHead)
                        currentCallDefinitionHeadList
                    }
                }
            } else {
                mutableListOf(callDefinitionHead)
            }
        }
    }

    fun add(callDefinitionClause: CallDefinitionClause) {
        val callDefinition = callDefinitionClause.callDefinition
        val modularName = callDefinition.modularName()
        val callDefinitionClauseListByArityByName = callDefinitionClauseListByArityByNameByModularName.computeIfAbsent(modularName) { mutableMapOf() }
        val callDefinitionClauseListByArity = callDefinitionClauseListByArityByName.computeIfAbsent(callDefinition.name()) { mutableMapOf() }

        callDefinitionClauseListByArity.compute(callDefinition.arity) { _, currentCallDefinitionClauseList ->
            if (currentCallDefinitionClauseList != null) {
                if (currentCallDefinitionClauseList.isDecompiled()) {
                    if (callDefinitionClause.isDecompiled()) {
                        // collect all decompiled for all environments
                        currentCallDefinitionClauseList.add(callDefinitionClause)
                        currentCallDefinitionClauseList
                    } else {
                        // prefer source over all decompiled
                        mutableListOf(callDefinitionClause)
                    }
                } else {
                    if (callDefinitionClause.isDecompiled()) {
                        // ignore decompiled when there is source
                        currentCallDefinitionClauseList
                    } else {
                        // collect all source
                        currentCallDefinitionClauseList.add(callDefinitionClause)
                        currentCallDefinitionClauseList
                    }
                }
            } else {
                mutableListOf(callDefinitionClause)
            }
        }
    }

    fun add(callDefinitionSpecification: CallDefinitionSpecification) {
        // currently we don't decompile specifications, so no need to check
        callDefinitionSpecificationList.add(callDefinitionSpecification)
    }

    fun add(modular: Modular) {
        modularListByName.compute(modularName(modular)) { _, currentModularList ->
            if (currentModularList != null) {
                if (currentModularList.isDecompiled()) {
                    if (modular.isDecompiled()) {
                        // collect all decompiled for all environments
                        currentModularList.add(modular)
                        currentModularList
                    } else {
                        // prefer source over all decompiled
                        mutableListOf(modular)
                    }
                } else {
                    if (modular.isDecompiled()) {
                        // ignore decompiled when there is source
                        currentModularList
                    } else {
                        // collect all source
                        currentModularList.add(modular)
                        currentModularList
                    }
                }
            } else {
                mutableListOf(modular)
            }
        }
    }

    fun toTypedArray(): Array<NavigationItem> {
        val navigationItemList =
                modularListByName.values.flatten() as List<NavigationItem> +
                        callDefinitionClauseListByArityByNameByModularName.values.flatMap { it.values.flatMap { it.values.flatten() } } +
                        callDefinitionListByArityByNameByModularName.values.flatMap { it.values.flatMap { it.values.flatten() } } +
                        callDefinitionSpecificationList +
                        callDefinitionHeadListByArityByNameByModularName.values.flatMap { it.values.flatMap { it.values.flatten() } } +
                        callbackList

        return navigationItemList
                .distinctBy { navigationItem ->
                    navigationItem
                            .let { it as? StructureViewTreeElement }
                            ?.value
                            ?: navigationItem
                }
                .toTypedArray()
    }

    private val callDefinitionListByArityByNameByModularName = mutableMapOf<ModularName, MutableMap<Name, MutableMap<Arity, MutableList<CallDefinition>>>>()
    private val callDefinitionClauseListByArityByNameByModularName = mutableMapOf<ModularName, MutableMap<Name, MutableMap<Arity, MutableList<CallDefinitionClause>>>>()
    private val callDefinitionHeadListByArityByNameByModularName = mutableMapOf<ModularName, MutableMap<Name, MutableMap<Arity, MutableList<CallDefinitionHead>>>>()
    private val callDefinitionSpecificationList = mutableListOf<CallDefinitionSpecification>()
    private val callbackList = mutableListOf<Callback>()
    private val modularListByName = mutableMapOf<Name, MutableList<Modular>>()
}

private fun Any.isDecompiled(): Boolean =
    when (this) {
        is CallDefinition -> isDecompiled()
        is CallDefinitionClause -> isDecompiled()
        is Module -> isDecompiled()
        is PsiElement -> isDecompiled()
        is PsiElementResolveResult -> isDecompiled()
        else -> TODO("Don't know how to check if ${this.javaClass} is decompiled")
    }

private fun CallDefinition.isDecompiled(): Boolean = modular.isDecompiled()
private fun CallDefinitionClause.isDecompiled(): Boolean = callDefinition.isDecompiled()

private fun Modular.isDecompiled(): Boolean =
        when (this) {
            is Module -> isDecompiled()
            is Quote -> callDefinitionClause.isDecompiled()
            else -> false
        }

private fun Module.isDecompiled(): Boolean = (value as PsiElement).containingFile.originalFile is BeamFileImpl
private fun PsiElementResolveResult.isDecompiled(): Boolean = element.isDecompiled()

typealias ModularName = String
typealias Name = String
typealias Arity = Int

private fun CallDefinition.modularName() = modularName(modular)
private fun modularName(presentable: Presentable): String =
        when (presentable) {
            is CallDefinitionClause -> presentable.callDefinition.modularName()
            is Module -> presentable.name
            is Quote -> "${modularName(presentable.callDefinitionClause)} quote"
            else -> null
        } ?: "?"
