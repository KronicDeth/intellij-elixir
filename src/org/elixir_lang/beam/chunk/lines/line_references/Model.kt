package org.elixir_lang.beam.chunk.lines.line_references

import org.elixir_lang.beam.chunk.lines.LineReference
import javax.swing.table.AbstractTableModel

class Model(private val lineReferenceList: List<LineReference>): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> = Integer::class.java
    override fun getColumnCount(): Int = 3

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "Index"
                1 -> "File Name Index"
                2 -> "Line"
                else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int = lineReferenceList.size

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        val lineReference = lineReferenceList[rowIndex]

        return when (columnIndex) {
            0 -> rowIndex
            1 -> lineReference.fileNameIndex
            2 -> lineReference.line
            else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
        }
    }
}
