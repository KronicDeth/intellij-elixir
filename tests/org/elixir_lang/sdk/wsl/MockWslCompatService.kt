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
        return path.startsWith("\\\\wsl$\\") ||
               path.startsWith("//wsl$/") ||
               path.startsWith("\\\\wsl.localhost\\") ||
               path.startsWith("//wsl.localhost/")
    }

    override fun patchCommandLine(commandLine: GeneralCommandLine, sdkHome: String?): Boolean {
        // Mock implementation always succeeds if it's a WSL path
        return isWslUncPath(sdkHome)
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

        // Handle //wsl$/Ubuntu/... or \\wsl$\Ubuntu\...
        if (result.startsWith("//wsl$/") || result.startsWith("\\\\wsl$\\")) {
            result = result.substring(result.indexOf('/', 8)) // Skip past //wsl$/Ubuntu
        }

        // Handle //wsl.localhost/Ubuntu/... or \\wsl.localhost\Ubuntu\...
        if (result.startsWith("//wsl.localhost/") || result.startsWith("\\\\wsl.localhost\\")) {
            result = result.substring(result.indexOf('/', 16)) // Skip past //wsl.localhost/Ubuntu
        }

        // Normalize to forward slashes
        result = result.replace('\\', '/')

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
}
