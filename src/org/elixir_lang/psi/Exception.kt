package org.elixir_lang.psi

import com.intellij.openapi.util.Pair
import org.elixir_lang.NameArity
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module

object Exception {
    val NAME_ARITY_LIST = listOf(
            NameArity("exception", 1),
            NameArity("message", 1)
    )

    @JvmStatic
    fun `is`(call: Call): Boolean {
        return call.isCalling(Module.KERNEL, Function.DEFEXCEPTION, 1)
    }

    fun isCallback(nameArity: Pair<String, Int>): Boolean =
            NAME_ARITY_LIST.any { callbackNameArity ->
                nameArity.second == callbackNameArity.arity && nameArity.first == callbackNameArity.name
            }
}
