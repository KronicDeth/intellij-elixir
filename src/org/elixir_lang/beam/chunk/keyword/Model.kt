package org.elixir_lang.beam.chunk.keyword

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.Keyword
import javax.swing.table.AbstractTableModel

class Model(private val keyword: Keyword?): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> OtpErlangAtom::class.java
                1 -> OtpErlangObject::class.java
                else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 2

    override fun getColumnName(column: Int): String =
            when (column) {
                0 -> "Key"
                1 -> "Value"
                else -> throw IllegalArgumentException("Column $column out of bounds")
            }

    override fun getRowCount(): Int = keyword?.size ?: 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val entry = keyword!![rowIndex]

        return when (columnIndex) {
            0 -> entry.key
            1 -> entry.value
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
