package elixir

import platform.detectPlatform
import platform.logPlatformDetection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

/**
 * BuildService that manages Elixir installation and build.
 * Provides platform-specific executable paths to other services and tasks.
 *
 * Lifecycle:
 * - Lazy initialization: build() is called on first use
 * - No cleanup needed: downloaded files are cached for reuse
 */
abstract class ElixirService : BuildService<ElixirService.Params> {

    interface Params : BuildServiceParameters {
        /** Elixir version being used */
        val elixirVersion: Property<String>

        /** Project directory for computing paths */
        val projectDir: DirectoryProperty
    }

    @get:Inject
    abstract val execOps: ExecOperations

    private val logger = Logging.getLogger(ElixirService::class.java)
    private val platform = detectPlatform()
    private val elixirPlatform = createElixirPlatform(platform)

    @Volatile
    private var ready = false

    // Compute paths dynamically to avoid circular references
    private fun getElixirPath(): File {
        val version = parameters.elixirVersion.get()
        val projectDir = parameters.projectDir.get().asFile
        return File(projectDir, "cache/elixir-$version")
    }

    private fun getMixHome(): File {
        val projectDir = parameters.projectDir.get().asFile
        return File(projectDir, "cache/mix_home")
    }

    private fun getMixArchives(): File {
        val projectDir = parameters.projectDir.get().asFile
        return File(projectDir, "cache/mix_archives")
    }

    /**
     * Ensures Elixir is fully ready: built + Mix environment configured.
     * Safe to call multiple times - only initializes once.
     * Includes Windows workarounds (setupMixEnvironment, installLocalRebar, installLocalHex).
     */
    fun ensureReady() {
        if (ready) return
        synchronized(this) {
            if (ready) return

            logPlatformDetection(logger)

            // 1. Build Elixir from source
            buildElixir()

            // 2. Setup Mix environment (creates dirs, installs rebar/hex on Windows)
            setupMixEnvironment()

            ready = true
            logger.lifecycle("Elixir is fully ready: ${getExecutable("mix")}")
        }
    }

    /**
     * Returns the full path to a platform-specific Elixir executable.
     * @param name Command name (e.g., "mix", "elixir", "iex")
     * @return Absolute path to the executable
     */
    fun getExecutable(name: String): String {
        val binPath = getElixirPath().resolve("bin")
        return elixirPlatform.getExecutable(name, binPath)
    }

    /**
     * Returns Mix environment variables that should be set for all Mix commands.
     * @return Map of environment variable names to values
     */
    fun getMixEnvironment(): Map<String, String> {
        return elixirPlatform.getMixEnvironment(getMixHome(), getMixArchives())
    }

    private fun buildElixir() {
        val elixirDir = getElixirPath()
        logger.lifecycle("Building Elixir ${parameters.elixirVersion.get()} in: ${elixirDir.absolutePath}")
        elixirPlatform.build(execOps, elixirDir, logger)
    }

    private fun setupMixEnvironment() {
        val elixirBinPath = getElixirPath().resolve("bin")
        elixirPlatform.setupMixEnvironment(execOps, getMixHome(), getMixArchives(), elixirBinPath, logger)
    }
}
