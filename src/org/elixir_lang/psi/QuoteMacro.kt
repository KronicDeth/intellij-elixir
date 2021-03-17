package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.QUOTE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.macroChildCallSequence

object QuoteMacro {
    fun treeWalkUp(quoteCall: Call, resolveState: ResolveState, keepProcessing: (PsiElement, ResolveState) -> Boolean): Boolean {
        var accumulatorKeepProcessing = true

        for (childCall in quoteCall.macroChildCallSequence()) {
            if (!resolveState.hasBeenVisited(childCall)) {
                accumulatorKeepProcessing = when {
                    Unquote.`is`(childCall) -> {
                        val childResolveState = resolveState.putVisitedElement(childCall)

                        Unquote.treeWalkUp(childCall, childResolveState, keepProcessing)
                    }
                    Use.`is`(childCall) -> {
                        val childResolveState = resolveState.putVisitedElement(childCall)

                        Use.treeWalkUp(childCall, childResolveState, keepProcessing)
                    }
                    else -> keepProcessing(childCall, resolveState)
                }

                if (!accumulatorKeepProcessing) {
                    break
                }
            }
        }

        return accumulatorKeepProcessing
    }

    @JvmStatic
    fun `is`(call: Call): Boolean {
        // TODO change Elixir.Kernel to Elixir.Kernel.SpecialForms when resolving works
        return call.isCallingMacro(KERNEL, QUOTE, 1) || // without keyword arguments
                call.isCallingMacro(KERNEL, QUOTE, 2) // with keyword arguments
    }

}
