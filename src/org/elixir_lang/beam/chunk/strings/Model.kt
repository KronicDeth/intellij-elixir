package org.elixir_lang.beam.chunk.strings

import org.elixir_lang.beam.chunk.Strings
import javax.swing.table.AbstractTableModel

class Model(val strings: Strings?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
        when (columnIndex) {
            0 -> String::class.java
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }

    override fun getColumnCount(): Int = 1

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Pool"
                else -> throw IllegalArgumentException("Column $column out of bounds")
            }

    override fun getRowCount(): Int =
            if (strings != null) {
                1
            } else {
                0
            }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val pool = strings!!.pool

        return when (columnIndex) {
            0 -> pool
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
