package org.elixir_lang.run

import com.intellij.execution.configurations.PtyCommandLine
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.IOException

private val LOG = Logger.getInstance(WslAwarePtyCommandLine::class.java)

/**
 * A PtyCommandLine subclass that automatically applies WSL path conversion
 * right before process creation.
 *
 * This is the PTY equivalent of WslAwareCommandLine, ensuring interactive
 * sessions (IEx, Distillery consoles) also get WSL path conversion.
 *
 * @see PtyCommandLine
 * @see WslAwareCommandLine
 * @see org.elixir_lang.sdk.wsl.WslCompatService.convertCommandLineArgumentsForWsl
 */
open class WslAwarePtyCommandLine : PtyCommandLine {
    constructor() : super()

    @Throws(IOException::class)
    override fun createProcess(processBuilder: ProcessBuilder): Process {
        // Apply WSL path conversion right before creating the process
        // This catches ALL parameters regardless of when they were added
        // Using the protected method (same extension point as PtyCommandLine itself)
        wslCompat.convertCommandLineArgumentsForWsl(this)

        LOG.debug(formatCommandLineForLogging(this, "PTY Command line"))

        return super.createProcess(processBuilder)
    }
}
