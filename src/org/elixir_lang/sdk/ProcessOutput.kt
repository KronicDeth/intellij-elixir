package org.elixir_lang.sdk

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.Function
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.containers.ContainerUtil
import org.elixir_lang.run.WslAwareCommandLine
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

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

    @RequiresBackgroundThread
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
    @RequiresBackgroundThread
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
    @RequiresBackgroundThread
    @Throws(ExecutionException::class)
    fun execute(
        cmd: GeneralCommandLine,
        timeout: Int = STANDARD_TIMEOUT
    ): com.intellij.execution.process.ProcessOutput {
        ThreadingAssertions.assertBackgroundThread()
        val processHandler = CapturingProcessHandler(cmd)
        return if (timeout < 0) processHandler.runProcess() else processHandler.runProcess(timeout)
    }

    /**
     * Indicates whether the current IntelliJ Platform-based IDE is a "small IDE."
     *
     * A "small IDE" is determined by comparing the product code of the running application
     * against a predefined list of product codes for full IntelliJ IDEA editions.
     * If the IDE is not one of these editions, it is considered a "small IDE."
     *
     * This property is primarily used to adjust behavior for lightweight or simplified IDEs
     * (e.g., excluding advanced features or configurations available in full editions).
     *
     * If an exception occurs during the product code retrieval or verification process,
     * a default value of `true` is used, and a warning is logged.
     *
     * This sort of thing appears to be frowned upon by JB. Originally it used `PlatformUtils.isIntelliJ()`
     * but that was internal and prevented submission of the plugin. Then we tried detecting if
     * `Class.forName("com.intellij.openapi.roots.JavadocOrderRootType")` returned anything, but at least
     * since 2026.1 that returns a class in RubyMine etc, so became unreliable.
     *
     * Ideally you should prefer to implement the suggestion below from JB's PlatformUtils class, but if that's
     * not possible, this can be used instead.
     *
     * _"If you need to customize behavior of the platform somewhere, you should create a special application service
     * for that and override it in a specific IDE (look at_ {@link com.intellij.lang.IdeLanguageCustomization} _and_
     * {@link com.intellij.openapi.updateSettings.UpdateStrategyCustomization} _for example)."_
     *
     */
    @JvmStatic
    val isSmallIde: Boolean
        get() = try {
            val productCode = ApplicationInfo.getInstance().build.productCode.uppercase(Locale.US)
            productCode !in INTELLIJ_IDEA_PRODUCT_CODES
        } catch (throwable: Throwable) {
            LOGGER.warn("Unable to detect IDE product; defaulting to small IDE behavior", throwable)
            true
        }

    private val INTELLIJ_IDEA_PRODUCT_CODES: Set<String> = setOf("IC", "IU", "IE")
}
