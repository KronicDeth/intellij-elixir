package org.elixir_lang.structure_view.element.modular

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.ElementDescriptionUtil
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewShortNameLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArity
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.psi.Arguments
import org.elixir_lang.psi.CallDefinitionClause.isMacro
import org.elixir_lang.psi.QuoteMacro
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.CREATE
import org.elixir_lang.psi.call.name.Function.DEFMODULE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.MODULE
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.enclosingMacroCall
import org.elixir_lang.psi.impl.locationString
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Or
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.Quote
import org.elixir_lang.structure_view.element.call_definition_by_name_arity.FunctionByNameArity
import org.elixir_lang.structure_view.element.call_definition_by_name_arity.MacroByNameArity
import org.elixir_lang.structure_view.element.structure.Structure
import org.elixir_lang.structure_view.node_provider.Used
import org.jetbrains.annotations.Contract
import java.util.*

/**
 *
 * @param parent the parent [Module] or [org.elixir_lang.structure_view.element.Quote] that scopes
 * `call`.
 * @param call the `Kernel.defmodule/2` call nested in `parent`.
 */
open class Module(protected val parent: Modular?, call: Call) : Element<Call>(call), Modular {
    constructor(call: Call) : this(null, call)

    override fun getChildren(): Array<TreeElement> = callChildren(this, navigationItem)

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation {
        return org.elixir_lang.navigation.item_presentation.modular.Module(location(), navigationItem)
    }

    protected fun location(): String? =
            parent?.presentation.let { it as? Parent }?.locatedPresentableText ?: navigationItem.locationString()

