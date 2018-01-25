package org.elixir_lang.beam.chunk.functions

import org.elixir_lang.beam.chunk.Functions
import javax.swing.table.AbstractTableModel

class Model(val functions: Functions?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0, 1, 3, 4, 5, 6 -> Long::class.java
                2 -> String::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 7

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Index"
                1 -> "Atom"
                2 -> "Name"
                3 -> "Arity"
                4 -> "Code Offset"
                5 -> "Free Variable Count"
                6 -> "O Unique"
                else -> throw IllegalArgumentException("Column $column out of bounds")
            }

    override fun getRowCount(): Int = functions?.size() ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val function = functions!![rowIndex]

        return when (columnIndex) {
            0 -> function.index
            1 -> function.atomIndex
            2 -> function.name
            3 -> function.arity
            4 -> function.codeOffset
            5 -> function.freeVariableCount
            6 -> function.oUnique
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
