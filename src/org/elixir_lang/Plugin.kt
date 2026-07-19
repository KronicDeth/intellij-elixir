package org.elixir_lang

import com.intellij.ide.plugins.cl.PluginAwareClassLoader

/**
 * Version of this plugin, read from the descriptor attached to the plugin's own classloader.
 *
 * `PluginManagerCore.getPlugin(PluginId)` and `PluginManager.findEnabledPlugin(PluginId)` are both
 * `@ApiStatus.Internal` as of 262, so the sanctioned route to our own descriptor is the classloader,
 * which is what `PluginManager.getPluginByClass` does internally. [PluginAwareClassLoader] itself is
 * only `@ApiStatus.NonExtendable`, and `getPluginDescriptor()` is unannotated, across 253-262.
 *
 * The fallback covers tests, where plugin classes are loaded by a plain classloader.
 */
fun pluginVersion(): String =
    (object {}.javaClass.classLoader as? PluginAwareClassLoader)?.pluginDescriptor?.version ?: "unknown"
