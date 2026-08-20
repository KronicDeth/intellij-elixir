package org.elixir_lang.jps.shared.cli

import com.intellij.util.system.OS
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

const val EXT_BAT = ".bat"
const val EXT_EXE = ".exe"

/**
 * For use with [CliArguments] and [org.elixir_lang.cli.CliArguments]
 */
enum class CliTool(
    val executableBaseName: String,
    val executableExtOnWindows: String,
) {
    MIX("mix", EXT_BAT),
    IEX("iex", EXT_BAT),
    ELIXIR("elixir", EXT_BAT),
    ELIXIRC("elixirc", EXT_BAT),
    ERL("erl", EXT_EXE);

    /**
     * If WSL is not a concern, this doesn't need an argument passed to it. If you do need to deal with
     * WSL, pass in the OS.Linux for WSL contexts.
     */
    fun getExecutableFilename(os: OS = OS.CURRENT): String =
        when (os) {
            OS.Windows -> executableBaseName + executableExtOnWindows
            else -> executableBaseName
        }

    /**
     * If WSL is not a concern, this doesn't need an argument passed to it. If you do need to deal with
     * WSL, pass in the OS.Linux for WSL contexts.
     */
    fun getExecutableFilepath(sdkHomePath: String, os: OS = OS.CURRENT): String =
        Path(sdkHomePath, "bin", getExecutableFilename(os)).absolutePathString()

}
