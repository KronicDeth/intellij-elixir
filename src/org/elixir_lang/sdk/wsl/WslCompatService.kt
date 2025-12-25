package org.elixir_lang.sdk.wsl

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger

/**
 * Service wrapper for WSL (Windows Subsystem for Linux Integration).
 *
 * This service provides an abstraction layer over the IntelliJ Platform legacy WSL API
 * (com.intellij.execution.wsl), enabling:
 * 1. Detection of WSL paths (e.g., \\wsl$\Ubuntu\usr\bin\elixir)
 * 2. Path conversion for command-line arguments and environment variables
 * 3. Testability through mocking in CI environments without WSL
 *
 * ## Architecture
 *
 * WSL path conversion is automatically applied via the WslAwareCommandLine subclass.
 * All command lines constructed through the plugin's factory methods (Mix.commandLine(),
 * Elixir.commandLine(), IEx.commandLine()) use WslAwareCommandLine, which converts
 * paths right before process creation.
 *
 * This ensures:
 * - External tools (Credo, Dialyzer, Mix Format, New Project Wizard) get WSL support
 * - Run Configurations (Mix, IEx, ExUnit, ESpec, Elixir, Distillery) get WSL support
 * - Parameters added AFTER factory methods return are still converted
 * - Conversion happens once, at the last possible moment
 *
 * ## Usage Example
 *
 * ```kotlin
 * // Factory methods return WslAwareCommandLine instances:
 * val commandLine = Mix.commandLine(env, workDir, sdk)
 * commandLine.addParameters("--extra", "params")  // These will be converted too
 * commandLine.createProcess()  // Conversion happens here
 * ```
 *
 * Implementation uses the Legacy WSL API to avoid the large refactor required by the Targets API.
 *
 * @see org.elixir_lang.run.WslAwareCommandLine
 */
interface WslCompatService {
    /**
     * Logger for this service implementation.
     * Each implementation should override this to use its own class name for better log tracing.
     */
    val log: Logger
        get() = Logger.getInstance(WslCompatService::class.java)

    /**
     * Checks if the given path is a WSL path.
     *
     * @param path the path to check (e.g., \\wsl$\Ubuntu\usr\bin\elixir or /usr/bin/elixir from Windows)
     * @return true if the path points to a WSL location, false otherwise
     */
    fun isWslUncPath(path: String?): Boolean

    /**
     * Converts a single Windows path to WSL Linux format.
     * This is the platform-specific method that implementations must provide.
     *
     * @param windowsPath the Windows path to convert (either UNC path like \\wsl$\Ubuntu\path or drive path like C:\path)
     * @param distribution the WSL distribution to convert the path for
     * @return the converted Linux path, or null if conversion fails
     */
    fun convertSingleWslPath(windowsPath: String, distribution: WSLDistribution): String?

    /**
     * Converts WSL UNC paths and Windows drive paths embedded in process builder arguments and environment
     * variables to POSIX paths for WSL execution.
     *
     * When running commands in WSL, paths need to be converted so the WSL executable can understand them.
     * This method detects WSL context from the process builder's working directory and performs conversions.
     *
     * Examples of conversions in arguments and environment variables:
     * - `--path=\\wsl$\Ubuntu\home\user` → `--path=/home/user`
     * - `--map=\\wsl$\Ubuntu\home\user\dir1:\\wsl$\Ubuntu\home\user\dir2` → `--map=/home/user/dir1:/home/user/dir2`
     * - `\\wsl.localhost\Ubuntu\home\user\file.txt` → `/home/user/file.txt`
     * - `C:/Users/steve/file.txt` → `/mnt/c/Users/steve/file.txt`
     * - `D:\data\file.txt` → `/mnt/d/data/file.txt`
     *
     * @param processBuilder The process builder to convert (modified in place)
     */
    fun convertProcessBuilderArgumentsForWsl(processBuilder: ProcessBuilder, commandLine: GeneralCommandLine) {
        val distribution = determineDistribution(commandLine) ?: return

        // Modify ProcessBuilder commands in place
        val commands = processBuilder.command()
        processBuilder.command(commands.map { convertWslPathsInString(it, distribution) })

        // Modify ProcessBuilder environment in place
        val env = processBuilder.environment()
        env.replaceAll { _, value -> convertWslPathsInString(value, distribution) }
    }

    /**
     * Gets the WSL distribution for a given path.
     *
     * @param path the WSL path
     * @return the WSL distribution, or null if the path is not a WSL path or distribution cannot be determined
     */
    fun getDistributionByWindowsUncPath(path: String?): WSLDistribution?

    /**
     * Converts a Windows UNC path (e.g., //wsl.localhost/Ubuntu/usr/lib) to a Linux path (e.g., /usr/lib).
     *
     * @param windowsUncPath the Windows UNC path to convert
     * @return the Linux path, or null if the path cannot be converted
     */
    fun parseWindowsUncPath(windowsUncPath: String?): String?

    /**
     * Gets a list of available WSL distributions installed on the system.
     *
     * @return list of available WSL distributions, empty list if none found or WSL not available
     */
    fun getInstalledDistributions(): List<WSLDistribution>

    /**
     * Gets the user home directory for a WSL distribution.
     *
     * @param distribution the WSL distribution
     * @return the user home path (e.g., /home/username), or null if it cannot be determined
     */
    fun getWslUserHome(distribution: WSLDistribution): String?

