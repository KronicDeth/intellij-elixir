package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.ui.table.JBTable
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel

const val DEFAULT_MIN_COLUMN_WIDTH = 15
private const val MAX_COLUMN_WIDTH = 500

open class Table(model: TableModel): JBTable(model) {
    init {
        autoResizeMode = AUTO_RESIZE_OFF
        setDefaultRenderer(OtpErlangObject::class.java, OtpErlangObjectTableCellRenderer())
        packColumns()
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
        val contentWidth = getColumnModel().columnMargin + Math.max(expandedWidth, headerWidth)
        val newWidth = contentWidth.coerceAtMost(MAX_COLUMN_WIDTH)

        column.preferredWidth = newWidth
        column.width = newWidth
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
