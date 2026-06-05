package org.elixir_lang.tool_manager.mise

import org.elixir_lang.mise.Mise
import org.elixir_lang.tool_manager.ElixirToolManager
import org.elixir_lang.tool_manager.ToolManagerVersions
import java.nio.file.Path

/**
 * [ElixirToolManager] implementation backed by [mise](https://mise.jdx.dev/).
 *
 * Delegates to [Mise.resolveVersions] and wraps the result in [MiseToolManagerVersions].
 * All threading constraints of [Mise.resolveVersions] apply here - must not be called on
 * the EDT or under a read lock.
 */
class MiseToolManager : ElixirToolManager {
    override val name: String = NAME
    override val displayName: String = "mise"

    override fun resolveVersions(contentRoot: Path): ToolManagerVersions? =
        Mise.resolveVersions(contentRoot)?.let { MiseToolManagerVersions(it) }

    companion object {
        /** Stable persistence key - do not rename after first release. */
        const val NAME = "mise"
    }
}
