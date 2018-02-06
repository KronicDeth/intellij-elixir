package org.elixir_lang.beam.chunk.lines.file_names

import org.elixir_lang.beam.chunk.Table
import javax.swing.JLabel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableModel

class Table(model: TableModel): Table(model) {
    init {
        columnModel.getColumn(1).cellRenderer =
                DefaultTableCellRenderer().apply { horizontalAlignment = JLabel.LEFT }
    }
}
