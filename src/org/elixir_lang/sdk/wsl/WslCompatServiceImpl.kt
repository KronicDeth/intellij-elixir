package org.elixir_lang.sdk.wsl

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.execution.wsl.WslPath
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.progress.ProgressManager

/**
 * Default implementation of WslCompatService using the IntelliJ Platform WSL API.
 * Delegates to native IntelliJ APIs where possible to avoid redundancy.
 */
class WslCompatServiceImpl : WslCompatService {
    override val logger = Logger.getInstance(WslCompatServiceImpl::class.java)

    override fun isWslUncPath(path: String?): Boolean {
        if (path.isNullOrEmpty()) {
            return false
        }

        return try {
            // Delegate to native IntelliJ API
            WslPath.isWslUncPath(path)
        } catch (e: Exception) {
            logger.debug(e) { "Error checking if path is WSL: $path" }
            false
        }
    }

    override fun getDistributionByWindowsUncPath(path: String?): WSLDistribution? {
        if (path.isNullOrEmpty() || !isWslUncPath(path)) {
            return null
        }

        return try {
            // Delegate to native IntelliJ API
            WslPath.getDistributionByWindowsUncPath(path)
        } catch (e: Exception) {
            logger.debug(e) { "Error getting WSL distribution for path: $path" }
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
            logger.debug(e) { "Error converting Windows UNC path to Linux path: $windowsUncPath" }
            null
        }
    }

    override fun getInstalledDistributions(): List<WSLDistribution> {
        return try {
            WslDistributionManager.getInstance().installedDistributions
        } catch (e: Exception) {
            logger.debug(e) { "Error getting WSL distributions" }
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
            logger.debug(e) { "Error getting WSL user home for distribution: ${distribution.msId}" }
            null
        }
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
            logger.debug(e) { "Error converting Linux path to Windows UNC: $linuxPath" }
            null
        }
    }

    override fun convertSingleWslPath(windowsPath: String, distribution: WSLDistribution): String? {
        return try {
            // Normalize to backslashes for Path.of() on Windows
            val normalizedPath = windowsPath.replace('/', '\\')
            distribution.getWslPath(java.nio.file.Path.of(normalizedPath))
        } catch (e: Exception) {
            logger.debug(e) { "Failed to convert path using getWslPath: $windowsPath" }
            null
        }
    }
}
