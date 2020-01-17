package org.elixir_lang.debugger.settings.stepping.module_filter.editor

import com.intellij.ide.util.gotoByName.ChooseByNameModel
import com.intellij.openapi.project.Project
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.stub.index.AllName
import javax.swing.DefaultListCellRenderer

class ChooseByNameModel(project: Project): ChooseByNameModel {
    private val names by lazy { StubIndex.getInstance().getAllKeys(AllName.KEY, project).toTypedArray()  }

    override fun getCheckBoxMnemonic() = 'd'
    override fun getCheckBoxName(): String? = null

    override fun getElementsByName(name: String, checkBoxState: Boolean, pattern: String): Array<String> {
        return names
    }

    override fun getElementName(element: Any): String? = element as String?
    override fun getFullName(element: Any): String? = element as String?
    override fun getHelpId(): String? = null
    override fun getListCellRenderer() = DefaultListCellRenderer()
    override fun useMiddleMatching() = true
    override fun getNames(checkBoxState: Boolean) = names
    override fun getNotFoundMessage() = "No Module found"
    override fun getNotInMessage() = "No Module found"
    override fun getPromptText() = "Enter Module name or pattern:"
    override fun getSeparators() = emptyArray<String>()
    override fun loadInitialCheckBoxState()= false

    override fun saveInitialCheckBoxState(state: Boolean) {
    }

    override fun willOpenEditor() = false
}
