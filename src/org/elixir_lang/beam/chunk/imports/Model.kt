package org.elixir_lang.beam.chunk.imports

import org.elixir_lang.beam.chunk.Imports
import javax.swing.table.AbstractTableModel

class Model(private val imports: Imports?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
        when (columnIndex) {
            0, 1, 3, 5 -> Long::class.java
            2, 4 -> String::class.java
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }

    override fun getColumnCount(): Int = 5

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Index"
                1 -> "Module Atom Index"
                2 -> "Module Atom"
                3 -> "Function Atom Index"
                4 -> "Function Atom"
                5 -> "Arity"
                else -> throw IllegalArgumentException("Column $column out of bounds")
            }

    override fun getRowCount(): Int = imports?.size() ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val import = imports!![rowIndex]

        return when (columnIndex) {
            0 -> rowIndex
            1 -> import.moduleAtomIndex
            2 -> import.moduleName
            3 -> import.functionAtomIndex
            4 -> import.functionName
            5 -> import.arity
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
