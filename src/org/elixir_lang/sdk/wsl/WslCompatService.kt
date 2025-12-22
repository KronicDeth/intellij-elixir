package org.elixir_lang.sdk.wsl

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Key

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
     * Checks if the given path is a WSL path.
     *
     * @param path the path to check (e.g., \\wsl$\Ubuntu\usr\bin\elixir or /usr/bin/elixir from Windows)
     * @return true if the path points to a WSL location, false otherwise
     */
    fun isWslUncPath(path: String?): Boolean

    /**
     * Converts WSL UNC paths and Windows drive paths embedded in command line arguments and environment
     * variables to POSIX paths for WSL execution.
     *
     * When running commands in WSL, paths need to be converted so the WSL executable can understand them.
     * This method detects WSL context from the command line's working directory and performs conversions.
     *
     * Examples of conversions in arguments and environment variables:
     * - `--path=\\wsl$\Ubuntu\home\user` → `--path=/home/user`
     * - `--map=\\wsl$\Ubuntu\home\user\dir1:\\wsl$\Ubuntu\home\user\dir2` → `--map=/home/user/dir1:/home/user/dir2`
     * - `\\wsl.localhost\Ubuntu\home\user\file.txt` → `/home/user/file.txt`
     * - `C:/Users/steve/file.txt` → `/mnt/c/Users/steve/file.txt`
     * - `D:\data\file.txt` → `/mnt/d/data/file.txt`
     *
     * @param commandLine The command line to convert (modified in place)
     */
    fun convertCommandLineArgumentsForWsl(commandLine: GeneralCommandLine)

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
    fun getWslUserHomeUncPath(distribution: WSLDistribution): String?

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
     * Converts a path to WSL Linux format if the SDK context indicates WSL usage.
     * This is needed because WSL executables expect Linux-style paths, not Windows UNC paths.
     *
     * @param path The path to convert (e.g., executable path, directory path)
     * @param sdkHomePath The SDK home path that determines whether we're in a WSL context
     * @return The converted Linux path if sdkHomePath indicates WSL usage, otherwise the original path
     */
    fun maybeConvertPathForWsl(path: String, sdkHomePath: String?): String

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

/**
 * Key used to track whether a GeneralCommandLine runs on WSL.
 * - null: Not yet determined (will check workDir and exePath)
 * - true: Runs on WSL (apply conversion)
 * - false: Does not run on WSL (skip conversion)
 *
 * This allows nested calls to wslCompatCommandLine to work correctly by
 * avoiding redundant WSL detection checks.
 */
val RUNS_ON_WSL = Key.create<Boolean>("RUNS_ON_WSL")
