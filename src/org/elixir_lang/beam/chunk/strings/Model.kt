package org.elixir_lang.beam.chunk.strings

import org.elixir_lang.beam.chunk.Code
import org.elixir_lang.beam.chunk.Strings
import javax.swing.table.AbstractTableModel

class Model(val strings: Strings?, val code: Code? = null): AbstractTableModel() {
    private val entries: List<Strings.Entry> by lazy { strings?.entries(code) ?: emptyList() }

    override fun getColumnClass(columnIndex: Int): Class<*> =
        when (columnIndex) {
            0 -> Int::class.javaObjectType
            1 -> Int::class.javaObjectType
            2 -> String::class.java
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }

    override fun getColumnCount(): Int = 3

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Offset"
                1 -> "Length"
                2 -> "String"
                else -> throw IllegalArgumentException("Column $column out of bounds")
            }

    override fun getRowCount(): Int = entries.size

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val entry = entries[rowIndex]

        return when (columnIndex) {
            0 -> entry.offset
            1 -> entry.value.toByteArray(Charsets.UTF_8).size
            2 -> entry.value
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
