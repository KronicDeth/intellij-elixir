package org.elixir_lang.sdk

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Version
import com.intellij.util.system.CpuArch
import com.intellij.util.system.OS
import org.elixir_lang.jps.HomePath
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.function.Function

/**
 * Consolidated SDK home path scanning for Elixir and Erlang SDKs.
 *
 * Scans multiple platforms (Mac, Windows, Linux, WSL) and version managers
 * (ASDF, mise, Homebrew, kerl, Nix) to discover SDK installations.
 */
object SdkHomeScan {
    private val LOG = Logger.getInstance(SdkHomeScan::class.java)

    /**
     * Configuration for SDK-specific scanning behavior.
     *
     * @property toolName The tool name used by version managers (e.g., "elixir", "erlang")
     * @property nixPattern Regex pattern for matching Nix store packages
     * @property linuxDefaultPath System install path on Linux (e.g., "/usr/local/lib/elixir")
     * @property linuxMintPath Linux Mint install path (e.g., "/usr/lib/elixir")
     * @property windowsDefaultPath Primary Windows install path (null if no default)
     * @property windows32BitPath 32-bit Windows install path (null if same as default)
     * @property homebrewTransform Path transform for Homebrew (null = identity)
     * @property nixTransform Path transform for Nix Store (null = identity)
     * @property kerlTransform Path transform for kerl (null = skip kerl scanning)
     * @property travisCIKerlTransform Path transform for Travis CI kerl (null = skip)
     */
    data class Config(
        val toolName: String,
        val nixPattern: java.util.regex.Pattern,
        val linuxDefaultPath: String,
        val linuxMintPath: String,
        val windowsDefaultPath: String?,
        val windows32BitPath: String? = null,

        // Path transformations (null = identity for homebrew/nix, skip for kerl)
        val homebrewTransform: ((File) -> File)? = null,
        val nixTransform: ((File) -> File)? = null,
        val kerlTransform: ((File) -> File)? = null,
        val travisCIKerlTransform: ((File) -> File)? = null
    )

    /**
     * Scans for SDK installations across all platforms.
     *
     * @param path Project directory for WSL distribution filtering (null = scan all)
     * @param config SDK-specific configuration
     * @return Map of versions to SDK home paths, sorted by version (descending)
     */
    fun homePathByVersion(path: Path?, config: Config): Map<Version, String> {
        LOG.debug("Scanning for ${config.toolName} SDKs (path: $path, platform: ${OS.CURRENT})")
        val homePathByVersion: MutableMap<Version, String> = TreeMap(Comparator.reverseOrder())

        val result = when (OS.CURRENT) {
            OS.macOS ->
                homePathByVersionMac(homePathByVersion, config)

            OS.Windows ->
                homePathByVersionWindows(path, homePathByVersion, config)

            OS.Linux ->
                homePathByVersionLinux(homePathByVersion, config)

            else -> {
                LOG.debug("Unsupported platform: ${OS.CURRENT}")
                homePathByVersion
            }
        }

        LOG.debug("Found ${result.size} ${config.toolName} SDK(s): ${result.values.take(3).joinToString(", ")}${if (result.size > 3) "..." else ""}")
        return result
    }

    /**
     * Scans macOS for SDK installations via ASDF, Mise, kerl, Homebrew, and Nix.
     * Note: path parameter not used on macOS (no distribution filtering needed).
     */
    private fun homePathByVersionMac(
        homePathByVersion: MutableMap<Version, String>,
        config: Config
    ): Map<Version, String> {
        HomePath.mergeASDF(homePathByVersion, config.toolName)
        HomePath.mergeMise(homePathByVersion, config.toolName)

        if (config.kerlTransform != null) {
            HomePath.mergeKerl(homePathByVersion, config.kerlTransform.toJavaFunction())
        }

        val homebrewTransform = config.homebrewTransform?.toJavaFunction() ?: Function.identity()
        HomePath.mergeHomebrew(homePathByVersion, config.toolName, homebrewTransform)

        val nixTransform = config.nixTransform?.toJavaFunction() ?: Function.identity()
        HomePath.mergeNixStore(homePathByVersion, config.nixPattern, nixTransform)

        return homePathByVersion
    }

    /**
     * Scans Windows for SDK installations via native paths and WSL distributions.
     */
    private fun homePathByVersionWindows(
        path: Path?,
        homePathByVersion: MutableMap<Version, String>,
        config: Config
    ): Map<Version, String> {
        // Native Windows paths - select based on CPU architecture
        val windowsPath = if (CpuArch.CURRENT.width == 32) {
            config.windows32BitPath ?: config.windowsDefaultPath
        } else {
            config.windowsDefaultPath
        }

        windowsPath?.let {
            putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, it)
        }

