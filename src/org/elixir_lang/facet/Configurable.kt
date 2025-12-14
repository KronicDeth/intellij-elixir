package org.elixir_lang.facet

import com.intellij.openapi.module.Module
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.components.JBLabel
import org.elixir_lang.facet.sdk.ComboBox
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Either project or module
 */
abstract class Configurable(val module: Module) : UnnamedConfigurable {
    abstract fun initSdk(): Sdk?
    abstract fun applySdk(sdk: Sdk?)
    private val sdksService by lazy { SdksService.getInstance()!! }
    private val projectSdksModel by lazy { sdksService.getModel() }
    var sdk: Sdk? = null
    private lateinit var rootPanel: JPanel
    private lateinit var sdkComboBox: ComboBox
//    private lateinit var deleteButton: JButton

    // https://github.com/JetBrains/intellij-community/blob/84601d73c4ae4cc3615bcd73304e5b32e8ef8686/python/python-community-configure/src/com/jetbrains/python/configuration/PythonSdkDetailsDialog.java#L119-L159
    override fun createComponent(): JComponent {
        if (!::rootPanel.isInitialized) {
            sdkComboBox = ComboBox()
//            deleteButton = JButton("Delete SDK", AllIcons.General.Remove).apply {
//                toolTipText = "Delete the selected SDK configuration"
//                addActionListener {
//                    deleteSelectedSdk()
//                }
//            }

            rootPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0)).apply {
                add(JBLabel("Elixir SDK:"))
                add(sdkComboBox)
//                add(deleteButton)
            }

            // Update delete button state when selection changes
//            sdkComboBox.addActionListener {
//                updateDeleteButtonState()
//            }
//            updateDeleteButtonState()
        }
        return rootPanel
    }

//    private fun updateDeleteButtonState() {
//        if (::deleteButton.isInitialized) {
//            deleteButton.isEnabled = sdkComboBox.selectedItem != null
//        }
//    }
//
//    private fun deleteSelectedSdk() {
//        val selectedSdk = sdkComboBox.selectedItem as? Sdk ?: return
//
//        val result = Messages.showYesNoDialog(
//            rootPanel,
//            "Are you sure you want to delete the SDK '${selectedSdk.name}'?\n\nThis will remove it from all projects using this SDK.",
//            "Delete SDK",
//            Messages.getQuestionIcon()
//        )
//
//        if (result == Messages.YES) {
//            // Remove from the project model
//            projectSdksModel.removeSdk(selectedSdk)
//
//            // Select null (no SDK) in the combo box
//            sdkComboBox.selectedItem = null
//
//            // Update button state
//            updateDeleteButtonState()
//        }
//    }

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
//            updateDeleteButtonState()
        }
    }
}
