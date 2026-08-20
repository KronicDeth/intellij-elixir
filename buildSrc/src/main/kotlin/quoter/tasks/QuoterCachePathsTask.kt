package quoter.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * Reports the `actions/cache` path patterns covering the quoter's build tree, for the "Resolve quoter
 * cache paths" step in `.github/workflows/shared-test.yml`.
 *
 * The workflow used to derive these itself: grep `quoterRef` out of `gradle.properties`, slugify it, and
 * rebuild the Elixir/OTP pair token in bash. Both were second implementations of rules that live in
 * `build.gradle.kts` and `sdk.pairToken`, free to drift from them, and drifting was silent - a cache
 * entry naming a directory nothing writes misses on every restore and then collides on save. The grep
 * could not see `-PquoterRef`, `ORG_GRADLE_PROJECT_quoterRef` or `~/.gradle/gradle.properties` either,
 * each of which changes which quoter the build fetches.
 *
 * The patterns are always logged, one per line, which is what you want by hand. Given a
 * `--github-output`, the whole GitHub Actions step output is appended to that file as well, so the
 * workflow needs a single line and no shell of its own:
 *
 *     ./gradlew -q quoterCachePaths --github-output="$GITHUB_OUTPUT"
 *
 * Writing GitHub's own output syntax is a coupling this task already has - `actions/cache` glob
 * patterns, `!` exclusions and all, are no more portable than the envelope around them.
 *
 * Deliberately declares no outputs, so it runs every time. `$GITHUB_OUTPUT` is a different file for
 * every step, and a task Gradle believed was up to date would leave the step's output unset - an empty
 * `path`, which actions/cache accepts and silently caches nothing under.
 */
abstract class QuoterCachePathsTask : DefaultTask() {

    /**
     * Patterns in `actions/cache` syntax - a leading `!` excludes. Order is significant and must be
     * byte-identical between the Restore and Save steps: actions/cache identifies an entry by
     * (key, version), where the version is a sha256 of the literal patterns joined by "|", not of the
     * files they expand to. Two different lists are therefore two different entries under one key,
     * which is why both steps read one output rather than each listing the paths.
     */
    @get:Input
    abstract val patterns: ListProperty<String>

    /** Name of the step output the patterns are published as. */
    @get:Input
    abstract val outputName: Property<String>

    /**
     * Where to append the step output, normally `$GITHUB_OUTPUT`. Appended rather than replaced: the
     * runner reads the file after the step finishes, so truncating it would drop anything an earlier
     * command in the same step had written.
     */
    @get:Optional
    @get:Input
    @get:Option(
        option = "github-output",
        description = "File to append the GitHub Actions step output to, normally \$GITHUB_OUTPUT."
    )
    abstract val githubOutput: Property<String>

    @TaskAction
    fun report() {
        val lines = patterns.get()
        // QUIET, not lifecycle: reporting the patterns is the whole job, and the workflow runs this
        // with -q, which silences lifecycle. At lifecycle level `gradlew -q quoterCachePaths` - the
        // obvious way to inspect them by hand - printed nothing at all.
        logger.quiet(lines.joinToString("\n"))

        val destination = githubOutput.orNull?.takeIf { it.isNotBlank() } ?: return
        // A heredoc-style block, because the value is multi-line; `name=value` handles one line only.
        // The delimiter has to appear on no line of the value, which glob patterns never produce.
        val delimiter = "QUOTER_CACHE_PATHS_EOF"
        val block = (listOf("${outputName.get()}<<$delimiter") + lines + delimiter)
            // LF, not the platform separator: the runner parses this file the same way on every OS.
            .joinToString("\n", postfix = "\n")
        // UTF-8 and LF are what the runner reads, on Windows runners too - it is also what the bash
        // `echo` this replaced produced. Stated rather than left to the default because appending to a
        // file someone else began is where encoding goes wrong: seed the target with PowerShell 5.1's
        // `>` (UTF-16LE, with a BOM) and every reader honours that BOM and renders this block as
        // mojibake. Nothing to fix here when that happens - the target is the wrong encoding, not this.
        File(destination).appendText(block, Charsets.UTF_8)
    }
}
