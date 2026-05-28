package org.elixir_lang.mise

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.concurrency.ThreadingAssertions
import org.jetbrains.annotations.VisibleForTesting
import java.nio.file.Path

private val LOG = logger<Mise>()

/**
 * A single tool entry from `mise ls --json`.
 */
data class MiseToolEntry(
    val version: String,
    val requestedVersion: String,
    val installPath: String,
    val sourceType: String?,
    val sourcePath: String?,
    val installed: Boolean,
    val active: Boolean,
)

/**
 * Resolved mise tool versions for the current project directory.
 */
data class MiseVersions(
    val elixir: MiseToolEntry?,
    val erlang: MiseToolEntry?,
)

/**
 * Raw Gson model matching the snake_case JSON output of `mise ls --json`.
 * Converted to [MiseToolEntry] after parsing.
 */
private data class RawMiseEntry(
    val version: String?,
    val requested_version: String?,
    val install_path: String?,
    val source: RawMiseSource?,
    val installed: Boolean?,
    val active: Boolean?,
)

private data class RawMiseSource(
    val type: String?,
    val path: String?,
)

/**
 * Integrates with [mise](https://mise.jdx.dev/) to resolve tool versions for the current project.
 *
 * Shells out to `mise ls --local --json` using the module content root as the working directory,
 * which causes mise to apply its normal walk-up resolution rules (.tool-versions, mise.toml, etc.)
 * for that directory.
 *
 * All failures (mise not installed, non-zero exit, unparseable output) are swallowed and return
 * `null` - the feature simply doesn't activate when mise is unavailable.
 */
object Mise {
    private const val TIMEOUT_MS = 10_000

    /**
     * Invokes `mise ls --local --json` in [workDir] and parses the result.
     *
     * Returns `null` if mise is not on PATH, returns a non-zero exit code, times out, or
     * produces output that cannot be parsed. Callers should treat `null` as "mise unavailable".
     *
     * **Threading**: Must not be called on the EDT or under a read lock. This method spawns
     * a subprocess and blocks for up to [TIMEOUT_MS]ms. Callers must ensure they are running
     * on a background thread outside of any read lock (e.g. inside `withContext(Dispatchers.IO)`
     * after releasing any read lock).
     */
    fun resolveVersions(workDir: Path): MiseVersions? {
        // `assertBackgroundThread()` only checks EDT, NOT holdsReadLock - both guards are needed.
        ThreadingAssertions.assertBackgroundThread()
        check(!ApplicationManager.getApplication().holdsReadLock()) {
            "Mise.resolveVersions() must not be called under a read lock - " +
            "it spawns a subprocess that blocks for up to ${TIMEOUT_MS}ms"
        }
        return try {
            val commandLine = GeneralCommandLine("mise", "ls", "--local", "--json")
                .withWorkDirectory(workDir.toFile())
            val handler = CapturingProcessHandler(commandLine)
            val output = handler.runProcess(TIMEOUT_MS)

            if (output.exitCode != 0) {
                LOG.debug("mise ls --local --json exited with ${output.exitCode} in $workDir: ${output.stderr.take(200)}")
                return null
            }

            parseOutput(output.stdout)
        } catch (e: Exception) {
            LOG.debug("mise ls --local --json failed in $workDir: ${e.message}")
            null
        }
    }

    /**
     * Parses the JSON output of `mise ls --local --json` into a [MiseVersions] result.
     *
     * Takes the first entry for each tool key where `installed == true && active == true`.
     *
     * `internal` for testing - use [resolveVersions] in production code.
     */
    @VisibleForTesting
    internal fun parseOutput(json: String): MiseVersions? {
        return try {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
            val type = object : TypeToken<Map<String, List<RawMiseEntry>>>() {}.type
            // fromJson returns a platform type (T!) which can be null for empty/invalid JSON.
            @Suppress("UNCHECKED_CAST")
            val allTools = gson.fromJson(json, type) as? Map<String, List<RawMiseEntry>>
                ?: return null

            val elixir = allTools["elixir"]
                ?.firstOrNull { it.installed == true && it.active == true }
                ?.toMiseToolEntry()

            val erlang = allTools["erlang"]
                ?.firstOrNull { it.installed == true && it.active == true }
                ?.toMiseToolEntry()

            MiseVersions(elixir, erlang)
        } catch (e: Exception) {
            LOG.debug("Failed to parse mise ls output: ${e.message}")
            null
        }
    }

    private fun RawMiseEntry.toMiseToolEntry(): MiseToolEntry? {
        val v = version ?: return null
        val ip = install_path ?: return null
        return MiseToolEntry(
            version = v,
            requestedVersion = requested_version ?: v,
            installPath = ip,
            sourceType = source?.type,
            sourcePath = source?.path,
            installed = installed ?: false,
            active = active ?: false,
        )
    }

    /**
     * Strips the `-otp-XX` suffix that mise appends to Elixir version strings
     * (e.g. `"1.13.4-otp-24"` → `"1.13.4"`).
     *
     * **Note**: this is a temporary workaround. Once `ElixirVersionDetector` reads
     * the `vsn` field from `elixir.app` directly, both sides of the version comparison
     * will be bare version strings and this function can be removed.
     */
    @VisibleForTesting
    internal fun stripElixirOtpSuffix(version: String): String {
        val idx = version.indexOf("-otp-")
        return if (idx >= 0) version.substring(0, idx) else version
    }
}
