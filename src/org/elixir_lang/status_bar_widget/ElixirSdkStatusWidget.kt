package org.elixir_lang.status_bar_widget

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.messages.MessageBusConnection
import org.elixir_lang.Icons
import org.elixir_lang.jps.HomePath
import org.elixir_lang.sdk.elixir.Type
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.jetbrains.annotations.NotNull

class ElixirSdkStatusWidget(@param:NotNull private val project: Project) : StatusBarWidget,
    StatusBarWidget.MultipleTextValuesPresentation {

    companion object {
        const val ID = "ElixirSdkStatus"

        fun isAvailableOnProject(@Suppress("UNUSED_PARAMETER") project: Project): Boolean {
            // Only show widget if experimental setting is enabled
            return ElixirExperimentalSettings.instance.state.enableStatusBarWidget
        }
    }

    private var statusBar: StatusBar? = null
    private var messageBusConnection: MessageBusConnection? = null

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

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

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
    }

    // MultipleTextValuesPresentation implementation
    override fun getSelectedValue(): String = getCurrentPresentation().text

    override fun getIcon(): javax.swing.Icon? = getCurrentPresentation().icon

    override fun getPopup(): JBPopup? = null

    override fun getTooltipText(): String = getCurrentPresentation().tooltip

    private fun updateWidget() {
        // Invalidate cached presentation when updating
        cachedPresentation = null
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
                text = "Elixir: ${status.elixirVersion} ⚠",
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
    }

    private fun detectSdkStatus(): SdkStatus {
        val elixirSdk = Type.mostSpecificSdk(project) ?: return SdkStatus.NotConfigured
        val elixirVersion = elixirSdk.versionString ?: "Unknown"

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
