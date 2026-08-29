package org.elixir_lang.debugger

import junit.framework.TestCase
import java.io.File

/**
 * `erlArgumentList` interpolates a temporary file's path into an Erlang `-eval` string. Erlang reads
 * `\d` and `\s` inside a `<<"...">>` binary as escape sequences, so an un-doubled Windows path turns
 * `C:\deps\src` into control characters and the debugged node dies at boot with
 * `init terminating in do_boot` before any breakpoint is reachable.
 *
 * This pins the doubling. The interesting leg is `windows-2025` in `.github/scripts/compose-legs.js`,
 * where the temporary path genuinely contains backslashes; on the Linux legs the round-trip assertion
 * still holds but the escaping is a no-op.
 */
class ModulesTest : TestCase() {
    fun testRequireFilePathsEscapeBackslashes() {
        val requireFilePaths = requireFilePaths()

        assertFalse(
            "Expected at least one require_file -eval argument",
            requireFilePaths.isEmpty()
        )

        for (path in requireFilePaths) {
            // Removing every doubled backslash must leave none behind: a lone `\` is one Erlang would
            // consume as the start of an escape sequence.
            assertFalse(
                "Un-doubled backslash in require_file path, which Erlang reads as an escape " +
                        "sequence and fails to boot on: $path",
                path.replace("\\\\", "").contains('\\')
            )
        }
    }

    fun testRequireFilePathsRoundTripToRealFiles() {
        for (path in requireFilePaths()) {
            val unescaped = path.replace("\\\\", "\\")

            assertTrue(
                "require_file path does not name a file that exists once unescaped: $path",
                File(unescaped).isFile
            )
        }
    }

    private fun requireFilePaths(): List<String> =
        Modules
            .erlArgumentList()
            .filter { it.contains("require_file") }
            .map { argument ->
                val start = argument.indexOf(BINARY_PREFIX)
                val end = argument.lastIndexOf(BINARY_SUFFIX)
                check(start >= 0 && end > start) { "Unrecognized require_file argument: $argument" }

                argument.substring(start + BINARY_PREFIX.length, end)
            }

    companion object {
        private const val BINARY_PREFIX = "<<\""
        private const val BINARY_SUFFIX = "\">>"
    }
}
