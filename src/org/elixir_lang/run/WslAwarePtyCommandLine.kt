package org.elixir_lang.run

import com.intellij.execution.configurations.PtyCommandLine
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.IOException

/**
 * A PtyCommandLine subclass that automatically applies WSL path conversion
 * right before process creation.
 *
 * This is the PTY equivalent of WslAwareCommandLine, ensuring interactive
 * sessions (IEx, Distillery consoles) also get WSL path conversion.
 *
 * @see PtyCommandLine
 * @see WslAwareCommandLine
 * @see org.elixir_lang.sdk.wsl.WslCompatService.convertProcessBuilderArgumentsForWsl
 */
open class WslAwarePtyCommandLine : PtyCommandLine {
    constructor() : super()

    @Throws(IOException::class)
    override fun createProcess(processBuilder: ProcessBuilder): Process {
        wslCompat.convertProcessBuilderArgumentsForWsl(processBuilder, this)
        return super.createProcess(processBuilder)
    }
}
