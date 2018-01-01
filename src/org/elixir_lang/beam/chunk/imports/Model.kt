package org.elixir_lang.beam.chunk.imports

import org.elixir_lang.beam.chunk.Imports
import javax.swing.table.AbstractTableModel

class Model(private val imports: Imports?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
        when (columnIndex) {
            0, 2, 4 -> Long::class.java
            1, 3 -> String::class.java
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }

    override fun getColumnCount(): Int = 5

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Module Atom Index"
                1 -> "Module Atom"
                2 -> "Function Atom Index"
                3 -> "Function Atom"
                4 -> "Arity"
                else -> throw IllegalArgumentException("Column $column out of bounds")
            }

    override fun getRowCount(): Int = imports?.size() ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val import = imports!![rowIndex]!!

        return when (columnIndex) {
            0 -> import.moduleAtomIndex
            1 -> import.moduleName
            2 -> import.functionAtomIndex
            3 -> import.functionName
            4 -> import.arity
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
