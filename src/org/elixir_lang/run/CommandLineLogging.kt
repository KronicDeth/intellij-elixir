package org.elixir_lang.run

import com.intellij.util.execution.ParametersListUtil

/**
 * Formats a command line for debug logging with:
 * - Command and arguments on separate lines with proper indentation
 * - Environment variables
 * - Working directory
 */
internal fun formatCommandLineForLogging(processBuilder: ProcessBuilder, prefix: String = "Command line"): String {
    val msgBuilder = StringBuilder()
    // Get the command list (exe + arguments) - this is WSL-aware after conversion
    val commandList = processBuilder.command()

    msgBuilder.append("WARNING, THIS MAY LOG SENSITIVE INFORMATION\n")
    msgBuilder.append("$prefix:\n")
    msgBuilder.append("  Executable: ${commandList.firstOrNull() ?: "<none>"}\n")

    if (commandList.size > 1) {
        msgBuilder.append("  Arguments:\n")
        commandList.drop(1).forEach { arg: String ->
            msgBuilder.append("    $arg\n")
        }
    }

    val userEnvVars = processBuilder.environment()
    val sensitivePatterns = listOf("pass", "token", "secret", "auth", "sig", "key")
    val environmentCliDeclaration = mutableListOf<String>()
    if (userEnvVars.isNotEmpty()) {
        msgBuilder.append("  Environment variables:\n")
        userEnvVars.entries.sortedBy { it.key }.forEach { (name, value) ->
            val redactedValue = if (sensitivePatterns.any { name.contains(it, ignoreCase = true) }) {
                "<REDACTED>"
            } else {
                value
            }
            msgBuilder.append("    $name=$redactedValue\n")
            val quotedValue = ParametersListUtil.escape(redactedValue)
            environmentCliDeclaration.add("$name=$quotedValue")
        }
    }

    // Show working directory
    val workDir = processBuilder.directory()
    if (workDir != null) {
        msgBuilder.append("  Working directory: ${workDir.path}\n")
    }

    msgBuilder.append("\nCLI:\n${environmentCliDeclaration.joinToString(" ")} ${ParametersListUtil.join(commandList)}")
    return msgBuilder.toString().trimEnd()
}
