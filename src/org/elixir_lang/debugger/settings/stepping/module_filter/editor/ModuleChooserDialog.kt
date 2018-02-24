package org.elixir_lang.debugger.settings.stepping.module_filter.editor

import com.intellij.ide.IdeBundle
import com.intellij.ide.projectView.impl.AbstractProjectTreeStructure
import com.intellij.ide.projectView.impl.ProjectTreeBuilder
import com.intellij.ide.util.gotoByName.ChooseByNamePanel
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent
import com.intellij.ide.util.treeView.AlphaComparator
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy
import com.intellij.ui.*
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

class ModuleChooserDialog(private val project: Project) : DialogWrapper(project, true) {
    var selected: String? = null

    // GUI
    private lateinit var gotoByNamePanel: ChooseByNamePanel
    private lateinit var tabbedPane: TabbedPaneWrapper
    private lateinit var tree: Tree

    private fun calcModule(): String? =
        if (tabbedPane.selectedIndex == 0) {
            gotoByNamePanel.chosenElement as String
        } else {
            tree.selectionPath?.let { path ->
                val node = path.lastPathComponent as DefaultMutableTreeNode
                selectedFromTreeUserObject(node)
            }
        }

    private fun selectedFromTreeUserObject(node: DefaultMutableTreeNode): String? {
        return null
    }

    override fun createCenterPanel(): JComponent {
        val model = DefaultTreeModel(DefaultMutableTreeNode())
        tree = Tree(model)

        val treeStructure = object : AbstractProjectTreeStructure(project) {
            override fun isAbbreviatePackageNames() = false
            override fun isFlattenPackages() = false
            override fun isHideEmptyMiddlePackages() = true
            override fun isShowLibraryContents() = true
            override fun isShowMembers() = false
            override fun isShowModules() = false
        }

        val builder = ProjectTreeBuilder(project, tree, model, AlphaComparator.INSTANCE, treeStructure)

        tree.apply {
            isRootVisible = false
            showsRootHandles = true
            expandRow(0)
            selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
            cellRenderer = NodeRenderer()
        }

        UIUtil.setLineStyleAngled(tree)

        val scrollPane = ScrollPaneFactory.createScrollPane(tree)
        scrollPane.preferredSize = JBUI.size(500, 300)
        scrollPane.putClientProperty(UIUtil.KEEP_BORDER_SIDES, SideBorder.RIGHT or SideBorder.LEFT or SideBorder.BOTTOM)

        tree.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(keyEvent: KeyEvent) {
                if (KeyEvent.VK_ENTER == keyEvent.keyCode) {
                    doOKAction()
                }
            }
        })

        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                val path = tree.getPathForLocation(event.x, event.y)

                return if (path != null && tree.isPathSelected(path)) {
                    doOKAction()
                    true
                } else {
                    false
                }
            }
        }.installOn(tree)

        tree.addTreeSelectionListener { handleSelectionChanged() }

        TreeSpeedSearch(tree)

        tabbedPane = TabbedPaneWrapper(disposable)

        val dummyPanel = JPanel(BorderLayout())
        val name: String? = null

        gotoByNamePanel = object : ChooseByNamePanel(project, ChooseByNameModel(project), name, true, null) {
            override fun showTextFieldPanel() {}

            override fun close(isOk: Boolean) {
                super.close(isOk)

                if (isOk) {
                    doOKAction()
                } else {
                    doCancelAction()
                }
            }

            override fun initUI(callback: ChooseByNamePopupComponent.Callback, modalityState: ModalityState?, allowMultipleSelection: Boolean) {
                super.initUI(callback, modalityState, allowMultipleSelection)
                val panel = gotoByNamePanel.panel

                dummyPanel.add(panel, BorderLayout.CENTER)
                IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
                    IdeFocusManager.getGlobalInstance()
                            .requestFocus(IdeFocusTraversalPolicy.getPreferredFocusedComponent(panel), true)
                }
            }

            override fun chosenElementMightChange() {
                handleSelectionChanged()
            }
        }

        Disposer.register(myDisposable, gotoByNamePanel)

        tabbedPane.addTab(IdeBundle.message("tab.chooser.search.by.name"), dummyPanel)
        tabbedPane.addTab(IdeBundle.message("tab.chooser.project"), scrollPane)

        gotoByNamePanel.invoke(Callback(), getModalityState(), false)

        tabbedPane.addChangeListener { handleSelectionChanged() }

        return tabbedPane.component
    }

    private fun handleSelectionChanged() {
        val selection = calcModule()
        isOKActionEnabled = selection != null
    }

    private inner class Callback : ChooseByNamePopupComponent.Callback() {
        override fun elementChosen(element: Any) {
            selected = element as String
            close(DialogWrapper.OK_EXIT_CODE)
        }
    }

    private fun getModalityState(): ModalityState {
        return ModalityState.stateForComponent(rootPane)
    }
}
