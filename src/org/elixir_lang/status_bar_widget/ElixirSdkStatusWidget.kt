package org.elixir_lang.status_bar_widget

import com.intellij.facet.FacetManager
import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.UI
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.ui.ClickListener
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elixir_lang.Facet
import org.elixir_lang.Icons
import org.elixir_lang.isElixirModule
import org.elixir_lang.mix.project.ProjectModuleSetupValidator
import org.elixir_lang.mix.project.ProjectModuleSetupValidator.FolderMarkIssue
import org.elixir_lang.sdk.SdkEbinPaths
import org.elixir_lang.sdk.elixir.SdkSettingsOpener
import org.elixir_lang.sdk.elixir.Type
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.util.ElixirCoroutineService
import org.jetbrains.annotations.NotNull
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.JComponent
import kotlin.time.Duration.Companion.seconds

private val LOG = logger<ElixirSdkStatusWidget>()

class ElixirSdkStatusWidget(@param:NotNull private val project: Project) : CustomStatusBarWidget {

    companion object {
        const val ID = "ElixirSdkStatus"
    }

    private var statusBar: StatusBar? = null
    private var messageBusConnection: MessageBusConnection? = null

    // Child scope of the project-level ElixirCoroutineService scope.
    // Cancelled explicitly in dispose(); also cancelled automatically on project close / plugin unload.
    private val widgetScope = project.service<ElixirCoroutineService>().supervisedChildScope("ElixirSdkStatusWidget")

