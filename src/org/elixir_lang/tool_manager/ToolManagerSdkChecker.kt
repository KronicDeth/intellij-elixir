package org.elixir_lang.tool_manager

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import com.intellij.ui.EditorNotifications
import com.intellij.util.concurrency.annotations.RequiresReadLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elixir_lang.isElixirModule
import org.elixir_lang.sdk.SdkRegistrar
import org.elixir_lang.sdk.elixir.ElixirSdkLookup
import org.elixir_lang.sdk.elixir.ElixirVersionDetector
import org.elixir_lang.sdk.elixir.sdk
import org.elixir_lang.sdk.erlang.ErlangVersionDetector
import org.elixir_lang.sdk.erlang.Release
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import java.nio.file.Path

private val LOG = logger<ToolManagerSdkChecker>()

/**
 * Handles the tool-manager side of the SDK notification scan:
 * collecting module data, resolving versions from all enabled tool managers, detecting
 * version mismatches, building comparison tables, and configuring SDKs from tool-manager results.
 *
 * The widget is responsible for threading (readAction / Dispatchers.IO / EDT) and for
 * combining the results here with its own non-tool-manager checks (dangling refs, classpath, etc.).
 *
 * @param project       The project this checker operates on.
 * @param toolManagers  Ordered list of all registered tool managers.  The checker queries enabled
 *                      managers in order and uses the first non-null result per content root.
 * @param settings      Project-level enable/disable flags for each tool manager.
 */
