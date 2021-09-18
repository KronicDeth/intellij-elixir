package org.elixir_lang.psi.ex_unit

import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.resolvesToModularName

object Case {
    fun resolvesTo(call: Call, state: ResolveState): Boolean =
            resolvesToModularName(call, state, "ExUnit.Case")
}
