package org.elixir_lang.sdk.wsl

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.system.OS
import org.jetbrains.annotations.TestOnly
import org.mockito.Mockito

/**
 * Mock implementation of WslCompatService for testing purposes.
 *
 * This class demonstrates that the Service Wrapper pattern enables testing
 * without requiring WSL installation. In production, the real WslCompatServiceImpl
 * is used, while in tests this mock can be injected.
 */
@TestOnly
class MockWslCompatService(
    private val distributionOverride: ((String?) -> WSLDistribution?)? = null,
    private val conversionOverride: ((WSLDistribution, String?) -> String?)? = null,
    private val prefixConversionOverride: Pair<String, String>? = LEGACY_WSL_PREFIX to MODERN_WSL_PREFIX,
) : WslCompatService {
    override val log = Logger.getInstance(MockWslCompatService::class.java)

    override fun isWslUncPath(path: String?): Boolean {
        if (path.isNullOrEmpty()) {
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
        distributionOverride?.let { return it(path) }

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

        return Mockito.mock(WSLDistribution::class.java).also {
            Mockito.`when`(it.msId).thenReturn(distroName)
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

    override fun convertLinuxPathToWindowsUnc(distribution: WSLDistribution, linuxPath: String?): String? {
        conversionOverride?.let { return it(distribution, linuxPath) }

        if (linuxPath.isNullOrEmpty()) {
            return null
        }

        // Mock implementation returns a well-formed Windows UNC path
        val linuxAsWindows = linuxPath.replace('/', '\\')
        val converted = "\\\\wsl.localhost\\${distribution.msId}$linuxAsWindows"
        return try {
            canonicalizePath(converted)
        } catch (_: Exception) {
            converted
        }
    }

    override fun convertSingleWslPath(windowsPath: String, distribution: WSLDistribution): String? {
        // Mock implementation uses simple string manipulation
        // For UNC paths, use parseWindowsUncPath
        if (isWslUncPath(windowsPath)) {
            return parseWindowsUncPath(windowsPath)
        }

        // For drive paths (C:/, D:\), convert to /mnt/c/, /mnt/d/
        val drivePattern = Regex("""([A-Za-z]):([\\/].*)""")
        val match = drivePattern.matchEntire(windowsPath)
        if (match != null) {
            val driveLetter = match.groupValues[1].lowercase()
            val pathPart = match.groupValues[2].replace('\\', '/')
            return "/mnt/$driveLetter$pathPart"
        }

        return null
    }

    /**
     * Returns [prefixConversionOverride] (default: legacy→modern) instead of consulting the host OS,
     * so tests are deterministic across the CI matrix (Windows 11, older Windows, and Linux runners).
     * Only the OS-version policy is stubbed; the real prefix rewrite in [canonicalizeWslPrefix] still runs.
     * Pass `prefixConversionOverride = null` to simulate the "no conversion" (non-Windows) case.
     */
    override fun wslPrefixConversion(currentOs: OS): Pair<String, String>? = prefixConversionOverride
}