    /**
     * Gets the WSL user home directory as a Windows UNC path.
     * This is useful for accessing WSL files from Windows using the `\\wsl.localhost\` format.
     *
     * @param distribution the WSL distribution
     * @return the Windows UNC path to the user home directory (e.g., `\\wsl.localhost\<distro>\<path>`),
     *         or null if it cannot be determined
     */
    fun getWslUserHomeUncPath(distribution: WSLDistribution): String? {
        val wslUserHome = getWslUserHome(distribution)
        return convertLinuxPathToWindowsUnc(distribution, wslUserHome)
    }

    /**
     * Converts a Linux path to a Windows UNC path for the given WSL distribution.
     * This method converts paths in the format /path/to/file to the Windows UNC format.
     *
     * @param distribution the WSL distribution
     * @param linuxPath the Linux path to convert (e.g., /home/username/.asdf)
     * @return the Windows UNC path (e.g., `\\wsl.localhost\<distro>\<linux-path>`),
     *         or null if linuxPath is null or empty
     */
    fun convertLinuxPathToWindowsUnc(distribution: WSLDistribution, linuxPath: String?): String?

    /**
     * Detects if the command line runs on WSL, caches the result, and returns the distribution.
     */
    private fun determineDistribution(commandLine: GeneralCommandLine): WSLDistribution? {
        val workDirectory = commandLine.workDirectory.toString()
        val workDirectoryDistribution = getDistributionByWindowsUncPath(workDirectory)

        if (workDirectoryDistribution == null) {
            log.debug("Cannot determine WSL distribution from workDirectory: $workDirectory")
            return null
        }

        val exePath = commandLine.exePath
        val exePathDistribution = getDistributionByWindowsUncPath(exePath)

        if (exePathDistribution == null) {
            log.debug("Skipping conversion: exePath is not WSL UNC: $exePath")
            return null
        }

        if (exePathDistribution.msId != workDirectoryDistribution.msId) {
            log.warn("Work directory distribution ($workDirectory = $workDirectoryDistribution) does not match exePath distribution ($exePath = $exePathDistribution)")
            return null
        }
        return workDirectoryDistribution
    }

    /**
     * Converts WSL UNC paths and Windows drive paths embedded in a string to POSIX paths.
     *
     * Examples of conversions:
     * - \\wsl$\distro\path or //wsl$/distro/path -> /path
     * - \\wsl.localhost\distro\path or //wsl.localhost/distro/path -> /path
     * - C:/path or C:\path -> /mnt/c/path
     *
     * Paths are matched greedily until invalid Windows path characters (< > : " | ? *) are encountered.
     * Uses convertSingleWslPath() for actual conversion to respect implementation-specific settings.
     */
    private fun convertWslPathsInString(input: String, distribution: WSLDistribution): String {
        var result = input

        // Convert Windows drive paths (C:/, D:\) to WSL mount paths (/mnt/c/, /mnt/d/)
        // Matches drive letter followed by colon and path, stopping at whitespace or invalid chars
        val drivePattern = Regex("""([A-Za-z]):([\\/][^\s<>:"|?*]*)""")
        val driveMatches = drivePattern.findAll(result).toList()

        // Process in reverse order to maintain string indices
        for (match in driveMatches.reversed()) {
            val windowsPath = match.value
            try {
                val wslPath = convertSingleWslPath(windowsPath, distribution)
                if (wslPath != null) {
                    result = result.substring(0, match.range.first) + wslPath + result.substring(match.range.last + 1)
                } else {
                    log.debug("convertSingleWslPath returned null for drive path: $windowsPath")
                }
            } catch (e: Exception) {
                log.debug("Failed to convert Windows drive path: $windowsPath", e)
            }
        }

        // Convert WSL UNC paths to POSIX paths
        // Matches \\wsl$ or \\wsl.localhost (with forward or backslashes)
        // followed by distro name and path, stopping at invalid Windows path characters
        val pattern = Regex("""([\\/]{2}wsl(?:\$|\.localhost)[\\/][^\\/]+[\\/][^<>:"|?*]+)""", RegexOption.IGNORE_CASE)
        val matches = pattern.findAll(result).toList()

        // Process in reverse order to maintain string indices
        for (match in matches.reversed()) {
            val uncPath = match.groupValues[1]

            // Only convert paths from the same distribution
            val pathDistribution = getDistributionByWindowsUncPath(uncPath)
            if (pathDistribution?.msId != distribution.msId) {
                continue
            }

            try {
                val posixPath = convertSingleWslPath(uncPath, distribution)
                if (posixPath != null) {
                    result = result.substring(0, match.range.first) + posixPath + result.substring(match.range.last + 1)
                } else {
                    log.warn("convertSingleWslPath returned null for WSL UNC path: $uncPath")
                }
            } catch (e: Exception) {
                log.warn("Failed to convert WSL UNC path: $uncPath", e)
            }
        }

        return result
    }

    companion object {
        /**
         * Gets the service instance.
         *
         * @return the WslCompatService instance
         */
        @JvmStatic
        fun getInstance(): WslCompatService {
            return ApplicationManager.getApplication().getService(WslCompatService::class.java)
        }
    }
}

/**
 * Extension property for convenient access to WslCompatService.
 *
 * Usage: `wslCompat.maybeConvertPathForWsl(path, sdkHome)`
 *
 * Tests can still mock the service using IntelliJ's standard service replacement:
 * `ApplicationManager.getApplication().replaceService(WslCompatService::class.java, mockService, testRootDisposable)`
 */
val wslCompat: WslCompatService
    get() = ApplicationManager.getApplication().getService(WslCompatService::class.java)
