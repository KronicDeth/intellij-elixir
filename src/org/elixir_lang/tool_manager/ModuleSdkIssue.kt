package org.elixir_lang.tool_manager

/**
 * A per-module SDK issue detected during the project-wide notification scan.
 *
 * Produced by both:
 * - [ToolManagerSdkChecker.detectMismatchIssues] - tool-manager version mismatches.
 * - `ElixirEditorBasedSdkWidget.detectModuleSdkIssues` - dangling SDK references and
 *   module/project SDK disagreements (not tool-manager-specific).
 *
 * @param moduleName  IntelliJ module name.
 * @param issue       Human-readable description of the problem (used in log output and
 *                    as a fallback in multi-module notification text).
 * @param isDangling  `true` when the module references an SDK name that no longer exists
 *                    in the JDK table.  Dangling issues are displayed differently
 *                    (error level, navigation hint) compared to version mismatches.
 */
data class ModuleSdkIssue(
    val moduleName: String,
    val issue: String,
    val isDangling: Boolean,
)
