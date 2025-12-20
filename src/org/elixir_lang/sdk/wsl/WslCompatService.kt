package org.elixir_lang.sdk.wsl

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.application.ApplicationManager

/**
 * Service wrapper for WSL (Windows Subsystem for Linux Integration).
 *
 * This service provides an abstraction layer over the IntelliJ Platform legacy WSL API
 * (com.intellij.execution.wsl), enabling:
 * 1. Detection of WSL paths (e.g., \\wsl$\Ubuntu\usr\bin\elixir)
 * 2. Command-line patching to execute commands via wsl.exe
 * 3. Testability through mocking in CI environments without WSL
 *
 * Implementation uses the Legacy WSL API to avoid the large refactor required by the Targets API.
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
     * Patches a command line to execute via WSL if the SDK home is in WSL.
     *
     * This method modifies the GeneralCommandLine to wrap the execution through wsl.exe,
     * translating Windows paths to Linux paths as needed.
     *
     * @param commandLine the IntelliJ Platform GeneralCommandLine to patch
     * @param sdkHome the SDK home path (used to determine WSL distribution)
     * @return true if the command line was patched for WSL execution, false if no patching was needed
     */
    fun patchCommandLine(commandLine: GeneralCommandLine, sdkHome: String?): Boolean

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
