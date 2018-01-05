package org.elixir_lang.beam.chunk.debug_info

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.DebugInfo
import javax.swing.table.AbstractTableModel

class Model(private val debugInfo: DebugInfo?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> OtpErlangObject::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 1

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "Term"
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int =
            if (debugInfo != null) {
                1
            } else {
                0
            }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        return when (columnIndex) {
            0 -> debugInfo!!.term
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
