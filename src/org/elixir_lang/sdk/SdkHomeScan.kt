package org.elixir_lang.sdk

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.system.CpuArch
import com.intellij.util.system.OS
import org.elixir_lang.jps.shared.sdk.SdkPaths
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.File
import java.nio.file.Path
import java.util.*


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
     * @property homebrewTransform Path transform for Homebrew (null = identity)
     * @property nixTransform Path transform for Nix Store (null = identity)
     * @property windowsDefaultPath Primary Windows install path (null if no default)
     * @property windows32BitPath 32-bit Windows install path (null if same as default)
     * @property kerlTransform Path transform for kerl (null = skip kerl scanning)
     * @property travisCIKerlTransform Path transform for Travis CI kerl (null = skip)
     */
    data class Config(
        val toolName: String,
        val nixPattern: java.util.regex.Pattern,
        val windowsDefaultPath: String?,
        val windows32BitPath: String? = null,
        val elixirInstallScriptDirName: String,

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
    fun homePathByVersion(path: Path?, config: Config): Map<SdkHomeKey, String> {
        LOG.debug("Scanning for ${config.toolName} SDKs (path: $path, platform: ${OS.CURRENT})")
        val homePathByVersion: MutableMap<SdkHomeKey, String> = TreeMap()
        // A project inside a distribution can only use that distribution's SDKs, and conversely a
        // host project is not offered a distribution's: enumerating distributions deploys an ijent
        // agent and boots stopped ones, which is too costly to do for every scan.
        if (path != null && wslCompat.isWslUncPath(path.toString())) {
            homePathByVersionWSLs(path, homePathByVersion, config)
        } else {
            mergePosixSources(homePathByVersion, config, System.getProperty("user.home")) { it }
            mergeWindowsInstallerPath(homePathByVersion, config)

            // The only source located by running a command rather than by looking at a path, so it
            // is the only one worth skipping when its transform says the tool does not use it.
            config.kerlTransform?.let { SdkHomePaths.mergeKerl(homePathByVersion, it) }
        }

        LOG.debug(
            "Found ${homePathByVersion.size} ${config.toolName} SDK(s): " +
                homePathByVersion.values.take(3).joinToString(", ") +
                if (homePathByVersion.size > 3) "..." else ""
        )

        return homePathByVersion
    }

    /**
     * The installer path that only means anything with a drive letter, so it has no POSIX
     * equivalent. Scanned everywhere regardless: off Windows it names a directory that cannot exist.
     */
    private fun mergeWindowsInstallerPath(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        config: Config
    ) {
        val windowsPath = if (CpuArch.CURRENT.width == 32) {
            config.windows32BitPath ?: config.windowsDefaultPath
        } else {
            config.windowsDefaultPath
        }

        windowsPath?.let { putIfDirectory(homePathByVersion, SdkHomePaths.unknownVersionKey(it), it) }
    }

    /**
     * Every source installed on a POSIX filesystem, for one reached via [toLocalPath] and owning
     * [userHome].
     *
     * Running on Linux passes identity and this JVM's home; scanning a WSL distribution passes its
     * UNC mapping and the distribution's own home. Adding a source here reaches both, which is the
     * point - the WSL scan used to restate the Linux one by hand and could silently fall behind it.
     *
     * [toLocalPath] returns null for a path this machine cannot reach, and [userHome] is null when
     * it cannot be determined; both mean "skip that source" rather than "scan nothing".
     */
    private fun mergePosixSources(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        config: Config,
        userHome: String?,
        toLocalPath: (String) -> String?,
    ) {
        SdkHomePaths.mergeLinuxSystemHomePaths(homePathByVersion, config.toolName, toLocalPath)

        if (userHome != null) {
            SdkHomePaths.mergeASDF(homePathByVersion, config.toolName, userHome)
            SdkHomePaths.mergeMise(homePathByVersion, config.toolName, userHome)
            SdkHomePaths.mergeElixirInstallScript(
                homePathByVersion, config.elixirInstallScriptDirName, userHome
            )
            config.travisCIKerlTransform?.let {
                SdkHomePaths.mergeTravisCIKerl(homePathByVersion, it, userHome)
            }
        }

        SdkHomePaths.mergeHomebrew(
            homePathByVersion, config.toolName, config.homebrewTransform ?: { it },
            SdkHomePaths.homebrewCellarPaths(userHome, toLocalPath)
        )

        toLocalPath(SdkPaths.NIX_STORE_PATH)?.let {
            SdkHomePaths.mergeNixStore(
                homePathByVersion, config.nixPattern, config.nixTransform ?: { it }, it
            )
        }
    }

    /**
     * Scans the distribution the project lives in, which is the only caller's case - a host project
     * is never offered a distribution's SDKs, so there is no "scan them all" path here.
     */
    private fun homePathByVersionWSLs(
        path: Path,
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        config: Config
    ) {
        val distribution = wslCompat.getDistributionByWindowsUncPath(path.toString())

        val distributionsToScan = if (distribution != null) {
            LOG.debug("Project in WSL (${distribution.msId}), scanning only that distribution")
            listOf(distribution)
        } else {
            LOG.debug("Couldn't determine WSL distribution from $path, scanning all")
            wslCompat.getInstalledDistributions()
        }

        distributionsToScan.forEach { homePathByVersionWSL(it, homePathByVersion, config) }
    }

    private fun homePathByVersionWSL(
        distribution: WSLDistribution,
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        config: Config
    ) {
        mergePosixSources(
            homePathByVersion, config, wslCompat.getWslUserHomeUncPath(distribution)
        ) { wslCompat.convertLinuxPathToWindowsUnc(distribution, it) }
    }

    /**
     * Adds a home path to the map only if it exists as a directory.
     */
    private fun putIfDirectory(
        homePathByVersion: MutableMap<SdkHomeKey, String>,
        key: SdkHomeKey,
        homePath: String
    ) {
        val homeFile = File(homePath)
        if (homeFile.isDirectory) {
            homePathByVersion[key] = homePath
        }
    }

}
