package org.elixir_lang.beam.chunk.code

import org.elixir_lang.beam.chunk.Code
import javax.swing.table.AbstractTableModel

class Model(val code: Code?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
        when (columnIndex) {
            0 -> Integer::class.java
            1 -> String::class.java
            2 -> Integer::class.java
            3 -> Operation::class.java
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }

    override fun getColumnCount(): Int = 4

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "Code"
                1 -> "Function"
                2 -> "Arity"
                3 -> "Arguments"
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int = code?.size() ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val operation = code!![rowIndex]

        return when (columnIndex) {
            0 -> operation.code.number
            1 -> operation.code.function
            2 -> operation.code.arity
            3 -> operation.argumentsString()
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
