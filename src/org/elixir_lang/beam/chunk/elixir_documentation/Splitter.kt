package org.elixir_lang.beam.chunk.elixir_documentation

import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.ElixirDocumentation

class Splitter(elixirDocumentation: ElixirDocumentation, project: Project, moduleName: String?): OnePixelSplitter(false) {
    init {
        val tree = Tree(moduleName, Model(elixirDocumentation))

        firstComponent = JBScrollPane(tree)
        secondComponent = Panel(tree, project)
    }
}
