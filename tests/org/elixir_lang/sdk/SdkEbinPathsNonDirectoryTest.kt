package org.elixir_lang.sdk

import org.elixir_lang.PlatformTestCase
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator

/**
 * Pins that a regular file sitting among the application directories under `<home>/lib` is skipped
 * rather than descended into.
 *
 * Reported as issue #1097: an SDK home whose `lib` directory contained `libproxy.so.1.0.0` alongside
 * the real application directories threw `NotDirectoryException` out of `HomePath.eachEbinPath`,
 * because the walk recursed into every entry without asking whether it was a directory. Fixed by
 * 57d9624043e1a7b8831443d3ad7087a1368657bb ("Filter directories of homePath lib to find apps",
 * 2018-08-11), which shipped in v8.0.0 and put a directory filter on both walks. The class has since
 * moved from `org.elixir_lang.jps.HomePath` to [SdkEbinPaths], and the filter survived the move as
 * `wslSafeIsDirectory` - but nothing has ever asserted it, so a refactor that dropped it would have
 * been silent.
 *
 * Both walks now catch `IOException` and log it, so without the filter the plain file no longer
 * throws - it is reported as an error and the walk moves on. Silence is therefore the contract:
 * every case captures logged errors and asserts there were none, which is what turns the filter's
 * absence into a failure rather than a log line.
 *
 * The reported crash is not platform-specific despite the issue title saying "on Linux": its
 * duplicate #1143 carries the same exception on macOS. A non-directory among the app directories is
 * all it takes, which is what these fixtures build.
 */
class SdkEbinPathsNonDirectoryTest : PlatformTestCase() {
    private lateinit var home: Path

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        home = Files.createTempDirectory("issue_1097_sdk_home")
    }

    @Throws(Exception::class)
    override fun tearDown() {
        try {
            if (::home.isInitialized) {
                Files.walk(home).sorted(Comparator.reverseOrder()).forEach(Files::delete)
            }
        } finally {
            super.tearDown()
        }
    }

    /**
     * Builds the shape #1097 reported: one real application directory with an `ebin`, and a plain
     * file as a sibling of it directly under `lib`.
     */
    private fun libWithNonDirectorySibling(): Path {
        val lib = Files.createDirectory(home.resolve("lib"))
        Files.createDirectories(lib.resolve("stdlib-3.5").resolve("ebin"))
        Files.createFile(lib.resolve("libproxy.so.1.0.0"))
        return lib
    }

    /**
     * `eachEbinPath` yields the real application's `ebin` and nothing for the plain file - and, most
     * of all, reports nothing. `NotDirectoryException` is what #1097 reported.
     */
    fun testEachEbinPathSkipsNonDirectoryUnderLib() {
        libWithNonDirectorySibling()

        val ebinPaths = eachEbinPathSilently()

        assertEquals(1, ebinPaths.size)
        assertEquals("ebin", ebinPaths[0].fileName.toString())
        assertEquals("stdlib-3.5", ebinPaths[0].parent.fileName.toString())
    }

    /**
     * `hasEbinPath` carries its own copy of the same filter, so it needs its own assertion: a fix
     * applied to one walk and not the other would otherwise pass.
     */
    fun testHasEbinPathSkipsNonDirectoryUnderLib() {
        libWithNonDirectorySibling()

        assertTrue(hasEbinPathSilently())
    }

    /**
     * The plain file on its own, with no real application beside it. Distinguishes "skipped the file"
     * from "found an app and stopped looking": the walk has to complete over a `lib` whose only entry
     * is a non-directory.
     */
    fun testLibContainingOnlyANonDirectoryYieldsNothing() {
        val lib = Files.createDirectory(home.resolve("lib"))
        Files.createFile(lib.resolve("libproxy.so.1.0.0"))

        assertEmpty(eachEbinPathSilently())
        assertFalse(hasEbinPathSilently())
    }

    private fun eachEbinPathSilently(): List<Path> {
        val ebinPaths = mutableListOf<Path>()
        val (_, errors) = captureLoggedErrors {
            SdkEbinPaths.eachEbinPath(home.toString()) { ebinPath -> ebinPaths.add(ebinPath) }
        }

        assertEquals("eachEbinPath must skip a non-directory under lib silently", emptyList<Any>(), errors)

        return ebinPaths
    }

    private fun hasEbinPathSilently(): Boolean {
        val (hasEbinPath, errors) = captureLoggedErrors { SdkEbinPaths.hasEbinPath(home.toString()) }

        assertEquals("hasEbinPath must skip a non-directory under lib silently", emptyList<Any>(), errors)

        return hasEbinPath
    }
}
