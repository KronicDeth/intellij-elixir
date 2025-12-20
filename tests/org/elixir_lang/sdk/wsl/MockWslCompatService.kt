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

        // In mock mode, check for WSL-like patterns
        return path.startsWith("\\\\wsl$\\") || path.startsWith("//wsl$/")
    }

    override fun patchCommandLine(commandLine: GeneralCommandLine, sdkHome: String?): Boolean {
        // Mock implementation always succeeds if it's a WSL path
        return isWslUncPath(sdkHome)
    }

    override fun getDistributionByWindowsUncPath(path: String?): WSLDistribution? {
        if (!isWslUncPath(path)) {
            return null
        }

        // In mock mode, we don't return a real distribution
        // Tests should mock this separately if needed
        return null
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
