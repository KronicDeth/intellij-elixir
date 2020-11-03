package org.elixir_lang.debugger.settings.stepping.module_filter

import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ComponentWithEmptyText
import com.intellij.util.ui.StatusText
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import org.elixir_lang.debugger.settings.stepping.module_filter.editor.table.Model
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.JTable
import org.elixir_lang.debugger.settings.stepping.module_filter.editor.table.cell_renderer.ModuleFilter as ModuleFilterCellRenderer

class Editor: JPanel(BorderLayout()), ComponentWithEmptyText {
    var filters: List<ModuleFilter>
         get() = tableModel.filters
         set(value) {
             tableModel.filters = value
         }

    private val tableModel = Model()
    val table = JBTable(tableModel).apply {
        setShowGrid(false)
        intercellSpacing = Dimension(0, 0)
        autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
        columnSelectionAllowed = false
        preferredScrollableViewportSize = Dimension(200, rowHeight * JBTable.PREFERRED_SCROLLABLE_VIEWPORT_HEIGHT_IN_ROWS)

        tableHeader = null
        columnModel.getColumn(Model.ENABLED_COLUMN_INDEX).let { column ->
            TableUtil.setupCheckboxColumn(column, columnModel.columnMargin)
            column.cellEditor = BooleanTableCellEditor()
            column.cellRenderer = BooleanTableCellRenderer()
        }
        columnModel.getColumn(Model.FILTER_COLUMN_INDEX).let { column ->
            column.cellRenderer = ModuleFilterCellRenderer(this@Editor)
        }
    }

    init {
        val decorator = ToolbarDecorator
                .createDecorator(table)
                .setAddAction { addPatternFilter()  }
                .setRemoveAction { TableUtil.removeSelectedItems(table) }
                .setButtonComparator("Add", "Remove")
                .createPanel()

        add(decorator, BorderLayout.CENTER)

        emptyText.text = "No module pattern configured"
    }

    override fun getEmptyText(): StatusText = table.emptyText

    fun stopEditing() {
        table.cellEditor?.stopCellEditing()
    }

    // Private Functions

    private fun addFilter(pattern: String) {
        val filter = ModuleFilter(pattern = pattern)

        tableModel.addRow(filter).let { row ->
            selectRow(row)
            scrollToRow(row)
        }

        focus()
    }

    private fun addPatternFilter() {
        addFilter("*")
    }

    private fun scrollToRow(row: Int) {
        table.scrollRectToVisible(table.getCellRect(row, 0, true))
    }

    private fun selectRow(row: Int) {
        table.selectionModel.setSelectionInterval(row, row)
    }

    private fun focus() {
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
            IdeFocusManager.getGlobalInstance().requestFocus(table, true)
        }
    }
}
