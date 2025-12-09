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

    override fun createComponent(): JComponent {
        if (!::rootPanel.isInitialized) {
            sdkComboBox = ComboBox()
            rootPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0)).apply {
                add(JBLabel("Elixir SDK:"))
                add(sdkComboBox)
            }
        }
        return rootPanel
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
        }
    }
}
