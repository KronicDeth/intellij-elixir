package org.elixir_lang.tool_manager

import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import org.elixir_lang.tool_manager.mise.MiseToolManager

/**
 * Central list of all built-in [ElixirToolManager] implementations, in priority order.
 *
 * When multiple managers are enabled, [ToolManagerSdkChecker] queries them in this order
 * and uses the first non-null result per content root.
 *
 * To add a new built-in tool manager, add it here. Nothing else needs to change.
 */
val allBuiltInToolManagers: List<ElixirToolManager> = listOf(
    MiseToolManager(),
)
