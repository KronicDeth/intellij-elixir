package org.elixir_lang.tool_manager

import java.nio.file.Path

/**
 * A tool manager that can resolve installed Elixir/Erlang versions for a project directory.
 *
 * Implementations (mise, asdf, …) must:
 * - Return `null` from [resolveVersions] whenever the tool manager is not active for the given
 *   directory (not installed, no config file, subprocess failure, etc.).  This is the normal
 *   "not applicable" signal - callers do not distinguish "not installed" from "no config file".
 * - Be stateless with respect to the project; all context is provided through [resolveVersions].
 *
 * [name] is the **stable persistence key** written to `elixir.xml` via [ToolManagerSettings].
 * Do not change it after the first release that ships this tool manager.
 */
interface ElixirToolManager {
    /** Stable persistence key. Used by [ToolManagerSettings] and as a fallback display label. */
    val name: String

    /** Human-readable label shown in the Settings UI (may be localised in the future). */
    val displayName: String

    /**
     * Resolves tool versions for [contentRoot] by querying the tool manager.
     *
     * **Threading**: must NOT be called on the EDT or under a read lock - implementations
     * may spawn subprocesses or perform file I/O. Call only from [kotlinx.coroutines.Dispatchers.IO].
     *
     * Returns `null` if this tool manager is not applicable for [contentRoot].
     */
    fun resolveVersions(contentRoot: Path): ToolManagerVersions?
}
