package org.elixir_lang.tool_manager

/**
 * A single row in an [SdkVersionTable], comparing the configured SDK version against the
 * version a tool manager reports for the same module root.
 *
 * @param label             Display name for the tool, e.g. `"Elixir"` or `"Erlang"`.
 * @param configuredVersion Version currently configured in the IDE SDK, or `null` if no SDK is set.
 * @param toolManagerVersion Version reported by the tool manager, or `null` if unavailable.
 * @param isMismatch        `true` when the two versions differ and a warning should be surfaced.
 */
data class SdkVersionRow(
    val label: String,
    val configuredVersion: String?,
    val toolManagerVersion: String?,
    val isMismatch: Boolean,
)

/**
 * A per-module comparison table produced by [ToolManagerSdkChecker.detectMismatchIssues].
 * Contains one [SdkVersionRow] for every tool (Elixir, Erlang) that the tool manager reported,
 * including rows where the versions match (so the notification table shows the full picture).
 *
 * @param moduleName       Name of the IntelliJ module this table belongs to.
 * @param toolManagerName  Stable name of the tool manager that produced this table (e.g. `"mise"`).
 * @param rows             Comparison rows; at least one has [SdkVersionRow.isMismatch] == `true`.
 */
data class SdkVersionTable(
    val moduleName: String,
    val toolManagerName: String,
    val rows: List<SdkVersionRow>,
)
