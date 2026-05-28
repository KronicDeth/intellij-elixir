package org.elixir_lang.action

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.*
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.mix.project.CANONICAL_FOLDER_MARKS
import org.elixir_lang.mix.project.FolderMark
import org.elixir_lang.isElixirMixModule
import org.elixir_lang.sdk.elixir.findAllActiveElixirSdks
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType

private val LOG = logger<ReconfigureModuleSetupAction>()

/**
 * Additively applies canonical folder marks from [CANONICAL_FOLDER_MARKS] and fixes module SDK
 * references for all Elixir Mix modules in the project.
 *
 * **Additive behavior**: Existing user-customized source roots and exclusions are preserved.
 * The action only *adds* missing canonical marks and fixes dangling/mismatched module SDK entries.
 *
 * Registered as `Elixir.ReconfigureModuleSetup` in `plugin.xml`.
 *
 * @see org.elixir_lang.mix.Project.addFolders
 * @see org.elixir_lang.mix.project.ProjectModuleSetupValidator
 */
class ReconfigureModuleSetupAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        // Enable when at least one module has a mix.exs in its content root,
        // or any module has an active Elixir SDK configured.
        val hasElixirModule = ModuleManager.getInstance(project).modules.any { it.isElixirMixModule() }
        val hasElixirSdk = findAllActiveElixirSdks(project).isNotEmpty()

        e.presentation.isEnabledAndVisible = hasElixirModule || hasElixirSdk
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        val hasRootMixExs = project.basePath?.let { basePath ->
            LocalFileSystem.getInstance().findFileByPath(basePath)?.findChild("mix.exs")
        } != null

        var modulesReconfigured = 0
        var foldersAdded = 0
        var sdksFixed = 0

        WriteCommandAction.runWriteCommandAction(project, "Reconfigure Elixir Modules", null, {
            // Collect all content entry URLs upfront so umbrella sub-app detection can skip
            // sub-apps that are already covered by their own content entry.
            val allContentEntryUrls: Set<String> = ModuleManager.getInstance(project).modules
                .flatMap { ModuleRootManager.getInstance(it).contentEntries.map { ce -> ce.url } }
                .toSet()

            for (module in ModuleManager.getInstance(project).modules) {
                val rootManager = ModuleRootManager.getInstance(module)
                val model = rootManager.modifiableModel

                if (!module.isElixirMixModule()) {
                    model.dispose()
                    continue
                }

                var moduleChanged = false

                for (contentEntry in model.contentEntries) {
                    val root = contentEntry.file ?: continue
                    if (root.findChild("mix.exs") == null) continue  // Not a Mix app

                    val added = addMissingFolderMarks(contentEntry, root)
                    if (added > 0) {
                        foldersAdded += added
                        moduleChanged = true
                    }

                    // Also apply marks for umbrella sub-apps that are not already covered by
                    // their own content entry.
                    val umbrellaAdded = addMissingUmbrellaSubAppFolderMarks(contentEntry, root, allContentEntryUrls)
                    if (umbrellaAdded > 0) {
                        foldersAdded += umbrellaAdded
                        moduleChanged = true
                    }
                }

                if (hasRootMixExs && projectSdk != null && projectSdk.sdkType is ElixirSdkType) {
                    if (fixModuleSdk(model, projectSdk)) {
                        sdksFixed++
                        moduleChanged = true
                    }
                }

                if (moduleChanged) {
                    modulesReconfigured++
                }

                if (model.isChanged) {
                    model.commit()
                } else {
                    model.dispose()
                }
            }
        })

        // Show result notification
        showResultNotification(e, modulesReconfigured, foldersAdded, sdksFixed)
    }

    /**
     * Additively applies missing source folder marks and exclusions from [CANONICAL_FOLDER_MARKS]
     * to [contentEntry] by URL (even if the backing directory does not yet exist on disk).
     * Returns the number of marks added.
     */
    private fun addMissingFolderMarks(contentEntry: ContentEntry, root: VirtualFile): Int =
        applyCanonicalMarksForBaseUrl(contentEntry, root.url)

    /**
     * Scans the `apps/` directory under [root] for umbrella sub-apps that are NOT already
     * covered by their own content entry (i.e. whose URL is absent from [allContentEntryUrls]),
     * and additively applies missing canonical folder marks for each such sub-app on [contentEntry].
     *
     * Returns the total number of marks added across all discovered sub-apps.
     */
    private fun addMissingUmbrellaSubAppFolderMarks(
        contentEntry: ContentEntry,
        root: VirtualFile,
        allContentEntryUrls: Set<String>,
    ): Int {
        val appsDir = root.findChild("apps") ?: return 0

        var added = 0

        for (subAppDir in appsDir.children) {
            if (!subAppDir.isDirectory) continue
            if (subAppDir.findChild("mix.exs") == null) continue

            // Skip sub-apps already covered by their own content entry.
            if (subAppDir.url in allContentEntryUrls) continue

            added += applyCanonicalMarksForBaseUrl(contentEntry, "${root.url}/apps/${subAppDir.name}")
        }

        return added
    }

    /**
     * Applies [CANONICAL_FOLDER_MARKS] to [contentEntry] for every canonical folder relative to
     * [baseUrl].  Existing correct marks are left untouched; incorrect source marks are replaced;
     * missing exclusions are added.  Returns the number of marks added or replaced.
     *
     * This is the single implementation shared by [addMissingFolderMarks] (top-level content root)
     * and [addMissingUmbrellaSubAppFolderMarks] (umbrella sub-apps).
     */
    private fun applyCanonicalMarksForBaseUrl(contentEntry: ContentEntry, baseUrl: String): Int {
        val existingSourceUrls = contentEntry.sourceFolders.associate {
            it.url to if (it.isTestSource) FolderMark.TEST_SOURCES else FolderMark.SOURCES
        }
        val existingExcludeUrls = contentEntry.excludeFolderUrls.toSet()

        var added = 0

        for (canonical in CANONICAL_FOLDER_MARKS) {
            val url = "$baseUrl/${canonical.relativePath}"

            when (canonical.folderMark) {
                FolderMark.SOURCES, FolderMark.TEST_SOURCES -> {
                    val existingMark = existingSourceUrls[url]
                    if (existingMark != canonical.folderMark) {
                        // Remove incorrect mark if present (e.g. Sources instead of Test Sources)
                        if (existingMark != null) {
                            contentEntry.sourceFolders
                                .find { it.url == url }
                                ?.let { contentEntry.removeSourceFolder(it) }
                        }
                        contentEntry.addSourceFolder(url, canonical.folderMark == FolderMark.TEST_SOURCES)
                        added++
                    }
                }

                FolderMark.EXCLUDED -> {
                    if (url !in existingExcludeUrls) {
                        contentEntry.addExcludeFolder(url)
                        added++
                    }
                }
            }
        }

        return added
    }

    /**
     * Fixes the module SDK entry to inherit from [projectSdk] when the current entry is
     * dangling (references a non-existent SDK) or mismatches the project SDK.
     *
     * Returns `true` if a fix was applied.
     */
    private fun fixModuleSdk(model: ModifiableRootModel, projectSdk: com.intellij.openapi.projectRoots.Sdk): Boolean {
        var fixed = false

        for (entry in model.orderEntries) {
            if (entry !is JdkOrderEntry) continue

            val jdkName = entry.jdkName ?: continue  // skip entries without an SDK name
            val resolvedSdk = entry.jdk

            val isDangling = resolvedSdk == null
            val isMismatch = resolvedSdk != null && resolvedSdk.sdkType is ElixirSdkType && resolvedSdk != projectSdk

            if (isDangling || isMismatch) {
                LOG.info("Fixing module SDK: ${if (isDangling) "dangling '$jdkName'" else "mismatch"} → inherit project SDK")
                model.inheritSdk()
                fixed = true
                break  // inheritSdk() replaces the entire SDK entry; no further iteration needed
            }
        }

        return fixed
    }

    private fun showResultNotification(e: AnActionEvent, modulesReconfigured: Int, foldersAdded: Int, sdksFixed: Int) {
        val project = e.project ?: return

        if (modulesReconfigured == 0 && sdksFixed == 0) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Elixir")
                .createNotification(
                    "Elixir Module setup",
                    "All modules are configured correctly.",
                    NotificationType.INFORMATION
                )
                .notify(project)
            return
        }

        val parts = mutableListOf<String>()
        if (foldersAdded > 0) {
            parts.add("$foldersAdded folder ${StringUtil.pluralize("mark", foldersAdded)} applied")
        }
        if (sdksFixed > 0) {
            parts.add("$sdksFixed module ${StringUtil.pluralize("SDK", sdksFixed)} fixed")
        }
        val moduleWord = StringUtil.pluralize("module", modulesReconfigured)
        val message = "Reconfigured $modulesReconfigured $moduleWord: ${parts.joinToString(", ")}."

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Elixir Module setup",
                message,
                NotificationType.INFORMATION
            )
            .notify(project)

        LOG.info("ReconfigureModuleSetup: $message")
    }
}
