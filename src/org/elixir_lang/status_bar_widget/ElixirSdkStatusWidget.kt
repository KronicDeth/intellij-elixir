package org.elixir_lang.status_bar_widget

import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.*
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
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.ui.ClickListener
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.JBUI
import org.elixir_lang.Icons
import org.elixir_lang.sdk.SdkEbinPaths
import org.elixir_lang.sdk.elixir.SdkSettingsOpener
import org.elixir_lang.sdk.elixir.Type
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.jetbrains.annotations.NotNull
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.JComponent

private val LOG = logger<ElixirSdkStatusWidget>()

class ElixirSdkStatusWidget(@param:NotNull private val project: Project) : CustomStatusBarWidget {

    companion object {
        const val ID = "ElixirSdkStatus"
    }

    private var statusBar: StatusBar? = null
    private var messageBusConnection: MessageBusConnection? = null

    // Track last notified status to avoid spamming duplicate notifications
    @Volatile
    private var lastNotifiedIssueKey: String? = null

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

    private sealed interface SdkStatus {
        data class Configured(
            val elixirSdk: Sdk,
            val erlangSdk: Sdk,
            val elixirVersion: String
        ) : SdkStatus

        data class Warning(
            val elixirSdk: Sdk,
            val erlangSdk: Sdk,
            val elixirVersion: String,
            val issues: List<String>
        ) : SdkStatus

        data class Partial(
            val elixirSdk: Sdk?,
            val elixirVersion: String?,
            val issue: String
        ) : SdkStatus

        data class ModuleSdkError(
            val elixirSdk: Sdk?,
            val elixirVersion: String?,
            val moduleSdkIssues: List<ModuleSdkIssue>
        ) : SdkStatus

        data object NotConfigured : SdkStatus
    }

    private data class ModuleSdkIssue(val moduleName: String, val issue: String, val isDangling: Boolean)

    init {
        updateWidget()
    }

    override fun ID(): String = ID

