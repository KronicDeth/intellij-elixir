package org.elixir_lang.beam.chunk.elixir_documentation

import com.intellij.openapi.project.Project
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.ElixirDocumentation
import javax.swing.JComponent
import javax.swing.JPanel

fun component(
        elixirDocumentation: ElixirDocumentation?,
        project: Project,
        moduleName: String?,
        tabbedPane: TabbedPaneWrapper
): JComponent =
        if (elixirDocumentation != null) {
            Splitter(elixirDocumentation, project, moduleName, tabbedPane)
        } else {
            JBScrollPane(JPanel())
        }
