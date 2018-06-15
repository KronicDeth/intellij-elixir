package org.elixir_lang.iex.console

import com.intellij.execution.console.ConsoleHistoryController
import com.intellij.execution.console.LanguageConsoleImpl
import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.iex.console.view.Registry
import java.io.IOException
import java.io.OutputStreamWriter

class View(project: Project) : LanguageConsoleImpl(project, "Elixir Console", ElixirLanguage) {
    private var historyController: ConsoleHistoryController? = null
    private var processInputWriter: OutputStreamWriter? = null

    init {
        prompt = "iex>"
    }

    override fun attachToProcess(processHandler: ProcessHandler) {
        super.attachToProcess(processHandler)
        val processInput = processHandler.processInput!!

        processInputWriter = OutputStreamWriter(processInput)
        historyController = ConsoleHistoryController("iex", null, this)
        historyController!!.install()
        Registry.add(this)
    }

    override fun doAddPromptToHistory() {}

    override fun dispose() {
        super.dispose()
        Registry.remove(this)
    }

    fun append(text: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val document = currentEditor.document
            document.insertString(document.textLength, text)
        }
    }

    fun execute() {
        if (processInputWriter == null || historyController == null) {
            return
        }

        val processInputWriter = processInputWriter!!
        val historyController = historyController!!
        val consoleEditor = consoleEditor
        val editorDocument = consoleEditor.document
        val text = editorDocument.text

        addToHistoryInner(TextRange(0, text.length), consoleEditor, true, true)
        historyController.addToHistory(text)

        for (line in text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            try {
                processInputWriter.write(line + "\n")
                processInputWriter.flush()
            } catch (e: IOException) { // Ignore
            }
        }
    }
}
