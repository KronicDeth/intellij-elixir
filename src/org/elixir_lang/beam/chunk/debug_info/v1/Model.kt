package org.elixir_lang.beam.chunk.debug_info.v1

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.V1
import javax.swing.table.AbstractTableModel

class Model(val v1: V1): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> OtpErlangAtom::class.java
                1, 2 -> OtpErlangObject::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 3

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "Version"
                1 -> "Backend"
                2 -> "Metadata"
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int = 1

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        return when (columnIndex) {
            0 -> v1.term.tuple!!.elementAt(0)
            1 -> v1.backend
            2 -> v1.metadata
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