    override fun getComponent(): JComponent = component

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        setupListeners()
        updateWidget()
    }

    override fun dispose() {
        messageBusConnection?.disconnect()
        statusBar = null
        cachedPresentation = null
        Disposer.dispose(this)
        LOG.debug("Disposed ElixirSdkStatusWidget")
    }

    private fun showPopup(e: MouseEvent) {
        val actionGroup = DefaultActionGroup().apply {
            add(createAddSdkAction())
            val actionManager = ActionManager.getInstance()
            actionManager.getAction("Elixir.RefreshAllElixirSdks")?.let { add(it) }
            actionManager.getAction("Elixir.InstallMixDependencies")?.let { add(it) }
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
        cachedPresentation = null
        val presentation = getCurrentPresentation()

        component.text = presentation.text
        component.icon = presentation.icon
        component.toolTipText = presentation.tooltip

        statusBar?.updateWidget(ID)

        // Fire notification whenever a discrepancy is detected
        notifyIfNeeded()
    }

    private fun getCurrentPresentation(): WidgetPresentation {
        return cachedPresentation ?: run {
            val sdkStatus = detectSdkStatus()
            val presentation = createPresentation(sdkStatus)
            cachedPresentation = presentation
            presentation
        }
    }

    private fun createPresentation(status: SdkStatus): WidgetPresentation {
        return when (status) {
            is SdkStatus.Configured -> WidgetPresentation(
                text = "Elixir: ${status.elixirVersion}",
                icon = Icons.LANGUAGE,
                tooltip = "Elixir SDK: ${status.elixirVersion} (Configured correctly)"
            )

            is SdkStatus.Warning -> WidgetPresentation(
                text = "Elixir: ${status.elixirVersion} !",
                icon = Icons.LANGUAGE,
                tooltip = "Elixir SDK: ${status.elixirVersion} (Warning: ${status.issues.joinToString(", ")})"
            )

            is SdkStatus.Partial -> WidgetPresentation(
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

    private fun notifyIfNeeded() {
        val sdkStatus = detectSdkStatus()
        val issueKey = computeIssueKey(sdkStatus)

        // No issue, or same issue already notified — skip
        if (issueKey == null) {
            lastNotifiedIssueKey = null
            return
        }
        if (issueKey == lastNotifiedIssueKey) return
        lastNotifiedIssueKey = issueKey

        val (title, message, type) = buildNotificationContent(sdkStatus) ?: return

        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(title, message, type)

        notification.addAction(object : AnAction("Open Project Structure") {
            override fun actionPerformed(e: AnActionEvent) {
                SdkSettingsOpener.getInstance().open(e)
                notification.expire()
            }
        })

        notification.notify(project)
        LOG.warn("SDK discrepancy in project '${project.name}': $message")
    }

    private fun computeIssueKey(status: SdkStatus): String? {
        return when (status) {
            is SdkStatus.Configured -> null
            is SdkStatus.NotConfigured -> "not-configured"
            is SdkStatus.Partial -> "partial:${status.issue}"
            is SdkStatus.Warning -> "warning:${status.issues.sorted().joinToString(",")}"
            is SdkStatus.ModuleSdkError -> "module-error:${status.moduleSdkIssues.map { "${it.moduleName}:${it.isDangling}:${it.issue}" }.sorted().joinToString(",")}"
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

            is SdkStatus.Partial -> NotificationContent(
                "Elixir SDK Issue",
                "Elixir SDK: ${status.elixirVersion ?: "Unknown"} — ${status.issue}.",
                NotificationType.WARNING
            )

            is SdkStatus.Warning -> NotificationContent(
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
        }
    }

    private fun setupListeners() {
        messageBusConnection = project.messageBus.connect(this)

        // Listen for project JDK table changes (SDK added/removed/modified)
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

        // Listen for project root changes (project SDK changes)
        messageBusConnection?.subscribe(
            com.intellij.openapi.roots.ModuleRootListener.TOPIC,
            object : com.intellij.openapi.roots.ModuleRootListener {
                override fun rootsChanged(event: com.intellij.openapi.roots.ModuleRootEvent) {
                    updateWidget()
                }
            })

        // Listen for SDK refresh events (from RefreshAllElixirSdksAction)
        messageBusConnection?.subscribe(ElixirSdkRefreshListener.TOPIC, ElixirSdkRefreshListener {
            updateWidget()
        })
    }

    private fun detectSdkStatus(): SdkStatus {
        val elixirSdk = Type.mostSpecificSdk(project) ?: return SdkStatus.NotConfigured
        val versionString = elixirSdk.versionString ?: "Unknown"

        // Add WSL distribution suffix if this is a WSL SDK
        val elixirVersion = org.elixir_lang.sdk.Type.appendWslSuffix(versionString, elixirSdk.homePath)

        if (!isValidSdk(elixirSdk)) {
            return SdkStatus.Partial(elixirSdk, elixirVersion, "Invalid Elixir SDK")
        }

        val erlangSdk = getErlangSdk(elixirSdk)
            ?: return SdkStatus.Partial(elixirSdk, elixirVersion, "Missing Erlang SDK")

        // Check module-level SDK consistency (dangling references or mismatches)
        val moduleSdkIssues = detectModuleSdkIssues()
        if (moduleSdkIssues.isNotEmpty()) {
            return SdkStatus.ModuleSdkError(elixirSdk, elixirVersion, moduleSdkIssues)
        }

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

        return if (issues.isEmpty()) {
            SdkStatus.Configured(elixirSdk, erlangSdk, elixirVersion)
        } else {
            SdkStatus.Warning(elixirSdk, erlangSdk, elixirVersion, issues)
        }
    }

    private fun detectModuleSdkIssues(): List<ModuleSdkIssue> {
        val issues = mutableListOf<ModuleSdkIssue>()
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        val hasRootMixExs = project.basePath?.let { basePath ->
            LocalFileSystem.getInstance().findFileByPath(basePath)?.findChild("mix.exs")
        } != null

        for (module in ModuleManager.getInstance(project).modules) {
            val rootManager = ModuleRootManager.getInstance(module)

            for (entry in rootManager.orderEntries) {
                if (entry !is JdkOrderEntry) continue

                val jdkName = entry.jdkName ?: continue
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
                } else if (hasRootMixExs && projectSdk != null && resolvedSdk != projectSdk) {
                    // MISMATCH: Module uses different SDK than project in a single-version project
                    issues.add(
                        ModuleSdkIssue(
                            module.name,
                            "uses '$jdkName' but project SDK is '${projectSdk.name}'",
                            isDangling = false
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
