package org.elixir_lang.run

/**
 * Formats a command line for debug logging with:
 * - Command and arguments on separate lines with proper indentation
 * - Environment variables
 * - Working directory
 */
internal fun formatCommandLineForLogging(processBuilder: ProcessBuilder, prefix: String = "Command line"): String {
    val builder = StringBuilder()

    // Get the command list (exe + arguments) - this is WSL-aware after conversion
    val commandList = processBuilder.command()

    builder.append("WARNING, THIS MAY LOG SENSITIVE INFORMATION\n")
    builder.append("$prefix:\n")
    builder.append("  Executable: ${commandList.firstOrNull() ?: "<none>"}\n")

    if (commandList.size > 1) {
        builder.append("  Arguments:\n")
        commandList.drop(1).forEach { arg: String ->
            builder.append("    $arg\n")
        }
    }

    val userEnvVars = processBuilder.environment()
    val sensitivePatterns = listOf("pass", "token", "secret", "auth", "sig", "key")
    if (userEnvVars.isNotEmpty()) {
        builder.append("  Environment variables:\n")
        userEnvVars.entries.sortedBy { it.key }.forEach { (name, value) ->
            val redactedValue = if (sensitivePatterns.any { name.contains(it, ignoreCase = true) }) {
                "<REDACTED>"
            } else {
                value
            }
            builder.append("    $name=$redactedValue\n")
        }
    }

    // Show working directory
    val workDir = processBuilder.directory()
    if (workDir != null) {
        builder.append("  Working directory: ${workDir.path}\n")
    }

    return builder.toString().trimEnd()
}
