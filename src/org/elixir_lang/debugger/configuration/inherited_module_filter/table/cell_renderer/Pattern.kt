package org.elixir_lang.debugger.configuration.inherited_module_filter.table.cell_renderer

import com.intellij.util.ui.UIUtil
import org.elixir_lang.debugger.configuration.inherited_module_filter.table.Model
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.table.DefaultTableCellRenderer

class Pattern : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component =
        tableFocusCellBackgroundPropertyTransaction {
            UIManager.put(UIUtil.TABLE_FOCUS_CELL_BACKGROUND_PROPERTY, table!!.selectionBackground)

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).apply {
                if (this is JLabel) {
                    border = DefaultTableCellRenderer.noFocusBorder
                }

                isEnabled = isSelected || table.getValueAt(row, Model.ENABLED_COLUMN_INDEX) as Boolean
            }
        }

    private fun <T> tableFocusCellBackgroundPropertyTransaction(inside: () -> T): T =
        UIUtil.getTableFocusCellBackground().let { color ->
            val output = inside()

            UIManager.put(UIUtil.TABLE_FOCUS_CELL_BACKGROUND_PROPERTY, color)

            output
        }
}
