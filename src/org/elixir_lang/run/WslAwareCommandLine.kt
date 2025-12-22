package org.elixir_lang.run

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.IOException

private val LOG = Logger.getInstance(WslAwareCommandLine::class.java)

/**
 * A GeneralCommandLine subclass that automatically applies WSL path conversion
 * right before process creation.
 *
 * This ensures ALL command line parameters, environment variables, and paths
 * are properly converted for WSL execution, regardless of when they were added
 * to the command line.
 *
 * ## Why Subclass?
 *
 * Previously, WSL conversion was applied at command line construction time via
 * wslCompatCommandLine { } wrappers. However, external tools and run configurations
 * often add parameters AFTER the factory method returns, causing those parameters
 * to bypass WSL conversion:
 *
 * ```kotlin
 * val commandLine = Mix.commandLine(...)  // WSL converted
 * commandLine.addParameters(newParams)     // NOT converted!
 * commandLine.createProcess()
 * ```
 *
 * By overriding the protected createProcess(ProcessBuilder) method, we apply conversion
 * at the last possible moment, ensuring ALL parameters are converted.
 *
 * This uses the same safe extension point that IntelliJ's PtyCommandLine uses.
 *
 * ## Usage
 *
 * Simply construct WslAwareCommandLine instead of GeneralCommandLine:
 * ```kotlin
 * val commandLine = WslAwareCommandLine()
 *     .withExePath(exePath)
 *     .withParameters(params)
 * // WSL conversion happens automatically in createProcess()
 * ```
 *
 * @see GeneralCommandLine
 * @see org.elixir_lang.sdk.wsl.WslCompatService.convertCommandLineArgumentsForWsl
 */
open class WslAwareCommandLine : GeneralCommandLine {
    constructor() : super()

    @Throws(IOException::class)
    override fun createProcess(processBuilder: ProcessBuilder): Process {
        // Apply WSL path conversion right before creating the process
        // This catches ALL parameters regardless of when they were added
        // Using the protected method (same extension point as PtyCommandLine)
        wslCompat.convertCommandLineArgumentsForWsl(this)

        LOG.debug(formatCommandLineForLogging(this))

        return super.createProcess(processBuilder)
    }
}