class ToolManagerSdkChecker(
    private val project: Project,
    private val toolManagers: List<ElixirToolManager>,
    private val settings: ToolManagerSettings,
) {

    // -------------------------------------------------------------------------
    // Public data types
    // -------------------------------------------------------------------------

    /**
     * Per-module snapshot of the data needed to compare configured SDK versions against
     * tool-manager-resolved versions.  Collected inside a read action.
     */
    data class ModuleCheckData(
        val moduleName: String,
        /** Elixir SDK configured for the module (if any). */
        val elixirSdk: Sdk?,
        /** Home path of the Internal Erlang SDK paired with [elixirSdk] (if any).
         *  Used in the IO phase to read the full OTP release version from `OTP_VERSION`. */
        val erlangSdkHomePath: String?,
        /** First content root of the module; used as the working directory for tool-manager queries. */
        val contentRoot: Path?,
    )

    // -------------------------------------------------------------------------
    // Phase 1 - model data (call inside readAction)
    // -------------------------------------------------------------------------

    /**
     * Collects per-module data needed by the tool-manager scan.
     * Must be called inside a `readAction { }` block.
     */
    @RequiresReadLock
    fun collectModuleCheckData(): List<ModuleCheckData> {
        return ModuleManager.getInstance(project).modules
            .filter { it.isElixirModule() }
            .map { module ->
                val elixirSdk = ElixirSdkLookup.resolve(module).sdk
                val erlangSdkHomePath = elixirSdk
                    ?.let { (it.sdkAdditionalData as? SdkAdditionalData)?.getErlangSdk() }
                    ?.homePath
                val contentRoot = ModuleRootManager.getInstance(module)
                    .contentRoots
                    .firstOrNull()
                    ?.toNioPathOrNull()
                LOG.trace("collectModuleCheckData: module='${module.name}' elixirSdk='${elixirSdk?.name}' contentRoot=$contentRoot")
                ModuleCheckData(
                    moduleName = module.name,
                    elixirSdk = elixirSdk,
                    erlangSdkHomePath = erlangSdkHomePath,
                    contentRoot = contentRoot,
                )
            }
    }

    // -------------------------------------------------------------------------
    // Phase 2 - IO (call inside Dispatchers.IO, outside read lock)
    // -------------------------------------------------------------------------

    /**
     * Queries all enabled tool managers for each path in [contentRoots], returning the first non-null
     * result per content root (priority order matches [toolManagers]).
     *
     * Must NOT be called on the EDT or under a read lock - tool managers may spawn subprocesses.
     */
    fun resolveVersions(contentRoots: List<Path>): Map<Path, ToolManagerVersions?> {
        val enabledManagers = toolManagers.filter { settings.isEnabled(it) }
        return contentRoots.associateWith { contentRoot ->
            enabledManagers.firstNotNullOfOrNull { manager ->
                LOG.trace("resolveVersions: trying '${manager.name}' for $contentRoot")
                manager.resolveVersions(contentRoot).also { result ->
                    LOG.trace("resolveVersions: '${manager.name}' returned $result for $contentRoot")
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Phase 3 - pure analysis (no platform access needed)
    // -------------------------------------------------------------------------

    /**
     * Detects version mismatches between configured SDKs and tool-manager-resolved versions.
     *
     * For each module that has tool-manager data, this builds an [SdkVersionTable] containing
     * both matching and mismatching rows (so the notification table shows the full picture).
     * A [ModuleSdkIssue] is also emitted for each mismatch (used for notification deduplication
     * and the issue key).
     *
     * @param moduleCheckData              Collected in Phase 1.
     * @param toolManagerVersionsByRoot    Collected in Phase 2.
     * @param elixirVersionBySdk           Canonical Elixir version (from `elixir.app`) per SDK.
     * @param erlangReleaseByHomePath      Full OTP [Release] per Erlang SDK home path.
     * @param elixirVersionByInstallPath   Canonical Elixir version per tool-manager install path.
     *
     * @return Pair of (issues list, module-name → SdkVersionTable map).
     */
    fun detectMismatchIssues(
        moduleCheckData: List<ModuleCheckData>,
        toolManagerVersionsByRoot: Map<Path, ToolManagerVersions?>,
        elixirVersionBySdk: Map<Sdk, String?>,
        erlangReleaseByHomePath: Map<String, Release?>,
        elixirVersionByInstallPath: Map<String, String?>,
    ): Pair<List<ModuleSdkIssue>, Map<String, SdkVersionTable>> {
        val issues = mutableListOf<ModuleSdkIssue>()
        val tables = mutableMapOf<String, SdkVersionTable>()

        for (data in moduleCheckData) {
            val contentRoot = data.contentRoot ?: run {
                LOG.trace("detectMismatchIssues: '${data.moduleName}' skipped - no content root")
                continue
            }
            val tmVersions = toolManagerVersionsByRoot[contentRoot] ?: run {
                LOG.trace("detectMismatchIssues: '${data.moduleName}' skipped - no tool manager result")
                continue
            }

            val toolName = tmVersions.toolManagerName
            val rows = mutableListOf<SdkVersionRow>()

            // --- Elixir version ---
            val tmElixir = tmVersions.elixir
            if (tmElixir != null) {
                val configuredVersion = data.elixirSdk?.let { elixirVersionBySdk[it] }
                val tmVersion = elixirVersionByInstallPath[tmElixir.installPath]
                LOG.trace(
                    "detectMismatchIssues: '${data.moduleName}' Elixir " +
                            "configured=$configuredVersion tm=$tmVersion installPath=${tmElixir.installPath}"
                )
                val isMismatch =
                    configuredVersion != null && tmVersion != null && configuredVersion != tmVersion
                if (isMismatch) {
                    LOG.info("'${data.moduleName}': Elixir $configuredVersion ≠ $toolName ${tmElixir.version}")
                    issues.add(
                        ModuleSdkIssue(
                            moduleName = data.moduleName,
                            issue = "Elixir SDK is $configuredVersion but $toolName resolves ${tmElixir.version}",
                            isDangling = false,
                        )
                    )
                }
                rows.add(SdkVersionRow("Elixir", configuredVersion, tmVersion ?: tmElixir.version, isMismatch))
            }

            // --- Erlang OTP version ---
            val tmErlang = tmVersions.erlang
            val erlangRelease = data.erlangSdkHomePath?.let { erlangReleaseByHomePath[it] }
            if (tmErlang != null && erlangRelease != null) {
                val tmMajor = tmErlang.version.substringBefore(".")
                val isMismatch = tmMajor != erlangRelease.otpMajor
                if (isMismatch) {
                    LOG.info("'${data.moduleName}': Erlang OTP ${erlangRelease.otpMajor} ≠ $toolName ${tmErlang.version}")
                    issues.add(
                        ModuleSdkIssue(
                            moduleName = data.moduleName,
                            issue = "Internal Erlang SDK OTP ${erlangRelease.otpMajor} does not match $toolName (${tmErlang.version})",
                            isDangling = false,
                        )
                    )
                }
                rows.add(SdkVersionRow("Erlang", erlangRelease.otpVersion, tmErlang.version, isMismatch))
            }

            if (rows.any { it.isMismatch }) {
                tables[data.moduleName] = SdkVersionTable(data.moduleName, toolName, rows)
            }
        }

        return Pair(issues, tables)
    }

    /**
     * Builds the map of module name → [ToolManagerVersions] for every Elixir module whose
     * content root has an *installed* tool-manager Elixir version.
     *
     * Used to populate the "Configure from <tool manager>" action in notifications.
     */
    fun buildAssignments(
        moduleCheckData: List<ModuleCheckData>,
        toolManagerVersionsByRoot: Map<Path, ToolManagerVersions?>,
    ): Map<String, ToolManagerVersions> {
        val result = LinkedHashMap<String, ToolManagerVersions>()
        for (data in moduleCheckData) {
            val contentRoot = data.contentRoot ?: continue
            val versions = toolManagerVersionsByRoot[contentRoot] ?: continue
            if (versions.elixir?.installed == true) {
                result[data.moduleName] = versions
            }
        }
        return result
    }

    // -------------------------------------------------------------------------
    // IO data collected alongside resolveVersions (convenience helpers)
    // -------------------------------------------------------------------------

    /**
     * Resolves the canonical Elixir version string (from `lib/elixir/ebin/elixir.app`) for every
     * unique install path referenced by [toolManagerVersionsByRoot].
     *
     * Must be called on a background thread outside any read lock.
     */
    fun collectElixirVersionByInstallPath(
        toolManagerVersionsByRoot: Map<Path, ToolManagerVersions?>,
    ): Map<String, String?> {
        return toolManagerVersionsByRoot.values
            .filterNotNull()
            .mapNotNull { it.elixir?.installPath }
            .distinct()
            .associateWith { installPath ->
                ElixirVersionDetector.elixirVersion(installPath, null).also { v ->
                    LOG.trace("collectElixirVersionByInstallPath: '$installPath' → $v")
                }
            }
    }

    /**
     * Reads the full OTP [Release] (major + patch version) for every unique Erlang SDK home path
     * referenced by [moduleCheckData].
     *
     * Must be called on a background thread outside any read lock.
     */
    fun collectErlangReleaseByHomePath(
        moduleCheckData: List<ModuleCheckData>,
    ): Map<String, Release?> {
        return moduleCheckData
            .mapNotNull { it.erlangSdkHomePath }
            .distinct()
            .associateWith { homePath -> ErlangVersionDetector.detectRelease(homePath) }
    }

    // -------------------------------------------------------------------------
    // Phase 4 - SDK configuration (call from EDT action handler)
    // -------------------------------------------------------------------------

    /**
     * Registers Elixir and Erlang SDKs from [assignments] and assigns them to their respective
     * modules.  All work - SDK registration, classpath root setup, and module assignment - is
     * performed inside a **single** write action with module assignment coming **last**.
     *
     * Root setup (`setupSdkPaths` equivalent) is moved inside the write action so that the
     * `applyStateToProject` events fired by `sdkModificator.commitChanges()` (via
     * `GlobalWorkspaceModel.onChanged`) happen *before* the module assignment rather than after
     * it.  The VirtualFile lookups that would normally happen inside `setupSdkPaths` are
     * pre-gathered on the background thread (off EDT) to avoid VFS refresh inside the write lock.
     *
     * Uses [runWithModalProgressBlocking] internally, so this must be called from a blocking
     * context on the EDT (e.g. an `AnAction.actionPerformed` handler).
     *
     * @param assignments  Module name → [ToolManagerVersions] as built by [buildAssignments].
     */
    fun configureSdks(assignments: Map<String, ToolManagerVersions>) {
        val toolName = assignments.values.firstOrNull()?.toolManagerName ?: "tool manager"
        LOG.trace("configureSdks: assignments keys=${assignments.keys}")
        runWithModalProgressBlocking(
            ModalTaskOwner.project(project),
            "Configuring Elixir SDK from $toolName"
        ) {
            // Register unique (erlang, elixir) install-path combinations once each.
            // Deduplication key = elixir install path so that multiple modules sharing the same
            // tool-manager config register the SDK only once.
            val elixirSdkByInstallPath = mutableMapOf<String, Sdk>()

            for (versions in assignments.values) {
                val elixirEntry = versions.elixir ?: continue
                val elixirPath = elixirEntry.installPath
                if (elixirPath in elixirSdkByInstallPath) continue

                val erlangSdk = versions.erlang?.let { erlang ->
                    LOG.trace("configureSdks: registering Erlang SDK at '${erlang.installPath}'")
                    SdkRegistrar.registerOrUpdateErlangSdk(erlang.installPath)
                }
                LOG.trace("configureSdks: registering Elixir SDK at '$elixirPath' erlang='${erlangSdk?.name}'")
                val elixirSdk = SdkRegistrar.registerOrUpdateElixirSdk(
                    homePath = elixirPath,
                    erlangSdk = erlangSdk,
                    project = project,
                ) ?: run {
                    LOG.warn("configureSdks: registerOrUpdateElixirSdk returned null for '$elixirPath'")
                    continue
                }
                LOG.trace("configureSdks: registered Elixir SDK '${elixirSdk.name}' for '$elixirPath'")
                elixirSdkByInstallPath[elixirPath] = elixirSdk
            }

            if (elixirSdkByInstallPath.isEmpty()) {
                LOG.warn("configureSdks: no SDKs registered, skipping module assignment")
                return@runWithModalProgressBlocking
            }

            // Force jdk.table.xml to disk before assigning module SDKs.
            //
            // setupSdkPaths → commitChanges() causes workspace model changes that trigger
            // DelayedProjectSynchronizer to retry (Attempt 2). That retry reads jdk.table.xml
            // to decide which SDKs to keep in the global model. Without this flush the new SDK
            // is still only in memory, so Attempt 2 removes it - undoing the module assignment.
            //
            // Application.saveSettings() synchronously writes all dirty PersistentStateComponents
            // (including ProjectJdkTable → jdk.table.xml) before this method returns, ensuring
            // Attempt 2 finds the newly registered SDK on disk.
            withContext(Dispatchers.IO) {
                LOG.trace("configureSdks: flushing jdk.table.xml before module assignment")
                ApplicationManager.getApplication().saveSettings()
                LOG.trace("configureSdks: jdk.table.xml flush complete")
            }

            edtWriteAction {
                for ((moduleName, versions) in assignments) {
                    val elixirEntry = versions.elixir ?: run {
                        LOG.trace("configureSdks: skipping '$moduleName' - no elixir entry")
                        continue
                    }
                    val elixirSdk = elixirSdkByInstallPath[elixirEntry.installPath] ?: run {
                        LOG.warn("configureSdks: SDK not found for installPath='${elixirEntry.installPath}' (module='$moduleName')")
                        continue
                    }
                    val module = ModuleManager.getInstance(project)
                        .findModuleByName(moduleName)
                        ?.takeIf { !it.isDisposed } ?: run {
                        LOG.warn("configureSdks: module '$moduleName' not found or disposed")
                        continue
                    }
                    val modifiableModel = ModuleRootManager.getInstance(module).modifiableModel
                    var committed = false
                    try {
                        modifiableModel.sdk = elixirSdk
                        modifiableModel.commit()
                        committed = true
                        LOG.info("configureSdks: committed '${elixirSdk.name}' → '$moduleName'")
                    } catch (ex: Exception) {
                        LOG.warn("configureSdks: failed to commit '${elixirSdk.name}' → '$moduleName'", ex)
                    } finally {
                        if (!committed) modifiableModel.dispose()
                    }
                }
            }

            EditorNotifications.getInstance(project).updateAllNotifications()
        }
    }
}
