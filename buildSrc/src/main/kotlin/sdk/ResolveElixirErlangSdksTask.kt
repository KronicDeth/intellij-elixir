package sdk

import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Depends on PATH, environment variables, and external tools.")
/**
 * Resolves Erlang/Elixir SDKs and writes their paths to a properties file for tests.
 */
abstract class ResolveElixirErlangSdksTask : DefaultTask() {

    @get:Internal
    abstract val projectDir: DirectoryProperty

    @get:Optional
    @get:org.gradle.api.tasks.InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val toolVersionsFile: RegularFileProperty

    @get:Optional
    @get:org.gradle.api.tasks.InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val gradlePropertiesFile: RegularFileProperty

    @get:Input
    abstract val elixirVersion: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Inject
    abstract val execOps: ExecOperations

    @get:Inject
    abstract val archiveOps: ArchiveOperations

    @get:Inject
    abstract val fileOps: FileSystemOperations

    @TaskAction
    fun resolve() {
        // Read expected versions from .tool-versions and gradle.properties.
        logger.lifecycle("Resolving Erlang and Elixir SDKs for tests.")
        val toolVersions = readToolVersions(toolVersionsFile.orNull?.asFile)
        val gradleElixirVersion = readGradleElixirVersion(gradlePropertiesFile.orNull?.asFile)
        logger.lifecycle(
            "Detected versions: .tool-versions elixir=${toolVersions.elixir ?: "not set"}, " +
                "erlang=${toolVersions.erlang ?: "not set"}; gradle.properties elixirVersion=${gradleElixirVersion ?: "not set"}"
        )
        if (!toolVersions.elixir.isNullOrBlank() &&
            !gradleElixirVersion.isNullOrBlank() &&
            !isCompatibleVersion(gradleElixirVersion, toolVersions.elixir)
        ) {
            logger.warn(
                "Elixir version mismatch between gradle.properties (elixirVersion=$gradleElixirVersion) " +
                    "and .tool-versions (elixir ${toolVersions.elixir})."
            )
        }

        val expectedElixirVersion = elixirVersion.orNull ?: gradleElixirVersion ?: toolVersions.elixir
        logger.lifecycle("Expected Elixir version: ${expectedElixirVersion ?: "not set"}")
        val installer = ElixirSourceInstaller(
            logger,
            projectDir.get().asFile,
            expectedElixirVersion.orEmpty(),
            execOps,
            archiveOps,
            fileOps
        )
        val resolver = ElixirErlangSdkResolver(
            logger = logger,
            projectDir = projectDir.get().asFile,
            expectedErlangVersion = toolVersions.erlang,
            expectedElixirVersion = expectedElixirVersion,
            elixirInstaller = installer
        )
        val resolved = resolver.resolve()

        val output = outputFile.get().asFile
        output.parentFile.mkdirs()
        output.writeText(resolved.toPropertiesString())
        logger.lifecycle(
            "Resolved SDKs: erlang=${resolved.erlang.homePath} (source=${resolved.erlang.source}, " +
                "version=${resolved.erlang.actualVersion ?: "unknown"}), " +
                "elixir=${resolved.elixir.homePath} (source=${resolved.elixir.source}, " +
                "version=${resolved.elixir.actualVersion ?: "unknown"})."
        )
        logger.lifecycle("Wrote SDK properties to ${output.absolutePath}")
    }
}
