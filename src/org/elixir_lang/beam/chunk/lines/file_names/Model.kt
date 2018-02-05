package org.elixir_lang.beam.chunk.lines.file_names

import javax.swing.table.AbstractTableModel

class Model(private val fileNameList: List<String>): AbstractTableModel() {
    override fun getColumnClass(columnIndex: Int): Class<*> = Integer::class.java
    override fun getColumnCount(): Int = 2

    override fun getColumnName(columnIndex: Int): String =
            when (columnIndex) {
                0 -> "Index"
                1 -> "File Name"
                else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
            }

    override fun getRowCount(): Int = fileNameList.size

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        if (rowIndex !in 0 until rowCount) {
            throw IllegalArgumentException("Row $rowIndex out of bounds")
        }

        return when (columnIndex) {
            0 -> rowIndex
            1 -> fileNameList[rowIndex]
            else -> throw IndexOutOfBoundsException("Column $columnIndex out of bounds")
        }
    }
}
