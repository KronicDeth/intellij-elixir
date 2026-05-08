package org.elixir_lang.status_bar_widget

import com.intellij.facet.FacetManager
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.edtWriteAction
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorNotifications
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.ModuleRootListener
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.messages.MessageBusConnection
import java.nio.file.Path
import java.util.concurrent.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elixir_lang.Facet
import org.elixir_lang.Icons
import org.elixir_lang.isElixirModule
import org.elixir_lang.mise.Mise
import org.elixir_lang.mise.MiseVersions
import org.elixir_lang.mix.project.ProjectModuleSetupValidator
import org.elixir_lang.mix.project.ProjectModuleSetupValidator.FolderMarkIssue
import org.elixir_lang.sdk.SdkEbinPaths
import org.elixir_lang.sdk.SdkRegistrar
import org.elixir_lang.sdk.elixir.SdkSettingsOpener
import org.elixir_lang.sdk.elixir.Type
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import kotlin.time.Duration.Companion.milliseconds

private val LOG = logger<ElixirEditorBasedSdkWidget>()

/**
 * Aggregated project-level SDK health status for driving notifications.
 * NOT used for widget display - the widget uses platform [EditorBasedStatusBarPopup.WidgetState]
 * via [ElixirEditorBasedSdkWidget.getWidgetState]. This sealed interface exists solely to power
 * the background notification scan triggered by rootsChanged events.
 */
internal sealed interface SdkStatus {
    data class Configured(
        val elixirSdk: Sdk,
        val erlangSdk: Sdk,
        val elixirVersion: String
    ) : SdkStatus

    data class ClasspathIssue(
        val elixirSdk: Sdk,
        val erlangSdk: Sdk,
        val elixirVersion: String,
        val issues: List<String>
    ) : SdkStatus

    data class InvalidSdk(
        val elixirSdk: Sdk?,
        val elixirVersion: String?,
        val issue: String
    ) : SdkStatus

    data class ModuleSdkError(
        val elixirSdk: Sdk?,
        val elixirVersion: String?,
        val moduleSdkIssues: List<ModuleSdkIssue>
    ) : SdkStatus

    data class FolderMarkWarning(
        val elixirSdk: Sdk,
        val elixirVersion: String,
        val folderMarkIssues: List<FolderMarkIssue>
    ) : SdkStatus

    data object NotConfigured : SdkStatus

    /**
     * No Elixir SDK is configured, but mise has an installed Elixir version for the module's
     * content root. Surfaces a notification with a one-click "Configure from mise" action.
     */
    data class NotConfiguredMiseAvailable(val miseVersions: MiseVersions) : SdkStatus
}

internal data class ModuleSdkIssue(val moduleName: String, val issue: String, val isDangling: Boolean)

