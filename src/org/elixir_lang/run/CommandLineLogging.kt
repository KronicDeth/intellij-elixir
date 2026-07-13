package org.elixir_lang.run

import com.intellij.execution.CommandLineUtil
import com.intellij.execution.Platform
import org.elixir_lang.sdk.wsl.wslCompat

/**
 * Formats a command line for debug logging with:
 * - Command and arguments on separate lines with proper indentation
 * - Environment variables
 * - Working directory
 * - A copy-pasteable `CLI:` line, escaped for the destination shell
 *
 * The destination shell is inferred from the working directory: a path starting with a
 * drive letter (e.g. `C:\...`) targets Windows (cmd.exe), everything else (POSIX paths and
 * WSL UNC paths like `\\wsl.localhost\...`) targets a POSIX/Bourne shell. Arguments are
 * escaped natively for that shell ([CommandLineUtil.posixQuote] for POSIX,
 * [CommandLineUtil.escapeParameterOnWindows] for Windows) and the line is prefixed with a
 * `cd` into the (POSIX-converted) working directory plus the environment assignments, so it
 * can be pasted and run as-is.
 */
internal fun formatCommandLineForLogging(processBuilder: ProcessBuilder, prefix: String = "Command line"): String {
    val msgBuilder = StringBuilder()
    // Get the command list (exe + arguments) - this is WSL-aware after conversion
    val commandList = processBuilder.command()

    val workDir = processBuilder.directory()
    val platform = destinationPlatform(workDir?.path)

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
    val redactedEnv = mutableListOf<Pair<String, String>>()
    if (userEnvVars.isNotEmpty()) {
        msgBuilder.append("  Environment variables:\n")
        userEnvVars.entries.sortedBy { it.key }.forEach { (name, value) ->
            val redactedValue = if (sensitivePatterns.any { name.contains(it, ignoreCase = true) }) {
                "<REDACTED>"
            } else {
                value
            }
            msgBuilder.append("    $name=$redactedValue\n")
            redactedEnv.add(name to redactedValue)
        }
    }

    // Show working directory
    if (workDir != null) {
        msgBuilder.append("  Working directory: ${workDir.path}\n")
    }

    msgBuilder.append("\nCLI:\n${buildCli(commandList, redactedEnv, workDir?.path, platform)}")
    return msgBuilder.toString().trimEnd()
}

/**
 * Infers the destination shell from the working directory path: a leading drive letter
 * (`C:`, `D:`, ...) means Windows, anything else (POSIX paths, WSL UNC paths) means POSIX.
 * Falls back to the host platform when there is no working directory.
 */
private fun destinationPlatform(workDirPath: String?): Platform = when {
    workDirPath == null -> Platform.current()
    workDirPath.length >= 2 && workDirPath[0].isLetter() && workDirPath[1] == ':' -> Platform.WINDOWS
    else -> Platform.UNIX
}

/**
 * Assembles a single copy-pasteable command line for [platform]: `cd` into the working
 * directory, then the environment assignments, then the escaped command, chained so the whole
 * thing can be pasted and run. Escaping uses the platform's native helpers.
 */
private fun buildCli(
    commandList: List<String>,
    env: List<Pair<String, String>>,
    workDirPath: String?,
    platform: Platform
): String = when (platform) {
    Platform.UNIX -> buildPosixCli(commandList, env, workDirPath)
    Platform.WINDOWS -> buildWindowsCli(commandList, env, workDirPath)
}

/**
 * POSIX/Bourne form: `cd '<dir>' && NAME=val ... <exe> <args>`.
 *
 * Per-argument [CommandLineUtil.posixQuote] is required because [CommandLineUtil.toCommandLine]
 * intentionally leaves Unix arguments unquoted (they are passed to `exec` literally, not through a
 * shell). The (typically WSL UNC) working directory is converted to its POSIX form so the line can
 * be pasted inside the WSL shell.
 */
private fun buildPosixCli(
    commandList: List<String>,
    env: List<Pair<String, String>>,
    workDirPath: String?
): String {
    val command = buildString {
        env.forEach { (name, value) -> append("$name=${CommandLineUtil.posixQuote(value)} ") }
        append(commandList.joinToString(" ") { CommandLineUtil.posixQuote(it) })
    }
    val cd = workDirPath?.let { "cd ${CommandLineUtil.posixQuote(wslCompat.maybeParseWindowsUncPath(it))} && " } ?: ""
    return cd + command
}

/**
 * cmd.exe form: `cd /d "<dir>" && set "NAME=val" && ... <exe> <args>`.
 *
 * cmd.exe has no inline `NAME=value cmd` syntax, so each variable gets its own `set`, chained with
 * `&&`. Command tokens are escaped with `isWinShell = true` so cmd metacharacters (`& | < > ( ) ^`)
 * are neutralized for a paste into an interactive cmd.exe session; the `cd` target uses CRT-style
 * quoting (`isWinShell = false`).
 *
 * Known limits (rare for the paths/flags logged here): a `set` value containing `"`, `%`, or `!`
 * is not fully escapable for interactive cmd.exe, and the line assumes cmd.exe rather than PowerShell.
 */
private fun buildWindowsCli(
    commandList: List<String>,
    env: List<Pair<String, String>>,
    workDirPath: String?
): String {
    val segments = mutableListOf<String>()
    workDirPath?.let { segments.add("cd /d ${CommandLineUtil.escapeParameterOnWindows(it, false)}") }
    env.forEach { (name, value) -> segments.add("""set "$name=$value"""") }
    if (commandList.isNotEmpty()) {
        segments.add(commandList.joinToString(" ") { CommandLineUtil.escapeParameterOnWindows(it, true) })
    }
    return segments.joinToString(" && ")
}
