package org.elixir_lang.debugger.settings.stepping.module_filter.editor.table

import com.intellij.util.ui.ItemRemovable
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import javax.swing.table.AbstractTableModel

class Model : AbstractTableModel(), ItemRemovable {
    var filters: List<ModuleFilter>
        get() {
            _filterList.removeIf { it.pattern.isBlank() }

            return _filterList
        }
        set(value) {
            _filterList.clear()
            _filterList.addAll(value)
        }

    private val _filterList = mutableListOf<ModuleFilter>()

    fun addRow(filter: ModuleFilter): Int {
        _filterList.add(filter)
        val row = _filterList.size - 1
        fireTableRowsInserted(row, row)

        return row
    }

    override fun getColumnClass(columnIndex: Int): Class<*> =
        when (columnIndex) {
            0 -> Boolean::class.java
            1 -> String::class.java
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }

    override fun getColumnCount(): Int = 2

    override fun getRowCount(): Int = _filterList.size

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val filter = _filterList[rowIndex]

        return when (columnIndex) {
            0 -> filter.enabled
            1 -> filter.pattern
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = true

    override fun removeRow(rowIndex: Int) {
        _filterList.removeAt(rowIndex)
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val filter = _filterList[rowIndex]

        when (columnIndex) {
            0 -> _filterList[rowIndex] = ModuleFilter(enabled = value as Boolean, pattern = filter.pattern)
            1 -> _filterList[rowIndex] = ModuleFilter(enabled = filter.enabled, pattern = value as String)
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }

        fireTableCellUpdated(rowIndex, columnIndex)
    }

    companion object {
        const val ENABLED_COLUMN_INDEX = 0
        const val FILTER_COLUMN_INDEX = 1
    }
}