    companion object {
        fun addClausesToCallDefinition(
                call: Call,
                name: String,
                arityRange: kotlin.ranges.IntRange,
                callDefinitionByNameArity: MutableMap<NameArity, CallDefinition>,
                modular: Modular,
                time: Timed.Time,
                callDefinitionInserter: (CallDefinition) -> Unit
        ) {
            for (arity in arityRange) {
                val nameArity = NameArity(name, arity)

                callDefinitionByNameArity.computeIfAbsent(nameArity) { (name, arity) ->
                    CallDefinition(
                            modular,
                            time,
                            name,
                            arity
                    ).also {
                        callDefinitionInserter(it)
                    }
                }.clause(call)
            }
        }

        fun callChildren(modular: Modular, call: Call): Array<TreeElement> {
            val childCalls = call.macroChildCalls()
            return childCallTreeElements(modular, childCalls)
        }

        @JvmStatic
        fun elementDescription(call: Call, location: ElementDescriptionLocation): String? =
                when(location) {
                    UsageViewLongNameLocation.INSTANCE -> {
                        val enclosingCall = call.enclosingMacroCall()
                        // indirect recursion through ElementDescriptionUtil.getElementDescription because it is @NotNull and will
                        // default to element text when not implemented, so a bug, but not an error will result.
                        val relative = ElementDescriptionUtil.getElementDescription(call, UsageViewShortNameLocation.INSTANCE)

                        if (enclosingCall != null) {
                            val qualified = ElementDescriptionUtil.getElementDescription(enclosingCall, location)
                            "$qualified.$relative"
                        } else {
                            relative
                        }
                    }
                    UsageViewShortNameLocation.INSTANCE -> call.name
                    UsageViewTypeLocation.INSTANCE -> "module"
                    else -> null
                }

        @JvmStatic
        fun `is`(call: Call): Boolean =
                (call.isCallingMacro(KERNEL, DEFMODULE, 2) &&
                        /**
                         * See https://github.com/KronicDeth/intellij-elixir/issues/1301
                         *
                         * Check that the this is not the redefinition of defmodule in distillery
                         */
                        ApplicationManager
                                .getApplication()
                                .runReadAction(Computable {
                                    call
                                            .parent.let { it  as? Arguments }
                                            ?.parent?.let { it as? Call }?.let { isMacro(it) }
                                }) != true) ||
                        call.isCalling(MODULE, CREATE, 3)

        @JvmStatic
        fun nameIdentifier(call: Call): PsiElement? = call.primaryArguments()?.firstOrNull()?.stripAccessExpression()

        @Contract(pure = true)
        private fun childCallTreeElements(modular: Modular, childCalls: Array<Call>?): Array<TreeElement> {
            var treeElements: Array<TreeElement>? = null

            if (childCalls != null) {
                val childCallQueue = ArrayDeque(Arrays.asList(*childCalls))
                val length = childCalls.size
                val treeElementList = ArrayList<TreeElement>(length)
                val functionByNameArity = FunctionByNameArity(length, treeElementList, modular)
                val macroByNameArity = MacroByNameArity(length, treeElementList, modular)
                val overridableSet = HashSet<Overridable>()
                val useSet = HashSet<org.elixir_lang.structure_view.element.Use>()

                while (!childCallQueue.isEmpty()) {
                    val childCall = childCallQueue.remove()

                    when {
                        childCall is Or -> childCallQueue.addAll(orChildCallList(childCall as Or))
                        Callback.`is`(childCall) -> treeElementList.add(Callback(modular, childCall))
                        Delegation.`is`(childCall) -> functionByNameArity.addDelegationToTreeElementList(childCall)
                        Exception.`is`(childCall) -> functionByNameArity.exception = Exception(modular, childCall)
                        org.elixir_lang.psi.CallDefinitionClause.isFunction(childCall) -> functionByNameArity.addClausesToCallDefinition(childCall)
                        CallDefinitionSpecification.`is`(childCall) -> functionByNameArity.addSpecificationToCallDefinition(childCall)
                        Implementation.`is`(childCall) -> treeElementList.add(Implementation(modular, childCall))
                        org.elixir_lang.psi.CallDefinitionClause.isMacro(childCall) -> macroByNameArity.addClausesToCallDefinition(childCall)
                        Module.`is`(childCall) -> treeElementList.add(Module(modular, childCall))
                        Overridable.`is`(childCall) -> {
                            val overridable = Overridable(modular, childCall)
                            overridableSet.add(overridable)
                            treeElementList.add(overridable)
                        }
                        Protocol.`is`(childCall) -> treeElementList.add(Protocol(modular, childCall))
                        QuoteMacro.`is`(childCall) -> treeElementList.add(Quote(modular, childCall))
                        Structure.`is`(childCall) -> treeElementList.add(Structure(modular, childCall))
                        Type.`is`(childCall) -> treeElementList.add(Type.fromCall(modular, childCall))
                        org.elixir_lang.psi.Use.`is`(childCall) -> {
                            val use = org.elixir_lang.structure_view.element.Use(modular, childCall)
                            useSet.add(use)
                            treeElementList.add(use)
                        }
                        Unknown.`is`(childCall) -> // Should always be last since it will match all macro calls
                            treeElementList.add(Unknown(modular, childCall))
                    }
                }

                for (overridable in overridableSet) {
                    for (treeElement in overridable.children) {
                        (treeElement as CallReference).let { callReference ->
                            callReference.arity()?.let { arity ->
                                callReference
                                        .name()
                                        .let { name -> NameArity(name, arity) }
                                        .let { nameArity ->
                                            functionByNameArity[nameArity]?.apply {
                                                isOverridable = true
                                            }
                                        }
                            }
                        }
                    }
                }

                val useCollection = HashSet<TreeElement>(useSet.size)
                useCollection.addAll(useSet)
                val nodesFromUses = Used.provideNodesFromChildren(useCollection)
                val useFunctionByNameArity = Used.functionByNameArity(nodesFromUses)

                for ((useNameArity, useFunction) in useFunctionByNameArity) {
                    if (useFunction.isOverridable) {
                        functionByNameArity[useNameArity]?.override = true
                    }
                }

                treeElements = treeElementList.toTypedArray()
            }

            return treeElements ?: emptyArray()
        }

        private fun orChildCallList(or: Or): List<Call> {
            val childCallList = ArrayList<Call>()

            (or.leftOperand() as? Call)?.run {
                childCallList.add(this)
            }

            (or.rightOperand() as? Call)?.run {
                childCallList.add(this)
            }

            return childCallList
        }
    }

}

private inline fun CallDefinition.also(block: (CallDefinition) -> Unit): CallDefinition {
    block(this)
    return this
}

