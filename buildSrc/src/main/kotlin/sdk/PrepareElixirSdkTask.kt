package sdk

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Downloads and builds Elixir from the network and local tools.")
/**
 * Gradle task that ensures a source-built Elixir SDK exists in the cache.
 */
abstract class PrepareElixirSdkTask : DefaultTask() {

    @get:Input
    abstract val elixirVersion: Property<String>

    @get:Internal
    abstract val projectDir: DirectoryProperty

    @get:OutputDirectory
    abstract val elixirHome: DirectoryProperty

    @get:OutputFile
    abstract val markerFile: RegularFileProperty

    @get:Inject
    abstract val execOps: ExecOperations

    @TaskAction
    fun install() {
        // Use ElixirSourceInstaller to download/build if the cache is missing.
        val installer = ElixirSourceInstaller(
            logger,
            projectDir.get().asFile,
            elixirVersion.get(),
            execOps
        )
        installer.ensureInstalled()

        val marker = markerFile.get().asFile
        marker.parentFile.mkdirs()
        marker.writeText("installed")
    }
}
