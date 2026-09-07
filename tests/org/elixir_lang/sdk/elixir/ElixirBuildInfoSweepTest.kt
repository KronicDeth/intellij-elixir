package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.BeamBytes
import com.intellij.openapi.util.io.FileUtil
import org.elixir_lang.sdk.wsl.wslCompat
import java.io.File
import java.util.concurrent.Callable

/**
 * SDK-wide coverage for [ElixirBuildInfo.elixirOtpRelease] and for the values overload of
 * [ElixirSdkValidation.detectOtpMismatch], against the Elixir/Erlang SDKs this run actually
 * resolved rather than a checked-in BEAM.
 *
 * The CI matrix runs every leg on a different pair, so across a matrix this sweeps the parse over
 * Elixir 1.13 to 1.20 and OTP 25 to 29 - breadth a single fixture cannot give. It also means no
 * `Elixir.System.beam` has to be committed to `testData`.
 *
 * `System.build_info/0` gained its `:otp_release` key in Elixir 1.6, so below that there is nothing
 * to read and declining is correct. Every version this plugin supports is above that floor.
 */
class ElixirBuildInfoSweepTest : PlatformTestCase() {

    // The cache is process-wide and these cases share a key, so without this the first to run
    // warms it for the rest and whether any of them reads a BEAM depends on method order.
    override fun setUp() {
        super.setUp()
        ElixirBuildInfo.clearCache()
    }

    fun testResolvedElixirSdkReportsItsOtpMajor() {
        val elixirHome = requireEnv("ELIXIR_LANG_ELIXIR_PATH")

        // elixirOtpRelease's contract is a canonical home, and every production caller passes one.
        val otpMajor = onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(wslCompat.canonicalizePath(elixirHome)) }

        assertNotNull(
            "Elixir.System.beam under $elixirHome (Elixir ${System.getenv("ELIXIR_VERSION")}) " +
                    "must report the OTP release it was built against",
            otpMajor,
        )
        assertTrue(
            "OTP major should be numeric, was '$otpMajor'",
            otpMajor!!.isNotEmpty() && otpMajor.all { it.isDigit() },
        )

