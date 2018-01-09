package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import javax.swing.table.AbstractTableModel

class Model(private val v1: V1): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> String::class.java
                1 -> Object::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 2

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "Section"
                1 -> "Term"
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int = 8

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? =
            when (columnIndex) {
                0 ->
                    when (rowIndex) {
                        0 -> "File"
                        1 -> "Line"
                        2 -> "Module"
                        3 -> "Compile Options"
                        4 -> "Attributes"
                        5 -> "Definitions"
                        6 -> "Unreachable"
                        7 -> "Specs"
                        else -> throw IllegalArgumentException("Row $rowIndex out of bounds")
                    }
                1 ->
                    when (rowIndex) {
                        0 -> v1.file
                        1 -> v1.line
                        2 -> v1.module
                        3 -> v1.compileOpts
                        4 -> v1.attributes
                        5 -> v1.definitions
                        6 -> v1.unreachable
                        7 -> v1.specs
                        else -> throw IllegalArgumentException("Row $rowIndex out of bounds")
                    }
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }
}
