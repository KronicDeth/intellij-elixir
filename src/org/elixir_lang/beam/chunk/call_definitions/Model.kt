package org.elixir_lang.beam.chunk.call_definitions

import org.elixir_lang.beam.chunk.CallDefinitions
import javax.swing.table.AbstractTableModel

class Model(callDefinitions: CallDefinitions?): AbstractTableModel() {
    private val sortedCallDefinitions: List<CallDefinition>? = callDefinitions?.callDefinitionCollection?.sorted()

    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0, 2, 3 -> Integer::class.java
                1 -> String::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 4

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Atom Index"
                1 -> "Name"
                2 -> "Arity"
                3 -> "Label"
                else -> throw IllegalArgumentException("Column $column out of bounds")
            }

    override fun getRowCount(): Int = sortedCallDefinitions?.size ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val callDefinition = sortedCallDefinitions!![rowIndex]

        return when (columnIndex) {
            0 -> callDefinition.atomIndex
            1 -> callDefinition.name
            2 -> callDefinition.arity
            3 -> callDefinition.label
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
