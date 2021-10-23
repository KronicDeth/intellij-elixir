package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.resolvesToModularName

open class ModuleWalker(val name: String, vararg nameArityRangeWalkers: NameArityRangeWalker) {
    private val nameArityRangeWalkerByName = nameArityRangeWalkers.associateBy { it.nameArityRange.name }

    fun isChild(call: Call, state: ResolveState): Boolean =
            hasChildNameArityRange(call) && resolvesTo(call, state)

    private fun hasChildNameArityRange(call: Call): Boolean =
            walkerWithName(call)?.hasArity(call) ?: false

    protected fun resolvesTo(call: Call, state: ResolveState) =
            resolvesToModularName(call, state, name)

    fun walkChild(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            walkerWithName(call)?.let { nameArityRangeWalkerByName ->
                if (nameArityRangeWalkerByName.hasArity(call)) {
                    nameArityRangeWalkerByName.walk(call, state, keepProcessing)
                } else {
                    true
                }
            } ?: true

    private fun walkerWithName(call: Call): NameArityRangeWalker? =
            call.functionName()?.let {
                nameArityRangeWalkerByName[it]
            }
}
