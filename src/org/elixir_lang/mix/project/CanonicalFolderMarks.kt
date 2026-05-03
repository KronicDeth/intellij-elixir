package org.elixir_lang.mix.project

import com.intellij.openapi.project.ProjectBundle

/**
 * The mark that a canonical Mix project folder should have, matching the specification enforced by
 * [org.elixir_lang.mix.Project.addFolders].
 *
 * The [displayName] is resolved from [ProjectBundle] to match IntelliJ's Project Structure UI
 * terminology exactly (e.g. the "Mark Directory as" context menu labels).  This avoids hardcoded
 * strings that could drift if the platform renames these labels.
 *
 * Verified present in platform tags `idea/253.28294.334` (2025.3.2) and `idea/261.23567.138`
 * (2026.1.1) on 2026-05-03.
 */
enum class FolderMark(private val bundleKey: String) {
    SOURCES("module.toggle.sources.action"),
    TEST_SOURCES("module.toggle.test.sources.action"),
    EXCLUDED("module.toggle.excluded.action");

    val displayName: String get() = ProjectBundle.message(bundleKey)
}

/**
 * A folder in a standard Elixir Mix project and the IntelliJ mark that
 * [org.elixir_lang.mix.Project.addFolders] applies to it.
 *
 * @param relativePath  Path relative to the content root (e.g. `"lib"`, `"assets/node_modules/phoenix"`).
 * @param folderMark  The mark that `addFolders()` would apply.
 */
data class CanonicalFolder(
    val relativePath: String,
    val folderMark: FolderMark,
)

/**
 * The single authoritative list of folder marks for an Elixir Mix project.
 *
 * Shared by:
 * - [org.elixir_lang.mix.Project.addFolders] - to apply canonical marks when configuring modules.
 * - [ProjectModuleSetupValidator] - to detect missing/incorrect marks.
 * - `ReconfigureModuleSetupAction` - to additively apply missing marks.
 *
 * If `addFolders()` changes, update this list and both consumers pick up the change automatically.
 */
val CANONICAL_FOLDER_MARKS: List<CanonicalFolder> = listOf(
    // --- Source roots ---
    CanonicalFolder("lib", FolderMark.SOURCES),
    // Pre-Phoenix 1.3 projects used web/ instead of lib/ for source code
    CanonicalFolder("web", FolderMark.SOURCES),
    CanonicalFolder("spec", FolderMark.TEST_SOURCES),
    CanonicalFolder("test", FolderMark.TEST_SOURCES),

    // --- Excluded directories ---
    // Canonical exclusions are applied and validated by URL, even when the directories don't
    // exist on disk yet.
    CanonicalFolder(".elixir_ls", FolderMark.EXCLUDED),
    CanonicalFolder("assets/node_modules/phoenix", FolderMark.EXCLUDED),
    CanonicalFolder("assets/node_modules/phoenix_html", FolderMark.EXCLUDED),
    CanonicalFolder("cover", FolderMark.EXCLUDED),
    CanonicalFolder("deps", FolderMark.EXCLUDED),
    CanonicalFolder("doc", FolderMark.EXCLUDED),
    CanonicalFolder("logs", FolderMark.EXCLUDED),
)