        // WSL distributions
        homePathByVersionWSLs(path, homePathByVersion, config)

        return homePathByVersion
    }

    /**
     * Scans Linux for SDK installations via system paths, ASDF, Mise, kerl, Travis CI kerl, and Nix.
     * Note: path parameter not used on Linux (no distribution filtering needed).
     */
    private fun homePathByVersionLinux(
        homePathByVersion: MutableMap<Version, String>,
        config: Config
    ): Map<Version, String> {
        putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, config.linuxDefaultPath)
        putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, config.linuxMintPath)

        HomePath.mergeASDF(homePathByVersion, config.toolName)
        HomePath.mergeMise(homePathByVersion, config.toolName)

        if (config.kerlTransform != null) {
            HomePath.mergeKerl(homePathByVersion, config.kerlTransform.toJavaFunction())
        }

        if (config.travisCIKerlTransform != null) {
            HomePath.mergeTravisCIKerl(homePathByVersion, config.travisCIKerlTransform.toJavaFunction())
        }

        val nixTransform = config.nixTransform?.toJavaFunction() ?: Function.identity()
        HomePath.mergeNixStore(homePathByVersion, config.nixPattern, nixTransform)

        return homePathByVersion
    }

    /**
     * Determines which WSL distributions to scan based on the project path.
     * - If path is in a WSL distribution, only scan that distribution
     * - If path is Windows or null, scan all distributions
     */
    private fun homePathByVersionWSLs(
        path: Path?,
        homePathByVersion: MutableMap<Version, String>,
        config: Config
    ) {
        val distributionsToScan = when {
            path == null -> {
                LOG.debug("No project path, scanning all WSL distributions")
                wslCompat.getInstalledDistributions()
            }

            wslCompat.isWslUncPath(path.toString()) -> {
                val distribution = wslCompat.getDistributionByWindowsUncPath(path.toString())
                if (distribution != null) {
                    LOG.debug("Project in WSL (${distribution.msId}), scanning only that distribution")
                    listOf(distribution)
                } else {
                    LOG.debug("Couldn't determine WSL distribution, scanning all")
                    wslCompat.getInstalledDistributions()
                }
            }

            else -> {
                LOG.debug("Windows project path, scanning all WSL distributions")
                wslCompat.getInstalledDistributions()
            }
        }

        distributionsToScan.forEach { distribution ->
            homePathByVersionWSL(distribution, homePathByVersion, config)
        }
    }

    /**
     * Scans a single WSL distribution for SDK installations.
     * Mirrors the Linux scanning logic but converts paths to Windows UNC format.
     */
    private fun homePathByVersionWSL(
        distribution: WSLDistribution,
        homePathByVersion: MutableMap<Version, String>,
        config: Config
    ) {
        val wslUserHome = wslCompat.getWslUserHomeUncPath(distribution)

        if (wslUserHome != null) {
            HomePath.mergeASDF(homePathByVersion, config.toolName, wslUserHome)
            HomePath.mergeMise(homePathByVersion, config.toolName, wslUserHome)

            if (config.travisCIKerlTransform != null) {
                HomePath.mergeTravisCIKerl(homePathByVersion, config.travisCIKerlTransform.toJavaFunction(), wslUserHome)
            }
        }

        wslCompat.convertLinuxPathToWindowsUnc(distribution, config.linuxDefaultPath)?.let {
            putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, it)
        }
        wslCompat.convertLinuxPathToWindowsUnc(distribution, config.linuxMintPath)?.let {
            putIfDirectory(homePathByVersion, HomePath.UNKNOWN_VERSION, it)
        }

        wslCompat.convertLinuxPathToWindowsUnc(distribution, HomePath.NIX_STORE_PATH)?.let { wslNixStore ->
            val nixTransform = config.nixTransform?.toJavaFunction() ?: Function.identity()
            HomePath.mergeNixStore(homePathByVersion, config.nixPattern, nixTransform, wslNixStore)
        }
    }

    /**
     * Adds a home path to the map only if it exists as a directory.
     */
    private fun putIfDirectory(
        homePathByVersion: MutableMap<Version, String>,
        version: Version,
        homePath: String
    ) {
        val homeFile = File(homePath)
        if (homeFile.isDirectory) {
            homePathByVersion[version] = homePath
        }
    }

    /**
     * Converts a Kotlin lambda to a Java Function for interop with HomePath methods.
     */
    private fun ((File) -> File).toJavaFunction(): Function<File, File> = Function { file -> this(file) }
}
