package org.elixir_lang.sdk

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.util.Version
import org.elixir_lang.jps.shared.sdk.SdkPaths
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

object SdkHomePaths {
    private const val HEAD_PREFIX = "HEAD-"

    /** `make install` with the Makefile's default `PREFIX`, i.e. a build from source. */
    private const val LINUX_SOURCE_ROOT_PATH = "/usr/local/lib"

    /**
     * `PREFIX=/usr` with the default `LIBDIR`, i.e. a distribution package. Debian, Ubuntu, Mint,
     * Arch, Alpine and openSUSE all land here; it is not specific to Mint.
     */
    private const val LINUX_DISTRO_ROOT_PATH = "/usr/lib"

    /**
     * Roots a source build or a distribution package installs a tool under, in scan order.
     * [linuxSystemHomePaths] suffixes the tool name onto each; a new distro layout is added here and
     * nowhere else.
     */
    private val LINUX_SYSTEM_ROOT_PATHS = listOf(LINUX_SOURCE_ROOT_PATH, LINUX_DISTRO_ROOT_PATH)

    @JvmField
    val UNKNOWN_VERSION = Version(0, 0, 0)

    private val HOMEBREW_ROOT = File(SdkPaths.HOMEBREW_INTEL_CELLAR_PATH)
    private val NIX_STORE = File(SdkPaths.NIX_STORE_PATH)
    private val LOGGER = Logger.getInstance(SdkHomePaths::class.java)

    @JvmStatic
    fun nixPattern(name: String): Pattern {
        return Pattern.compile(".+-$name-(\\d+)\\.(\\d+)\\.(\\d+)")
    }

    /**
     * Every hardcoded Linux home path for [toolName], in scan order.
     */
    @JvmStatic
    fun linuxSystemHomePaths(toolName: String): List<String> =
        LINUX_SYSTEM_ROOT_PATHS.map { "$it/$toolName" }

    /**
     * Adds each of [linuxSystemHomePaths] that exists as a directory.
     *
     * The native Linux scan and the WSL scan differ only by [toLocalPath], which maps a Linux path
     * to how this machine reaches it - identity when running on Linux, a UNC path when reaching into
     * a WSL distribution, and null when it cannot be reached at all. Both therefore go through this
     * one call, so the WSL scan cannot be left without a path the native scan gained.
     */
    @JvmStatic
    fun mergeLinuxSystemHomePaths(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        toolName: String,
        toLocalPath: (String) -> String? = { it },
    ) {
        for (candidate in linuxSystemHomePaths(toolName)) {
            val homePath = toLocalPath(candidate)

            if (homePath == null) {
                LOGGER.trace { "$candidate: Not reachable" }
                continue
            }

            if (File(homePath).isDirectory) {
                homePathByVersion[unknownVersionKey(homePath)] = homePath
            } else {
                LOGGER.trace { "$homePath: Not a directory" }
            }
        }
    }

    @JvmStatic
    fun mergeASDF(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String) {
        mergeASDF(homePathByVersion, name, System.getProperty("user.home"))
    }

    @JvmStatic
    fun mergeASDF(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String, userHome: String) {
        mergeNameSubdirectories(
            homePathByVersion,
            Path.of(userHome, SdkPaths.ASDF_INSTALLS_PATH_FROM_HOME).toFile(),
            name, SdkPaths.SOURCE_NAME_ASDF
        )
    }

    @JvmStatic
    fun mergeMise(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String) {
        mergeMise(homePathByVersion, name, System.getProperty("user.home"))
    }

    @JvmStatic
    fun mergeMise(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String, userHome: String) {
        listOf(SdkPaths.MISE_POSIX_PATH_FROM_HOME, SdkPaths.MISE_WINDOWS_PATH_FROM_HOME).forEach { misePath ->
            mergeNameSubdirectories(
                homePathByVersion,
                Path.of(userHome, misePath).toFile(),
                name, SdkPaths.SOURCE_NAME_MISE
            )
        }
    }

    @JvmStatic
    fun mergeElixirInstallScript(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String) {
        mergeElixirInstallScript(homePathByVersion, name, System.getProperty("user.home"))
    }

    @JvmStatic
    fun mergeElixirInstallScript(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String, userHome: String) {
        mergeNameSubdirectories(
            homePathByVersion,
            Path.of(userHome, SdkPaths.ELIXIR_INSTALL_INSTALLS_PATH_FROM_HOME).toFile(),
            name, SdkPaths.SOURCE_NAME_ELIXIR_INSTALL
        )
    }

    @JvmStatic
    fun mergeHomebrew(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        name: String,
        versionPathToHomePath: (File) -> File,
    ) {
        mergeNameSubdirectories(
            homePathByVersion,
            HOMEBREW_ROOT,
            name,
            SdkPaths.SOURCE_NAME_HOMEBREW,
            versionPathToHomePath
        )
    }

