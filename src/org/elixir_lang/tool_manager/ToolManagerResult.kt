package org.elixir_lang.tool_manager

/**
 * The result of querying a single tool manager for one content root.
 *
 * `null` from [ElixirToolManager.resolveVersions] means the manager is not applicable for that
 * root (not installed, no config file, transient subprocess error, etc.).
 *
 * Non-null results are one of:
 * - [Success] - the manager found installed versions for the root.
 * - [Error]   - the manager is applicable for the root but encountered an actionable error
 *   (e.g. a config file that needs to be trusted).  [Error.description] is a human-readable
 *   explanation already formatted by the concrete manager implementation; the abstract layer and
 *   the IDE widget render it verbatim without needing to know what kind of error it is.
 */
sealed interface ToolManagerResult {

    /**
     * The tool manager successfully resolved versions for the content root.
     */
    data class Success(val versions: ToolManagerVersions) : ToolManagerResult

    /**
     * The tool manager encountered an actionable error for the content root.
     *
     * The error is manager-specific (e.g. an untrusted mise config file, a missing asdf plugin,
     * etc.).  Concrete [ElixirToolManager] implementations are responsible for producing a
     * [description] that explains the problem and tells the user how to fix it.
     *
     * @param toolManagerName  The [ElixirToolManager.name] of the manager that reported this.
     * @param description      Human-readable explanation and fix hint, already formatted by the
     *                         concrete manager.  The widget renders this verbatim.
     */
    data class Error(
        val toolManagerName: String,
        val description: String,
    ) : ToolManagerResult
}