        // mise names an install `<elixir>-otp-<major>` when it was built for a specific OTP. Where
        // the resolved home carries that, it is an independent statement of the answer. Matched
        // against the install directory's own name, never the whole path: the build's own
        // pairToken() produces `elixir-<v>-otp-<v>` directory names, so an ancestor could otherwise
        // supply an unrelated major.
        Regex("-otp-(\\d+)").find(File(elixirHome).name)?.groupValues?.get(1)?.let { fromPath ->
            assertEquals("Parsed OTP major contradicts the SDK home directory name", fromPath, otpMajor)
        }
    }

    /**
     * Pins the uncached path of the values overload: with no major supplied it must reach the BEAM
     * read and report what it finds.
     *
     * The Erlang side is synthetic and deliberately impossible, so the expected result is a non-null
     * pair on every CI leg. Comparing against the real Erlang SDK cannot do that - every configured
     * pair matches by construction, so both sides would be null and the assertion would hold with
     * the disk read deleted.
     */
    fun testDetectOtpMismatchValues_uncachedPathReadsTheBeam() {
        val elixirHome = requireEnv("ELIXIR_LANG_ELIXIR_PATH")

        val erlangHome = FileUtil.createTempDirectory("erlang_home_synthetic", null)
        try {
            val releaseDir = File(erlangHome, "releases/$IMPOSSIBLE_OTP_MAJOR")
            assertTrue(releaseDir.mkdirs())
            File(releaseDir, "OTP_VERSION").writeText("$IMPOSSIBLE_OTP_MAJOR.0\n")

            val elixirOtpMajor = onBackgroundThread {
                ElixirBuildInfo.elixirOtpRelease(wslCompat.canonicalizePath(elixirHome))
            }
            assertNotNull("Elixir OTP major not detected for $elixirHome", elixirOtpMajor)

            // Computing the expected value above warmed the cache under the very key the call below
            // uses, so without this it would be served from memory and never reach the BEAM.
            ElixirBuildInfo.clearCache()
            assertFalse(
                "the synthetic Erlang major must not collide with the real Elixir one",
                elixirOtpMajor == IMPOSSIBLE_OTP_MAJOR,
            )

            assertEquals(
                "with no cached major the Elixir side must be resolved, not skipped",
                elixirOtpMajor to IMPOSSIBLE_OTP_MAJOR,
                onBackgroundThread {
                    ElixirSdkValidation.detectOtpMismatch(
                        elixirHome = elixirHome,
                        cachedElixirOtpMajor = null,
                        erlangHome = erlangHome.path,
                    )
                },
            )
        } finally {
            FileUtil.delete(erlangHome)
        }
    }

    /**
     * The settings dialog re-reads the same BEAM on every Erlang SDK selection, so the result is
     * cached on `"<home>@<mtime>"`. Corrupting the file while holding its mtime shows the second
     * call never touched disk; bumping the mtime shows the key is not just the path.
     */
    fun testElixirOtpRelease_cachesOnHomeAndMtime() {
        val source = File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam")
        assertTrue("resolved SDK has no Elixir.System.beam at $source", source.isFile)

        val home = FileUtil.createTempDirectory("elixir_home_cache", null)
        try {
            val beam = File(home, "lib/elixir/ebin/Elixir.System.beam")
            assertTrue(beam.parentFile.mkdirs())
            FileUtil.copy(source, beam)

            val first = onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) }
            assertNotNull("the copied BEAM must parse", first)

            // Same mtime, unparseable content: only a cache hit can still answer.
            val mtime = beam.lastModified()
            beam.writeBytes(ByteArray(64))
            assertTrue("could not restore mtime", beam.setLastModified(mtime))

            assertEquals(
                "a second read of the same home and mtime must come from the cache",
                first,
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )

            // Same path, newer mtime: the key must change and the garbage must be seen. The read
            // is still wrapped so a regression to error-level reporting fails here rather than
            // somewhere unrelated.
            assertTrue("could not bump mtime", beam.setLastModified(mtime + 2_000))
            val (afterBump, errors) = captureLoggedErrors {
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) }
            }
            assertNull("a changed mtime must invalidate, not serve the previous major", afterBump)
            assertEquals(
                "a file the SDK home happens to hold is not an internal error, got $errors",
                emptyList<String>(),
                errors,
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /**
     * An SDK that appears at a home already asked about must be reported.
     *
     * This does NOT pin the `mtime == 0L` early return: without it the absent read still returns
     * null, and the miss is cached under `<home>@0`, which nothing consults once a real file gives
     * the home a real mtime. So no current test fails on removing it - said here so the next reader
     * does not go looking for one.
     *
     * That guard is not purely a fast path, though. `File.lastModified()` returns `0L` for an absent
     * file, for an I/O error, and for one genuinely stamped at the epoch, so it declines where the
     * `exists()` check it replaced would have parsed. Deliberate, to mirror
     * [org.elixir_lang.sdk.erlang.ErlangVersionDetector], and untested in that third case.
     */
    fun testElixirOtpRelease_reportsAnSdkThatAppearsLater() {
        val home = FileUtil.createTempDirectory("elixir_home_absent", null)
        try {
            val ebin = File(home, "lib/elixir/ebin")
            assertTrue(ebin.mkdirs())

            assertNull(
                "no BEAM yet, so nothing to report",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )

            FileUtil.copy(
                File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam"),
                File(ebin, "Elixir.System.beam"),
            )

            assertNotNull(
                "the earlier absence must not have been cached against this home",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /**
     * The half-written shape specifically: a file whose `FOR1..BEAM` header landed but whose chunk
     * list did not. `BeamReader.readResult` opens that as a BEAM with no chunks at all, so it is the atom
     * conversion - not the not-a-BEAM one - that has to reject it.
     *
     * The code conversion beside it is the same defence for a file that loses only its code chunk.
     * Nothing here constructs one: the atom chunk goes first, so any truncation short enough to
     * drop the code chunk drops that too, and no BEAM on disk is missing either.
     */
    fun testElixirOtpRelease_doesNotCacheATruncatedBeam() {
        val source = File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam")
        val home = FileUtil.createTempDirectory("elixir_home_truncated", null)
        try {
            val beam = File(home, "lib/elixir/ebin/Elixir.System.beam")
            assertTrue(beam.parentFile.mkdirs())

            beam.writeBytes(source.readBytes().copyOf(12))
            val mtime = beam.lastModified()

            // Unwrapped on purpose: LoggedErrorProcessor rethrows by default, so an error
            // logged on this path fails the test rather than being collected and ignored.
            val truncated = onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) }
            assertNull("a BEAM with no chunks cannot report a major", truncated)

            // Complete the file at the SAME mtime, so the key is unchanged and only the failure
            // policy can decide the outcome.
            FileUtil.copy(source, beam)
            assertTrue("could not align mtime", beam.setLastModified(mtime))

            assertNotNull(
                "the truncated read must not have been cached under this key",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /**
     * A BEAM that fails to PARSE must not be cached either. `BeamReader.readResult` returns null for
     * bytes that are not a BEAM rather than throwing, so without the conversion in `readOtpRelease` a
     * half-written file caches as "no otp_release" - indistinguishable from Elixir 1.5, and
     * surviving a repair, since restoring the bytes need not move the mtime the key carries.
     */
    fun testElixirOtpRelease_doesNotCacheAParseFailure() {
        val source = File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam")
        val home = FileUtil.createTempDirectory("elixir_home_corrupt", null)
        try {
            val beam = File(home, "lib/elixir/ebin/Elixir.System.beam")
            assertTrue(beam.parentFile.mkdirs())

            // Readable bytes that are not a BEAM: readBytes succeeds and BeamReader.readResult returns null.
            beam.writeBytes(ByteArray(64))
            val mtime = beam.lastModified()

            // Unwrapped on purpose: LoggedErrorProcessor rethrows by default, so an error
            // logged on this path fails the test rather than being collected and ignored.
            val corrupt = onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) }
            assertNull("a BEAM that does not parse cannot report a major", corrupt)

            // Repair in place at the SAME mtime, so the cache key is unchanged and only the
            // failure policy can decide the outcome.
            FileUtil.copy(source, beam)
            assertTrue("could not align mtime", beam.setLastModified(mtime))

            assertNotNull(
                "the parse failure must not have been cached under this key",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /** Cut inside the literal table, after the atom and code chunks it needs have landed. */
    fun testElixirOtpRelease_doesNotCacheABeamCutOffInsideItsLiterals() {
        val source = File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam")
        val home = FileUtil.createTempDirectory("elixir_home_cut_literals", null)
        try {
            val beam = File(home, "lib/elixir/ebin/Elixir.System.beam")
            assertTrue(beam.parentFile.mkdirs())

            val original = source.readBytes()
            val litT = BeamBytes.chunks(original).first { it.id == "LitT" }
            beam.writeBytes(original.copyOf(litT.data + minOf(100, litT.size / 2)))
            val mtime = beam.lastModified()

            val cut = onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) }
            assertNull("a module cut off inside its literals cannot report a major", cut)

            FileUtil.copy(source, beam)
            assertTrue("could not align mtime", beam.setLastModified(mtime))

            assertNotNull(
                "the cut-off file must not have been cached under this key",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /** Literals that do not decode are corruption, not a module without `otp_release`. */
    fun testElixirOtpRelease_doesNotCacheACorruptLiteralTable() {
        val source = File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam")
        val home = FileUtil.createTempDirectory("elixir_home_corrupt_literals", null)
        try {
            val beam = File(home, "lib/elixir/ebin/Elixir.System.beam")
            assertTrue(beam.parentFile.mkdirs())

            beam.writeBytes(BeamBytes.withUndecodableLiteralTable(source.readBytes()))
            val mtime = beam.lastModified()

            val corrupt = onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) }
            assertNull("a module whose literals do not decode cannot report a major", corrupt)

            FileUtil.copy(source, beam)
            assertTrue("could not align mtime", beam.setLastModified(mtime))

            assertNotNull(
                "the corrupt literal table must not have been cached under this key",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /**
     * A read that FAILED must not be cached. mtime does not change when a read fails, so caching
     * one would poison the key until the IDE restarts - the SDK would never report a major again.
     */
    fun testElixirOtpRelease_doesNotCacheAReadFailure() {
        val source = File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam")
        val home = FileUtil.createTempDirectory("elixir_home_failure", null)
        try {
            val beam = File(home, "lib/elixir/ebin/Elixir.System.beam")
            assertTrue(beam.parentFile.mkdirs())

            // A directory where the BEAM should be: readBytes throws, which is a failed read rather
            // than a parsed absence. Give it the mtime the real file will have, so the retry below
            // uses the same cache key and only the failure policy can decide the outcome.
            assertTrue(beam.mkdir())
            val mtime = beam.lastModified()

            // Unwrapped on purpose: LoggedErrorProcessor rethrows by default, so an error
            // logged on this path fails the test rather than being collected and ignored.
            val first = onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) }
            assertNull("an unreadable BEAM cannot report a major", first)

            assertTrue("could not replace the directory with the real BEAM", beam.delete())
            FileUtil.copy(source, beam)
            assertTrue("could not align mtime", beam.setLastModified(mtime))

            assertNotNull(
                "the earlier failure must not have been cached under this key",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /** No OTP release will ever carry this major, so a mismatch is guaranteed. */
    private val IMPOSSIBLE_OTP_MAJOR = "9001"

    /**
     * A chunk the parsers cannot name must decline, not throw. `Operation.from` throws on any opcode
     * above the 191 this decompiler models, so the first OTP release that adds one would otherwise
     * turn every `Elixir.System.beam` into an error report - once per combo box selection, since a
     * failure is deliberately not cached.
     */
    fun testElixirOtpRelease_declinesOnAnOpcodeItCannotName() {
        val source = File(requireEnv("ELIXIR_LANG_ELIXIR_PATH"), "lib/elixir/ebin/Elixir.System.beam")
        val home = FileUtil.createTempDirectory("elixir_home_future_opcode", null)
        try {
            val beam = File(home, "lib/elixir/ebin/Elixir.System.beam")
            assertTrue(beam.parentFile.mkdirs())
            beam.writeBytes(withFirstInstructionOpcode(source.readBytes(), opcode = 200))

            assertNull(
                "an opcode we cannot name is a BEAM we cannot read, not a crash",
                onBackgroundThread { ElixirBuildInfo.elixirOtpRelease(home.path) },
            )
        } finally {
            FileUtil.delete(home)
        }
    }

    /**
     * Overwrites the first instruction byte of the `Code` chunk. Its data starts with five unsigned
     * ints - sub-size, version, max opcode, label count, function count - and the instructions follow.
     */
    private fun withFirstInstructionOpcode(content: ByteArray, opcode: Int): ByteArray {
        val patched = content.copyOf()
        var offset = 12

        while (offset + 8 <= patched.size) {
            val id = String(patched, offset, 4, Charsets.ISO_8859_1)
            val size = (0 until 4).fold(0) { acc, i -> (acc shl 8) or (patched[offset + 4 + i].toInt() and 0xFF) }
            val data = offset + 8

            if (id == "Code") {
                patched[data + 20] = opcode.toByte()
                return patched
            }

            offset = data + size + ((4 - size % 4) % 4)
        }

        throw AssertionError("no Code chunk in the BEAM under test")
    }

    private fun requireEnv(name: String): String =
        System.getenv(name).also { assertNotNull("$name not set for the test JVM", it) }!!

    private fun <T> onBackgroundThread(block: () -> T): T =
        ApplicationManager.getApplication().executeOnPooledThread(Callable { block() }).get()
}
