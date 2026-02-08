package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import java.io.FileNotFoundException

object Distillery {
    /**
     * Keep in-sync with the JPS builder erl command line.
     */
    fun commandLine(pty: Boolean, environment: Map<String, String>, workingDirectory: String?, exePath: String?):
            GeneralCommandLine {
        val commandLine = commandLine(pty, environment, workingDirectory)
        commandLine.exePath = exePath ?: throw FileNotFoundException("Distillery release CLI path is not set")

        return commandLine
    }
}