    // Debounce rapid rootsChanged() events (e.g. bulk module import, DepsWatcher) into a single update.
    // DROP_OLDEST + extraBufferCapacity = 1 means tryEmit() never blocks and never fails.
    private val rootsChangedFlow = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )    // Track last notified status to avoid spamming duplicate notifications
    @Volatile
    private var lastNotifiedIssueKey: String? = null

    // The currently displayed notification -- expired when the issue resolves or changes
    @Volatile
    private var activeNotification: com.intellij.notification.Notification? = null

    private val component: TextPanel.WithIconAndArrows by lazy {
        val panel = TextPanel.WithIconAndArrows()
        panel.border = JBUI.CurrentTheme.StatusBar.Widget.iconBorder()

        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                showPopup(event)
                return true
            }
        }.installOn(panel, true)

        panel
    }

    // Cached widget presentation data - computed once per update
    @Volatile
    private var cachedPresentation: WidgetPresentation? = null

    private data class WidgetPresentation(
        val text: String,
        val icon: javax.swing.Icon?,
        val tooltip: String
    )

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
    }

    internal data class ModuleSdkIssue(val moduleName: String, val issue: String, val isDangling: Boolean)

    init {
        // Kick off the first presentation computation asynchronously.
        // updateWidget() only schedules a coroutine and returns immediately, so it is safe
        // to call from the EDT (the widget constructor runs on the EDT during widget creation).
        updateWidget()
    }

    override fun ID(): String = ID

    override fun getComponent(): JComponent = component

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        setupListeners()
        // Refresh immediately now that statusBar is wired up.  Safe to call from EDT - async.
        updateWidget()
    }

    override fun dispose() {
        messageBusConnection?.disconnect()
        widgetScope.cancel()
        activeNotification?.expire()
        activeNotification = null
        statusBar = null
        cachedPresentation = null
        LOG.debug("Disposed ElixirSdkStatusWidget")
    }

    private fun showPopup(e: MouseEvent) {
        val actionGroup = DefaultActionGroup().apply {
            add(createAddSdkAction())
            val actionManager = ActionManager.getInstance()
            actionManager.getAction("Elixir.RefreshAllElixirSdks")?.let { add(it) }
            actionManager.getAction("Elixir.InstallMixDependencies")?.let { add(it) }
            add(Separator.getInstance())
            actionManager.getAction("Elixir.ReconfigureModuleSetup")?.let { add(it) }
        }

        val dataContext = DataManager.getInstance().getDataContext(component)
        val popup = JBPopupFactory.getInstance().createActionGroupPopup(
            "Elixir SDK",
            actionGroup,
            dataContext,
            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
            false
        )

        // Position popup directly above the widget (same as CPU widget and EditorBasedStatusBarPopup)
        val dimension = popup.content.preferredSize
        val at = Point(0, -dimension.height)
        popup.show(RelativePoint(e.component, at))

        // Dispose popup when widget is disposed
        Disposer.register(this, popup)
    }

    private fun createAddSdkAction(): AnAction {

        return object : AnAction("Configure Elixir SDKs...", "", AllIcons.General.Add) {
            override fun actionPerformed(e: AnActionEvent) {
                SdkSettingsOpener.getInstance().open(e)
            }

            override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

            override fun update(e: AnActionEvent) {
                val status = detectSdkStatus()
                val isConfigured = status !is SdkStatus.NotConfigured
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

    private fun updateWidget() {
        widgetScope.launch {
            val sdkStatus = readAction { detectSdkStatus() }
            val presentation = createPresentation(sdkStatus)
            withContext(Dispatchers.UI) {
                cachedPresentation = presentation
                component.text = presentation.text
                component.icon = presentation.icon
                component.toolTipText = presentation.tooltip
                statusBar?.updateWidget(ID)
                // Fire notification whenever a discrepancy is detected
                notifyIfNeeded(sdkStatus)
            }
        }
    }

    private fun createPresentation(status: SdkStatus): WidgetPresentation {
        return when (status) {
            is SdkStatus.Configured -> WidgetPresentation(
                text = "Elixir: ${status.elixirVersion}",
                icon = Icons.LANGUAGE,
                tooltip = "Elixir SDK: ${status.elixirVersion} (Configured correctly)"
            )

            is SdkStatus.ClasspathIssue -> WidgetPresentation(
                text = "Elixir: ${status.elixirVersion} !",
                icon = Icons.LANGUAGE,
                tooltip = "Elixir SDK: ${status.elixirVersion} (Warning: ${status.issues.joinToString(", ")})"
            )

            is SdkStatus.InvalidSdk -> WidgetPresentation(
                text = "Elixir: Issues",
                icon = AllIcons.General.Warning,
                tooltip = "Elixir SDK: ${status.elixirVersion ?: "Unknown"} (${status.issue})"
            )

            is SdkStatus.ModuleSdkError -> {
                val hasDangling = status.moduleSdkIssues.any { it.isDangling }
                val danglingIssues = status.moduleSdkIssues.filter { it.isDangling }
                val moduleNames = danglingIssues.map { it.moduleName }.distinct()
                    .ifEmpty { status.moduleSdkIssues.map { it.moduleName }.distinct() }

                if (hasDangling) {
                    val sdkNames = danglingIssues.map { it.issue.substringAfter("SDK '").substringBefore("'") }.distinct()
                    val message = formatDanglingMessage(moduleNames, sdkNames)
                    WidgetPresentation(
                        text = "Elixir: SDK Error",
                        icon = AllIcons.General.Error,
                        tooltip = message
                    )
                } else {
                    val summary = status.moduleSdkIssues.joinToString("; ") { "'${it.moduleName}': ${it.issue}" }
                    WidgetPresentation(
                        text = "Elixir: ${status.elixirVersion ?: "Unknown"} !",
                        icon = AllIcons.General.Warning,
                        tooltip = "Module SDK mismatch. $summary"
                    )
                }
            }

            SdkStatus.NotConfigured -> {
                val availableElixirSdks = ProjectJdkTable.getInstance().allJdks.filter { it.sdkType is Type }
                val tooltip = if (availableElixirSdks.isNotEmpty()) {
                    "No Elixir SDK configured for this project (${availableElixirSdks.size} Elixir ${StringUtil.pluralize("SDK", availableElixirSdks.size)} available)"
                } else {
                    "No Elixir SDKs configured"
                }
                WidgetPresentation(
                    text = "No Elixir SDK",
                    icon = null,
                    tooltip = tooltip
                )
            }

            is SdkStatus.FolderMarkWarning -> {
                val issueCount = status.folderMarkIssues.size
                val moduleCount = status.folderMarkIssues.map { it.moduleName }.distinct().size
                val tooltip = if (issueCount == 1) {
                    val issue = status.folderMarkIssues.first()
                    "Module '${issue.moduleName}': ${issue.folderRelativePath}/ should be ${issue.folderMark.displayName} " +
                            "(currently ${issue.currentState}). Click to reconfigure."
                } else {
                    "$issueCount folder mark ${StringUtil.pluralize("issue", issueCount)} across " +
                            "$moduleCount ${StringUtil.pluralize("module", moduleCount)}. Click to reconfigure."
                }
                WidgetPresentation(
                    text = "Elixir: ${status.elixirVersion} ⚙",
                    icon = AllIcons.General.Warning,
                    tooltip = tooltip
                )
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
        return "$moduleWord $moduleList $verb non-existent $sdkWord $sdkList. " +
                "Code insight will not work until fixed in $navigationHint."
    }

    private fun formatNameList(names: List<String>): String {
        return if (names.size <= 3) {
            names.joinToString(", ") { "'$it'" }
        } else {
            names.take(3).joinToString(", ") { "'$it'" } +
                    " and ${names.size - 3} more"
        }
    }

    private fun notifyIfNeeded(sdkStatus: SdkStatus) {
        val issueKey = computeIssueKey(sdkStatus)

        // No issue -- expire any active notification and reset
        if (issueKey == null) {
            activeNotification?.expire()
            activeNotification = null
            lastNotifiedIssueKey = null
            return
        }
        // Same issue already notified -- keep the existing notification
        if (issueKey == lastNotifiedIssueKey) return

        // Issue changed -- expire the old notification before showing the new one
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
                        notification.expire()
                    }
                })
            }
        }

        notification.addAction(object : AnAction("Open Project Structure") {
            override fun actionPerformed(e: AnActionEvent) {
                SdkSettingsOpener.getInstance().open(e)
                notification.expire()
            }
        })

        notification.notify(project)
        activeNotification = notification
        LOG.warn("SDK discrepancy in project '${project.name}': $message")

        // Log individual issues for easier diagnosis in idea.log during IDE testing
        if (sdkStatus is SdkStatus.FolderMarkWarning) {
            for (issue in sdkStatus.folderMarkIssues) {
                LOG.warn("  Folder mark issue: module '${issue.moduleName}' -- " +
                        "${issue.folderRelativePath}/ should be ${issue.folderMark.displayName} " +
                        "but is ${issue.currentState}")
            }
        }
        if (sdkStatus is SdkStatus.ModuleSdkError) {
            for (issue in sdkStatus.moduleSdkIssues) {
                LOG.warn("  Module SDK issue: module '${issue.moduleName}' -- ${issue.issue}")
            }
        }
    }

    private fun computeIssueKey(status: SdkStatus): String? {
        return when (status) {
            is SdkStatus.Configured -> null
            is SdkStatus.NotConfigured -> "not-configured"
            is SdkStatus.InvalidSdk -> "invalid-sdk:${status.issue}"
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

    private fun setupListeners() {
        messageBusConnection = project.messageBus.connect(this)

        // Listen for project JDK table changes (SDK added/removed/modified).
        // These events are infrequent; call updateWidget() directly (it just launches a coroutine).
        messageBusConnection?.subscribe(ProjectJdkTable.JDK_TABLE_TOPIC, object : ProjectJdkTable.Listener {
            override fun jdkAdded(jdk: Sdk) {
                if (jdk.sdkType is Type) {
                    updateWidget()
                }
            }

            override fun jdkRemoved(jdk: Sdk) {
                if (jdk.sdkType is Type) {
                    updateWidget()
                }
            }

            override fun jdkNameChanged(jdk: Sdk, previousName: String) {
                if (jdk.sdkType is Type) {
                    updateWidget()
                }
            }
        })

        // Listen for project root changes (project SDK changes).
        // Debounced: bulk operations (import wizard, DepsWatcher) fire rootsChanged() rapidly;
        // collapse them into a single update after a 2-second quiet window.
        messageBusConnection?.subscribe(
            com.intellij.openapi.roots.ModuleRootListener.TOPIC,
            object : com.intellij.openapi.roots.ModuleRootListener {
                override fun rootsChanged(event: com.intellij.openapi.roots.ModuleRootEvent) {
                    rootsChangedFlow.tryEmit(Unit)
                }
            })

        // Collect debounced rootsChanged signals and trigger a widget update
        @OptIn(FlowPreview::class)
        widgetScope.launch {
            rootsChangedFlow.debounce(2.seconds).collect {
                updateWidget()
            }
        }

        // Listen for SDK refresh events (from RefreshAllElixirSdksAction)
        messageBusConnection?.subscribe(ElixirSdkRefreshListener.TOPIC, ElixirSdkRefreshListener {
            updateWidget()
        })
    }

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

    internal fun detectSdkStatus(): SdkStatus {
        val elixirSdk = findModuleLevelElixirSdk() ?: return SdkStatus.NotConfigured
        val versionString = elixirSdk.versionString ?: "Unknown"

        // Add WSL distribution suffix if this is a WSL SDK
        val elixirVersion = org.elixir_lang.sdk.Type.appendWslSuffix(versionString, elixirSdk.homePath)

        if (!isValidSdk(elixirSdk)) {
            return SdkStatus.InvalidSdk(elixirSdk, elixirVersion, "Invalid Elixir SDK")
        }

        val erlangSdk = getErlangSdk(elixirSdk)
            ?: return SdkStatus.InvalidSdk(elixirSdk, elixirVersion, "Missing Erlang SDK")

        // Check module-level SDK consistency (dangling references or mismatches)
        val moduleSdkIssues = detectModuleSdkIssues()
        if (moduleSdkIssues.isNotEmpty()) {
            return SdkStatus.ModuleSdkError(elixirSdk, elixirVersion, moduleSdkIssues)
        }

        // Check classpath/ebin configuration (breaks code resolution globally)
        val issues = mutableListOf<String>()
        if (hasEbinPathIssues(elixirSdk)) {
            issues.add("Missing Elixir ebin paths or classpath entries")
        }
        if (hasErlangSdkIssues(erlangSdk)) {
            issues.add("Erlang SDK configuration issues")
        }
        if (!Type.hasErlangClasspathInElixirSdk(elixirSdk, erlangSdk)) {
            issues.add("Erlang SDK classpath entries missing - reopen SDK settings to fix")
        }
        if (issues.isNotEmpty()) {
            return SdkStatus.ClasspathIssue(elixirSdk, erlangSdk, elixirVersion, issues)
        }

        // Check folder mark configuration (breaks specific per-directory features)
        val folderMarkIssues = ProjectModuleSetupValidator.detectFolderMarkIssues(project)
        if (folderMarkIssues.isNotEmpty()) {
            return SdkStatus.FolderMarkWarning(elixirSdk, elixirVersion, folderMarkIssues)
        }

        return SdkStatus.Configured(elixirSdk, erlangSdk, elixirVersion)
    }

    internal fun detectModuleSdkIssues(): List<ModuleSdkIssue> {
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
                        ModuleSdkIssue(
                            module.name,
                            "references non-existent SDK '$jdkName'",
                            isDangling = true
                        )
                    )
                } else if (projectSdk != null && projectSdk.sdkType is Type && resolvedSdk != projectSdk) {
                    // MISMATCH: Module uses different Elixir SDK than project in a single-version project.
                    // Only meaningful when the project SDK is itself Elixir; skip when it is Java/Python/etc.
                    issues.add(
                        ModuleSdkIssue(
                            module.name,
                            "uses '$jdkName' but project SDK is '${projectSdk.name}'",
                            isDangling = false
                        )
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

}
