package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import org.elixir_lang.beam.chunk.Table
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.keyword.Model
import javax.swing.JTabbedPane

/**
 * Tabbed pane with all multiple-value attributes from [org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1]
 */
class MultipleTabbedPane(debugInfo: V1): JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {
    init {
        addTab("Attributes", Table(Model(debugInfo.attributes)))
        addTab("Compile Options", Table(org.elixir_lang.beam.chunk.keyword.Model(debugInfo.compileOpts)))
    }
}
