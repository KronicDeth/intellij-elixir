package sdk

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * The version mise currently resolves for a tool in this project - `mise current <tool>`.
 *
 * This is the project's Elixir/Erlang pin. Asking mise, rather than parsing `mise.toml`, means the
 * build sees the version that will actually be on `PATH`: mise applies its whole precedence chain
 * (`mise.local.toml` > `mise.toml` > any `.tool-versions` > global config > `MISE_ENV`), so a
 * developer who overrides a version locally gets a build that agrees with their shell instead of one
 * that silently disagrees with it.
 *
 * A ValueSource rather than a plain configuration-time `exec` so the configuration cache can re-check
 * the value on each build instead of freezing a stale one.
 *
 * Returns null when mise is absent or the tool is not configured; callers fall back to the explicit
 * `-PelixirVersion` / `-PotpVersion` properties, which is how CI runs (no mise on the runners).
 */
abstract class MiseCurrentVersionValueSource : ValueSource<String, MiseCurrentVersionValueSource.Params> {

    interface Params : ValueSourceParameters {
        /** mise tool name, e.g. `elixir` or `erlang`. */
        val tool: Property<String>

        /** Directory to resolve from - mise walks up from here. */
        val workingDir: Property<File>
    }

    override fun obtain(): String? {
        val tool = parameters.tool.get()
        return try {
            // stdin and stderr are discarded rather than piped. Reading stdout to EOF is what
            // actually bounds this call - the waitFor below cannot fire until it returns - so mise
            // must never be able to block on a pipe we are not servicing: an unread stderr pipe
            // fills and deadlocks once mise's warnings exceed the OS buffer, and an unwritten stdin
            // pipe hangs anything that tries to read it. Both would stall Gradle *configuration*,
            // which runs on every invocation.
            val process = ProcessBuilder("mise", "current", tool)
                .directory(parameters.workingDir.orNull)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
            // Give stdin an immediate EOF instead of an open pipe nobody writes to.
            process.outputStream.close()
            val output = process.inputStream.bufferedReader().use { it.readText() }
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                return null
            }
            if (process.exitValue() != 0) {
                return null
            }
            // `mise current <tool>` prints the resolved version alone; blank means "not configured".
            output.lineSequence().map(String::trim).lastOrNull { it.isNotEmpty() }
        } catch (_: Exception) {
            // mise not installed, not on PATH, or not executable - the caller falls back.
            null
        }
    }
}
