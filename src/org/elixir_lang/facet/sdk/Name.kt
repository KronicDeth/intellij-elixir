package org.elixir_lang.facet.sdk
import com.intellij.openapi.projectRoots.Sdk
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class Name : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).apply {
            value?.let {
                text = (it as Sdk).name
            }
        }
    }
}
