package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions

class Splitter(debugInfo: AbstractCodeCompileOptions, project: Project): OnePixelSplitter(false) {
    init {
        val tree = Tree(Model(debugInfo))

        firstComponent = JBScrollPane(tree)
        secondComponent = Panel(tree, project)
    }
}
