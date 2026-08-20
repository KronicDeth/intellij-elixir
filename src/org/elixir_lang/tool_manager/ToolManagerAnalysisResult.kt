package org.elixir_lang.tool_manager

import java.nio.file.Path

/**
 * Listener notified when a new raw tool-manager scan completes.
 * Published by [ToolManagerSdkCheckerService] on [ToolManagerSdkCheckerService.SCAN_TOPIC].
 */
fun interface ToolManagerScanListener {
    /** Called with the raw scan results keyed by content root. */
    fun scanCompleted(results: Map<Path, ToolManagerResult?>)
}

/**
 * The fully-analysed output of a tool-manager scan cycle, ready for UI consumers.
 *
 * Produced by [ToolManagerSdkAnalyser] by comparing raw scan results against the currently
 * configured module SDKs.  Published on [ToolManagerSdkAnalyser.ANALYSIS_TOPIC].
 *
 * All fields are immutable snapshots at the time of analysis - safe to consume from any thread.
 *
 * @param tmIssues                   Per-module version-mismatch issues for inclusion in
 *                                   notification messages.
 * @param tmAssignments              Module name → [ToolManagerVersions] for every module that
 *                                   has an *installed* Elixir entry; used to populate the
 *                                   "Configure from <tool manager>" action.
 * @param sdkVersionTables           Per-module version comparison tables for rich notification
 *                                   HTML rendering.
 * @param toolManagerErrors          Actionable tool-manager errors (e.g. untrusted mise config)
 *                                   that prevented version resolution.
 * @param elixirVersionByInstallPath Canonical Elixir version (from `elixir.app`) per install
 *                                   path; used to display the version in notification messages.
 */
data class ToolManagerAnalysisResult(
    val tmIssues: List<ModuleSdkIssue>,
    val tmAssignments: Map<String, ToolManagerVersions>,
    val sdkVersionTables: Map<String, SdkVersionTable>,
    val toolManagerErrors: List<ToolManagerResult.Error>,
    val elixirVersionByInstallPath: Map<String, String?>,
)

/**
 * Listener notified when a completed [ToolManagerAnalysisResult] is available.
 * Published by [ToolManagerSdkAnalyser] on [ToolManagerSdkAnalyser.ANALYSIS_TOPIC].
 */
fun interface ToolManagerAnalysisListener {
    fun analysisCompleted(result: ToolManagerAnalysisResult)
}
