package quoter.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import sdk.mixArchivesDir
import sdk.mixEnvironment
import sdk.mixExecutable
import sdk.readPropertiesFile
import java.io.File
import javax.inject.Inject

/**
 * Creates a Mix release for the Quoter project using the Elixir/Erlang SDK resolved by
 * `resolveElixirErlangSdks` (mise/PATH/env aware) - no from-source Elixir build required. The
 * release bundles ERTS from the resolved Erlang, so the daemon is self-contained at runtime.
 */
abstract class ReleaseQuoterTask : DefaultTask() {

    /** Properties file written by `resolveElixirErlangSdks` (elixir.sdk.path / erlang.sdk.path). */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val sdkProperties: RegularFileProperty

    /**
     * Working directory for `mix`. @Internal rather than @InputDirectory because this task's own
     * `_build` output is nested inside it, which would make it perpetually out of date. The real
     * inputs (mix.exs, mix.lock, lib, config, deps) are declared at the registration site.
     */
    @get:Internal
    abstract val quoterDir: DirectoryProperty

    /** Populated by `getQuoterDeps` (which declares both as outputs); consumed here, never written. */
    @get:Internal
    abstract val mixHome: DirectoryProperty

    /** Root holding one [mixArchivesDir] per Elixir/OTP pair - not MIX_ARCHIVES itself. */
    @get:Internal
    abstract val mixArchivesRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val buildDir: DirectoryProperty

    @get:Inject
    abstract val execOps: ExecOperations

    init {
        outputs.cacheIf { true }
    }

    @TaskAction
    fun release() {
        val props = readPropertiesFile(sdkProperties.get().asFile)
        val elixirHome = File(props["elixir.sdk.path"] ?: throw GradleException("Missing elixir.sdk.path"))
        val erlangHome = File(props["erlang.sdk.path"] ?: throw GradleException("Missing erlang.sdk.path"))
        val mixExe = mixExecutable(elixirHome)
        val archives = mixArchivesDir(
            mixArchivesRoot.get().asFile,
            props["elixir.version"],
            props["erlang.version"]
        )
        val mixEnv = mixEnvironment(erlangHome, mixHome.get().asFile, archives)

        execOps.exec {
            commandLine(mixExe, "release", "--overwrite")
            workingDir(quoterDir.get().asFile)
            environment(mixEnv)
        }
    }
}
