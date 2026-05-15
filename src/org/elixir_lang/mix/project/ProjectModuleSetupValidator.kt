package org.elixir_lang.mix.project

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.util.concurrency.ThreadingAssertions
import org.elixir_lang.mixContentRoots

/**
 * Validates each Elixir Mix module's folder marks against the canonical specification defined in
 * [CANONICAL_FOLDER_MARKS] (which mirrors [org.elixir_lang.mix.Project.addFolders]).
 *
 * Threading: follows the same contract as `detectModuleSdkIssues()` in `ElixirSdkStatusWidget` -
 * accesses [ModuleRootManager] which requires read access.  Callers must either hold a read lock
 * or call this inside a `readAction { }` block.
 */
object ProjectModuleSetupValidator {

    /**
     * A single folder-mark discrepancy found in a Mix module.
     *
     * @param moduleName          The IntelliJ module name (e.g. `"my_app"`).
     * @param folderRelativePath  The path relative to the content root (e.g. `"test"`).
     * @param folderMark        The mark that `addFolders()` would apply.
     * @param currentState        Human-readable description of the actual state
     *                            (`"unmarked"`, `"sources"`, `"test sources"`, or `"excluded"`).
     */
    data class FolderMarkIssue(
        val moduleName: String,
        val folderRelativePath: String,
        val folderMark: FolderMark,
        val currentState: String,
    )

    /**
     * Inspects every module in [project] whose content root contains a `mix.exs` and returns a
     * list of folder-mark discrepancies relative to [CANONICAL_FOLDER_MARKS].
     *
     * **Threading**: must be called off the EDT (background thread).  The method accesses
     * [ModuleRootManager] which requires read access; the caller must either hold a read lock or
     * call this inside a `readAction { }` block.  The current call site in
     * `ElixirSdkStatusWidget.detectSdkStatus()` follows the same contract as
     * `detectModuleSdkIssues()` - both run in the widget's update cycle.
     */
    fun detectFolderMarkIssues(project: Project): List<FolderMarkIssue> {
        ThreadingAssertions.assertBackgroundThread()

        val issues = mutableListOf<FolderMarkIssue>()

        for (module in ModuleManager.getInstance(project).modules) {
            for (mixContentRoot in module.mixContentRoots()) {
                val contentEntry = mixContentRoot.contentEntry
                val root = mixContentRoot.root
                val actualMarks = actualMarks(contentEntry)
                for (canonical in CANONICAL_FOLDER_MARKS) {
                    val url = "${root.url}/${canonical.relativePath}"

                    val actualMark = actualMarks[url]

                    if (actualMark != canonical.folderMark) {
                        // Only warn when the folder actually exists on disk (via VFS).
                        // Non-existent folders with no mark are expected -- the reconfigure action
                        // applies marks by URL so they're ready when the folder appears later.
                        if (root.findFileByRelativePath(canonical.relativePath) == null) continue

                        issues.add(
                            FolderMarkIssue(
                                moduleName = module.name,
                                folderRelativePath = canonical.relativePath,
                                folderMark = canonical.folderMark,
                                currentState = actualMark?.displayName?.lowercase() ?: "unmarked",
                            )
                        )
                    }
                }
            }
        }

        return issues
    }

    // Build a unified map: folder URL → its actual mark (using the same
    // FolderMark enum so comparison is a simple equality check).
    private fun actualMarks(contentEntry: ContentEntry): Map<String, FolderMark> = buildMap {
        for (sourceFolder in contentEntry.sourceFolders) {
            put(sourceFolder.url, if (sourceFolder.isTestSource) FolderMark.TEST_SOURCES else FolderMark.SOURCES)
        }
        for (excludeUrl in contentEntry.excludeFolderUrls) {
            put(excludeUrl, FolderMark.EXCLUDED)
        }
    }
}
