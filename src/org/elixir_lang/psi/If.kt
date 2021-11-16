package org.elixir_lang.psi

import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.IF
import org.elixir_lang.psi.call.name.Module.KERNEL

object If {
    /**
     * Whether `call` is an
     *
     * ```
     * if ... do
     * ...
     * end
     * ```
     */
    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, IF, 2)
}
