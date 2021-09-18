package org.elixir_lang.psi.ex_unit.kase

import com.intellij.psi.ResolveState
import org.elixir_lang.ArityRange
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.ex_unit.Case.resolvesTo

object Describe {
    @JvmStatic
    fun `is`(call: Call, state: ResolveState): Boolean =
            call.functionName() == NAME &&
                    call.resolvedFinalArity() in ARITY_RANGE && resolvesTo(call, state)

    private val NAME: String = "describe"
    private val ARITY_RANGE: ArityRange = 1..2
}
