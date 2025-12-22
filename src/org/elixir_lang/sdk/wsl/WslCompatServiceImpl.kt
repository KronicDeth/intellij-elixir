package org.elixir_lang.sdk.wsl

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLDistribution
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.execution.wsl.WslPath
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager

/**
 * Default implementation of WslCompatService using the IntelliJ Platform WSL API.
 * Delegates to native IntelliJ APIs where possible to avoid redundancy.
 */
class WslCompatServiceImpl : WslCompatService {
    override fun isWslUncPath(path: String?): Boolean {
        if (path.isNullOrEmpty()) {
            return false
        }

        return try {
            // Delegate to native IntelliJ API
            WslPath.isWslUncPath(path)
        } catch (e: Exception) {
            LOG.debug("Error checking if path is WSL: $path", e)
            false
        }
    }

    override fun getDistributionByWindowsUncPath(path: String?): WSLDistribution? {
        if (path.isNullOrEmpty() || ! isWslUncPath(path)) {
            return null
        }

        return try {
            // Delegate to native IntelliJ API
            WslPath.getDistributionByWindowsUncPath(path)
        } catch (e: Exception) {
            LOG.debug("Error getting WSL distribution for path: $path", e)
            null
        }
    }

    override fun parseWindowsUncPath(windowsUncPath: String?): String? {
        if (windowsUncPath.isNullOrEmpty()) {
            return null
        }

        return try {
            // Delegate to native IntelliJ API
            val wslPath = WslPath.parseWindowsUncPath(windowsUncPath)
            wslPath?.linuxPath
        } catch (e: Exception) {
            LOG.debug("Error converting Windows UNC path to Linux path: $windowsUncPath", e)
            null
        }
    }

    override fun getInstalledDistributions(): List<WSLDistribution> {
        return try {
            WslDistributionManager.getInstance().installedDistributions
        } catch (e: Exception) {
            LOG.debug("Error getting WSL distributions", e)
            emptyList()
        }
    }

    override fun getWslUserHome(distribution: WSLDistribution): String? {
        return try {
            // getUserHome() requires a progress context in newer IntelliJ versions
            // Wrap the call with ProgressManager to provide the required context
            ProgressManager.getInstance().runProcessWithProgressSynchronously<String?, Exception>(
                { distribution.userHome },
                "Getting WSL User Home",
                false,
                null
            )
        } catch (e: Exception) {
            LOG.debug("Error getting WSL user home for distribution: ${distribution.msId}", e)
            null
        }
    }

    override fun getWslUserHomeUncPath(distribution: WSLDistribution): String? {
        val wslUserHome = getWslUserHome(distribution)
        return convertLinuxPathToWindowsUnc(distribution, wslUserHome)
    }

    override fun convertLinuxPathToWindowsUnc(distribution: WSLDistribution, linuxPath: String?): String? {
        if (linuxPath.isNullOrEmpty()) {
            return null
        }

        return try {
            // Delegate to native IntelliJ API
            val wslPath = WslPath(distribution.msId, linuxPath)
            wslPath.toWindowsUncPath()
        } catch (e: Exception) {
            LOG.debug("Error converting Linux path to Windows UNC: $linuxPath", e)
            null
        }
    }

    override fun maybeConvertPathForWsl(path: String, sdkHomePath: String?): String {
        if (sdkHomePath == null) return path

        return if (isWslUncPath(sdkHomePath)) {
            parseWindowsUncPath(path) ?: path
        } else {
            path
        }
    }

    override fun convertCommandLineArgumentsForWsl(commandLine: GeneralCommandLine) {
        val distribution = determineDistribution(commandLine) ?: return

        LOG.debug("Converting paths for WSL distribution: ${distribution.msId}")

        commandLine.withExePath(convertWslPathsInString(commandLine.exePath, distribution))

        // Convert command line parameters
        val originalParams = commandLine.parametersList.list
        val convertedParams = originalParams.map { param ->
            convertWslPathsInString(param, distribution)
        }
        commandLine.parametersList.clearAll()
        commandLine.parametersList.addAll(convertedParams)

        // Convert environment variables
        val convertedEnv = commandLine.environment.mapValues { (_, value) ->
            convertWslPathsInString(value, distribution)
        }
        commandLine.environment.clear()
        commandLine.environment.putAll(convertedEnv)

        LOG.debug("Converted command line for WSL: ${commandLine.commandLineString}")
    }

