package org.elixir_lang.tool_manager

/**
 * A single tool version entry as resolved by a tool manager (mise, asdf, etc.).
 *
 * @param version      The version string as reported by the tool manager (e.g. `"1.17.3-otp-27"`).
 * @param installPath  Absolute path to the tool manager's local install directory for this version.
 * @param installed    Whether the tool is actually installed (vs. just configured but not downloaded).
 */
data class ToolEntry(
    val version: String,
    val installPath: String,
    val installed: Boolean,
)
