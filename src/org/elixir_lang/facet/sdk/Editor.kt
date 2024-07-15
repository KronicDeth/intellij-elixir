package org.elixir_lang.facet.sdk

import com.google.common.collect.Lists
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.projectRoots.ui.PathEditor
import com.intellij.openapi.projectRoots.ui.SdkPathEditor
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.ActionCallback
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.navigation.History
import com.intellij.ui.navigation.Place
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import java.util.*
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class Editor(private val sdkModel: SdkModel, private val history: History, private var sdk: ProjectJdkImpl) :
    Configurable, Place.Navigator {
    private val sdkPathEditorByOrderRootType = HashMap<OrderRootType, SdkPathEditor>()
    private val additionalDataConfigurableListBySdkType = HashMap<SdkType, MutableList<AdditionalDataConfigurable>>()
    private val componentByAdditionalDataConfigurable = HashMap<AdditionalDataConfigurable, JComponent?>()
    private val editedSdkModificator = this.EditedSdkModificator()
    private val disposable = Disposer.newDisposable()
    private var _versionString: String? = null
    private var initialName: String = sdk.name
    private var initialPath: String? = sdk.homePath

    private val homeFieldLabelValue: String
        get() = (sdk.sdkType as SdkType).homeFieldLabel

    private val homeValue: String
        get() = homeComponent.text.trim { it <= ' ' }

    private val additionalDataConfigurable: List<AdditionalDataConfigurable>
        get() = initAdditionalDataConfigurable(sdk)

    // GUI components
    private lateinit var additionalDataPanel: JPanel
    private lateinit var homeComponent: TextFieldWithBrowseButton
    private lateinit var mainPanel: JPanel
    private lateinit var tabbedPane: TabbedPaneWrapper
    private lateinit var homeFieldLabel: JLabel

    init {
        createMainPanel()
        additionalDataConfigurable.forEach { additionalDataConfigurable -> additionalDataConfigurable.setSdk(sdk) }
        reset()
    }

    override fun getDisplayName(): String = "SDK Editor"

    override fun getHelpTopic(): String? = null

    override fun createComponent(): JComponent? {
        return mainPanel
    }

    private fun createMainPanel() {
        mainPanel = JPanel(GridBagLayout())

        tabbedPane = TabbedPaneWrapper(disposable)

        OrderRootType.getAllTypes()
            .filter { showTabForType(it) }
            .forEach { orderRootType ->
                pathEditor(orderRootType)?.let { pathEditor ->
                    pathEditor.setAddBaseDir(sdk.homeDirectory)
                    tabbedPane.addTab(pathEditor.displayName, pathEditor.createComponent())
                    sdkPathEditorByOrderRootType.put(orderRootType, pathEditor)
                }
            }

        tabbedPane.addChangeListener { history.pushQueryPlace() }

        homeComponent = createHomeComponent()
        homeComponent.textField.isEditable = false
        homeFieldLabel = JLabel(homeFieldLabelValue)
        mainPanel.add(
            homeFieldLabel, GridBagConstraints(
                0,
                GridBagConstraints.RELATIVE,
                1,
                1,
                0.0,
                0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                JBUI.insets(2, 10, 2, 2),
                0,
                0
            )
        )
        mainPanel.add(
            homeComponent, GridBagConstraints(
                1,
                GridBagConstraints.RELATIVE,
                1,
                1,
                1.0,
                0.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,
                JBUI.insets(2, 2, 2, 10),
                0,
                0
            )
        )

        additionalDataPanel = JPanel(BorderLayout())
        mainPanel.add(
            additionalDataPanel, GridBagConstraints(
                0,
                GridBagConstraints.RELATIVE,
                2,
                1,
                1.0,
                0.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                JBUI.insets(2, 10, 0, 10),
                0,
                0
            )
        )

        mainPanel.add(
            tabbedPane.component, GridBagConstraints(
                0,
                GridBagConstraints.RELATIVE,
                2,
                1,
                1.0,
                1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                JBUI.insetsTop(2),
                0,
                0
            )
        )
    }

    private fun createHomeComponent(): TextFieldWithBrowseButton = TextFieldWithBrowseButton { doSelectHomePath() }

    private fun showTabForType(type: OrderRootType): Boolean = (sdk.sdkType as SdkType).isRootTypeApplicable(type)

    override fun isModified(): Boolean =
        !Objects.equals(sdk.name, initialName) ||
                !Objects.equals(
                    FileUtil.toSystemIndependentName(homeValue),
                    FileUtil.toSystemIndependentName(initialPath!!)
                ) ||
                sdkPathEditorByOrderRootType.any { entry ->
                    entry.value.isModified
                } ||
                additionalDataConfigurable.any { additionalDataConfigurable ->
                    additionalDataConfigurable.isModified
                }

    @Throws(ConfigurationException::class)
    override fun apply() {
        if (!Objects.equals(initialName, sdk.name) && sdk.name.isEmpty()) {
            throw ConfigurationException(ProjectBundle.message("sdk.list.name.required.error"))
        }

        initialName = sdk.name
        initialPath = sdk.homePath

        ApplicationManager.getApplication().runWriteAction {
            val sdkModificator = sdk.sdkModificator
            sdkModificator.homePath = FileUtil.toSystemDependentName(homeValue)

            sdkPathEditorByOrderRootType.values.forEach { pathEditor -> pathEditor.apply(sdkModificator) }

            sdkModificator.commitChanges()
        }

        additionalDataConfigurable.forEach(AdditionalDataConfigurable::apply)
    }

    override fun reset() {
        ApplicationManager.getApplication().runWriteAction {
            val sdkModificator = sdk.sdkModificator
            for (type in sdkPathEditorByOrderRootType.keys) {
                sdkPathEditorByOrderRootType[type]?.reset(sdkModificator)
            }
            sdkModificator.commitChanges()
        }

        setHomePathValue(FileUtil.toSystemDependentName(sdk.homePath ?: ""))
        _versionString = null
        homeFieldLabel.text = homeFieldLabelValue
        updateAdditionalDataComponent()

        additionalDataConfigurable.forEach(AdditionalDataConfigurable::reset)

        homeComponent.isEnabled = true

        for (i in 0 until tabbedPane.tabCount) {
            tabbedPane.setEnabledAt(i, true)
        }
    }
    override fun disposeUIResources() {
        additionalDataConfigurableListBySdkType.keys
            .flatMap { additionalDataConfigurableListBySdkType[it]!! }
            .forEach(AdditionalDataConfigurable::disposeUIResources)
        additionalDataConfigurableListBySdkType.clear()
        componentByAdditionalDataConfigurable.clear()

        Disposer.dispose(disposable)
    }

    private fun clearAllPaths() = sdkPathEditorByOrderRootType.values.forEach(SdkPathEditor::clearList)

    private fun setHomePathValue(absolutePath: String?) {
        homeComponent.apply {
            setText(absolutePath)
            textField.foreground =
                if (absolutePath != null && !absolutePath.isEmpty()) {
                    val homeDir = File(absolutePath)
                    val homeMustBeDirectory = (sdk.sdkType as SdkType).homeChooserDescriptor.isChooseFolders

                    if (homeDir.exists() && homeDir.isDirectory == homeMustBeDirectory) {
                        UIUtil.getFieldForegroundColor()
                    } else {
                        PathEditor.INVALID_COLOR
                    }
                } else {
                    UIUtil.getFieldForegroundColor()
                }
        }
    }

    private fun doSelectHomePath() {
        val sdkType = sdk.sdkType as SdkType
        SdkConfigurationUtil.selectSdkHome(sdkType) { path -> doSetHomePath(path, sdkType) }
    }

    private fun doSetHomePath(homePath: String?, sdkType: SdkType) {
        if (homePath != null) {
            setHomePathValue(homePath.replace('/', File.separatorChar))

            sdk.name = suggestSdkName(homePath)

            try {
                val dummySdk = sdk.clone() as Sdk
                dummySdk.sdkModificator.apply {
                    this.homePath = homePath
                    removeAllRoots()
                    commitChanges()
                }

                sdkType.setupSdkPaths(dummySdk, sdkModel)

                clearAllPaths()
                _versionString = dummySdk.versionString

                if (_versionString == null) {
                    Messages.showMessageDialog(
                        ProjectBundle.message("sdk.java.corrupt.error", homePath),
                        ProjectBundle.message("sdk.java.corrupt.title"), Messages.getErrorIcon()
                    )
                }

                val sdkModificator = dummySdk.sdkModificator

                for (type in sdkPathEditorByOrderRootType.keys) {
                    sdkPathEditorByOrderRootType[type]?.apply {
                        setAddBaseDir(dummySdk.homeDirectory)
                        addPaths(*sdkModificator.getRoots(type))
                    }
                }

                sdkModel.multicaster.sdkHomeSelected(dummySdk, homePath)
            } catch (e: CloneNotSupportedException) {
                LOG.error(e) // should not happen in normal program
            }
        }
    }

    private fun suggestSdkName(homePath: String): String {
        val currentName = sdk.name
        val suggestedName = (sdk.sdkType as SdkType).suggestSdkName(currentName, homePath)

        return if (Objects.equals(currentName, suggestedName)) {
            currentName
        } else {
            var newSdkName = suggestedName
            val allNames = sdkModel.sdks.map(Sdk::getName).toSet()
            var i = 0

            while (allNames.contains(newSdkName)) {
                newSdkName = suggestedName + " (" + ++i + ")"
            }

            newSdkName
        }
    }

    private fun updateAdditionalDataComponent() {
        additionalDataPanel.removeAll()

        for (configurable in additionalDataConfigurable) {
            var component: JComponent? = componentByAdditionalDataConfigurable[configurable]

            if (component == null) {
                component = configurable.createComponent()
                componentByAdditionalDataConfigurable.put(configurable, component)
            }

            if (component != null) {
                additionalDataPanel.add(component, BorderLayout.CENTER)
            }
        }
    }

    private fun initAdditionalDataConfigurable(sdk: Sdk): MutableList<AdditionalDataConfigurable> {
        val sdkType = sdk.sdkType as SdkType
        var configurables: MutableList<AdditionalDataConfigurable>? = additionalDataConfigurableListBySdkType[sdkType]

        if (configurables == null) {
            configurables = Lists.newArrayList()
            additionalDataConfigurableListBySdkType.put(sdkType, configurables)

            sdkType.createAdditionalDataConfigurable(sdkModel, editedSdkModificator)?.let {
                configurables!!.add(it)
            }
        }

        return configurables!!
    }

    override fun navigateTo(place: Place?, requestFocus: Boolean): ActionCallback {
        place?.let {
            tabbedPane.selectedTitle = it.getPath(SDK_TAB) as String?
        }

        return ActionCallback.DONE
    }

    override fun queryPlace(place: Place) {
        place.putPath(SDK_TAB, tabbedPane.selectedTitle)
    }

    override fun setHistory(history: History) {}

    private inner class EditedSdkModificator : SdkModificator {
        override fun getName(): String = sdk.name

        override fun setName(name: String) {
            sdk.name = name
        }

        override fun getHomePath(): String = homeValue

        override fun setHomePath(path: String) {
            doSetHomePath(path, sdk.sdkType as SdkType)
        }

        override fun getVersionString(): String? = _versionString ?: sdk.versionString

        // not supported for this editor
        override fun setVersionString(versionString: String) = throw UnsupportedOperationException()

        override fun getSdkAdditionalData(): SdkAdditionalData? = sdk.sdkAdditionalData

        // not supported for this editor
        override fun setSdkAdditionalData(data: SdkAdditionalData) = throw UnsupportedOperationException()

        override fun getRoots(rootType: OrderRootType): Array<VirtualFile> =
            sdkPathEditorByOrderRootType[rootType]?.roots
                ?: throw IllegalStateException("no editor for root type " + rootType)

        override fun addRoot(root: VirtualFile, rootType: OrderRootType) =
            sdkPathEditorByOrderRootType[rootType]!!.addPaths(root)

        override fun removeRoot(root: VirtualFile, rootType: OrderRootType) =
            sdkPathEditorByOrderRootType[rootType]!!.removePaths(root)

        override fun removeRoots(rootType: OrderRootType) = sdkPathEditorByOrderRootType[rootType]!!.clearList()

        override fun removeAllRoots() =
            sdkPathEditorByOrderRootType.values.forEach { editor -> editor.clearList() }

        override fun commitChanges() {}

        override fun isWritable(): Boolean = true
    }

    companion object {
        private val LOG = Logger.getInstance(Editor::class.java)
        private val SDK_TAB = "sdkTab"
    }
}
