package org.elixir_lang.psi

import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.UNLESS
import org.elixir_lang.psi.call.name.Module.KERNEL

object Unless {
    /**
     * Whether `call` is an
     *
     * ```
     * unless ... do
     * ...
     * end
     * ```
     */
    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, UNLESS, 2)
}
