package org.elixir_lang.sdk.wsl

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLCommandLineOptions
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
            wslPath?.linuxPath?.also {
                LOG.debug("parseWindowsUncPath: $windowsUncPath -> $it")
            }
        } catch (e: Exception) {
            LOG.debug("Error converting Windows UNC path to Linux path: $windowsUncPath", e)
            null
        }
    }

    override fun patchCommandLine(commandLine: GeneralCommandLine, sdkHome: String?): Boolean {
        if (sdkHome == null || !isWslUncPath(sdkHome)) {
            return false
        }

        val distribution = getDistributionByWindowsUncPath(sdkHome)
        if (distribution == null) {
            LOG.warn(
                "WSL distribution not found for SDK home: $sdkHome. " +
                        "Please ensure WSL is installed and the distribution is accessible. " +
                        "Try running 'wsl --list --verbose' to see available distributions."
            )
            return false
        }

        return try {
            // Convert the executable path to Linux path if it's a WSL path
            val exePath = commandLine.exePath

            if (isWslUncPath(exePath)) {
                commandLine.exePath = WslPath.parseWindowsUncPath(exePath)?.linuxPath ?: exePath

                commandLine.workingDirectory?.let { workingPath ->
                    val wslAbsoluteWorkingPath = WslPath.parseWindowsUncPath(workingPath.toAbsolutePath().toString())
                    wslAbsoluteWorkingPath?.let {
                        commandLine.setWorkDirectory(it.linuxPath)
                    }
                }

                val originalParams = commandLine.parametersList.list
                val parametersAsLinux = originalParams.map { param ->
                    if (WslPath.isWslUncPath(param)) {
                        WslPath.parseWindowsUncPath(param)?.linuxPath ?: param
                    } else {
                        param
                    }
                }
                commandLine.parametersList.clearAll()
                commandLine.withParameters(parametersAsLinux)
            }
            LOG.warn("*** WSL COMMAND: ${commandLine.commandLineString}")

            // Patch the command line to execute via WSL
            // WSLDistribution.patchCommandLine() handles:
            // - Converting Windows UNC paths (\\wsl.localhost\...) to Linux paths
            // - Wrapping the command with wsl.exe -d <distro> -e <command> <args...>
            // - Converting path arguments to Linux format
            val options = WSLCommandLineOptions()
            distribution.patchCommandLine(commandLine, null, options)

            LOG.info("Patched command line for WSL execution via ${distribution.msId}")
            true
        } catch (e: Exception) {
            LOG.error("Failed to patch command line for WSL execution", e)
            false
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

    companion object {
        private val LOG = Logger.getInstance(WslCompatServiceImpl::class.java)
    }
}
