package org.elixir_lang.sdk.erlang_dependent

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
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

        for (sdk in sdkModel.sdks) {
            if (Type.staticIsValidDependency(sdk)) {
                internalErlangSdksComboBoxModel.addElement(sdk)
            }
        }
    }

    override fun setSdk(sdk: Sdk) {
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
                    updateWarningLabel()
                }
            }
        }

        modified = true
        updateWarningLabel()

        return wholePanel
    }

    private fun internalErlangSdkUpdate(sdk: Sdk) {
        val sdkAdditionalData = sdk.sdkAdditionalData as SdkAdditionalData?

        val erlangSdk = sdkAdditionalData?.getErlangSdk()

        if (erlangSdk == null || internalErlangSdksComboBoxModel.getIndexOf(erlangSdk) == -1) {
            internalErlangSdksComboBoxModel.addElement(erlangSdk)
        } else {
            internalErlangSdksComboBoxModel.setSelectedItem(erlangSdk)
        }
    }

    override fun isModified(): Boolean = modified

    @Throws(ConfigurationException::class)
    override fun apply() {
        writeInternalErlangSdk(internalErlangSdksComboBox.selectedItem as Sdk)
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
        freeze = true
        updateJdkList()
        freeze = false

        if (elixirSdk != null && elixirSdk!!.sdkAdditionalData is SdkAdditionalData) {
            val sdkAdditionalData = elixirSdk!!.sdkAdditionalData as SdkAdditionalData?
            val erlangSdk = sdkAdditionalData!!.getErlangSdk()

            if (erlangSdk != null) {
                for (i in 0 until internalErlangSdksComboBoxModel.size) {
                    if (Comparing.strEqual(
                            internalErlangSdksComboBoxModel.getElementAt(i)!!.name,
                            erlangSdk.name,
                        )
                    ) {
                        internalErlangSdksComboBox.selectedIndex = i
                        break
                    }
                }
            }

            modified = false
            updateWarningLabel()
        }
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
        val sdks = sdkModel.sdks

        for (currentSdk in sdks) {
            if (currentSdk.sdkType is org.elixir_lang.sdk.elixir.Type) {
                val sdkAdditionalData = currentSdk.sdkAdditionalData as SdkAdditionalData?
                val erlangSdk = sdkAdditionalData!!.getErlangSdk()

                if (erlangSdk != null && erlangSdk.name == previousName) {
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
}
