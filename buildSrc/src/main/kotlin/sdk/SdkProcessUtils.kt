package sdk

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Executes a command with a short timeout and returns combined stdout/stderr.
 */
fun execAndCapture(
    command: List<String>,
    workingDir: File?,
    environment: Map<String, String> = emptyMap()
): ProcessResult? {
    return try {
        val processBuilder = ProcessBuilder(command)
            .directory(workingDir)
            .redirectErrorStream(true)
        if (environment.isNotEmpty()) {
            processBuilder.environment().putAll(environment)
        }
        val process = processBuilder.start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val finished = process.waitFor(30, TimeUnit.SECONDS)
        if (!finished) {
            process.destroyForcibly()
            ProcessResult(-1, output.trim())
        } else {
            ProcessResult(process.exitValue(), output.trim())
        }
    } catch (e: IOException) {
        null
    }
}

/**
 * Returns the last non-empty line from the given text, trimming whitespace.
 */
fun lastNonEmptyLine(text: String): String? {
    return text.lineSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .lastOrNull()
}

/**
 * Executes a small Erlang snippet via escript or erl and returns its output.
 */
fun executeErlangCode(code: String, binDir: File?): String? {
    val escriptCandidates = if (binDir != null) {
        listOf(File(binDir, erlangEscriptExecutableName()))
            .filter { it.isFile }
            .map { it.absolutePath }
    } else {
        listOf("escript")
    }
    val erlCandidates = if (binDir != null) {
        listOf(File(binDir, erlangExecutableName()))
            .filter { it.isFile }
            .map { it.absolutePath }
    } else {
        listOf("erl")
    }

    escriptCandidates.forEach { escript ->
        val output = runEscriptCode(escript, code)
        if (!output.isNullOrBlank()) {
            return output
        }
    }

    erlCandidates.forEach { erl ->
        val output = runErlCode(erl, code)
        if (!output.isNullOrBlank()) {
            return output
        }
    }

    return null
}

private fun runEscriptCode(escriptPath: String, code: String): String? {
    return try {
        val tempScript = Files.createTempFile("erl_exec_", ".erl").toFile()
        tempScript.deleteOnExit()
        tempScript.writeText(
            buildString {
                appendLine("#!/usr/bin/env escript")
                appendLine("%%! -noshell")
                appendLine("main(_) -> $code")
            }
        )
        val result = execAndCapture(listOf(escriptPath, tempScript.absolutePath), null)
        tempScript.delete()
        if (result == null || result.exitCode != 0) null else lastNonEmptyLine(result.output)
    } catch (e: IOException) {
        null
    }
}

private fun runErlCode(erlPath: String, code: String): String? {
    val result = execAndCapture(listOf(erlPath, "-noshell", "-eval", code), null) ?: return null
    return if (result.exitCode == 0) lastNonEmptyLine(result.output) else null
}

/**
 * True when running on Windows (used for executable naming).
 */
fun isWindows(): Boolean {
    val osName = System.getProperty("os.name").lowercase(Locale.US)
    return osName.contains("windows")
}

// Platform-specific executable names used in resolution and validation.
fun erlangExecutableName(): String = if (isWindows()) "erl.exe" else "erl"

fun erlangEscriptExecutableName(): String = if (isWindows()) "escript.exe" else "escript"

fun elixirExecutableName(): String = if (isWindows()) "elixir.bat" else "elixir"
