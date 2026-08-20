package org.elixir_lang

import com.intellij.ide.plugins.cl.PluginAwareClassLoader
import com.intellij.openapi.extensions.PluginId

object Plugin {
    val ID: PluginId = PluginId.getId("org.elixir_lang")

    /**
     * The installed plugin's version, or `null` when running from sources or tests, where plugin classes are loaded
     * by a plain classloader instead of a [PluginAwareClassLoader].
     * Replace with `PluginDetailsService` when the `sinceBuild` reaches 2026.2.
     */
    val version: String?
        get() = (Plugin::class.java.classLoader as? PluginAwareClassLoader)?.pluginDescriptor?.version
}
