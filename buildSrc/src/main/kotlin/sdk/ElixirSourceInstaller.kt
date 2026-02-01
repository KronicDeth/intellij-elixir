package sdk

import elixir.createElixirPlatform
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.process.ExecOperations
import platform.detectPlatform
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream

/**
 * Downloads and builds Elixir from source into the project cache when needed.
 */
class ElixirSourceInstaller(
    private val logger: Logger,
    private val projectDir: File,
    private val elixirVersion: String,
    private val execOps: ExecOperations
) {
    val elixirHome: File = File(projectDir, "cache/elixir-$elixirVersion")
    val zipFile: File = File(projectDir, "cache/Elixir.$elixirVersion.zip")

    fun ensureInstalled(): File {
        // Keep the work in the project cache to avoid repeated downloads.
        if (elixirVersion.isBlank()) {
            throw GradleException("Elixir version is empty; cannot download Elixir sources.")
        }

        if (isValidElixirHome(elixirHome)) {
            return elixirHome
        }

        elixirHome.parentFile.mkdirs()

        if (!zipFile.isFile) {
            downloadElixir()
        }

        unzipElixir()

        if (!isValidElixirHome(elixirHome)) {
            buildElixir()
        }

        if (!isValidElixirHome(elixirHome)) {
            throw GradleException("Elixir SDK was not successfully built at ${elixirHome.absolutePath}")
        }

        return elixirHome
    }

    private fun downloadElixir() {
        // Download the official source zip for the configured version.
        val url = URI.create("https://github.com/elixir-lang/elixir/archive/v$elixirVersion.zip").toURL()
        logger.lifecycle("Downloading Elixir $elixirVersion from $url")

        try {
            url.openStream().use { input ->
                Files.copy(input, zipFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: IOException) {
            throw GradleException("Failed to download Elixir $elixirVersion from $url", e)
        }
    }

    private fun unzipElixir() {
        // Strip the top-level directory from the zip to match the cache layout.
        logger.lifecycle("Unzipping Elixir sources to ${elixirHome.absolutePath}")
        ZipInputStream(Files.newInputStream(zipFile.toPath())).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val strippedName = entry.name.substringAfter('/', "")
                if (strippedName.isNotEmpty()) {
                    val outputFile = File(elixirHome, strippedName)
                    if (entry.isDirectory) {
                        outputFile.mkdirs()
                    } else {
                        outputFile.parentFile.mkdirs()
                        Files.copy(zip, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }
                }
                entry = zip.nextEntry
            }
        }
    }

    private fun buildElixir() {
        // Build with the platform-specific helper (uses make under the hood).
        val platform = createElixirPlatform(detectPlatform())
        platform.build(execOps, elixirHome, logger)
    }

}