    @JvmStatic
    fun mergeNixStore(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        nixPattern: Pattern,
        versionPathToHomePath: (File) -> File,
    ) {
        mergeNixStore(homePathByVersion, nixPattern, versionPathToHomePath, NIX_STORE.absolutePath)
    }

    @JvmStatic
    fun mergeNixStore(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        nixPattern: Pattern,
        versionPathToHomePath: (File) -> File,
        nixStorePath: String,
    ) {
        val nixStore = File(nixStorePath)
        if (nixStore.isDirectory) {
            nixStore.listFiles { dir, name ->
                val matcher: Matcher = nixPattern.matcher(name)
                var accept = false
                if (matcher.matches()) {
                    val major = matcher.group(1).toInt()
                    val minor = matcher.group(2).toInt()
                    val bugfix = matcher.group(3).toInt()
                    val version = Version(major, minor, bugfix)
                    val homePath = versionPathToHomePath(File(dir, name)).absolutePath
                    val key = SdkHomeKey(version, name, SdkPaths.SOURCE_NAME_NIX, homePath)
                    homePathByVersion[key] = homePath
                    accept = true
                }
                accept
            }
        }
    }

    @JvmStatic
    fun mergeTravisCIKerl(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        versionPathToHomePath: (File) -> File,
    ) {
        val userHome = System.getProperty("user.home")
        if (userHome != null) {
            mergeTravisCIKerl(homePathByVersion, versionPathToHomePath, userHome)
        }
    }

    @JvmStatic
    fun mergeTravisCIKerl(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        versionPathToHomePath: (File) -> File,
        userHome: String,
    ) {
        mergeNameSubdirectories(
            homePathByVersion,
            File(userHome),
            SdkPaths.TRAVIS_CI_KERL_DIR_NAME,
            SdkPaths.SOURCE_NAME_KERL,
            versionPathToHomePath
        )
    }

    @JvmStatic
    fun mergeKerl(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        versionPathToHomePath: (File) -> File,
    ) {
        if (!isCommandAvailable("kerl")) {
            return
        }

        try {
            val process = ProcessBuilder("kerl", "list", "installations")
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split(" ", limit = 2)
                    if (parts.size == 2) {
                        val versionString = parts[0].trim()
                        val path = parts[1].trim()
                        val homePath = versionPathToHomePath(File(path))
                        if (homePath.isDirectory) {
                            val version = parseVersion(versionString)
                            val key =
                                SdkHomeKey(version, versionString, SdkPaths.SOURCE_NAME_KERL, homePath.absolutePath)
                            homePathByVersion[key] = homePath.absolutePath
                        }
                    }
                }
            }

            process.waitFor(5, TimeUnit.SECONDS)
        } catch (e: IOException) {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("kerl list installations failed: ${e.message}")
            }
        } catch (e: InterruptedException) {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("kerl list installations failed: ${e.message}")
            }
        }
    }

    private fun mergeNameSubdirectories(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        parent: File,
        name: String,
        source: String,
        versionPathToHomePath: (File) -> File = { it },
    ) {
        LOGGER.trace { "$parent: Scanning for SDK Home Paths" }
        if (!parent.isDirectory) {
            LOGGER.trace { "$parent: Not a directory" }
            return
        }

        val nameDirectory = File(parent, name)
        LOGGER.trace { "$nameDirectory: Scanning" }
        if (!nameDirectory.isDirectory) {
            LOGGER.trace { "$nameDirectory: Not a directory" }
            return
        }

        val children = nameDirectory.listFiles() ?: return
        for (child in children) {
                LOGGER.trace { "$child: Scanning" }
            if (child.isDirectory) {
                val homePath = wslCompat.canonicalizePath(versionPathToHomePath(child).absolutePath)
                val versionString = File(homePath).name
                val version = parseVersion(versionString)
                val key = SdkHomeKey(version, versionString, source, homePath)
                LOGGER.trace { "$child: Adding $key" }
                homePathByVersion[key] = homePath
            }
        }
    }

    private fun parseVersion(versionString: String): Version {
        var version = Version.parseVersion(versionString)
        if (version == null) {
            version = if (versionString.startsWith(HEAD_PREFIX)) {
                val sha1 = versionString.substring(HEAD_PREFIX.length)
                Version(0, 0, Integer.parseInt(sha1, 16))
            } else {
                UNKNOWN_VERSION
            }
        }
        return version
    }

    private fun isCommandAvailable(command: String): Boolean {
        return try {
            val process = ProcessBuilder("which", command).start()
            process.waitFor() == 0
        } catch (_: IOException) {
            false
        } catch (_: InterruptedException) {
            false
        }
    }

    fun unknownVersionKey(path: String) = SdkHomeKey(UNKNOWN_VERSION, null, null, path)
}
