package org.elixir_lang.navigation

import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import org.elixir_lang.beam.psi.BeamFileImpl
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Modular
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Quote

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

        callDefinitionClauseListByArity.compute(callDefinition.arity) { arity, currentCallDefinitionClauseList ->
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

    fun add(implementation: Implementation) {
        implementationListByName.compute(implementation.name ?: "?") { _, currentImplementationList ->
            if (currentImplementationList != null) {
                if (currentImplementationList.isDecompiled()) {
                    if (implementation.isDecompiled()) {
                        // collect all decompiled for all environments
                        currentImplementationList.add(implementation)
                        currentImplementationList
                    } else {
                        // prefer source over all decompiled
                        mutableListOf(implementation)
                    }
                } else {
                    if (implementation.isDecompiled()) {
                        // ignore decompiled when there is source
                        currentImplementationList
                    } else {
                        // collect all source
                        currentImplementationList.add(implementation)
                        currentImplementationList
                    }
                }
            } else {
                mutableListOf(implementation)
            }
        }
    }

    fun add(module: Module) {
        moduleListByName.compute(module.name ?: "?") { _, currentModuleList ->
            if (currentModuleList != null) {
                if (currentModuleList.isDecompiled()) {
                    if (module.isDecompiled()) {
                        // collect all decompiled for all environments
                        currentModuleList.add(module)
                        currentModuleList
                    } else {
                        // prefer source over all decompiled
                        mutableListOf(module)
                    }
                } else {
                    if (module.isDecompiled()) {
                        // ignore decompiled when there is source
                        currentModuleList
                    } else {
                        // collect all source
                        currentModuleList.add(module)
                        currentModuleList
                    }
                }
            } else {
                mutableListOf(module)
            }
        }
    }

    fun toTypedArray(): Array<NavigationItem> =
            (moduleListByName.values.flatten() as List<NavigationItem> +
                    callDefinitionClauseListByArityByNameByModularName.values.flatMap { it.values.flatMap { it.values.flatten() } } +
                    callDefinitionListByArityByNameByModularName.values.flatMap { it.values.flatMap { it.values.flatten() } } +
                    callDefinitionSpecificationList +
                    callDefinitionHeadListByArityByNameByModularName.values.flatMap { it.values.flatMap { it.values.flatten() } } +
                    callbackList +
                    implementationListByName.values.flatten()
                    ).toTypedArray()

    private val callDefinitionListByArityByNameByModularName = mutableMapOf<ModularName, MutableMap<Name, MutableMap<Arity, MutableList<CallDefinition>>>>()
    private val callDefinitionClauseListByArityByNameByModularName = mutableMapOf<ModularName, MutableMap<Name, MutableMap<Arity, MutableList<CallDefinitionClause>>>>()
    private val callDefinitionHeadListByArityByNameByModularName = mutableMapOf<ModularName, MutableMap<Name, MutableMap<Arity, MutableList<CallDefinitionHead>>>>()
    private val callDefinitionSpecificationList = mutableListOf<CallDefinitionSpecification>()
    private val callbackList = mutableListOf<Callback>()
    private val implementationListByName = mutableMapOf<Name, MutableList<Implementation>>()
    private val moduleListByName = mutableMapOf<Name, MutableList<Module>>()
}

private fun <E> List<E>.isDecompiled(): Boolean = all { it!!.isDecompiled() }

private fun Any.isDecompiled(): Boolean =
    when (this) {
        is CallDefinition -> isDecompiled()
        is CallDefinitionClause -> isDecompiled()
        is Module -> isDecompiled()
        else -> TODO()
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
