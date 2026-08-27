package org.elixir_lang.sdk.erlang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

/**
 * OTP ships its sources beside the compiled beams - `lib/<app>-<version>/{ebin,src}` - so the
 * sibling of every ebin holds the source for what that ebin contains. Until these became
 * `SOURCES` roots an Erlang frame in a stack trace named a file nothing indexed could find, and
 * `gen_server.erl` was not navigable.
 *
 * [Type.sourcePaths] is the whole decision; registering what it returns is the platform's own root
 * bookkeeping. Testing it against a home built on disk keeps this to the mapping and needs no SDK.
 */
class TypeSourcePathsTest : BasePlatformTestCase() {
    fun testTakesTheSiblingSrcOfEveryEbin() {
        val home = erlangHome(
            "stdlib-7.1" to true,
            "kernel-10.1" to true,
        )

        val sourcePaths = Type.sourcePaths(home)

        assertEquals("Expected one source path per application, got: $sourcePaths", 2, sourcePaths.size)
        assertContainsElements(
            sourcePaths.map { it.parent.fileName.toString() },
            "stdlib-7.1",
            "kernel-10.1",
        )
        assertTrue("Every path is a src directory, got: $sourcePaths", sourcePaths.all { it.fileName.toString() == "src" })
    }

    /** A pre-built application ships an ebin with no sources beside it, and contributes nothing. */
    fun testSkipsAnApplicationWithNoSrc() {
        val home = erlangHome(
            "stdlib-7.1" to true,
            "compiler-9.0" to false,
        )

        val sourcePaths = Type.sourcePaths(home)

        assertEquals("Only the application with a src belongs, got: $sourcePaths", 1, sourcePaths.size)
        assertEquals("stdlib-7.1", sourcePaths.single().parent.fileName.toString())
    }

    /** A home with no `lib` at all is not an error - an SDK can be pointed anywhere. */
    fun testTakesNothingFromAHomeWithoutLib() {
        val home = createTempDir("erlang-home-empty").absolutePath

        assertEmpty(Type.sourcePaths(home))
    }

    /**
     * Builds an Erlang home on disk: each pair is an application directory and whether it has a
     * `src` beside its `ebin`.
     */
    private fun erlangHome(vararg applications: Pair<String, Boolean>): String {
        val home = createTempDir("erlang-home")

        for ((application, hasSrc) in applications) {
            val applicationDirectory = File(home, "lib${File.separator}$application")
            assertTrue(File(applicationDirectory, "ebin").mkdirs())

            if (hasSrc) {
                assertTrue(File(applicationDirectory, "src").mkdirs())
            }
        }

        return home.absolutePath
    }
}
