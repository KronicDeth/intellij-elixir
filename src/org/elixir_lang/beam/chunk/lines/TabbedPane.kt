package org.elixir_lang.beam.chunk.lines

import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.Lines
import org.elixir_lang.beam.chunk.Table
import javax.swing.JTabbedPane
import org.elixir_lang.beam.chunk.lines.file_names.Model as FileNamesModel
import org.elixir_lang.beam.chunk.lines.file_names.Table as FileNamesTable
import org.elixir_lang.beam.chunk.lines.line_references.Model as LineReferencesModel

class TabbedPane(lines: Lines): JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {
    init {
        addTab("Line References", JBScrollPane(Table(LineReferencesModel(lines.lineReferenceList))))
        addTab("File Names", JBScrollPane(FileNamesTable(FileNamesModel(lines.fileNameList))))
    }
}
