package org.elixir_lang.psi

import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.QUOTE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.macroChildCallSequence

object QuoteMacro {
    fun callDefinitionClauseCallWhile(quoteCall: Call, keepProcessing: (Call) -> Boolean): Boolean {
        var accumulatorKeepProcessing = true

        for (childCall in quoteCall.macroChildCallSequence()) {
            accumulatorKeepProcessing = when {
                CallDefinitionClause.`is`(childCall) -> keepProcessing(childCall)
                Import.`is`(childCall) -> Import.callDefinitionClauseCallWhile(childCall, keepProcessing)
                Use.`is`(childCall) -> Use.callDefinitionClauseCallWhile(childCall, keepProcessing)
                else -> true
            }

            if (!accumulatorKeepProcessing) {
                break
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
