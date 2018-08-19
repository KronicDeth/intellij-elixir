package org.elixir_lang.navigation.item_presentation.modular

import org.elixir_lang.Icons
import org.elixir_lang.psi.call.Call
import javax.swing.Icon

/**
 * @param location the parent name of the Module that scopes `call`; `null` when scope is `quote`.
 * @param call     a `Kernel.defprotocol/2` call nested in `parent`.
 */
class Protocol(location: String?, call: Call) : Module(location, call) {
    /**
     * The protocol icon
     */
    override fun getIcon(unused: Boolean): Icon = Icons.PROTOCOL

    override fun definer(): String = "defprotocol"
}
