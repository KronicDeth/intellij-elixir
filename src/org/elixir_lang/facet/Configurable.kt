package org.elixir_lang.facet

import com.intellij.openapi.module.Module
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.panel
import org.elixir_lang.facet.sdk.ComboBox
import org.elixir_lang.sdk.elixir.ModuleSdkStatus
import org.elixir_lang.sdk.elixir.summaryHtml
import org.elixir_lang.tool_manager.ToolManagerSdkAnalyser
import org.elixir_lang.tool_manager.ToolManagerSdkCheckerService
import javax.swing.JButton
import javax.swing.JComponent

/**
 * Either project or module
 */
abstract class Configurable(val module: Module) : UnnamedConfigurable {
    abstract fun initSdk(): Sdk?
    abstract fun applySdk(sdk: Sdk?)
    private val project get() = module.project
    private val sdksService by lazy { SdksService.getInstance()!! }
    private val projectSdksModel by lazy { sdksService.getModel() }
    var sdk: Sdk? = null
    private lateinit var rootPanel: DialogPanel
    private lateinit var sdkComboBox: ComboBox

    /** Live status line under the selector, mirroring the status-bar widget's per-module state. */
    private lateinit var statusLabel: JBLabel

    override fun createComponent(): JComponent {
        if (!::rootPanel.isInitialized) {
            sdkComboBox = ComboBox()
            statusLabel = JBLabel()
            val toolManagerButton = createToolManagerButton()

            rootPanel = panel {
                row("Elixir SDK:") {
                    cell(sdkComboBox)
                    toolManagerButton?.let { cell(it) }
                }
                row { cell(statusLabel) }
            }

            // Live update: reflect whatever SDK is currently picked in the dropdown, before Apply.
            sdkComboBox.addActionListener { updateStatusLabel() }
            updateStatusLabel()
        }
        return rootPanel
    }

    /**
     * A "Configure from <tool manager>" button, present only when the tool manager (e.g. mise) has
     * an installed Elixir version assigned to **this** module. Clicking configures only this module,
     * leaving other modules (which may lack a tool-manager config, or be deliberately configured
     * differently) untouched.
     */
    private fun createToolManagerButton(): JButton? {
        val analyser = ToolManagerSdkAnalyser.getInstanceIfRegistered(project) ?: return null
        val versions = analyser.latestAnalysisResult
            ?.tmAssignments
            ?.get(module.name)
            ?.takeIf { it.elixir?.installed == true }
            ?: return null

        return JButton("Configure from ${versions.toolManagerName}").apply {
            addActionListener {
                ToolManagerSdkCheckerService.getInstance(project).configureSdks(mapOf(module.name to versions))
                onModuleConfiguredExternally()
            }
        }
    }

    /**
     * After the tool-manager button configures this module, [ToolManagerSdkChecker.configureSdks] has
     * registered the SDK in the JDK table and set this module's Facet SDK directly (bypassing the
     * dialog's model). Surface it in the dropdown by adding it to the model (which fires `sdkAdded`
     * so the combo picks it up) and selecting it. Deliberately avoids `ProjectSdksModel.reset`, which
     * re-clones the shared model and would invalidate SDK references held by other open SDK views.
     */
    private fun onModuleConfiguredExternally() {
        val newSdk = initSdk() ?: return
        if (projectSdksModel.findSdk(newSdk.name) == null) {
            projectSdksModel.addSdk(newSdk)
        }
        sdkComboBox.selectedItem = projectSdksModel.findSdk(newSdk.name)
        updateStatusLabel()
    }

    private fun updateStatusLabel() {
        if (::statusLabel.isInitialized) {
            statusLabel.text = "<html>${ModuleSdkStatus.of(sdkComboBox.selectedItem as? Sdk).summaryHtml()}</html>"
        }
    }

    override fun isModified(): Boolean {
        if (!::sdkComboBox.isInitialized) {
            return false
        }

        val existingInitialSdk = initSdk()?.let {
            projectSdksModel.findSdk(it.name)
        }

        return existingInitialSdk != sdkComboBox.selectedItem
    }

    override fun apply() {
        if (::sdkComboBox.isInitialized) {
            applySdk(sdkComboBox.selectedItem as Sdk?)
        }
    }

    override fun reset() {
        if (::sdkComboBox.isInitialized) {
            sdkComboBox.selectedItem = initSdk()?.let { projectSdksModel.findSdk(it.name) }
            updateStatusLabel()
        }
    }
}
