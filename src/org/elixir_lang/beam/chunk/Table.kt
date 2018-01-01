package org.elixir_lang.beam.chunk

import com.intellij.ui.table.JBTable
import javax.swing.JTable
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel

const val DEFAULT_MIN_COLUMN_WIDTH = 15

class Table(model: TableModel): JBTable(model) {
    init {
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        packColumns()
        invalidate()
    }

    private fun packColumns() {
        val columnModel = this.columnModel

        for (columnIndex in 0 until columnCount) {
            packColumn(columnModel, columnIndex)
        }
    }

    private fun packColumn(columnModel: TableColumnModel, columnToPack: Int) {
        val column = columnModel.getColumn(columnToPack)
        val expandedWidth = getExpandedColumnWidth(columnToPack)
        val headerWidth = preferredHeaderWidth(column, columnToPack)
        val newWidth = getColumnModel().columnMargin + Math.max(expandedWidth, headerWidth)

        column.width = newWidth

        val tableSize = size
        tableSize.width += newWidth - column.width
        size = tableSize
    }

    private fun preferredHeaderWidth(column: TableColumn, columnIdx: Int): Int {
        var renderer: TableCellRenderer? = column.headerRenderer

        if (renderer == null) {
            val header = getTableHeader() ?: return DEFAULT_MIN_COLUMN_WIDTH
            renderer = header.defaultRenderer!!
        }

        return renderer
                .getTableCellRendererComponent(
                        this,
                        column.headerValue,
                        false,
                        false,
                        -1,
                        columnIdx
                )
                .preferredSize
                .width
    }
}
