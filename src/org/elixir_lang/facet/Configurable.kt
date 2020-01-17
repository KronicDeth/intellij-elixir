package org.elixir_lang.facet

import com.intellij.openapi.module.Module
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.facet.sdk.ComboBox
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

    // https://github.com/JetBrains/intellij-community/blob/84601d73c4ae4cc3615bcd73304e5b32e8ef8686/python/python-community-configure/src/com/jetbrains/python/configuration/PythonSdkDetailsDialog.java#L119-L159
    override fun createComponent(): JComponent = rootPanel

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
        sdkComboBox.selectedItem =  initSdk()?.let { projectSdksModel.findSdk(it.name) }
    }
}
