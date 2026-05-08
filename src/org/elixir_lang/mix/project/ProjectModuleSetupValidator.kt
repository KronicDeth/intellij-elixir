package org.elixir_lang.mix.project

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.ThreadingAssertions
import org.elixir_lang.mixContentRoots

/**
 * Validates each Elixir Mix module's folder marks against the canonical specification defined in
 * [CANONICAL_FOLDER_MARKS] (which mirrors [org.elixir_lang.mix.Project.addFolders]).
 *
 * Threading: follows the same contract as `detectModuleSdkIssues()` in `ElixirEditorBasedSdkWidget` -
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
     * `ElixirEditorBasedSdkWidget` follows the same contract as `detectModuleSdkIssues()` -
     * both run in the background notification job triggered by rootsChanged.
     */
    fun detectFolderMarkIssues(project: Project): List<FolderMarkIssue> {
        ThreadingAssertions.assertBackgroundThread()

        val issues = mutableListOf<FolderMarkIssue>()

        // Collect all content entry URLs across all modules so the umbrella scan can skip
        // sub-apps that are already covered by their own content entry.
        val allContentEntryUrls: Set<String> = ModuleManager.getInstance(project).modules
            .flatMap { ModuleRootManager.getInstance(it).contentEntries.map { ce -> ce.url } }
            .toSet()

        for (module in ModuleManager.getInstance(project).modules) {
            for (mixContentRoot in module.mixContentRoots()) {
                val contentEntry = mixContentRoot.contentEntry
                val root = mixContentRoot.root
                val actualMarks = actualMarks(contentEntry)

                // --- Top-level canonical mark check ---
                checkCanonicalMarks(module.name, root, "", actualMarks, issues)

                // --- Umbrella sub-app detection ---
                // If this content root looks like an umbrella project (has an apps/ directory
                // containing sub-apps with their own mix.exs), validate folder marks for any
                // sub-app that is NOT already covered by its own content entry.
                detectUmbrellaSubAppIssues(module.name, root, actualMarks, allContentEntryUrls, issues)
            }
        }

        return issues
    }

    /**
     * Scans the `apps/` directory under [root] for umbrella sub-apps and adds [FolderMarkIssue]s
     * for any sub-app whose folders are not correctly marked (as reflected by [actualMarks]).
     *
     * Sub-apps whose directory URL is already present in [allContentEntryUrls] are skipped because
     * they are validated independently via the standard per-content-entry loop.
     */
    private fun detectUmbrellaSubAppIssues(
        moduleName: String,
        root: VirtualFile,
        actualMarks: Map<String, FolderMark>,
        allContentEntryUrls: Set<String>,
        issues: MutableList<FolderMarkIssue>,
    ) {
        val appsDir = root.findChild("apps") ?: return

        for (subAppDir in appsDir.children) {
            if (!subAppDir.isDirectory) continue
            if (subAppDir.findChild("mix.exs") == null) continue

            // Skip if the sub-app already has its own content entry anywhere in the project.
            if (subAppDir.url in allContentEntryUrls) continue

            checkCanonicalMarks(moduleName, root, "apps/${subAppDir.name}", actualMarks, issues)
        }
    }

    /**
     * Checks [CANONICAL_FOLDER_MARKS] for a single content root (or umbrella sub-app) and appends
     * any discrepancies to [issues].
     *
     * @param pathPrefix  Empty string for a top-level content root; `"apps/{name}"` for an
     *                    umbrella sub-app.  The final relative path is
     *                    `"$pathPrefix/${canonical.relativePath}"` (or just `canonical.relativePath`
     *                    when [pathPrefix] is empty).
     */
    private fun checkCanonicalMarks(
        moduleName: String,
        root: VirtualFile,
        pathPrefix: String,
        actualMarks: Map<String, FolderMark>,
        issues: MutableList<FolderMarkIssue>,
    ) {
        for (canonical in CANONICAL_FOLDER_MARKS) {
            val relativePath =
                if (pathPrefix.isEmpty()) canonical.relativePath else "$pathPrefix/${canonical.relativePath}"
            val url = "${root.url}/$relativePath"

            val actualMark = actualMarks[url]

            if (actualMark != canonical.folderMark) {
                // Only warn when the folder actually exists on disk (via VFS).
                // Non-existent folders with no mark are expected - the reconfigure action
                // applies marks by URL so they're ready when the folder appears later.
                if (root.findFileByRelativePath(relativePath) == null) continue

                issues.add(
                    FolderMarkIssue(
                        moduleName = moduleName,
                        folderRelativePath = relativePath,
                        folderMark = canonical.folderMark,
                        currentState = actualMark?.displayName?.lowercase() ?: "unmarked",
                    )
                )
            }
        }
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
