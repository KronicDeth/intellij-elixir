package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.PtyCommandLine

fun commandLine(pty: Boolean, environment: Map<String, String>, workingDirectory: String?): GeneralCommandLine =
        commandLine(pty)
                .withCharset(Charsets.UTF_8)
                .withEnvironment(environment)
                .withWorkDirectory(workingDirectory)

private fun commandLine(pty: Boolean): GeneralCommandLine =
        if (pty) {
            PtyCommandLine()
        } else {
            GeneralCommandLine()
        }