@OptIn(FlowPreview::class)
class ElixirEditorBasedSdkWidget(
    project: Project,
    scope: CoroutineScope,
) : EditorBasedStatusBarPopup(project, isWriteableFileRequired = false, scope = scope) {

    companion object {
        const val ID = "ElixirSdkStatus"
    }

    // Track last notified status to avoid spamming duplicate notifications
    @Volatile
    private var lastNotifiedIssueKey: String? = null

    // The currently displayed notification - expired when the issue resolves or changes
    @Volatile
    private var activeNotification: com.intellij.notification.Notification? = null

    // Cached count of Elixir modules in the project.  Updated on rootsChanged (which is the
    // only event that can add/remove modules) so that getWidgetState() remains O(1) and does
    // not iterate all modules on every editor switch.  Initialised to -1 (unknown) so the
    // first getWidgetState() call computes it lazily; subsequent rootsChanged events refresh it.
    @Volatile
    private var cachedElixirModuleCount: Int = -1

    /**
     * Debounced flow for the project-wide notification scan.  The platform already debounces
     * getWidgetState via its own MutableSharedFlow (300ms), but the notification scan is a
     * separate heavier path (O(modules × orderEntries)) that we debounce independently to
     * avoid redundant work during bulk rootsChanged events.
     *
     * Primary trigger: `mix deps.get` → VFS detects new files under `deps/` and `_build/` →
     * [org.elixir_lang.DepsWatcher] syncs libraries → each per-dep `libraryModifiableModel.commit()` and
     * `ModuleRootManager.modifiableModel.commit()` fires a separate rootsChanged event.
     * A Phoenix project with 30 deps can easily generate 60–90+ events in quick succession.
     */
    private val notificationScanRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        // Cancel the notification scan collector when this widget instance is disposed.
        // This mirrors the semantics of the platform's own debounce collector in
        // EditorBasedStatusBarPopup (which uses @Internal cancelOnDispose).  Without this,
        // multiframe widget instances would leave behind active collectors until project close.
        val notificationScanJob = scope.launch {
            notificationScanRequests
                .debounce(500.milliseconds)  // slightly longer than widget debounce since notifications are less urgent
                // Use `collect` (not `collectLatest`) deliberately: if events arrive while a scan
                // is running, they queue in the debounce operator and trigger one follow-up scan
                // after the current one completes + 500ms of quiet.  `collectLatest` would cancel
                // a partially-complete scan and restart, wasting work - safe but inefficient since
                // the notification outcome is idempotent (duplicate issueKeys are suppressed).
                .collect {
                    val modelData = readAction { collectNotificationScanModelData() }
                    val ioData = withContext(Dispatchers.IO) { collectNotificationScanIoData(modelData) }

                    val miseIssues = detectMiseSdkMismatchIssues(
                        modelData.moduleMiseCheckData,
                        ioData.elixirVersionBySdk,
                        ioData.miseByContentRoot,
                    )
                    val miseSuggestion = detectMiseSdkForUnconfiguredModules(
                        modelData.moduleMiseCheckData,
                        ioData.miseByContentRoot,
                    )
                    notifyIfNeeded(
                        moduleSdkIssues = modelData.moduleSdkIssues + miseIssues,
                        folderMarkIssues = modelData.folderMarkIssues,
                        miseSuggestion = miseSuggestion,
                        projectSdkSnapshot = modelData.projectSdkSnapshot,
                        classpathIssues = ioData.classpathIssues,
                    )
                }
        }
        // Bind job lifecycle to widget disposal using only public Disposer API.
        // When dispose() is called on this widget, the registered child cancels the job.
        // When the job completes naturally, it disposes the child to avoid leaking Disposer entries.
        val jobDisposable = Disposer.newDisposable("ElixirEditorBasedSdkWidget.notificationScanJob")
        Disposer.register(this, jobDisposable)
        notificationScanJob.invokeOnCompletion { Disposer.dispose(jobDisposable) }
        Disposer.register(jobDisposable) {
            notificationScanJob.cancel(CancellationException("ElixirEditorBasedSdkWidget disposed"))
        }
    }

    override fun ID(): String = ID

    // -------------------------------------------------------------------------
    // EditorBasedStatusBarPopup required overrides
    // -------------------------------------------------------------------------

    /**
     * Returns the widget state for the currently open file.
     *
     * This method is called by the platform inside a readAction on a background thread
     * (see EditorBasedStatusBarPopup.doUpdate). Do NOT nest another readAction here.
     * Performs only O(1) SDK lookups - no project-wide scans or notifications.
     */
    @RequiresBackgroundThread
    override fun getWidgetState(file: VirtualFile?): WidgetState {
        if (file == null) return WidgetState.HIDDEN
        val module = ModuleUtilCore.findModuleForFile(file, project)
            ?: return WidgetState.HIDDEN
        if (!module.isElixirModule()) return WidgetState.HIDDEN
        return buildModuleWidgetState(module)
    }

    override fun createPopup(context: DataContext): ListPopup {
        val actionGroup = DefaultActionGroup().apply {
            add(createAddSdkAction())
            val actionManager = ActionManager.getInstance()
            actionManager.getAction("Elixir.RefreshAllElixirSdks")?.let { add(it) }
            actionManager.getAction("Elixir.InstallMixDependencies")?.let { add(it) }
            add(Separator.getInstance())
            actionManager.getAction("Elixir.ReconfigureModuleSetup")?.let { add(it) }
        }
        return JBPopupFactory.getInstance().createActionGroupPopup(
            "Elixir SDK",
            actionGroup,
            context,
            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
            false
        )
    }

    override fun createInstance(project: Project): StatusBarWidget =
        ElixirEditorBasedSdkWidget(project, scope)

    override fun registerCustomListeners(connection: MessageBusConnection) {
        // JDK table changes (SDK added/removed/renamed) - affects both widget display and
        // project-wide notifications (e.g. SDK removal creates dangling references).
        connection.subscribe(ProjectJdkTable.JDK_TABLE_TOPIC, object : ProjectJdkTable.Listener {
            override fun jdkAdded(jdk: Sdk) {
                if (jdk.sdkType is Type) {
                    update()
                    notificationScanRequests.tryEmit(Unit)
                }
            }

            override fun jdkRemoved(jdk: Sdk) {
                if (jdk.sdkType is Type) {
                    update()
                    notificationScanRequests.tryEmit(Unit)
                }
            }

            override fun jdkNameChanged(jdk: Sdk, previousName: String) {
                if (jdk.sdkType is Type) {
                    update()
                    notificationScanRequests.tryEmit(Unit)
                }
            }
        })

        // Module root changes - update the widget (platform debounces at 300ms) and
        // request a project-wide notification scan via the debounced flow.
        connection.subscribe(ModuleRootListener.TOPIC, object : ModuleRootListener {
            override fun rootsChanged(event: com.intellij.openapi.roots.ModuleRootEvent) {
                cachedElixirModuleCount = -1  // invalidate; recomputed lazily in getWidgetState()
                update()  // triggers getWidgetState for per-file widget display
                notificationScanRequests.tryEmit(Unit)  // debounced notification scan
            }
        })

        // SDK refresh (from RefreshAllElixirSdksAction) - re-scan notifications since SDK
        // validity may have changed (e.g. Erlang SDK path restored).
        connection.subscribe(ElixirSdkRefreshListener.TOPIC, ElixirSdkRefreshListener {
            update()
            notificationScanRequests.tryEmit(Unit)
        })
    }

    // -------------------------------------------------------------------------
    // Per-module widget state
    // -------------------------------------------------------------------------

    private fun buildModuleWidgetState(module: com.intellij.openapi.module.Module): WidgetState {
        val elixirSdk = Type.mostSpecificSdk(module)

        // Determine whether to show module name in tooltip (only in multi-module projects).
        // Uses the cached count (refreshed on rootsChanged) to keep getWidgetState() O(1).
        var count = cachedElixirModuleCount
        if (count < 0) {
            // First call before any rootsChanged - compute lazily and cache.
            count = ModuleManager.getInstance(project).modules.count { it.isElixirModule() }
            cachedElixirModuleCount = count
        }
        val showModuleName = count > 1

        if (elixirSdk == null) {
            val state = WidgetState(
                toolTip = if (showModuleName) "No Elixir SDK configured for module '${module.name}'" else "No Elixir SDK configured",
                text = "No Elixir SDK",
                isActionEnabled = true
            )
            return state
        }

        val versionString = elixirSdk.versionString ?: "Unknown"
        val elixirVersion = org.elixir_lang.sdk.Type.appendWslSuffix(versionString, elixirSdk.homePath)

        if (!isValidSdk(elixirSdk)) {
            val state = WidgetState(
                toolTip = if (showModuleName) "Elixir SDK: $elixirVersion - Invalid SDK (module '${module.name}')" else "Elixir SDK: $elixirVersion - Invalid SDK",
                text = "Elixir: SDK Error",
                isActionEnabled = true
            )
            state.icon = AllIcons.General.Error
            return state
        }

        val erlangSdk = getErlangSdk(elixirSdk)
        if (erlangSdk == null) {
            val state = WidgetState(
                toolTip = if (showModuleName) "Elixir SDK: $elixirVersion - Missing Erlang SDK (module '${module.name}')" else "Elixir SDK: $elixirVersion - Missing Erlang SDK",
                text = "Elixir: SDK Error",
                isActionEnabled = true
            )
            state.icon = AllIcons.General.Error
            return state
        }

        val tooltipBase = if (showModuleName) "Elixir SDK: $elixirVersion (module '${module.name}')" else "Elixir SDK: $elixirVersion"
        val state = WidgetState(
            toolTip = tooltipBase,
            text = "Elixir $elixirVersion",
            isActionEnabled = true
        )
        state.icon = Icons.LANGUAGE
        return state
    }

    // -------------------------------------------------------------------------
    // Project-wide notification scan (triggered from rootsChanged background job)
    // -------------------------------------------------------------------------

    /**
     * Runs the project-wide notification scan. Called from the debounced collector -
     * never from getWidgetState(). This is O(modules) for SDK resolution and should not run
     * per-editor-switch.
     *
     * **Threading**: the [moduleSdkIssues] and [folderMarkIssues] parameters are pre-computed
     * inside `readAction` by the caller. This method performs additional model access
     * ([findModuleLevelElixirSdk], [getErlangSdk]) which also requires read access, so
     * it wraps that access in its own `readAction` call.
     */
    private fun notifyIfNeeded(
        moduleSdkIssues: List<ModuleSdkIssue>,
        folderMarkIssues: List<FolderMarkIssue>,
        miseSuggestion: MiseVersions? = null,
        projectSdkSnapshot: ProjectSdkSnapshot,
        classpathIssues: List<String>,
    ) {
        val elixirSdk = projectSdkSnapshot.elixirSdk
        val erlangSdk = projectSdkSnapshot.erlangSdk
        val elixirVersion = projectSdkSnapshot.elixirVersion ?: "Unknown"

        val sdkStatus: SdkStatus = when {
            moduleSdkIssues.isNotEmpty() ->
                SdkStatus.ModuleSdkError(elixirSdk, projectSdkSnapshot.elixirVersion, moduleSdkIssues)
            folderMarkIssues.isNotEmpty() && elixirSdk != null ->
                SdkStatus.FolderMarkWarning(elixirSdk, elixirVersion, folderMarkIssues)
            elixirSdk == null && miseSuggestion != null ->
                SdkStatus.NotConfiguredMiseAvailable(miseSuggestion)
            elixirSdk == null ->
                SdkStatus.NotConfigured
            erlangSdk == null ->
                SdkStatus.InvalidSdk(elixirSdk, elixirVersion, "Missing internal Erlang SDK dependency")
            classpathIssues.isNotEmpty() ->
                SdkStatus.ClasspathIssue(elixirSdk, erlangSdk, elixirVersion, classpathIssues)
            else ->
                SdkStatus.Configured(elixirSdk, erlangSdk, elixirVersion)
        }

        val issueKey = computeIssueKey(sdkStatus)

        // No issue - expire any active notification and reset
        if (issueKey == null) {
            activeNotification?.expire()
            activeNotification = null
            lastNotifiedIssueKey = null
            return
        }
        // Same issue already notified - keep the existing notification
        if (issueKey == lastNotifiedIssueKey) return

        // Issue changed - expire the old notification before showing the new one
        activeNotification?.expire()
        activeNotification = null
        lastNotifiedIssueKey = issueKey

        val (title, message, type) = buildNotificationContent(sdkStatus) ?: return

        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(title, message, type)

        // For module SDK errors or folder mark warnings, offer one-click fix via ReconfigureModuleSetupAction
        if (sdkStatus is SdkStatus.ModuleSdkError || sdkStatus is SdkStatus.FolderMarkWarning) {
            val reconfigureAction = ActionManager.getInstance().getAction("Elixir.ReconfigureModuleSetup")
            if (reconfigureAction != null) {
                notification.addAction(object : AnAction("Reconfigure Now") {
                    override fun actionPerformed(e: AnActionEvent) {
                        reconfigureAction.actionPerformed(e)
                        // Allow the same issue to be shown again if reconfigure did not resolve it.
                        lastNotifiedIssueKey = null
                        activeNotification = null
                        notificationScanRequests.tryEmit(Unit)
                        notification.expire()
                    }
                })
            }
        }

        // For mise-available case, offer one-click SDK configuration from mise install paths.
        if (sdkStatus is SdkStatus.NotConfiguredMiseAvailable) {
            notification.addAction(object : AnAction("Configure from Mise") {
                override fun actionPerformed(e: AnActionEvent) {
                    notification.expire()
                    configureSdkFromMise(project, sdkStatus.miseVersions)
                }
            })
        }

        notification.addAction(object : AnAction("Open Project Structure") {
            override fun actionPerformed(e: AnActionEvent) {
                SdkSettingsOpener.getInstance().open(e)
                notification.expire()
            }
        })

        notification.notify(project)
        activeNotification = notification
        LOG.info("SDK discrepancy in project '${project.name}': $message")

        if (sdkStatus is SdkStatus.FolderMarkWarning) {
            for (issue in sdkStatus.folderMarkIssues) {
                LOG.info("  Folder mark issue: module '${issue.moduleName}' -- ${issue.folderRelativePath}/ should be ${issue.folderMark.displayName} but is ${issue.currentState}")
            }
        }
        if (sdkStatus is SdkStatus.ModuleSdkError) {
            for (issue in sdkStatus.moduleSdkIssues) {
                LOG.info("  Module SDK issue: module '${issue.moduleName}' -- ${issue.issue}")
            }
        }
    }

    private fun computeIssueKey(status: SdkStatus): String? {
        return when (status) {
            is SdkStatus.Configured -> null
            is SdkStatus.NotConfigured -> "not-configured"
            is SdkStatus.NotConfiguredMiseAvailable ->
                "not-configured-mise:${status.miseVersions.elixir?.let { Mise.stripElixirOtpSuffix(it.version) }}"
            is SdkStatus.InvalidSdk -> "partial:${status.issue}"
            is SdkStatus.ClasspathIssue -> "warning:${status.issues.sorted().joinToString(",")}"
            is SdkStatus.ModuleSdkError -> "module-error:${status.moduleSdkIssues.map { "${it.moduleName}:${it.isDangling}:${it.issue}" }.sorted().joinToString(",")}"
            is SdkStatus.FolderMarkWarning -> "folder-marks:${status.folderMarkIssues.map { "${it.moduleName}:${it.folderRelativePath}:${it.folderMark}" }.sorted().joinToString(",")}"
        }
    }

    private data class NotificationContent(val title: String, val message: String, val type: NotificationType)

    private fun buildNotificationContent(status: SdkStatus): NotificationContent? {
        return when (status) {
            is SdkStatus.Configured -> null

            is SdkStatus.NotConfigured -> NotificationContent(
                "Elixir SDK Not Configured",
                "No Elixir SDK is configured for this project. Code insight will not work.",
                NotificationType.WARNING
            )

            is SdkStatus.NotConfiguredMiseAvailable -> {
                val displayVersion = status.miseVersions.elixir?.let {
                    Mise.stripElixirOtpSuffix(it.version)
                } ?: "unknown"
                NotificationContent(
                    "Elixir SDK Not Configured",
                    "No Elixir SDK configured, but Elixir $displayVersion is available via mise.",
                    NotificationType.WARNING
                )
            }

            is SdkStatus.InvalidSdk -> NotificationContent(
                "Elixir SDK Issue",
                "Elixir SDK: ${status.elixirVersion ?: "Unknown"} - ${status.issue}.",
                NotificationType.WARNING
            )

            is SdkStatus.ClasspathIssue -> NotificationContent(
                "Elixir SDK Warning",
                "Elixir SDK ${status.elixirVersion}: ${status.issues.joinToString("; ")}.",
                NotificationType.WARNING
            )

            is SdkStatus.ModuleSdkError -> {
                val hasDangling = status.moduleSdkIssues.any { it.isDangling }
                if (hasDangling) {
                    val danglingIssues = status.moduleSdkIssues.filter { it.isDangling }
                    val moduleNames = danglingIssues.map { it.moduleName }.distinct()
                    val sdkNames = danglingIssues.map { it.issue.substringAfter("SDK '").substringBefore("'") }.distinct()
                    NotificationContent(
                        "Elixir SDK Error: Module SDK ${StringUtil.pluralize("reference", moduleNames.size)} broken",
                        formatDanglingMessage(moduleNames, sdkNames),
                        NotificationType.ERROR
                    )
                } else {
                    val summary = status.moduleSdkIssues.joinToString("; ") { "'${it.moduleName}': ${it.issue}" }
                    NotificationContent(
                        "Elixir SDK: Module SDK Mismatch",
                        "Module SDK mismatch detected. $summary",
                        NotificationType.WARNING
                    )
                }
            }

            is SdkStatus.FolderMarkWarning -> {
                val issueCount = status.folderMarkIssues.size
                val moduleCount = status.folderMarkIssues.map { it.moduleName }.distinct().size
                val summary = if (issueCount == 1) {
                    val issue = status.folderMarkIssues.first()
                    "${issue.folderRelativePath}/ is not marked as ${issue.folderMark.displayName} in module '${issue.moduleName}'"
                } else {
                    "$issueCount folder mark ${StringUtil.pluralize("issue", issueCount)} across $moduleCount ${StringUtil.pluralize("module", moduleCount)}"
                }
                NotificationContent(
                    "Elixir: Project Structure Suboptimal",
                    "$summary. Some code insight features may not work correctly.",
                    NotificationType.WARNING
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // SDK detection helpers (shared between getWidgetState and notification scan)
    // -------------------------------------------------------------------------

    /**
     * Finds the active Elixir SDK by scanning all Elixir modules.
     *
     * Uses [Type.mostSpecificSdk] (module overload) which checks Facet SDK → module SDK → project SDK,
     * returning the first non-null result. This covers both Rich IDEs (JdkOrderEntry) and Small IDEs
     * (Facet library entry) without additional branching.
     *
     * When both the project SDK and a module SDK are Elixir, this returns the **module** SDK - the one
     * that actually drives code insight - rather than the project-level one. The mismatch between them
     * is reported separately by [detectModuleSdkIssues].
     */
    internal fun findModuleLevelElixirSdk(): Sdk? {
        for (module in ModuleManager.getInstance(project).modules) {
            if (!module.isElixirModule()) continue
            val moduleSdk = Type.mostSpecificSdk(module)
            if (moduleSdk != null) return moduleSdk
        }
        return null
    }

    /**
     * Detects project-wide module SDK issues (dangling references and mismatches).
     * O(modules × orderEntries) - only called from the rootsChanged background job,
     * never from [getWidgetState].
     *
     * **Threading**: must be called off the EDT (background thread).  Accesses
     * [ModuleRootManager] which requires read access; the caller must either hold a read lock
     * or call this inside a `readAction { }` block.
     */
    @RequiresBackgroundThread
    internal fun detectModuleSdkIssues(): List<ModuleSdkIssue> {
        ThreadingAssertions.assertBackgroundThread()
        val issues = mutableListOf<ModuleSdkIssue>()
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk

        for (module in ModuleManager.getInstance(project).modules) {
            if (!module.isElixirModule()) continue

            val rootManager = ModuleRootManager.getInstance(module)

            // --- Rich IDE path: detect dangling/mismatch JdkOrderEntry ---
            var hasJdkEntry = false
            for (entry in rootManager.orderEntries) {
                if (entry !is JdkOrderEntry) continue

                val jdkName = entry.jdkName ?: continue
                hasJdkEntry = true
                // Only check Elixir SDK entries (when SDK is resolvable, check its type)
                val resolvedSdk = entry.jdk
                if (resolvedSdk != null && resolvedSdk.sdkType !is Type) continue

                if (resolvedSdk == null) {
                    // DANGLING: SDK name exists in .iml but not in JDK table
                    issues.add(
                        ModuleSdkIssue(module.name, "references non-existent SDK '$jdkName'", isDangling = true)
                    )
                } else if (projectSdk != null && projectSdk.sdkType is Type && resolvedSdk != projectSdk) {
                    // MISMATCH: Module uses different Elixir SDK than project in a single-version project.
                    // Only meaningful when the project SDK is itself Elixir; skip when it is Java/Python/etc.
                    issues.add(
                        ModuleSdkIssue(module.name, "uses '$jdkName' but project SDK is '${projectSdk.name}'", isDangling = false)
                    )
                }
            }

            // --- Small IDE path: detect dangling Facet SDK ---
            // Only check if the Rich IDE path didn't find any JDK entries (Small IDE modules use
            // a Facet library entry instead of a JdkOrderEntry to hold the Elixir SDK).
            if (!hasJdkEntry) {
                val facet = FacetManager.getInstance(module).getFacetByType(Facet.ID)
                if (facet != null && Facet.sdk(module) == null && Facet.sdks().isNotEmpty()) {
                    // The module has an Elixir Facet and Elixir SDKs exist in the table, but the
                    // Facet library entry doesn't match any current SDK name - stale/dangling reference.
                    issues.add(
                        ModuleSdkIssue(
                            module.name,
                            "Elixir Facet SDK is not configured (stale reference). Reconfigure in Settings → Languages → Elixir.",
                            isDangling = true
                        )
                    )
                }
            }
        }
        return issues
    }

    // -------------------------------------------------------------------------
    // Mise version mismatch detection (Features B & C)
    // -------------------------------------------------------------------------

    /**
     * Data gathered from the module model (requires a read lock) that is later used by
     * [detectMiseSdkMismatchIssues] to compare against mise's resolved versions.
     */
    private data class ModuleMiseCheckData(
        val moduleName: String,
        /** Elixir SDK configured for the module (if any). */
        val elixirSdk: Sdk?,
        /** OTP major release reported by the Internal Erlang SDK (e.g. `"25"`). */
        val erlangOtpRelease: String?,
        /** First content root of the module, used as the working directory for `mise ls`. */
        val contentRoot: Path?,
    )

    private data class ProjectSdkSnapshot(
        val elixirSdk: Sdk?,
        val elixirVersion: String?,
        val erlangSdk: Sdk?,
    )

    private data class NotificationScanModelData(
        val moduleSdkIssues: List<ModuleSdkIssue>,
        val folderMarkIssues: List<FolderMarkIssue>,
        val moduleMiseCheckData: List<ModuleMiseCheckData>,
        val projectSdkSnapshot: ProjectSdkSnapshot,
    )

    private data class NotificationScanIoData(
        val miseByContentRoot: Map<Path, MiseVersions?>,
        val elixirVersionBySdk: Map<Sdk, String?>,
        val classpathIssues: List<String>,
    )

    private fun collectNotificationScanModelData(): NotificationScanModelData {
        val moduleSdkIssues = detectModuleSdkIssues()
        val folderMarkIssues = ProjectModuleSetupValidator.detectFolderMarkIssues(project)
        val moduleMiseCheckData = collectModuleMiseCheckData()
        val projectSdkSnapshot = collectProjectSdkSnapshot()

        return NotificationScanModelData(
            moduleSdkIssues = moduleSdkIssues,
            folderMarkIssues = folderMarkIssues,
            moduleMiseCheckData = moduleMiseCheckData,
            projectSdkSnapshot = projectSdkSnapshot,
        )
    }

    private fun collectProjectSdkSnapshot(): ProjectSdkSnapshot {
        val elixirSdk = findModuleLevelElixirSdk()
        val elixirVersion = elixirSdk?.versionString?.let {
            org.elixir_lang.sdk.Type.appendWslSuffix(it, elixirSdk.homePath)
        }
        val erlangSdk = elixirSdk?.let { getErlangSdk(it) }

        return ProjectSdkSnapshot(
            elixirSdk = elixirSdk,
            elixirVersion = elixirVersion,
            erlangSdk = erlangSdk,
        )
    }

    private fun collectNotificationScanIoData(modelData: NotificationScanModelData): NotificationScanIoData {
        val miseByContentRoot = modelData.moduleMiseCheckData
            .mapNotNull { it.contentRoot }
            .distinct()
            .associateWith { contentRoot -> Mise.resolveVersions(contentRoot) }

        val elixirVersionBySdk = modelData.moduleMiseCheckData
            .mapNotNull { it.elixirSdk }
            .distinct()
            .associateWith { sdk -> Type.canonicalVersion(sdk) }

        val classpathIssues = detectClasspathIssues(modelData.projectSdkSnapshot)

        return NotificationScanIoData(
            miseByContentRoot = miseByContentRoot,
            elixirVersionBySdk = elixirVersionBySdk,
            classpathIssues = classpathIssues,
        )
    }

    private fun detectClasspathIssues(projectSdkSnapshot: ProjectSdkSnapshot): List<String> {
        val elixirSdk = projectSdkSnapshot.elixirSdk ?: return emptyList()
        val erlangSdk = projectSdkSnapshot.erlangSdk ?: return emptyList()
        val classpathIssues = mutableListOf<String>()

        if (hasEbinPathIssues(elixirSdk)) {
            classpathIssues.add("Missing Elixir ebin paths or classpath entries")
        }
        if (hasErlangSdkIssues(erlangSdk)) {
            classpathIssues.add("Erlang SDK configuration issues")
        }
        if (!Type.hasErlangClasspathInElixirSdk(elixirSdk, erlangSdk)) {
            classpathIssues.add("Erlang SDK classpath entries missing - reopen SDK settings to fix")
        }

        return classpathIssues
    }

    /**
     * Gathers module SDK info needed for mise version-mismatch checks.
     *
     * Must be called inside a read action (accesses [ModuleRootManager]).
     */
    private fun collectModuleMiseCheckData(): List<ModuleMiseCheckData> {
        return ModuleManager.getInstance(project).modules
            .filter { it.isElixirModule() }
            .map { module ->
                val elixirSdk = Type.mostSpecificSdk(module)
                val erlangSdk = elixirSdk?.let { getErlangSdk(it) }
                val contentRoot = ModuleRootManager.getInstance(module)
                    .contentRoots
                    .firstOrNull()
                    ?.toNioPathOrNull()
                ModuleMiseCheckData(
                    moduleName = module.name,
                    elixirSdk = elixirSdk,
                    erlangOtpRelease = erlangSdk?.versionString
                        ?.let { org.elixir_lang.sdk.erlang.Release.fromString(it)?.otpRelease },
                    contentRoot = contentRoot,
                )
            }
    }

    /**
     * Feature A: for Elixir modules with no SDK configured, checks if mise has an installed
     * Elixir version for that module's content root.
     *
     * Returns the first [MiseVersions] with an installed Elixir entry, or null if no unconfigured
     * module has mise data available.
     *
     * Must NOT be called under a read lock - runs `mise ls` as a subprocess.
     */
    private fun detectMiseSdkForUnconfiguredModules(
        moduleDataList: List<ModuleMiseCheckData>,
        miseByContentRoot: Map<Path, MiseVersions?>,
    ): MiseVersions? {
        for (moduleData in moduleDataList) {
            if (moduleData.elixirSdk != null) continue
            val contentRoot = moduleData.contentRoot ?: continue
            val versions = miseByContentRoot[contentRoot] ?: continue
            if (versions.elixir?.installed == true) {
                return versions
            }
        }
        return null
    }

    /**
     * Registers Erlang and Elixir SDKs from mise install paths, pairs them, and assigns the
     * Elixir SDK to the first unconfigured Elixir module.
     *
     * Called from the EDT (notification action click handler). Uses
     * [runWithModalProgressBlocking] so the user sees a modal progress dialog while SDKs are
     * being registered. Write-action mutations use [edtWriteAction].
     */
    private fun configureSdkFromMise(project: Project, miseVersions: MiseVersions) {
        runWithModalProgressBlocking(ModalTaskOwner.project(project), "Configuring Elixir SDK from mise") {
            val erlangSdk = miseVersions.erlang?.let { erlang ->
                SdkRegistrar.registerOrUpdateErlangSdk(erlang.installPath)
            }

            val elixirSdk = miseVersions.elixir?.let { elixir ->
                SdkRegistrar.registerOrUpdateElixirSdk(
                    homePath = elixir.installPath,
                    erlangSdk = erlangSdk,
                    project = project,
                )
            } ?: return@runWithModalProgressBlocking

            // Assign to the first unconfigured Elixir module.
            val module = readAction {
                ModuleManager.getInstance(project).modules
                    .firstOrNull { it.isElixirModule() && Type.mostSpecificSdk(it) == null }
            } ?: return@runWithModalProgressBlocking

            edtWriteAction {
                val modifiableModel = ModuleRootManager.getInstance(module).modifiableModel
                modifiableModel.sdk = elixirSdk
                modifiableModel.commit()
            }

            EditorNotifications.getInstance(project).updateAllNotifications()
        }
    }

    /**
     * Feature B: warns when the configured Elixir SDK version differs from the version mise
     * resolves for the module directory.
     *
     * Feature C: warns when the Internal Erlang SDK OTP major release differs from the OTP major
     * in the mise-resolved erlang version string.
     *
     * Runs `mise ls --local --json` (a subprocess) for each module. Must NOT be called inside a
     * read lock - use [collectModuleMiseCheckData] first to extract what you need, then call this.
     */
    private fun detectMiseSdkMismatchIssues(
        moduleDataList: List<ModuleMiseCheckData>,
        elixirVersionBySdk: Map<Sdk, String?>,
        miseByContentRoot: Map<Path, MiseVersions?>,
    ): List<ModuleSdkIssue> {
        val issues = mutableListOf<ModuleSdkIssue>()

        for (data in moduleDataList) {
            val contentRoot = data.contentRoot ?: continue
            val miseVersions = miseByContentRoot[contentRoot] ?: continue

            // Feature B: Elixir version mismatch
            val miseElixir = miseVersions.elixir
            if (miseElixir != null) {
                val configuredVersion = data.elixirSdk?.let { elixirVersionBySdk[it] }
                val miseVersion = Mise.stripElixirOtpSuffix(miseElixir.version)
                if (configuredVersion != null && configuredVersion != miseVersion) {
                    LOG.info(
                        "Mise version mismatch in module '${data.moduleName}': " +
                        "SDK=$configuredVersion, mise=$miseVersion"
                    )
                    issues.add(
                        ModuleSdkIssue(
                            moduleName = data.moduleName,
                            issue = "Module '${data.moduleName}' uses Elixir $configuredVersion " +
                                    "but mise resolves ${miseElixir.version}. " +
                                    "Run 'Refresh Active Elixir SDKs' or reconfigure.",
                            isDangling = false,
                        )
                    )
                }
            }

            // Feature C: OTP version mismatch between Internal Erlang SDK and mise erlang
            val miseErlang = miseVersions.erlang
            if (miseErlang != null && data.erlangOtpRelease != null) {
                val miseErlangMajor = miseErlang.version.substringBefore(".")
                if (miseErlangMajor != data.erlangOtpRelease) {
                    LOG.info(
                        "Mise OTP mismatch in module '${data.moduleName}': " +
                        "SDK OTP=${data.erlangOtpRelease}, mise=${miseErlang.version}"
                    )
                    issues.add(
                        ModuleSdkIssue(
                            moduleName = data.moduleName,
                            issue = "Internal Erlang SDK OTP ${data.erlangOtpRelease} does not " +
                                    "match mise (${miseErlang.version}). " +
                                    "Run 'Refresh Active Elixir SDKs' or reconfigure.",
                            isDangling = false,
                        )
                    )
                }
            }
        }

        return issues
    }

    private fun isValidSdk(sdk: Sdk?): Boolean {
        if (sdk == null) return false
        val sdkType = sdk.sdkType as? Type ?: return false
        val homePath = sdk.homePath ?: return false
        return sdkType.isValidSdkHome(homePath)
    }

    private fun getErlangSdk(elixirSdk: Sdk): Sdk? {
        val additionalData = elixirSdk.sdkAdditionalData as? SdkAdditionalData
        return additionalData?.getErlangSdk()
    }

    private fun hasEbinPathIssues(elixirSdk: Sdk): Boolean {
        val homePath = elixirSdk.homePath ?: return true

        // Check if Elixir SDK has proper ebin paths in lib directory
        if (!SdkEbinPaths.hasEbinPath(homePath)) {
            return true
        }

        // Check if SDK has proper classpath roots configured
        val classRoots = elixirSdk.rootProvider.getFiles(com.intellij.openapi.roots.OrderRootType.CLASSES)
        return classRoots.isEmpty()
    }

    private fun hasErlangSdkIssues(erlangSdk: Sdk): Boolean {
        val homePath = erlangSdk.homePath ?: return true

        // Check if Erlang SDK has proper ebin paths
        if (!SdkEbinPaths.hasEbinPath(homePath)) {
            return true
        }

        // Validate that this is a proper Erlang SDK type
        return !org.elixir_lang.sdk.erlang_dependent.Type.staticIsValidDependency(erlangSdk)
    }

    private fun createAddSdkAction(): AnAction {
        return object : AnAction("Configure Elixir SDKs...", "", AllIcons.General.Add) {
            override fun actionPerformed(e: AnActionEvent) {
                SdkSettingsOpener.getInstance().open(e)
            }

            override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

            override fun update(e: AnActionEvent) {
                val isConfigured = findModuleLevelElixirSdk() != null
                val text = if (isConfigured) "Configure Elixir SDKs..." else "Add Elixir SDK..."
                val targetName = SdkSettingsOpener.getInstance().targetName()
                val description = if (isConfigured) {
                    "Configure Elixir SDKs in $targetName"
                } else {
                    "Open $targetName to add an Elixir SDK"
                }
                e.presentation.text = text
                e.presentation.description = description
            }
        }
    }

    private fun formatDanglingMessage(moduleNames: List<String>, sdkNames: List<String>): String {
        val moduleCount = moduleNames.size
        val moduleWord = StringUtil.pluralize("Module", moduleCount)
        val moduleList = formatNameList(moduleNames)
        val verb = if (moduleCount == 1) "references" else "reference"
        val sdkWord = StringUtil.pluralize("SDK", sdkNames.size)
        val sdkList = sdkNames.joinToString(", ") { "'$it'" }
        val navigationHint = if (moduleCount == 1) {
            "Project Structure → Modules → ${moduleNames.first()} → Dependencies → Module SDK. Set it to \"Project SDK\""
        } else {
            "Project Structure → Modules → <module> → Dependencies → Module SDK. Set each to \"Project SDK\""
        }
        return "$moduleWord $moduleList $verb non-existent $sdkWord $sdkList. Code insight will not work until fixed in $navigationHint."
    }

    private fun formatNameList(names: List<String>): String {
        return if (names.size <= 3) {
            names.joinToString(", ") { "'$it'" }
        } else {
            names.take(3).joinToString(", ") { "'$it'" } + " and ${names.size - 3} more"
        }
    }
}
