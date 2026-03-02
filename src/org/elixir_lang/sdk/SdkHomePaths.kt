package org.elixir_lang.sdk

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.util.Version
import org.elixir_lang.jps.shared.sdk.SdkPaths
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

object SdkHomePaths {
    private const val HEAD_PREFIX = "HEAD-"
    const val LINUX_MINT_HOME_PATH = "/usr/lib"
    const val LINUX_DEFAULT_HOME_PATH = "/usr/local/lib"
    const val NIX_STORE_PATH = "/nix/store"

    @JvmField
    val UNKNOWN_VERSION = Version(0, 0, 0)

    private val HOMEBREW_ROOT = File("/usr/local/Cellar")
    private val NIX_STORE = File(NIX_STORE_PATH)
    private val LOGGER = Logger.getInstance(SdkHomePaths::class.java)

    @JvmStatic
    fun nixPattern(name: String): Pattern {
        return Pattern.compile(".+-$name-(\\d+)\\.(\\d+)\\.(\\d+)")
    }

    @JvmStatic
    fun mergeASDF(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String) {
        mergeASDF(homePathByVersion, name, System.getProperty("user.home"))
    }

    @JvmStatic
    fun mergeASDF(homePathByVersion: MutableMap<SdkHomeKey, String>, name: String, userHome: String) {
        mergeNameSubdirectories(
            homePathByVersion,
            Path.of(userHome, ".asdf", "installs").toFile(),
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
            Path.of(userHome, ".elixir-install", "installs").toFile(),
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
            "otp",
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
                val versionString = child.name
                val version = parseVersion(versionString)
                val homePath = versionPathToHomePath(child).absolutePath
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
