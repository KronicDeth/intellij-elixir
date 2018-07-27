package org.elixir_lang.debugger

import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.execution.ui.layout.PlaceInGrid
import com.intellij.icons.AllIcons
import com.intellij.ui.content.Content
import com.intellij.xdebugger.ui.XDebugTabLayouter

class TabLayouter(private val node: Node) : XDebugTabLayouter() {
    private val interpretedModuleEditor: org.elixir_lang.debugger.interpreted_modules.Editor by lazy {
        org.elixir_lang.debugger.interpreted_modules.Editor(node)
    }

    override fun registerConsoleContent(ui: RunnerLayoutUi, console: ExecutionConsole): Content =
            registerDebuggedConsoleContent(ui, console)

    override fun registerAdditionalContent(ui: RunnerLayoutUi) {
        registerInterpretedModules(ui)
    }

    private fun registerDebuggedConsoleContent(ui: RunnerLayoutUi, console: ExecutionConsole): Content =
        registerConsoleContent(ui, console, "DebuggedConsoleContent", "Debugged Console", 1, PlaceInGrid.bottom, false)

    private fun registerConsoleContent(
            ui: RunnerLayoutUi,
            console: ExecutionConsole,
            contentId: String,
            displayName: String,
            defaultTabId: Int,
            defaultPlaceInGrid: PlaceInGrid,
            defaultMinimized: Boolean
    ): Content {
        val content = ui.createContent(
                contentId,
                console.component,
                displayName,
                AllIcons.Debugger.Console,
                console.preferredFocusableComponent
        )
        content.isCloseable = false
        ui.addContent(content, defaultTabId, defaultPlaceInGrid, defaultMinimized)

        return content
    }

    private fun registerInterpretedModules(ui: RunnerLayoutUi): Content {
        val content = ui.createContent("InterpretedModulesContent", interpretedModuleEditor, "Interpreted Modules", AllIcons.General.Filter, interpretedModuleEditor.table)
        ui.addContent(content, 0, PlaceInGrid.left, true)

        return content
    }

    fun interpretedModules(interpretedModuleList: List<InterpretedModule>) {
        interpretedModuleEditor.interpretedModules(interpretedModuleList)
    }
}