    /**
     * Determines the WSL distribution for a command line.
     * Returns null if the command line does not run on WSL.
     * Uses cached WSL detection result to avoid redundant checks on subsequent calls.
     */
    private fun determineDistribution(commandLine: GeneralCommandLine): WSLDistribution? {
        val runsOnWsl = commandLine.getUserData(RUNS_ON_WSL)

        return when (runsOnWsl) {
            false -> {
                LOG.debug("Skipping conversion: command line does not run on WSL (cached)")
                null
            }
            true -> getDistributionFromWorkDirectory(commandLine)
            null -> detectWslAndGetDistribution(commandLine)
        }
    }

    /**
     * Gets the distribution from the command line's working directory.
     * Assumes WSL detection has already determined this runs on WSL.
     */
    private fun getDistributionFromWorkDirectory(commandLine: GeneralCommandLine): WSLDistribution? {
        val workDirectory = commandLine.workDirectory.toString()
        return getDistributionByWindowsUncPath(workDirectory) ?: run {
            LOG.warn("RUNS_ON_WSL is true but cannot determine distribution from workDirectory: $workDirectory")
            null
        }
    }

    /**
     * Detects if the command line runs on WSL, caches the result, and returns the distribution.
     */
    private fun detectWslAndGetDistribution(commandLine: GeneralCommandLine): WSLDistribution? {
        val workDirectory = commandLine.workDirectory.toString()
        val workDirectoryDistribution = getDistributionByWindowsUncPath(workDirectory)

        if (workDirectoryDistribution == null) {
            LOG.debug("Cannot determine WSL distribution from workDirectory: $workDirectory")
            commandLine.putUserData(RUNS_ON_WSL, false)
            return null
        }

        val exePath = commandLine.exePath
        val exePathDistribution = getDistributionByWindowsUncPath(exePath)

        if (exePathDistribution == null) {
            LOG.debug("Skipping conversion: exePath is not WSL UNC: $exePath")
            commandLine.putUserData(RUNS_ON_WSL, false)
            return null
        }

        if (exePathDistribution != workDirectoryDistribution) {
            LOG.warn("Work directory distribution ($workDirectory = $workDirectoryDistribution) does not match exePath distribution ($exePath = $exePathDistribution)")
            commandLine.putUserData(RUNS_ON_WSL, false)
            return null
        }

        commandLine.putUserData(RUNS_ON_WSL, true)
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
     * Uses WSLDistribution.getWslPath() for actual conversion to respect distribution-specific settings.
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
                // Normalize to backslashes for Path.of() on Windows
                val normalizedPath = windowsPath.replace('/', '\\')
                val wslPath = distribution.getWslPath(java.nio.file.Path.of(normalizedPath))
                if (wslPath != null) {
                    result = result.substring(0, match.range.first) + wslPath + result.substring(match.range.last + 1)
                } else {
                    LOG.debug("getWslPath returned null for drive path: $windowsPath")
                }
            } catch (e: Exception) {
                LOG.debug("Failed to convert Windows drive path using getWslPath: $windowsPath", e)
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
                // Normalize to backslashes for Path.of() on Windows
                val normalizedPath = uncPath.replace('/', '\\')
                val posixPath = distribution.getWslPath(java.nio.file.Path.of(normalizedPath))
                if (posixPath != null) {
                    result = result.substring(0, match.range.first) + posixPath + result.substring(match.range.last + 1)
                } else {
                    LOG.warn("getWslPath returned null for WSL UNC path: $uncPath")
                }
            } catch (e: Exception) {
                LOG.warn("Failed to convert WSL UNC path using getWslPath: $uncPath", e)
            }
        }

        return result
    }

    companion object {
        private val LOG = Logger.getInstance(WslCompatServiceImpl::class.java)
    }
}
