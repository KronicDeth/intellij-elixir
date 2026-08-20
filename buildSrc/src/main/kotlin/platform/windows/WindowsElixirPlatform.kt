package platform.windows

import elixir.ElixirPlatform
import java.io.File

/**
 * Windows implementation of ElixirPlatform.
 * Uses .bat wrappers for all Elixir executables.
 * All other behavior inherited from ElixirPlatform defaults.
 */
class WindowsElixirPlatform : ElixirPlatform {

    override fun getPlatformName(): String = "Windows"

    override fun getExecutable(name: String, binPath: File): String {
        // Windows Elixir uses .bat wrappers for all commands
        return binPath.resolve("$name.bat").absolutePath
    }
}
