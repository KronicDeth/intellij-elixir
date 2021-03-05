package org.elixir_lang.psi

import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.QUOTE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.scope.hasBeenVisited
import org.elixir_lang.psi.scope.putVisitedElement

object QuoteMacro {
    fun callDefinitionClauseCallWhile(quoteCall: Call, resolveState: ResolveState, keepProcessing: (Call, ResolveState) -> Boolean): Boolean {
        var accumulatorKeepProcessing = true

        for (childCall in quoteCall.macroChildCallSequence()) {
            if (!resolveState.hasBeenVisited(childCall)) {
                accumulatorKeepProcessing = when {
                    CallDefinitionClause.`is`(childCall) -> {
                        val childResolveState = resolveState.putVisitedElement(childCall)

                        keepProcessing(childCall, childResolveState)
                    }
                    Import.`is`(childCall) -> {
                        val childResolveState = resolveState.putVisitedElement(childCall)

                        Import.callDefinitionClauseCallWhile(childCall, childResolveState, keepProcessing)
                    }
                    Unquote.`is`(childCall) -> {
                        val childResolveState = resolveState.putVisitedElement(childCall)

                        Unquote.callDefinitionClauseCallWhile(childCall, childResolveState, keepProcessing)
                    }
                    Use.`is`(childCall) -> {
                        val childResolveState = resolveState.putVisitedElement(childCall)

                        Use.callDefinitionClauseCallWhile(childCall, childResolveState, keepProcessing)
                    }
                    else -> true
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
