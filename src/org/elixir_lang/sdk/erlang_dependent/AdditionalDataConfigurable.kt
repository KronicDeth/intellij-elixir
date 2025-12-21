package org.elixir_lang.sdk.erlang_dependent

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Comparing
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.util.ui.JBUI
import org.elixir_lang.sdk.elixir.Type.Companion.addNewCodePathsFromInternErlangSdk
import org.elixir_lang.sdk.elixir.Type.Companion.hasErlangClasspathInElixirSdk
import org.elixir_lang.sdk.elixir.Type.Companion.removeCodePathsFromInternalErlangSdk
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.*

class AdditionalDataConfigurable(
    private val sdkModel: SdkModel,
    private val sdkModificator: SdkModificator,
) : AdditionalDataConfigurable {
    private val internalErlangSdkLabel = JLabel("Internal Erlang SDK:")
    private val internalErlangSdksComboBoxModel = DefaultComboBoxModel<Sdk?>()
    private val internalErlangSdksComboBox = ComboBox<Sdk>(internalErlangSdksComboBoxModel)
    private val classpathWarningLabel = JLabel("Erlang classpath entries missing from Elixir SDK").apply {
        icon = AllIcons.General.Warning
        isVisible = false
    }
    private val saveHintLabel = JLabel("After changing the Erlang SDK, save settings and reopen this dialog to see updates").apply {
        icon = AllIcons.General.Information
        isVisible = false
    }
    private val sdkModelListener: SdkModel.Listener
    private var elixirSdk: Sdk? = null
    private var modified = false
    private var freeze = false

    init {
        sdkModelListener =
            object : SdkModel.Listener {
                override fun sdkAdded(sdk: Sdk) {
                    if (Type.staticIsValidDependency(sdk)) {
                        addErlangSdk(sdk)
                    }
                }

                override fun beforeSdkRemove(sdk: Sdk) {
                    if (Type.staticIsValidDependency(sdk)) {
                        removeErlangSdk(sdk)
                    }
                }

                override fun sdkChanged(
                    sdk: Sdk,
                    previousName: String,
                ) {
                    if (Type.staticIsValidDependency(sdk)) {
                        updateErlangSdkList(sdk, previousName)
                    }
                }

                override fun sdkHomeSelected(
                    sdk: Sdk,
                    newSdkHome: String,
                ) {
                    if (Type.staticIsValidDependency(sdk)) {
                        internalErlangSdkUpdate(sdk)
                    }
                }
            }
        sdkModel.addListener(sdkModelListener)
    }

    private fun updateJdkList() {
        internalErlangSdksComboBoxModel.removeAllElements()

        val validErlangSdks = mutableListOf<String>()
        for (sdk in sdkModel.sdks) {
            if (Type.staticIsValidDependency(sdk)) {
                internalErlangSdksComboBoxModel.addElement(sdk)
                validErlangSdks.add(sdk.name)
            }
        }
        LOG.debug("[updateJdkList] Found ${validErlangSdks.size} valid Erlang SDKs: $validErlangSdks")
    }

    override fun setSdk(sdk: Sdk) {
        LOG.debug("[setSdk] Setting Elixir SDK: ${sdk.name} (additionalData=${sdk.sdkAdditionalData?.javaClass?.simpleName})")
        elixirSdk = sdk
    }

    override fun createComponent(): JComponent {
        val wholePanel = JPanel(GridBagLayout())

        wholePanel.add(
            internalErlangSdkLabel,
            GridBagConstraints(
                0,
                GridBagConstraints.RELATIVE,
                1,
                1,
                0.0,
                1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                JBUI.emptyInsets(),
                0,
                0,
            ),
        )
        wholePanel.add(
            internalErlangSdksComboBox,
            GridBagConstraints(
                1,
                GridBagConstraints.RELATIVE,
                1,
                1,
                1.0,
                1.0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                JBUI.insetsLeft(30),
                0,
                0,
            ),
        )
        wholePanel.add(
            classpathWarningLabel,
            GridBagConstraints(
                0,
                GridBagConstraints.RELATIVE,
                2,
                1,
                1.0,
                0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                JBUI.insetsTop(8),
                0,
                0,
            ),
        )
        wholePanel.add(
            saveHintLabel,
            GridBagConstraints(
                0,
                GridBagConstraints.RELATIVE,
                2,
                1,
                1.0,
                0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                JBUI.insetsTop(4),
                0,
                0,
            ),
        )
        internalErlangSdksComboBox.setRenderer(
            object : SimpleListCellRenderer<Sdk>() {
                override fun customize(
                    list: JList<out Sdk>,
                    value: Sdk?,
                    index: Int,
                    selected: Boolean,
                    hasFocus: Boolean,
                ) {
                    text = value?.name
                }
            },
        )
        internalErlangSdksComboBox.addItemListener { itemEvent: ItemEvent ->
            if (!freeze) {
                val stateChange = itemEvent.stateChange
                val internalErlangSdk = itemEvent.item as Sdk

                if (stateChange == ItemEvent.DESELECTED) {
                    removeCodePathsFromInternalErlangSdk(
                        elixirSdk!!,
                        internalErlangSdk,
                        sdkModificator,
                    )
                } else if (stateChange == ItemEvent.SELECTED) {
                    addNewCodePathsFromInternErlangSdk(
                        elixirSdk!!,
                        internalErlangSdk,
                        sdkModificator,
                    )
                    // Force save to work around JetBrains settings persistence bug
                    ApplicationManager.getApplication().runWriteAction {
                        sdkModificator.commitChanges()
                    }
                    modified = true
                    // Show hint to save and reopen
                    saveHintLabel.isVisible = true
                    updateWarningLabel()
                }
            }
        }

        modified = true
        updateWarningLabel()

        return wholePanel
    }

    private fun internalErlangSdkUpdate(sdk: Sdk) {
        LOG.debug("[internalErlangSdkUpdate] SDK home selected for: ${sdk.name}")
        val sdkAdditionalData = sdk.sdkAdditionalData as? SdkAdditionalData

        val erlangSdk = sdkAdditionalData?.getErlangSdk(sdkModel) ?: findFirstErlangSdkInModel()
        LOG.debug("[internalErlangSdkUpdate] Erlang SDK: ${erlangSdk?.name}")

        if (erlangSdk == null || internalErlangSdksComboBoxModel.getIndexOf(erlangSdk) == -1) {
            LOG.debug("[internalErlangSdkUpdate] Adding Erlang SDK to combo box: ${erlangSdk?.name}")
            internalErlangSdksComboBoxModel.addElement(erlangSdk)
        } else {
            LOG.debug("[internalErlangSdkUpdate] Selecting existing Erlang SDK in combo box: ${erlangSdk.name}")
            internalErlangSdksComboBoxModel.setSelectedItem(erlangSdk)
        }
    }

    override fun isModified(): Boolean = modified

    @Throws(ConfigurationException::class)
    override fun apply() {
        writeInternalErlangSdk(internalErlangSdksComboBox.selectedItem as? Sdk)
        modified = false
    }

    private fun writeInternalErlangSdk(erlangSdk: Sdk?) {
        writeInternalErlangSdk(elixirSdk!!.sdkModificator, erlangSdk)
    }

    private fun writeInternalErlangSdk(
        sdkModificator: SdkModificator,
        erlangSdk: Sdk?,
    ) {
        val sdkAdditionData =
            SdkAdditionalData(
                erlangSdk,
                elixirSdk!!,
            )
        sdkModificator.sdkAdditionalData = sdkAdditionData
        ApplicationManager.getApplication().runWriteAction { sdkModificator.commitChanges() }

        this.sdkModificator.commitChanges()
    }

    override fun reset() {
        val sdk = elixirSdk
        val sdkName = sdk?.name ?: "null"
        LOG.debug("[$sdkName] reset() called")

        freeze = true
        updateJdkList()
        LOG.debug("[$sdkName] Populated combo box with ${internalErlangSdksComboBoxModel.size} Erlang SDKs")
        freeze = false

        if (sdk == null) {
            LOG.debug("[$sdkName] No Elixir SDK set, skipping Erlang SDK selection")
            return
        }

        // Try to get the Erlang SDK from additional data, using sdkModel for lookup
        val erlangSdk = when (val additionalData = sdk.sdkAdditionalData) {
            is SdkAdditionalData -> {
                LOG.debug("[$sdkName] Has SdkAdditionalData, looking up Erlang SDK (erlangSdkName=${additionalData.getErlangSdkName()})")
                additionalData.getErlangSdk(sdkModel)
            }
            null -> {
                LOG.debug("[$sdkName] sdkAdditionalData is null, auto-discovering Erlang SDK from model")
                findFirstErlangSdkInModel()
            }
            else -> {
                LOG.debug("[$sdkName] sdkAdditionalData is ${additionalData::class.java.simpleName}, auto-discovering Erlang SDK from model")
                findFirstErlangSdkInModel()
            }
        }

        if (erlangSdk != null) {
            LOG.debug("[$sdkName] Looking for Erlang SDK '${erlangSdk.name}' in combo box")
            var found = false
            for (i in 0 until internalErlangSdksComboBoxModel.size) {
                val element = internalErlangSdksComboBoxModel.getElementAt(i)
                if (Comparing.strEqual(element?.name, erlangSdk.name)) {
                    LOG.debug("[$sdkName] Found Erlang SDK at index $i, selecting")
                    internalErlangSdksComboBox.selectedIndex = i
                    found = true
                    break
                }
            }
            if (!found) {
                LOG.debug("[$sdkName] Erlang SDK '${erlangSdk.name}' not found in combo box (size=${internalErlangSdksComboBoxModel.size})")
            }
        } else {
            LOG.debug("[$sdkName] No Erlang SDK found to select")
        }

        modified = false
        saveHintLabel.isVisible = false
        updateWarningLabel()
    }

    /**
     * Finds the first valid Erlang SDK in the sdkModel.
     * Used when sdkAdditionalData is null/invalid and we need to auto-discover.
     */
    private fun findFirstErlangSdkInModel(): Sdk? {
        for (sdk in sdkModel.sdks) {
            if (Type.staticIsValidDependency(sdk)) {
                LOG.debug("[findFirstErlangSdkInModel] Found Erlang SDK: ${sdk.name}")
                return sdk
            }
        }
        LOG.debug("[findFirstErlangSdkInModel] No valid Erlang SDK found in model (${sdkModel.sdks.size} total SDKs)")
        return null
    }

    override fun disposeUIResources() {
        sdkModel.removeListener(sdkModelListener)
    }

    private fun addErlangSdk(sdk: Sdk) {
        internalErlangSdksComboBoxModel.addElement(sdk)
    }

    private fun removeErlangSdk(sdk: Sdk) {
        if (internalErlangSdksComboBoxModel.selectedItem == sdk) {
            modified = true
        }

        internalErlangSdksComboBoxModel.removeElement(sdk)
    }

    private fun updateErlangSdkList(
        sdk: Sdk,
        previousName: String,
    ) {
        LOG.debug("[updateErlangSdkList] Erlang SDK renamed: '$previousName' -> '${sdk.name}'")
        val sdks = sdkModel.sdks

        for (currentSdk in sdks) {
            if (currentSdk.sdkType is org.elixir_lang.sdk.elixir.Type) {
                val sdkAdditionalData = currentSdk.sdkAdditionalData as? SdkAdditionalData
                if (sdkAdditionalData == null) {
                    LOG.debug("[updateErlangSdkList] Skipping ${currentSdk.name}: no SdkAdditionalData")
                    continue
                }
                val erlangSdk = sdkAdditionalData.getErlangSdk(sdkModel)

                if (erlangSdk != null && erlangSdk.name == previousName) {
                    LOG.debug("[updateErlangSdkList] Updating ${currentSdk.name} to use renamed Erlang SDK")
                    sdkAdditionalData.setErlangSdk(sdk)
                }
            }
        }
        updateJdkList()
    }

    private fun updateWarningLabel() {
        val sdk = elixirSdk ?: run {
            classpathWarningLabel.isVisible = false
            return
        }
        val erlangSdk = (internalErlangSdksComboBox.selectedItem as? Sdk) ?: run {
            classpathWarningLabel.isVisible = false
            return
        }
        classpathWarningLabel.isVisible = !hasErlangClasspathInElixirSdk(sdk, erlangSdk)
    }

    companion object {
        private val LOG = Logger.getInstance(AdditionalDataConfigurable::class.java)
    }
}
