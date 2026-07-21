package org.elixir_lang

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId

object Plugin {
    val ID: PluginId = PluginId.getId("org.elixir_lang")

    val version: String
        get() = PluginManager.getInstance().findEnabledPlugin(ID)!!.version
}
