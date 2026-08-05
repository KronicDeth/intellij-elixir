package versioning

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * The source identity of the working tree, as `<commit time>.<commit>` with `-wip` appended when the
 * tree differs from `HEAD` in any way, including untracked files - for example
 * `20260804164541.64e3d69a-wip`.
 *
 * This exists because of a real triage failure. Untagged builds used to be versioned
 * `<base>-pre+<build timestamp>` and nothing else, and that timestamp is wall-clock at build time
 * with no relationship to the commit being built. Marketplace exception reports carry only that one
 * string to identify the code that crashed - `plugin.version` in the report payload becomes
 * `pluginVersion` in the exception-analyzer API, and there is no second metadata field to put this
 * in - so a report from a build stamped June 30 turned out to be running source from three weeks
 * earlier, and the only way to notice was spotting a stack frame in a file deleted since.
 *
 * **The timestamp is the commit's, not the build's**, which is what makes that failure unmissable
 * rather than merely detectable: the stale build above would now read `20260603...` and answer the
 * question outright. It also makes the whole string a pure function of (commit, dirty), so rebuilding
 * unchanged source produces a byte-identical version and leaves `patchPluginXml` - and everything
 * downstream of the patched descriptor - UP-TO-DATE. A wall-clock stamp re-runs all of it on every
 * reconfiguration.
 *
 * Committer date rather than author date: a rebased or amended commit is a different commit and
 * should sort later, which the author date would hide.
 *
 * The `-wip` marker is dash-joined so the commit and its qualifier read as one field against the
 * dot-separated timestamp, and everything stays inside `[0-9A-Za-z-]` because this string reaches both
 * the distribution filename (`intellij-elixir-<version>.zip`) and `<version>` in `plugin.xml` - which
 * rules out the shorter symbols: `*` and `?` are illegal in Windows filenames, `&` and `<` need XML
 * escaping, `+` is already the build-metadata delimiter, and `~` reads as "earlier than" to
 * Debian-style version comparison.
 *
 * A ValueSource rather than a configuration-time `exec` so the configuration cache re-checks the
 * value instead of freezing a stale commit into every subsequent build. The cost is deliberate and
 * worth naming: the cache is invalidated whenever `HEAD` moves or the tree's dirty state flips.
 * Ask for this only for builds that need it - a tagged release takes its version from the tag, which
 * already identifies its source exactly.
 *
 * Returns null when git is absent or this is not a work tree; callers fall back to a build-clock
 * stamp rather than failing the build. The two are told apart by the presence of a commit: a lone
 * timestamp is a build time, a timestamp followed by a commit is that commit's time.
 */
abstract class GitSourceIdValueSource : ValueSource<String, GitSourceIdValueSource.Params> {

    interface Params : ValueSourceParameters {
        /** Directory to run git from - the repository root, or anywhere inside the work tree. */
        val workingDir: Property<File>
    }

    override fun obtain(): String? {
        val commit = git("rev-parse", "--short=8", "HEAD")?.takeIf(String::isNotEmpty) ?: return null
        val committedAt = commitTimestamp() ?: return null
        return if (isDirty()) "$committedAt.$commit-wip" else "$committedAt.$commit"
    }

    /**
     * `HEAD`'s committer date as `yyyyMMddHHmmss` in UTC.
     *
     * Asks git for `%ct` (committer date, epoch seconds) and formats it here rather than using git's
     * own `--date=format-local:` - that would render in whatever zone the build machine is in, so the
     * same commit would stamp differently for two developers, and forcing UTC would mean injecting
     * `TZ` into the subprocess environment. Epoch seconds are zone-free, so the conversion is done
     * once, explicitly, in UTC.
     */
    private fun commitTimestamp(): String? {
        val epochSeconds = git("show", "-s", "--format=%ct", "HEAD")?.toLongOrNull() ?: return null
        val format = SimpleDateFormat("yyyyMMddHHmmss").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return format.format(Date(epochSeconds * 1000))
    }

    /**
     * Whether the work tree differs from `HEAD` in any way that could reach the artifact.
     *
     * Deliberately includes **untracked** files, not just modifications to tracked ones. A new
     * un-added `.kt` under a source root is compiled into the plugin exactly like a committed one, so
     * a build containing it is not reproducible from `HEAD` and must not claim to be. Checking only
     * `diff-index HEAD` misses that case entirely.
     *
     * One `git status` rather than the staged/modified/untracked trio: it covers all three, refreshes
     * the stat cache itself (so no separate `update-index --refresh` is needed to stop touched-but-
     * unchanged files reading as modified), and measures the same as a bare `diff-index` on this repo
     * because the untracked scan dominates either way.
     *
     * `--untracked-files=normal` is explicit because `status.showUntrackedFiles=no` in a developer's
     * git config otherwise suppresses untracked files from `--porcelain` output, which would silently
     * reintroduce the gap on exactly the machines most likely to have local work in progress.
     *
     * An unreadable status is treated as dirty. If the state cannot be determined, saying so is the
     * safe direction: a spurious `-wip` on every build is loud and gets fixed, whereas a spurious
     * "clean" is the failure this whole mechanism exists to prevent.
     */
    private fun isDirty(): Boolean =
        git("status", "--porcelain", "--untracked-files=normal")?.isNotEmpty() ?: true

    /**
     * Runs git and returns its trimmed stdout, or null when it cannot be run, times out, or exits
     * non-zero.
     *
     * The pipe handling mirrors [sdk.MiseCurrentVersionValueSource]: this runs during Gradle
     * *configuration*, on every invocation, so the subprocess must never be able to block on a pipe
     * nobody services. stderr is discarded rather than piped - an unread stderr pipe deadlocks once
     * git's output exceeds the OS buffer - and stdin is closed immediately to give it EOF.
     */
    private fun git(vararg args: String): String? =
        try {
            val process = ProcessBuilder(listOf("git") + args)
                .directory(parameters.workingDir.orNull)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
            process.outputStream.close()
            val output = process.inputStream.bufferedReader().use { it.readText() }
            when {
                !process.waitFor(30, TimeUnit.SECONDS) -> {
                    process.destroyForcibly()
                    null
                }
                process.exitValue() != 0 -> null
                else -> output.trim()
            }
        } catch (_: Exception) {
            // git not installed, not on PATH, or this is not a work tree - the caller falls back.
            null
        }
}
