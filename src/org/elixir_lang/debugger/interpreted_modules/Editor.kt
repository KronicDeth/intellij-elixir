package org.elixir_lang.debugger.interpreted_modules

import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TableUtil
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ComponentWithEmptyText
import com.intellij.util.ui.StatusText
import org.elixir_lang.debugger.InterpretedModule
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.interpreted_modules.editor.table.Model
import org.elixir_lang.debugger.interpreted_modules.editor.table.cell_renderer.Module
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.JTable

class Editor(node: Node) : JPanel(BorderLayout()), ComponentWithEmptyText {
    override fun getEmptyText(): StatusText = table.emptyText

    fun interpretedModules(interpretedModuleList: List<InterpretedModule>) {
        tableModel.interpretedModules(interpretedModuleList)
    }

    private val tableModel = Model(node)
    val table = JBTable(tableModel).apply {
        setShowGrid(false)
        intercellSpacing = Dimension(0, 0)
        autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
        columnSelectionAllowed = false
        preferredScrollableViewportSize = Dimension(200, rowHeight * JBTable.PREFERRED_SCROLLABLE_VIEWPORT_HEIGHT_IN_ROWS)

        tableHeader = null
        columnModel.getColumn(Model.INTERPRETED_COLUMN_INDEX).let { column ->
            TableUtil.setupCheckboxColumn(column, columnModel.columnMargin)
            column.headerValue = "Interpret"
            column.cellEditor = BooleanTableCellEditor()
            column.cellRenderer = BooleanTableCellRenderer()
        }
        columnModel.getColumn(Model.MODULE_COLUMN_INDEX).let { column ->
            column.headerValue = "Module"
            column.cellRenderer = Module(this@Editor)
        }
    }

    init {
        val scrollPane = ScrollPaneFactory.createScrollPane(table, true)
        add(scrollPane, BorderLayout.CENTER)
    }
}
