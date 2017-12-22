package org.elixir_lang.facet.sdks

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.IdeBorderFactory.createEmptyBorder
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.util.containers.FactoryMap
import org.elixir_lang.facet.SdksService
import org.elixir_lang.sdk.elixir.Type
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel

class Configurable: SearchableConfigurable, com.intellij.openapi.options.Configurable.NoScroll {
    private val modifiedSdkModificatorSet: MutableSet<SdkModificator> = HashSet()
    internal val sdksService by lazy { SdksService.getInstance()!! }
    private val projectSdksModel by lazy { sdksService.getModel() }
    private lateinit var rootSplitter: Splitter
    private val sdkModificatorBySdk: MutableMap<Sdk, SdkModificator> = object : FactoryMap<Sdk, SdkModificator>(){
        override fun create(sdk: Sdk): SdkModificator {
            return sdk.sdkModificator
        }
    }
    private lateinit var sdkList: List
    private lateinit var sdkListPanel: JPanel

    override fun apply() {
        modifiedSdkModificatorSet.forEach {
            if (it.isWritable) {
                it.commitChanges()
            }
        }
        sdkModificatorBySdk.clear()
        modifiedSdkModificatorSet.clear()
    }

    override fun createComponent(): JComponent? {
        sdkList = List(this)

        val decorator = ToolbarDecorator
                .createDecorator(sdkList)
                .disableUpDownActions()
                .setAddAction({
                    addSdk()
                })
                .setEditAction(null)
                .setRemoveAction({
                    removeSdk()
                })

        sdkListPanel = decorator.createPanel()
        sdkList.refresh()
        addListeners()
        // TODO show normal SDK configuration
        val sdkPanel = JPanel()
        rootSplitter = OnePixelSplitter(false).apply {
            border = createEmptyBorder()
            firstComponent = sdkListPanel
            secondComponent = sdkPanel
            isShowDividerControls = false
            setHonorComponentsMinimumSize(true)
        }

        return rootSplitter
    }

    override fun getDisplayName(): String = "SDKs"
    override fun getHelpTopic(): String? = null
    override fun getId(): String = "language.elixir.sdks"

    override fun isModified(): Boolean = projectSdksModel.isModified || !modifiedSdkModificatorSet.isEmpty()

    override fun reset() {
        sdkList.refresh()
    }

    private fun addCreatedSdk(sdk: Sdk?) {
        sdk?.let {
            if (it.sdkType == Type.getInstance()) {
                sdkList.apply {
                    refresh()
                    setSelectedValue(sdk, true)
                }
            }
        }
    }

    private fun addListeners() {
        val listener = object : SdkModel.Listener {
            override fun beforeSdkRemove(sdk: Sdk) {
            }

            override fun sdkAdded(sdk: Sdk) {
            }

            override fun sdkChanged(sdk: Sdk?, previousName: String?) = sdkList.refresh()

            override fun sdkHomeSelected(sdk: Sdk, newSdkHome: String?) {
            }
        }
        projectSdksModel.addListener(listener)
        sdkList.selectionModel.addListSelectionListener { updateSdkPanel(sdkList.selectedValue) }
    }

    private fun addSdk() {
        projectSdksModel.doAdd(sdkListPanel, Type.getInstance(), { sdk -> addCreatedSdk(sdk)  })
    }

    private fun removeSdk() {
        sdkList.selectedValue?.let {
            val sdk = projectSdksModel.findSdk(it)
            SdkConfigurationUtil.removeSdk(sdk)

            projectSdksModel.removeSdk(sdk)
            projectSdksModel.removeSdk(it)

            if (sdkModificatorBySdk.containsKey(it)) {
                val modificator = sdkModificatorBySdk[it]
                modifiedSdkModificatorSet.remove(modificator)
                sdkModificatorBySdk.remove(it)
            }

            sdkList.refresh()
        }
    }

    private fun updateSdkPanel(selectedValue: Sdk?) {
       // TODO switch SdkPanel
    }
}
