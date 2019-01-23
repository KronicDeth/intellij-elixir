package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1

class Splitter(debugInfo: V1, project: Project): OnePixelSplitter(false) {
    init {
        val tree = Tree(Model(debugInfo))

        firstComponent = JBScrollPane(tree)
        secondComponent = Panel(tree, project)
    }
}
