package org.elixir_lang.beam.chunk.atoms

import org.elixir_lang.beam.chunk.Atoms
import javax.swing.table.AbstractTableModel

class Model(private val atoms: Atoms?) : AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0, 1 -> Integer::class.java
                2 -> String::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 3

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "Index"
                1 -> "Byte Count"
                2 -> "Characters"
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int = atoms?.size() ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        // atomIndexes are 1-based while rowIndex is 0-based
        val atom = atoms!![rowIndex + 1]

        return when (columnIndex) {
            0 -> atom.index
            1 -> atom.byteCount
            2 -> atom.string
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
