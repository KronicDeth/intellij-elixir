package org.elixir_lang.facet

import com.intellij.openapi.module.Module
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.facet.sdk.ComboBox
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
/**
 * Either project or module
 */
abstract class Configurable(val module: Module) : UnnamedConfigurable {
    abstract fun initSdk(): Sdk?
    abstract fun applySdk(sdk: Sdk?)
    private val sdksService by lazy { SdksService.getInstance() }
    private val projectSdksModel by lazy { sdksService.getModel() }
    var sdk: Sdk? = null
    private lateinit var sdkComboBox: ComboBox

    // https://github.com/JetBrains/intellij-community/blob/84601d73c4ae4cc3615bcd73304e5b32e8ef8686/python/python-community-configure/src/com/jetbrains/python/configuration/PythonSdkDetailsDialog.java#L119-L159
    override fun createComponent(): JComponent {
        return JPanel(GridBagLayout()).apply {
            sdkComboBox = ComboBox()
            sdkComboBox.selectedItem = initSdk()

            val sdkLabel = JLabel("SDK:").apply {
                labelFor = sdkComboBox
            }

            add(sdkLabel, GridBagConstraints().apply { anchor = WEST; })
            add(sdkComboBox, GridBagConstraints().apply { anchor = WEST; fill = HORIZONTAL; gridx = 1 })
            add(JPanel(), GridBagConstraints().apply { anchor = WEST; fill = BOTH; gridwidth = 2; gridy = 1 })
        }
    }

    override fun isModified(): Boolean {
        val existingInitialSdk = initSdk()?.let {
            projectSdksModel.findSdk(it.name)
        }

        return existingInitialSdk != sdkComboBox.selectedItem
    }

    override fun apply() {
        applySdk(sdkComboBox.selectedItem as Sdk?)
    }

    override fun reset() {
        sdkComboBox.selectedItem = initSdk()
    }
}
