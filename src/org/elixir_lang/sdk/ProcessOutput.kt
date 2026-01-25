package org.elixir_lang.sdk

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.Function
import com.intellij.util.PlatformUtils
import com.intellij.util.containers.ContainerUtil
import org.elixir_lang.run.WslAwareCommandLine
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Created by zyuyou on 2015/5/27.
 *
 */
object ProcessOutput {
    /*
   * CONSTANTS
   */
    private val LOGGER = Logger.getInstance(
        ProcessOutput::class.java
    )
    const val STANDARD_TIMEOUT: Int = 10 * 1000

    fun <T> transformStdoutLine(
        output: com.intellij.execution.process.ProcessOutput,
        lineTransformer: Function<String?, T>
    ): T? {
        val lines = if (output.exitCode != 0 || output.isTimeout || output.isCancelled) {
            ContainerUtil.emptyList()
        } else {
            output.stdoutLines
        }

        var transformed: T? = null

        for (line in lines) {
            transformed = lineTransformer.`fun`(line)

            if (transformed != null) {
                break
            }
        }

        return transformed
    }

    fun <T> transformStdoutLine(
        lineTransformer: Function<String?, T>,
        timeout: Int,
        workDir: String,
        exePath: String,
        vararg arguments: String?
    ): T? {
        var transformed: T? = null

        try {
            val output = getProcessOutput(timeout, workDir, exePath, *arguments)

            transformed = transformStdoutLine(output, lineTransformer)
        } catch (executionException: ExecutionException) {
            LOGGER.warn(executionException)
        }

        return transformed
    }

    @JvmStatic
    @Throws(ExecutionException::class)
    fun getProcessOutput(
        timeout: Int,
        workDir: String?,
        exePath: String,
        vararg arguments: String?
    ): com.intellij.execution.process.ProcessOutput {
        if (workDir == null || !File(workDir).isDirectory || !File(exePath).canExecute()) {
            return com.intellij.execution.process.ProcessOutput()
        }

        val cmd = WslAwareCommandLine().withCharset(StandardCharsets.UTF_8)
        cmd.withWorkDirectory(workDir)
        cmd.exePath = exePath
        cmd.addParameters(*arguments)

        return execute(cmd, timeout)
    }

    @JvmOverloads
    @Throws(ExecutionException::class)
    fun execute(
        cmd: GeneralCommandLine,
        timeout: Int = STANDARD_TIMEOUT
    ): com.intellij.execution.process.ProcessOutput {
        val processHandler = CapturingProcessHandler(cmd)
        return if (timeout < 0) processHandler.runProcess() else processHandler.runProcess(timeout)
    }

    @JvmStatic
    val isSmallIde: Boolean
        get() = !(PlatformUtils.isIntelliJ() || PlatformUtils.getPlatformPrefix() == "AndroidStudio")
}
