package org.elixir_lang.psi

import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.ALIAS
import org.elixir_lang.psi.call.name.Module.KERNEL

object Alias {
    @JvmStatic
    fun `is`(call: Call): Boolean =
            call.isCalling(KERNEL, ALIAS)
}
