package quoter

import java.io.File

/**
 * Whether a Quoter daemon could be built for the Elixir/OTP pair this build targets. Written by
 * `releaseQuoter`, read by `startQuoter` and the test tasks.
 *
 * This marker, not the exit code of `mix release`, is what the build treats as authoritative:
 * `releaseQuoter` succeeds either way so that a pair whose quoter will not compile still runs the
 * tests that need no quoter.
 */
data class QuoterAvailability(val available: Boolean, val reason: String?) {

    /** Writes the marker, creating its parent directory. */
    fun writeTo(file: File) {
        file.parentFile?.mkdirs()
        val lines = buildList {
            add("$AVAILABLE_KEY=$available")
            reason?.takeIf { it.isNotBlank() }?.let { add("$REASON_KEY=${singleLine(it)}") }
        }
        file.writeText(lines.joinToString(System.lineSeparator()))
    }

    companion object {
        const val AVAILABLE_KEY = "quoter.available"
        const val REASON_KEY = "quoter.unavailable.reason"

        /** Environment variable carrying [available] to the test JVM. */
        const val AVAILABLE_ENVIRONMENT_VARIABLE = "QUOTER_AVAILABLE"

        /** Environment variable carrying [reason] to the test JVM; only set when unavailable. */
        const val REASON_ENVIRONMENT_VARIABLE = "QUOTER_UNAVAILABLE_REASON"

        /** How many trailing output lines of a failed `mix release` are kept as the reason. */
        private const val REASON_LINES = 5

        /** Bounds the reason: it ends up in an environment block. */
        private const val REASON_MAX_LENGTH = 500

        val AVAILABLE = QuoterAvailability(available = true, reason = null)

        fun unavailable(reason: String): QuoterAvailability =
            QuoterAvailability(available = false, reason = reason)

        /**
         * Reads the marker, or `null` when `releaseQuoter` has not run.
         *
         * A file that exists but does not say `available=true` reads as unavailable: mistaking a
         * working quoter for a missing one costs a clear message, the reverse costs a timeout per
         * quoting test.
         */
        fun readFrom(file: File): QuoterAvailability? {
            if (!file.isFile) return null

            val properties = file.readLines()
                .mapNotNull { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) return@mapNotNull null
                    val separator = trimmed.indexOf('=')
                    if (separator <= 0) return@mapNotNull null
                    trimmed.substring(0, separator).trim() to trimmed.substring(separator + 1).trim()
                }
                .toMap()

            return QuoterAvailability(
                available = properties[AVAILABLE_KEY].toBoolean(),
                reason = properties[REASON_KEY]?.takeIf { it.isNotBlank() }
            )
        }

        /**
         * The tail of a failed `mix release`, as one line that survives a properties file, an
         * environment variable and a JUnit XML.
         *
         * Elixir frames every diagnostic in box-drawing characters, unconditionally - `format_snippet/6`
         * in `elixir_errors.erl` offers no plain mode. They are stripped rather than matched on,
         * because each boundary this string crosses is another chance to render them as mojibake, and
         * they say nothing. What is left of a row that was only frame - gutter, caret, underline - is
         * dropped; the `└─ file:line:column: context` row is the diagnostic's only statement of where
         * it happened, so it survives as its text.
         */
        fun summarize(output: String): String =
            output.lines()
                .map { line -> line.filterNot { it in FRAME_CHARACTERS }.replace(WHITESPACE, " ").trim() }
                .filterNot { it.isEmpty() || it.all { character -> character in HIGHLIGHT_CHARACTERS } }
                .takeLast(REASON_LINES)
                .joinToString(" | ")
                .let(::singleLine)
                .let { if (it.length > REASON_MAX_LENGTH) it.take(REASON_MAX_LENGTH) + "..." else it }

        private val FRAME_CHARACTERS = "│┌├└─".toSet()

        /** What a frame row holds instead of text: the `^` position marker and the `~~~` span. */
        private val HIGHLIGHT_CHARACTERS = "^~".toSet()

        private val WHITESPACE = Regex("\\s+")

        private fun singleLine(text: String): String =
            text.replace(Regex("\\s*\\R\\s*"), " | ").trim()
    }
}
