package org.elixir_lang.debugger

import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.execution.ui.layout.PlaceInGrid
import com.intellij.icons.AllIcons
import com.intellij.ui.content.Content
import com.intellij.xdebugger.ui.XDebugTabLayouter

class TabLayouter(private val debuggerConsole: ExecutionConsole) : XDebugTabLayouter() {
    override fun registerConsoleContent(ui: RunnerLayoutUi, console: ExecutionConsole): Content =
            registerDebuggedConsoleContent(ui, console)

    override fun registerAdditionalContent(ui: RunnerLayoutUi) {
        registerDebuggerConsoleContent(ui, debuggerConsole)
    }

    private fun registerDebuggedConsoleContent(ui: RunnerLayoutUi, console: ExecutionConsole): Content =
        registerConsoleContent(ui, console, "DebuggedConsoleContent", "Debugged Console", 1, PlaceInGrid.bottom, false)

    private fun registerDebuggerConsoleContent(ui: RunnerLayoutUi, console: ExecutionConsole): Content =
        registerConsoleContent(ui, console, "DebuggerConsoleContent", "Debugger Console", 2, PlaceInGrid.right, true)

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
}
