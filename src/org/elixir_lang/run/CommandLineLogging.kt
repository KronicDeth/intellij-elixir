package org.elixir_lang.run

import com.intellij.execution.configurations.GeneralCommandLine

/**
 * Formats a command line for debug logging with:
 * - Command and arguments on separate lines with proper indentation
 * - Only user-added environment variables (not all system env vars)
 * - Working directory
 */
internal fun formatCommandLineForLogging(commandLine: GeneralCommandLine, prefix: String = "Command line"): String {
    val builder = StringBuilder()

    // Get the command list (exe + arguments) - this is WSL-aware after conversion
    val commandList = commandLine.getCommandLineList(null)

    builder.append("$prefix:\n")
    builder.append("  Executable: ${commandList.firstOrNull() ?: "<none>"}\n")

    if (commandList.size > 1) {
        builder.append("  Arguments:\n")
        commandList.drop(1).forEach { arg: String ->
            builder.append("    $arg\n")
        }
    }

    // Show only user-added environment variables (ones explicitly set on this command line)
    val userEnvVars = commandLine.environment
    if (userEnvVars.isNotEmpty()) {
        builder.append("  Extra Environment variables:\n")
        userEnvVars.entries.sortedBy { it.key }.forEach { (key, value) ->
            builder.append("    $key=$value\n")
        }
    }

    val systemEnvVars = commandLine.parentEnvironment
    if (systemEnvVars.isNotEmpty()) {
        builder.append("  System Environment variables:\n")
        systemEnvVars.entries.sortedBy { it.key }.forEach { (key, value) ->
            builder.append("    $key=$value\n")
        }
    }

    // Show working directory
    val workDir = commandLine.workDirectory
    if (workDir != null) {
        builder.append("  Working directory: ${workDir.path}\n")
    }

    return builder.toString().trimEnd()
}
