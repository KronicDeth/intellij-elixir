package org.elixir_lang.debugger.interpreted_modules.editor.table

import com.ericsson.otp.erlang.OtpErlangAtom
import org.elixir_lang.debugger.InterpretedModule
import org.elixir_lang.debugger.Node
import javax.swing.table.AbstractTableModel

class Model(private val node: Node) : AbstractTableModel() {
    fun interpretedModules(interpretedModuleList: List<InterpretedModule>) {
        _interpretedModuleList.clear()
        _interpretedModuleList.addAll(interpretedModuleList)
        this.fireTableDataChanged()
    }

    override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> Boolean::class.java
                1 -> OtpErlangAtom::class.java
                else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
            }

    override fun getColumnCount(): Int = 2
    override fun getRowCount(): Int = _interpretedModuleList.size
    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }

        val interpretedModule = _interpretedModuleList[rowIndex]

        return when (columnIndex) {
            0 -> interpretedModule.interpreted
            1 -> interpretedModule.module
            else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        if (rowIndex !in 0 until rowCount) {
            throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }

        return when (columnIndex) {
            0 -> true
            1 -> false
            else -> throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (rowIndex !in 0 until rowCount) {
            throw IndexOutOfBoundsException("Row $rowIndex out of bounds")
        }

        val interpretedModule = _interpretedModuleList[rowIndex]

        when (columnIndex) {
            0 -> {
                _interpretedModuleList[rowIndex] = interpretedModule.copy(interpreted = value as Boolean)

                if (value) {
                    node.interpret(interpretedModule.module)
                } else {
                    node.stopInterpreting(interpretedModule.module)
                }
            }
            1 -> throw IllegalArgumentException("Module is not editable")
            else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
        }
    }

    private val _interpretedModuleList = mutableListOf<InterpretedModule>()

    companion object {
        const val INTERPRETED_COLUMN_INDEX = 0
        const val MODULE_COLUMN_INDEX = 1
    }
}
