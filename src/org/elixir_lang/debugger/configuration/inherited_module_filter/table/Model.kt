package org.elixir_lang.debugger.configuration.inherited_module_filter.table

import com.intellij.util.ui.ItemRemovable
import org.elixir_lang.debugger.configuration.InheritedModuleFilter
import javax.swing.table.AbstractTableModel

class Model : AbstractTableModel(), ItemRemovable {
    var inheritedModuleFilterList: MutableList<InheritedModuleFilter> = mutableListOf()

    fun addRow(inheritedModuleFilter: InheritedModuleFilter): Int {
        inheritedModuleFilterList.add(inheritedModuleFilter)
        val row = inheritedModuleFilterList.size - 1
        fireTableRowsInserted(row, row)

        return row
    }

    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                ENABLED_COLUMN_INDEX -> Boolean::class.java
                PATTERN_COLUMN_INDEX -> String::class.java
                else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 2
    override fun getRowCount(): Int = inheritedModuleFilterList.size

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }

        val inheritedModuleFilter = inheritedModuleFilterList[rowIndex]

        return when (columnIndex) {
            ENABLED_COLUMN_INDEX -> inheritedModuleFilter.enabled
            PATTERN_COLUMN_INDEX -> inheritedModuleFilter.pattern
            else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        if (rowIndex !in 0 until rowCount) {
            throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }

        return when (columnIndex) {
            ENABLED_COLUMN_INDEX -> true
            PATTERN_COLUMN_INDEX -> !inheritedModuleFilterList[rowIndex].inherited
            else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
        }
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (rowIndex !in 0 until rowCount) {
            throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }

        val inheritedModuleFilter = inheritedModuleFilterList[rowIndex]

        when (columnIndex) {
            ENABLED_COLUMN_INDEX -> inheritedModuleFilterList[rowIndex] = inheritedModuleFilter.copy(enabled = value as Boolean)
            PATTERN_COLUMN_INDEX -> if (inheritedModuleFilter.inherited) {
                throw IllegalArgumentException("Inherited module filter patterns cannot be edited; they can only be enabled and disabled.")
            } else {
                inheritedModuleFilterList[rowIndex] = inheritedModuleFilter.copy(pattern = value as String)
            }
            else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
        }

        fireTableCellUpdated(rowIndex, columnIndex)
    }

    override fun removeRow(rowIndex: Int) {
        if (rowIndex !in 0 until rowCount) {
            throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }

        val inheritedModuleFilter = inheritedModuleFilterList[rowIndex]

        if (inheritedModuleFilter.inherited) {
            throw IllegalArgumentException("Inheritted module filter cannot be removed; they can only be enabled and disabled.")
        }

        inheritedModuleFilterList.removeAt(rowIndex)

        fireTableRowsDeleted(rowIndex, rowIndex)
    }

    companion object {
        const val ENABLED_COLUMN_INDEX = 0
        const val PATTERN_COLUMN_INDEX = 1
    }
}
