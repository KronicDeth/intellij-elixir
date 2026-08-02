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
 * Fetches Mix dependencies for the Quoter project using the Elixir/Erlang SDK resolved by
 * `resolveElixirErlangSdks` (mise/PATH/env aware) - no from-source Elixir build required.
 */
abstract class GetQuoterDepsTask : DefaultTask() {

    /** Properties file written by `resolveElixirErlangSdks` (elixir.sdk.path / erlang.sdk.path). */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val sdkProperties: RegularFileProperty

    /**
     * Working directory for `mix`. Deliberately @Internal, not @InputDirectory: this task's own
     * `deps` output - and `releaseQuoter`'s `_build` output - are nested inside it, so tracking the
     * whole tree as an input made every build invalidate this task and re-run `mix local.hex`
     * (a network round-trip; `--force` skips the prompt, not the download). The inputs that actually
     * decide the dependency set, `mix.exs` and `mix.lock`, are declared at the registration site.
     */
    @get:Internal
    abstract val quoterDir: DirectoryProperty

    /**
     * MIX_HOME and the MIX_ARCHIVES root are OUTPUTS, not internal state: `mix local.rebar` and
     * `mix local.hex` populate them, and `releaseQuoter`'s `mix release` cannot run without them.
     * Declaring them has two effects that the previous `@Internal` did not:
     *
     * - the build cache stores and restores them alongside `deps`, so a cache hit that skips this
     *   action still leaves hex/rebar in place (otherwise `mix release` fails with
     *   "Could not find Hex, which is needed to build dependency :credo");
     * - an up-to-date or from-cache task never runs the action, so `local.hex`/`local.rebar` stop
     *   hitting the network on every build - `--force` suppresses the prompt, not the download.
     */
    @get:OutputDirectory
    abstract val mixHome: DirectoryProperty

    /** Root holding one [mixArchivesDir] per Elixir/OTP pair - not MIX_ARCHIVES itself. */
    @get:OutputDirectory
    abstract val mixArchivesRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val depsDir: DirectoryProperty

    @get:Inject
    abstract val execOps: ExecOperations

    init {
        // Configure task to cache outputs
        outputs.cacheIf { true }
    }

    @TaskAction
    fun getDeps() {
        val props = readPropertiesFile(sdkProperties.get().asFile)
        val elixirHome = File(props["elixir.sdk.path"] ?: throw GradleException("Missing elixir.sdk.path"))
        val erlangHome = File(props["erlang.sdk.path"] ?: throw GradleException("Missing erlang.sdk.path"))
        val mixExe = mixExecutable(elixirHome)
        val home = mixHome.get().asFile.apply { mkdirs() }
        val archives = mixArchivesDir(
            mixArchivesRoot.get().asFile,
            props["elixir.version"],
            props["erlang.version"]
        ).apply { mkdirs() }
        val mixEnv = mixEnvironment(erlangHome, home, archives)
        val dir = quoterDir.get().asFile

        fun mix(vararg args: String) {
            execOps.exec {
                commandLine(listOf(mixExe) + args)
                workingDir(dir)
                environment(mixEnv)
            }
        }

        // hex + rebar are required to fetch dependencies.
        mix("local.rebar", "--force")
        mix("local.hex", "--force")
        mix("deps.get")
    }
}
