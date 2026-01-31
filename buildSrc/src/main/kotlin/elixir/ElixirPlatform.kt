package elixir

import platform.Platform
import platform.posix.PosixElixirPlatform
import platform.windows.WindowsElixirPlatform
import org.gradle.api.logging.Logger
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Platform-specific operations for managing Elixir installation and build.
 * Only getExecutable() needs platform-specific implementation.
 * All other methods have shared default implementations.
 */
interface ElixirPlatform {

    /**
     * Returns the platform name for logging.
     * @return Platform name (e.g., "POSIX", "Windows")
     */
    fun getPlatformName(): String

    /**
     * Returns the platform-specific executable name for an Elixir command.
     * This is the ONLY method that requires platform-specific implementation.
     * @param name Base command name (e.g., "mix", "elixir", "iex")
     * @param binPath Directory containing Elixir executables
     * @return Full path to the platform-specific executable
     */
    fun getExecutable(name: String, binPath: File): String

    /**
     * Builds Elixir from source in the specified directory.
     * Default implementation works for all platforms using standard make.
     * @param execOps Gradle exec operations for running commands
     * @param workingDir The Elixir source directory
     * @param logger Logger for output
     * @throws RuntimeException if build fails
     */
    fun build(execOps: ExecOperations, workingDir: File, logger: Logger) {
        logger.lifecycle("Building Elixir using 'make' (${getPlatformName()})...")

        val output = ByteArrayOutputStream()
        val result = execOps.exec {
            commandLine("make")
            workingDir(workingDir)
            standardOutput = output
            errorOutput = output
            isIgnoreExitValue = true
        }

        if (result.exitValue != 0) {
            logger.error("Elixir build failed with exit code ${result.exitValue}")
            logger.error("Output:\n${output.toString()}")
            logger.error("Note: `make` must be available on PATH")
            throw RuntimeException("Failed to build Elixir (exit code ${result.exitValue})")
        }

        logger.lifecycle("Elixir build completed successfully")
    }

    /**
     * Sets up Mix environment (creates directories, installs rebar/hex).
     * Required on all platforms to fetch dependencies from git repositories.
     * Default implementation works for all platforms.
     * @param execOps Gradle exec operations
     * @param mixHome MIX_HOME directory
     * @param mixArchives MIX_ARCHIVES directory
     * @param elixirBinPath Elixir bin directory (contains mix executable)
     * @param logger Logger for output
     */
    fun setupMixEnvironment(
        execOps: ExecOperations,
        mixHome: File,
        mixArchives: File,
        elixirBinPath: File,
        logger: Logger
    ) {
        logger.lifecycle("Setting up Mix environment (${getPlatformName()})...")

        // Create directories
        mixHome.mkdirs()
        mixArchives.mkdirs()

        // Install hex and rebar (required to fetch dependencies from git)
        installMixTools(execOps, mixHome, mixArchives, elixirBinPath, logger)

        logger.lifecycle("Mix environment ready")
    }

    /**
     * Returns Mix environment variables for all Mix commands.
     * Default implementation works for all platforms.
     * @param mixHome MIX_HOME directory
     * @param mixArchives MIX_ARCHIVES directory
     * @return Map of environment variable names to values
     */
    fun getMixEnvironment(mixHome: File, mixArchives: File): Map<String, String> {
        return mapOf(
            "MIX_HOME" to mixHome.absolutePath,
            "MIX_ARCHIVES" to mixArchives.absolutePath
        )
    }

    /**
     * Installs local rebar and hex packages.
     * Required on all platforms to fetch dependencies from git repositories.
     * Default implementation that works across platforms.
     * @param execOps Gradle exec operations
     * @param mixHome MIX_HOME directory
     * @param mixArchives MIX_ARCHIVES directory
     * @param elixirBinPath Elixir bin directory (contains mix executable)
     * @param logger Logger for output
     */
    fun installMixTools(
        execOps: ExecOperations,
        mixHome: File,
        mixArchives: File,
        elixirBinPath: File,
        logger: Logger
    ) {
        val mixExe = getExecutable("mix", elixirBinPath)
        val mixEnv = getMixEnvironment(mixHome, mixArchives)

        // Install local rebar
        logger.lifecycle("Installing local rebar...")
        val rebarOutput = ByteArrayOutputStream()
        val rebarResult = execOps.exec {
            commandLine(mixExe, "local.rebar", "--force")
            workingDir(mixHome)
            environment(mixEnv)
            standardOutput = rebarOutput
            errorOutput = rebarOutput
            isIgnoreExitValue = true
        }

        if (rebarResult.exitValue != 0) {
            logger.error("Failed to install local rebar")
            logger.error("Output:\n${rebarOutput.toString()}")
            throw RuntimeException("Failed to install local rebar (exit code ${rebarResult.exitValue})")
        }

        // Install local hex
        logger.lifecycle("Installing local hex...")
        val hexOutput = ByteArrayOutputStream()
        val hexResult = execOps.exec {
            commandLine(mixExe, "local.hex", "--force")
            workingDir(mixHome)
            environment(mixEnv)
            standardOutput = hexOutput
            errorOutput = hexOutput
            isIgnoreExitValue = true
        }

        if (hexResult.exitValue != 0) {
            logger.error("Failed to install local hex")
            logger.error("Output:\n${hexOutput.toString()}")
            throw RuntimeException("Failed to install local hex (exit code ${hexResult.exitValue})")
        }

        logger.lifecycle("Mix tools (rebar and hex) installed successfully")
    }
}

/**
 * Creates the appropriate platform-specific ElixirPlatform implementation.
 * @param platform The target platform
 * @return Platform-specific implementation
 */
fun createElixirPlatform(platform: Platform): ElixirPlatform {
    return when (platform) {
        Platform.WINDOWS -> WindowsElixirPlatform()
        Platform.POSIX -> PosixElixirPlatform()
    }
}
