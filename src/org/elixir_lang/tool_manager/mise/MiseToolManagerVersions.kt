package org.elixir_lang.tool_manager.mise

import org.elixir_lang.mise.MiseToolEntry
import org.elixir_lang.mise.MiseVersions
import org.elixir_lang.tool_manager.ToolEntry
import org.elixir_lang.tool_manager.ToolManagerVersions

/**
 * Adapts a [MiseVersions] result (the internal mise wire type) to the generic
 * [ToolManagerVersions] interface consumed by [ToolManagerSdkChecker][org.elixir_lang.tool_manager.ToolManagerSdkChecker].
 */
class MiseToolManagerVersions(miseVersions: MiseVersions) : ToolManagerVersions {
    override val toolManagerName: String = "mise"
    override val elixir: ToolEntry? = miseVersions.elixir?.toToolEntry()
    override val erlang: ToolEntry? = miseVersions.erlang?.toToolEntry()
}

private fun MiseToolEntry.toToolEntry() = ToolEntry(
    version = version,
    installPath = installPath,
    installed = installed,
)
