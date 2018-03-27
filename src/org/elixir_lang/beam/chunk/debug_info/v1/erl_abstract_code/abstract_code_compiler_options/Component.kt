package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options

import com.intellij.openapi.project.Project
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Splitter
import javax.swing.JPanel
import javax.swing.JTabbedPane

class Component(debugInfo: AbstractCodeCompileOptions, project: Project):
        JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {
    init {
        addTab("Abstract Code", Splitter(debugInfo, project))
        addTab("Compile Options", JPanel())
    }
}
