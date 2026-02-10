package org.elixir_lang

import com.intellij.execution.ExecutionException
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.diagnostic.traceThrowable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.util.execution.ParametersListUtil
import org.elixir_lang.sdk.HomePath
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.File

internal object ElixirCliDryRun {
    private val logger = Logger.getInstance(ElixirCliDryRun::class.java)
    enum class Tool {
        ELIXIR,
        MIX,
        IEX,
    }

    private const val DRY_RUN_ENV = "ELIXIR_CLI_DRY_RUN"
    private const val DRY_RUN_TIMEOUT_MS = 10_000

    fun baseArguments(
        tool: Tool,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
    ): kotlin.collections.List<String>? {
        logger.trace {
            "ELIXIR_CLI_DRY_RUN start: tool=$tool, sdkHome=${elixirSdk.homePath}, workingDir=$workingDirectory"
        }
        val application = ApplicationManager.getApplication()
        if (application != null && application.isDispatchThread) {
            logger.trace { "ELIXIR_CLI_DRY_RUN skip: tool=$tool reason=EDT" }
            return null
        }
        val effectiveWorkingDirectory = resolveWorkingDirectory(workingDirectory, elixirSdk)
        val exePath = toolExePath(tool, elixirSdk, effectiveWorkingDirectory)
        if (exePath == null) {
            logger.trace { "ELIXIR_CLI_DRY_RUN skip: tool=$tool reason=missing_exe_path" }
            return null
        }
        logger.trace { "ELIXIR_CLI_DRY_RUN exec: tool=$tool exePath=$exePath workDir=$effectiveWorkingDirectory" }
        val dryRunEnv = environment.toMutableMap().apply { put(DRY_RUN_ENV, "1") }

        val dryRunCommandLine = commandLine(
            pty = false,
            environment = dryRunEnv,
            workingDirectory = effectiveWorkingDirectory
        )
        dryRunCommandLine.exePath = exePath
        dryRunCommandLine.addParameter("--")

        val output = try {
            CapturingProcessHandler(dryRunCommandLine).runProcess(DRY_RUN_TIMEOUT_MS)
        } catch (e: ExecutionException) {
            logger.traceThrowable { Throwable("ELIXIR_CLI_DRY_RUN failed: tool=$tool reason=execution_exception", e) }
            return null
        }
        if (output.isTimeout || output.isCancelled) {
            logger.trace { "ELIXIR_CLI_DRY_RUN failed: tool=$tool reason=timeout_or_cancelled" }
            return null
        }

        val tokens = extractErlTokens(output.stdoutLines)
            ?: extractErlTokens(output.stderrLines)
            ?: run {
                logger.trace { "ELIXIR_CLI_DRY_RUN failed: tool=$tool reason=no_erl_tokens" }
                return null
            }

        val baseArguments = stripDryRunMarkers(tokens.drop(1))
        logger.trace { "ELIXIR_CLI_DRY_RUN success: tool=$tool\nbaseArgs=${baseArguments}\nOutput= $output" }
        return baseArguments
    }

    private fun extractErlTokens(lines: kotlin.collections.List<String>): kotlin.collections.List<String>? {
        for (line in lines) {
            val tokens = ParametersListUtil.parseToArray(line).toList()
            if (tokens.isEmpty()) {
                continue
            }
            val first = tokens.first()
            if (first.endsWith("erl") || first.endsWith("erl.exe")) {
                return tokens
            }
        }
        return null
    }

    private fun stripDryRunMarkers(arguments: kotlin.collections.List<String>): kotlin.collections.List<String> {
        val trimmed = arguments.toMutableList()
        if (trimmed.lastOrNull() == "--") {
            trimmed.removeAt(trimmed.lastIndex)
        }
        return trimmed
    }

    private fun toolExePath(
        tool: Tool,
        elixirSdk: Sdk,
        workingDirectory: String?,
    ): String? {
        val homePath = elixirSdk.homePath ?: return null
        val effectiveHomePath = maybeConvertHomePathForWsl(homePath, workingDirectory)
        val file = when (tool) {
            Tool.MIX -> executableFile(effectiveHomePath, "mix")
            Tool.ELIXIR -> executableFile(effectiveHomePath, "elixir")
            Tool.IEX -> executableFile(effectiveHomePath, "iex")
        }
        return if (file.exists()) file.absolutePath else null
    }

    private fun resolveWorkingDirectory(workingDirectory: String?, elixirSdk: Sdk): String? {
        val homePath = elixirSdk.homePath ?: return workingDirectory
        if (wslCompat.isWslUncPath(homePath)) {
            val homeDistribution = wslCompat.getDistributionByWindowsUncPath(homePath)
            val workDistribution = wslCompat.getDistributionByWindowsUncPath(workingDirectory)
            return if (workDistribution == null || homeDistribution == null ||
                workDistribution.msId != homeDistribution.msId) {
                homePath
            } else {
                workingDirectory
            }
        }
        return workingDirectory
    }

    private fun maybeConvertHomePathForWsl(homePath: String, workingDirectory: String?): String {
        if (wslCompat.isWslUncPath(homePath)) {
            return homePath
        }
        val distribution = wslCompat.getDistributionByWindowsUncPath(workingDirectory) ?: return homePath
        return wslCompat.convertLinuxPathToWindowsUnc(distribution, homePath) ?: homePath
    }

    private fun executableFile(homePath: String, executableName: String): File {
        val fileName = HomePath.getExecutableFileName(homePath, executableName, ".bat")
        val file = File(File(homePath, "bin"), fileName)
        if (file.exists()) {
            return file
        }
        throw RuntimeException("Unable to find executable for $executableName")
    }
}
