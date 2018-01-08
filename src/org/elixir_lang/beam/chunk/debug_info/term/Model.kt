package org.elixir_lang.beam.chunk.debug_info.term

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.DebugInfo
import org.elixir_lang.beam.chunk.debug_info.Term
import org.elixir_lang.beam.chunk.debug_info.v1.ElixirErl
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableModel

fun model(debugInfo: DebugInfo?): TableModel? =
    when (debugInfo) {
        is org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1 ->
            org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.Model(debugInfo)
        is ElixirErl ->
            org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.Model(debugInfo)
        is org.elixir_lang.beam.chunk.debug_info.V1 ->
            org.elixir_lang.beam.chunk.debug_info.v1.Model(debugInfo)
        is Term -> Model(debugInfo)
        else -> null
    }

class Model(private val term: Term?): AbstractTableModel() {
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
            if (term != null) {
                1
            } else {
                0
            }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        return when (columnIndex) {
            0 -> term!!.term
            else -> throw IllegalArgumentException("Column $columnIndex out of bounds")
        }
    }
}
