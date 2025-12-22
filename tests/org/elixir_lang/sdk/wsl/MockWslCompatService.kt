package org.elixir_lang.sdk.wsl

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLDistribution

/**
 * Mock implementation of WslCompatService for testing purposes.
 *
 * This class demonstrates that the Service Wrapper pattern enables testing
 * without requiring WSL installation. In production, the real WslCompatServiceImpl
 * is used, while in tests this mock can be injected.
 */
class MockWslCompatService : WslCompatService {

    override fun isWslUncPath(path: String?): Boolean {
        if (path == null) {
            return false
        }

        // In mock mode, check for WSL-like patterns (both old and new formats)
        // Old format: \\wsl$\Ubuntu\... or //wsl$/Ubuntu/...
        // New format: \\wsl.localhost\Ubuntu-24.04\... or //wsl.localhost/Ubuntu-24.04/...
        // Case-insensitive matching
        return path.startsWith("\\\\wsl$\\", ignoreCase = true) ||
               path.startsWith("//wsl$/", ignoreCase = true) ||
               path.startsWith("\\\\wsl.localhost\\", ignoreCase = true) ||
               path.startsWith("//wsl.localhost/", ignoreCase = true)
    }

    override fun getDistributionByWindowsUncPath(path: String?): WSLDistribution? {
        if (!isWslUncPath(path)) {
            return null
        }

        // Return a mock distribution for testing
        // Extract distribution name from path if possible
        val distroName = when {
            path?.contains("Ubuntu-24.04") == true -> "Ubuntu-24.04"
            path?.contains("Ubuntu") == true -> "Ubuntu"
            else -> "WSL"
        }

        return org.mockito.Mockito.mock(WSLDistribution::class.java).also {
            org.mockito.Mockito.`when`(it.msId).thenReturn(distroName)
        }
    }

    override fun parseWindowsUncPath(windowsUncPath: String?): String? {
        if (windowsUncPath == null) {
            return null
        }

        if (!isWslUncPath(windowsUncPath)) {
            return null
        }

        // Simple mock conversion: strip WSL prefix and convert to Linux-style path
        var result = windowsUncPath

        // Normalize to forward slashes first
        result = result.replace('\\', '/')

        // Handle //wsl$/Ubuntu/... format
        if (result.startsWith("//wsl$/", ignoreCase = true)) {
            // Find the second slash after //wsl$/
            val distroStart = 7 // Length of "//wsl$/"
            val pathStart = result.indexOf('/', distroStart)
            if (pathStart > distroStart) {
                result = result.substring(pathStart)
            } else {
                result = "/"
            }
        }
        // Handle //wsl.localhost/Ubuntu/... format
        else if (result.startsWith("//wsl.localhost/", ignoreCase = true)) {
            // Find the second slash after //wsl.localhost/
            val distroStart = 16 // Length of "//wsl.localhost/"
            val pathStart = result.indexOf('/', distroStart)
            if (pathStart > distroStart) {
                result = result.substring(pathStart)
            } else {
                result = "/"
            }
        }

        return if (result.isEmpty()) "/" else result
    }

    override fun getInstalledDistributions(): List<WSLDistribution> {
        // Mock service returns empty list (no WSL in test environment)
        return emptyList()
    }

    override fun getWslUserHome(distribution: WSLDistribution): String {
        // Mock implementation returns a typical Linux home path
        return "/home/testuser"
    }

    override fun getWslUserHomeUncPath(distribution: WSLDistribution): String? {
        val wslUserHome = getWslUserHome(distribution)
        return convertLinuxPathToWindowsUnc(distribution, wslUserHome)
    }

    override fun convertLinuxPathToWindowsUnc(distribution: WSLDistribution, linuxPath: String?): String? {
        if (linuxPath.isNullOrEmpty()) {
            return null
        }

        // Mock implementation returns a Windows UNC path
        return "\\\\wsl.localhost\\${distribution.msId}$linuxPath"
    }

    override fun maybeConvertPathForWsl(path: String, sdkHomePath: String?): String {
        return path
    }

    override fun convertCommandLineArgumentsForWsl(commandLine: GeneralCommandLine) {
        val distribution = determineWslDistribution(commandLine) ?: return

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
    }

    private fun determineWslDistribution(commandLine: GeneralCommandLine): WSLDistribution? {
        val runsOnWsl = commandLine.getUserData(RUNS_ON_WSL)

        return when (runsOnWsl) {
            false -> null
            true -> getDistributionFromWorkDirectory(commandLine)
            null -> detectWslAndGetDistribution(commandLine)
        }
    }

    private fun getDistributionFromWorkDirectory(commandLine: GeneralCommandLine): WSLDistribution? {
        val workDirectory = commandLine.workDirectory?.toString()
        return getDistributionByWindowsUncPath(workDirectory)
    }

    private fun detectWslAndGetDistribution(commandLine: GeneralCommandLine): WSLDistribution? {
        val workDirectory = commandLine.workDirectory?.toString()
        val workDirectoryDistribution = getDistributionByWindowsUncPath(workDirectory)

        if (workDirectoryDistribution == null) {
            commandLine.putUserData(RUNS_ON_WSL, false)
            return null
        }

        val exePath = commandLine.exePath
        val exePathDistribution = getDistributionByWindowsUncPath(exePath)

        if (exePathDistribution == null) {
            commandLine.putUserData(RUNS_ON_WSL, false)
            return null
        }

        if (exePathDistribution.msId != workDirectoryDistribution.msId) {
            commandLine.putUserData(RUNS_ON_WSL, false)
            return null
        }

        commandLine.putUserData(RUNS_ON_WSL, true)
        return workDirectoryDistribution
    }

    private fun convertWslPathsInString(input: String, distribution: WSLDistribution): String {
        var result = input

        // Convert Windows drive paths (C:/, D:\) to WSL mount paths (/mnt/c/, /mnt/d/)
        // Matches drive letter followed by colon and path, stopping at whitespace or invalid chars
        val drivePattern = Regex("""([A-Za-z]):([\\/][^\s<>:"|?*]*)""")
        val driveMatches = drivePattern.findAll(result).toList()

        // Process in reverse order to maintain string indices
        for (match in driveMatches.reversed()) {
            val driveLetter = match.groupValues[1].lowercase()
            val pathPart = match.groupValues[2].replace('\\', '/')
            val wslPath = "/mnt/$driveLetter$pathPart"
            result = result.substring(0, match.range.first) + wslPath + result.substring(match.range.last + 1)
        }

        // Convert WSL UNC paths to POSIX paths
        // Regex pattern to match WSL UNC paths
        val pattern = Regex("""([\\/]{2}wsl(?:\$|\.localhost)[\\/][^\\/]+[\\/][^<>:"|?*]+)""", RegexOption.IGNORE_CASE)
        val matches = pattern.findAll(result).toList()

        // Process matches in reverse order to maintain correct string indices
        for (match in matches.reversed()) {
            val uncPath = match.groupValues[1]

            // Verify the distribution matches
            val pathDistribution = getDistributionByWindowsUncPath(uncPath)
            if (pathDistribution?.msId != distribution.msId) {
                continue
            }

            // Convert the UNC path to POSIX
            val posixPath = parseWindowsUncPath(uncPath)
            if (posixPath != null) {
                result = result.substring(0, match.range.first) + posixPath + result.substring(match.range.last + 1)
            }
        }

        return result
    }
}
