package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.Table
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Tree
import javax.swing.JTabbedPane
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Model as DefinitionsModel
import org.elixir_lang.beam.chunk.keyword.Model as KeywordModel

/**
 * Tabbed pane with all multiple-value attributes from [org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1]
 */
class MultipleTabbedPane(debugInfo: V1): JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {
    init {
        addTab("Attributes", JBScrollPane(Table(KeywordModel(debugInfo.attributes))))
        addTab("Compile Options", JBScrollPane(Table(KeywordModel(debugInfo.compileOpts))))
        addTab("Definitions", JBScrollPane(Tree(DefinitionsModel(debugInfo))))
    }
}
