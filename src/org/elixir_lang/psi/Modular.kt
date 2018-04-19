package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.structure_view.element.CallDefinitionClause

object Modular {
    @JvmStatic
    fun callDefinitionClauseCallWhile(modular: Call, function: (Call) -> Boolean): Boolean {
        val childCalls = modular.macroChildCalls()
        var keepProcessing = true

        for (childCall in childCalls) {
            if (CallDefinitionClause.`is`(childCall) && !function(childCall)) {
                keepProcessing = false

                break
            }
        }

        return keepProcessing
    }

    @JvmStatic
    fun forEachCallDefinitionClauseNameIdentifier(
            modular: Call,
            functionName: String?,
            resolvedFinalArity: Int,
            function: Function<PsiElement, Boolean>
    ) {
        callDefinitionClauseCallWhile(modular, functionName, resolvedFinalArity) { call ->
            var keepProcessing = true

            if (call is Named) {
                val nameIdentifier = call.nameIdentifier

                if (nameIdentifier != null && !function.`fun`(nameIdentifier)) {
                    keepProcessing = false
                }
            }

            keepProcessing
        }
    }

    private fun callDefinitionClauseCallWhile(modular: Call,
                                              functionName: String?,
                                              resolvedFinalArity: Int,
                                              function: (Call) -> Boolean) {
        if (functionName != null) {
            callDefinitionClauseCallWhile(modular) { callDefinitionClauseCall ->
                val nameArityRange = CallDefinitionClause.nameArityRange(callDefinitionClauseCall)
                var keepProcessing = true

                if (nameArityRange != null) {
                    val name = nameArityRange.first

                    if (name != null && name == functionName) {
                        val arityRange = nameArityRange.second

                        if (arityRange.containsInteger(resolvedFinalArity) && !function(callDefinitionClauseCall)) {
                            keepProcessing = false
                        }
                    }
                }

                keepProcessing
            }
        }
    }
}
