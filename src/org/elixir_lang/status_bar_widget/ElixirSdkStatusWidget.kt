package org.elixir_lang.status_bar_widget

import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.ui.ClickListener
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.JBUI
import org.elixir_lang.Icons
import org.elixir_lang.action.RefreshAllElixirSdksAction
import org.elixir_lang.jps.HomePath
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

        data object NotConfigured : SdkStatus
    }

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
            add(RefreshAllElixirSdksAction())
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
        return object : AnAction("Add Elixir SDK...", "Open Project Structure to add an Elixir SDK", AllIcons.General.Add) {
            override fun actionPerformed(e: AnActionEvent) {
                // Open Project Structure dialog (File -> Project Structure)
                val action = ActionManager.getInstance().getAction("ShowProjectStructureSettings")
                if (action != null) {
                    ActionUtil.performAction(action, e)
                }
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

            SdkStatus.NotConfigured -> {
                val availableElixirSdks = ProjectJdkTable.getInstance().allJdks.filter { it.sdkType is Type }
                val tooltip = if (availableElixirSdks.isNotEmpty()) {
                    "No Elixir SDK configured for this project (${availableElixirSdks.size} Elixir SDK(s) available)"
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
        if (!HomePath.hasEbinPath(homePath)) {
            return true
        }

        // Check if SDK has proper classpath roots configured
        val classRoots = elixirSdk.rootProvider.getFiles(com.intellij.openapi.roots.OrderRootType.CLASSES)
        return classRoots.isEmpty()
    }

    private fun hasErlangSdkIssues(erlangSdk: Sdk): Boolean {
        val homePath = erlangSdk.homePath ?: return true

        // Check if Erlang SDK has proper ebin paths
        if (!HomePath.hasEbinPath(homePath)) {
            return true
        }

        // Validate that this is a proper Erlang SDK type
        return !org.elixir_lang.sdk.erlang_dependent.Type.staticIsValidDependency(erlangSdk)
    }

}
