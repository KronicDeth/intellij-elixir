package org.elixir_lang.debugger.interpreted_modules.editor.table.cell_renderer

import com.ericsson.otp.erlang.OtpErlangAtom
import com.intellij.util.ui.UIUtil
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.interpreted_modules.Editor
import org.elixir_lang.debugger.interpreted_modules.editor.table.Model
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.table.DefaultTableCellRenderer

class Module(private val editor: Editor) : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component =
        tableFocusCellBackgroundPropertyTransaction {
            UIManager.put(UIUtil.TABLE_FOCUS_CELL_BACKGROUND_PROPERTY, table!!.selectionBackground)

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).apply {
                if (this is JLabel) {
                    border = DefaultTableCellRenderer.noFocusBorder
                }

                isEnabled = isSelected || editor.isEnabled && table.getValueAt(row, Model.INTERPRETED_COLUMN_INDEX) as Boolean
            }
        }

    override fun setValue(value: Any?) {
        val stringValue: String = if (value != null) {
            inspect(value as OtpErlangAtom)
        } else {
            ""
        }

        super.setValue(stringValue)
    }

    private fun <T> tableFocusCellBackgroundPropertyTransaction(inside: () -> T): T =
        UIUtil.getTableFocusCellBackground().let { color ->
            val output = inside()

            UIManager.put(UIUtil.TABLE_FOCUS_CELL_BACKGROUND_PROPERTY, color)

            output
        }
}
