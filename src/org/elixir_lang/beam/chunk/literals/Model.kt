package org.elixir_lang.beam.chunk.literals

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.Literals
import javax.swing.table.AbstractTableModel

class Model(val literals: Literals?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> Integer::class.java
                1 -> OtpErlangObject::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 2

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "#"
                1 -> "Term"
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int = literals?.size() ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        return when (columnIndex) {
            0 -> rowIndex
            1 -> literals!![rowIndex]
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
