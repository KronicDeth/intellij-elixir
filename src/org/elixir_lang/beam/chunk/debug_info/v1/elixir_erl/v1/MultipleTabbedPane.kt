package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.Table
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Splitter as DefinitionsSplitter
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.Splitter as SpecificationsSplitter
import javax.swing.JTabbedPane
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Model as DefinitionsModel
import org.elixir_lang.beam.chunk.keyword.Model as KeywordModel

/**
 * Tabbed pane with all multiple-value attributes from [org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1]
 */
class MultipleTabbedPane(debugInfo: V1, project: Project): JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {
    init {
        addTab("Attributes", JBScrollPane(Table(KeywordModel(debugInfo.attributes))))
        addTab("Compile Options", JBScrollPane(Table(KeywordModel(debugInfo.compileOpts))))
        addTab("Definitions", DefinitionsSplitter(debugInfo, project))
        addTab("TypeSpecifications", SpecificationsSplitter(debugInfo, project))
    }
}
