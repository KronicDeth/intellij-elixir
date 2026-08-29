package org.elixir_lang.psi

import com.intellij.openapi.util.Pair
import org.elixir_lang.NameArity
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArity
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module

object Exception {
    val EXCEPTION = NameArity("exception", 1)
    val MESSAGE = NameArity("message", 1)

    val NAME_ARITY_LIST = listOf(
            EXCEPTION,
            MESSAGE
    )

    // finalArity, not resolvedFinalArity via isCalling(..., 1) - getChildren() reads finalArguments().

    @JvmStatic
    fun `is`(call: Call): Boolean {
        return call.isCalling(Module.KERNEL, Function.DEFEXCEPTION) && call.finalArity() == 1
    }

    fun isCallback(nameArity: Pair<String, Int>): Boolean =
            NAME_ARITY_LIST.any { callbackNameArity ->
                nameArity.second == callbackNameArity.arity && nameArity.first == callbackNameArity.name
            }
}
