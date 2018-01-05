package org.elixir_lang.beam.chunk.functions

import org.elixir_lang.beam.chunk.Functions
import javax.swing.table.AbstractTableModel

class Model(val functions: Functions?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0, 2, 3, 4, 5, 6 -> Long::class.java
                1 -> String::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 7

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Atom"
                1 -> "Name"
                2 -> "Arity"
                3 -> "Code Offset"
                4 -> "Index"
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
            0 -> function.atomIndex
            1 -> function.name
            2 -> function.arity
            3 -> function.codeOffset
            4 -> function.index
            5 -> function.freeVariableCount
            6 -> function.oUnique
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
