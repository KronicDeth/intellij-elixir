package platform.posix

import elixir.ElixirPlatform
import java.io.File

/**
 * POSIX implementation of ElixirPlatform (Linux, macOS, BSD).
 * Uses standard executables without extensions.
 * All other behavior inherited from ElixirPlatform defaults.
 */
class PosixElixirPlatform : ElixirPlatform {

    override fun getPlatformName(): String = "POSIX"

    override fun getExecutable(name: String, binPath: File): String {
        return binPath.resolve(name).absolutePath
    }
}
