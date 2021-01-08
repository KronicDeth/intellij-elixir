package org.elixir_lang.structure_view.element.modular

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.navigation.item_presentation.modular.Protocol
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function

/**
 * A protocol definition
 */
class Protocol : Module {
    /**
     *
     * @param call a top-level `Kernel.defprotocol/2` call.
     */
    constructor(call: Call) : super(call)

    /**
     * @param parent the parent [Module] or [org.elixir_lang.structure_view.element.Quote] that scopes
     * `call`.
     * @param call   the `Kernel.defprotocol/2` call nested in `parent`.
     */
    constructor(parent: Modular?, call: Call) : super(parent, call)

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation = Protocol(location(), navigationItem)

    companion object {
        @JvmStatic
        fun `is`(call: Call): Boolean =
                call.isCallingMacro(org.elixir_lang.psi.call.name.Module.KERNEL, Function.DEFPROTOCOL, 2)
    }
}
