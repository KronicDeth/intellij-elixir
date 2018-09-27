package org.elixir_lang.facet.sdks

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.options.ex.ConfigurableCardPanel
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.util.ActionCallback
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.navigation.History
import com.intellij.ui.navigation.Place
import com.intellij.util.ui.JBUI
import org.elixir_lang.facet.SdksService
import org.elixir_lang.facet.sdk.Editor
import javax.swing.JComponent
import javax.swing.JPanel

fun Library.ModifiableModel.addRoots(sdk: Sdk) =
        sdk
                .rootProvider
                .getFiles(OrderRootType.CLASSES)
                .let { addRoots(it) }

abstract class Configurable: SearchableConfigurable, com.intellij.openapi.options.Configurable.NoScroll {
    internal val sdksService by lazy { SdksService.getInstance()!! }
    private val projectSdksModel by lazy { sdksService.getModel() }
    private lateinit var rootSplitter: Splitter
    private val editorByProjectJdkImpl: MutableMap<ProjectJdkImpl, Editor> = mutableMapOf()
    private lateinit var sdkPanel: ConfigurableCardPanel
    private lateinit var sdkList: List
    private lateinit var sdkListPanel: JPanel
    private var history : History? = History(object : Place.Navigator {
        override fun setHistory(history: History?) {
            this@Configurable.history = history
        }

        override fun navigateTo(place: Place?, requestFocus: Boolean): ActionCallback? = null

        override fun queryPlace(place: Place) {
        }
    })

    override fun apply() {
        editorByProjectJdkImpl.forEach { _, editor ->
            editor.apply()
        }
        projectSdksModel.apply()
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
        sdkPanel = ConfigurableCardPanel()
        rootSplitter = OnePixelSplitter(false).apply {
            border = JBUI.Borders.empty()
            firstComponent = sdkListPanel
            secondComponent = sdkPanel
            isShowDividerControls = false
            setHonorComponentsMinimumSize(true)
        }

        return rootSplitter
    }

    override fun getHelpTopic(): String? = null

    override fun isModified(): Boolean =
            projectSdksModel.isModified || editorByProjectJdkImpl.any { entry -> entry.value.isModified }

    override fun reset() {
        sdkList.refresh()
    }

    abstract fun sdkType(): SdkType

    private fun addCreatedSdk(sdk: Sdk?) {
        sdk?.let {
            if (it.sdkType == sdkType()) {
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
                LibraryTablesRegistrar.getInstance().libraryTable.let { libraryTable ->
                    libraryTable.getLibraryByName(sdk.name)?.let { library ->
                        ApplicationManager.getApplication().runWriteAction {
                            libraryTable.removeLibrary(library)
                        }
                    }
                }
            }

            override fun sdkAdded(sdk: Sdk) {
                LibraryTablesRegistrar.getInstance().libraryTable.let { libraryTable ->
                    ApplicationManager.getApplication().runWriteAction {
                        libraryTable.createLibrary(sdk.name).modifiableModel.apply {
                            addRoots(sdk)
                            commit()
                        }
                    }
                }

                sdkList.refresh()
            }

            override fun sdkChanged(sdk: Sdk, previousName: String) {
                if (sdk.name != previousName) {
                    LibraryTablesRegistrar.getInstance().libraryTable.getLibraryByName(previousName)?.let { library ->
                        ApplicationManager.getApplication().runWriteAction {
                            library.modifiableModel.apply {
                                name = sdk.name
                                replaceRoots(sdk)
                                commit()
                            }
                        }
                    }
                } else {
                    LibraryTablesRegistrar.getInstance().libraryTable.getLibraryByName(sdk.name)?.let { library ->
                        ApplicationManager.getApplication().runWriteAction {
                            library.modifiableModel.apply {
                                replaceRoots(sdk)
                                commit()
                            }
                        }
                    }
                }

                sdkList.refresh()
            }

            override fun sdkHomeSelected(sdk: Sdk, newSdkHome: String?) {
            }
        }
        projectSdksModel.addListener(listener)
        sdkList.selectionModel.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                updateSdkPanel(sdkList.selectedValue)
            }
        }
    }

    private fun addSdk() {
        projectSdksModel.doAdd(sdkListPanel, sdkType(), { sdk -> addCreatedSdk(sdk)  })
    }

    private fun removeSdk() {
        sdkList.selectedValue?.let {
            val sdk = projectSdksModel.findSdk(it)
            SdkConfigurationUtil.removeSdk(sdk)

            projectSdksModel.removeSdk(sdk)
            projectSdksModel.removeSdk(it)

            editorByProjectJdkImpl[it]?.let {
                sdkPanel.getValue(sdkPanel.key, false).let {
                    sdkPanel.remove(it)
                }
            }
            editorByProjectJdkImpl.remove(it)

            sdkList.refresh()
        }
    }

    private fun updateSdkPanel(selectedValue: ProjectJdkImpl?) {
        val selectedEditor = selectedValue?.let {
            editorByProjectJdkImpl.computeIfAbsent(it, { Editor(projectSdksModel, history!!, it) })
        }

        sdkPanel.select(selectedEditor, true)
    }
}

private fun Library.ModifiableModel.addRoots(roots: Array<VirtualFile>) =
        roots.forEach {
            addRoot(it, com.intellij.openapi.roots.OrderRootType.CLASSES)
        }

private fun Library.ModifiableModel.clearRoots() {
    getUrls(OrderRootType.CLASSES).forEach {
        removeRoot(it, OrderRootType.CLASSES)
    }
}

private fun Library.ModifiableModel.replaceRoots(sdk: Sdk) {
    clearRoots()
    addRoots(sdk)
}
