package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType
import java.io.File
import java.util.concurrent.Callable

/**
 * Tests for [ElixirSdkValidation.detectOtpMismatch].
 *
 * The overloads exercised here, and the trap in each:
 *  - `detectOtpMismatch(elixirSdk, erlangSdk)` - the explicit-pairing comparison. The Elixir OTP
 *    major is supplied via [ElixirBuildInfo.ELIXIR_OTP_MAJOR_KEY] user data (no `Elixir.System.beam`
 *    read); the Erlang OTP major is read from a real `<home>/releases/<N>/OTP_VERSION` file (see
 *    [org.elixir_lang.sdk.erlang.ErlangVersionDetector.detectRelease]).
 *  - `detectOtpMismatch(sdk)` - the single-argument overload used by the refresh actions. It resolves
 *    the paired Erlang SDK via [SdkAdditionalData.getErlangSdk] (which needs the Erlang SDK registered
 *    in the [ProjectJdkTable]) and honours the per-SDK suppress flag. It is `@RequiresBackgroundThread`
 *    and takes its own short internal read action for that resolution - callers must NOT wrap the
 *    whole call in an outer [ReadAction], which would hold the read lock across its file I/O too.
 *  - `detectOtpMismatch(elixirHome, cachedElixirOtpMajor, erlangHome)` - the values overload the
 *    settings dialog uses, taking everything it needs as data so it holds no `Sdk` across threads.
 *
 * Every call is dispatched off the EDT to satisfy the `@RequiresBackgroundThread` contract.
 */
class OtpMismatchTest : PlatformTestCase() {

    private val createdSdks = mutableListOf<Sdk>()
    private val tempDirs = mutableListOf<File>()

    override fun tearDown() = runAll(
        {
            WriteAction.run<Throwable> {
                createdSdks.forEach { ProjectJdkTable.getInstance().removeJdk(it) }
            }
        },
        { tempDirs.forEach { FileUtil.delete(it) } },
        { createdSdks.clear() },
        { tempDirs.clear() },
        { super.tearDown() },
    )

    // -------------------------------------------------------------------------
    // detectOtpMismatch(elixirSdk, erlangSdk) - explicit pairing
    // -------------------------------------------------------------------------

    fun testDetectOtpMismatch_returnsNullWhenOtpMajorsMatch() {
        // Erlang OTP_VERSION is "26.2.5" but its major is the releases/ dir name "26"; pins that the
        // comparison is on major, not the full version string.
        val elixirSdk = newElixirSdk(otpMajor = "26")
        val erlangSdk = newErlangSdk(createErlangHome(major = "26", otpVersion = "26.2.5"))

        assertNull(
            "Matching OTP majors should not be reported as a mismatch",
            detectOnBackgroundThread(elixirSdk, erlangSdk),
        )
    }

    fun testDetectOtpMismatch_returnsMajorsWhenTheyDiffer() {
        val elixirSdk = newElixirSdk(otpMajor = "27")
        val erlangSdk = newErlangSdk(createErlangHome(major = "26", otpVersion = "26.2.5"))

        assertEquals(
            "Differing OTP majors should be reported as (elixirMajor, erlangMajor)",
            "27" to "26",
            detectOnBackgroundThread(elixirSdk, erlangSdk),
        )
    }

    fun testDetectOtpMismatch_returnsNullWhenErlangReleasesDirMissing() {
        val elixirSdk = newElixirSdk(otpMajor = "26")
        // Real directory but no releases/<N>/OTP_VERSION → detectRelease returns null (partial install).
        val emptyHome = FileUtil.createTempDirectory("erlang_home_empty", null).also { tempDirs.add(it) }
        val erlangSdk = newErlangSdk(emptyHome.path)

        assertNull(
            "An Erlang home without releases/<N>/OTP_VERSION must return null",
            detectOnBackgroundThread(elixirSdk, erlangSdk),
        )
    }

    // -------------------------------------------------------------------------
    // detectOtpMismatch(sdk) - resolves the paired Erlang SDK + suppress flag
    // -------------------------------------------------------------------------

    fun testDetectOtpMismatch_singleArg_reportsMismatchForPairedErlangSdk() {
        val erlangSdk = registerErlangSdk(createErlangHome(major = "25", otpVersion = "25.3"))
        val elixirSdk = newElixirSdkPairedWith(erlangSdk, otpMajor = "27")

        assertEquals(
            "Should resolve the paired Erlang SDK and report the mismatch",
            "27" to "25",
            detectSingleArgOnBackgroundThread(elixirSdk),
        )
    }

    fun testDetectOtpMismatch_singleArg_returnsNullWhenWarningSuppressed() {
        val erlangSdk = registerErlangSdk(createErlangHome(major = "25", otpVersion = "25.3"))
        // Same mismatching pairing as above, but the user has acknowledged/suppressed the warning.
        val elixirSdk = newElixirSdkPairedWith(erlangSdk, otpMajor = "27", suppressWarning = true)

        assertNull(
            "A suppressed OTP-mismatch warning must short-circuit to null even when majors differ",
            detectSingleArgOnBackgroundThread(elixirSdk),
        )
    }

    // -------------------------------------------------------------------------
    // detectOtpMismatch(elixirHome, cachedElixirOtpMajor, erlangHome) - values overload
    //
    // The settings dialog reads these three values on the EDT and passes them here, rather than
    // handing the background thread the Sdk objects, which SdkEditor mutates under a write action.
    //
    // These two cases differ only in cachedElixirOtpMajor and are the pair that discriminates:
    // together they pin that a supplied major is used as-is and that its absence is not silently
    // substituted: the first fails an implementation that ignores the parameter or substitutes a
    // different major, the second one that invents a value when none is available.
    //
    // Neither pins the on-disk read itself - both homes lack a BEAM, so a build that never read one
    // would satisfy both. ElixirBuildInfoSweepTest covers that against the resolved SDK.
    //
    // Neither needs an Elixir SDK, so both run on every leg. The uncached path reaching a
    // BEAM and producing a major is covered by ElixirBuildInfoSweepTest against the SDK the
    // run resolved, which spans the whole CI matrix instead of one checked-in beam.
    // -------------------------------------------------------------------------

    fun testDetectOtpMismatchValues_usesSuppliedMajorWithoutReadingDisk() {
        val elixirHome = createElixirHomeWithoutBeam()
        val erlangHome = createErlangHome(major = "26", otpVersion = "26.2.5")

        assertEquals(
            "A supplied Elixir OTP major must be used as-is, with no Elixir.System.beam read",
            "27" to "26",
            detectValuesOnBackgroundThread(elixirHome, "27", erlangHome),
        )
    }

    fun testDetectOtpMismatchValues_fallsBackToDiskWhenMajorNotCached() {
        val elixirHome = createElixirHomeWithoutBeam()
        val erlangHome = createErlangHome(major = "26", otpVersion = "26.2.5")

        // Same homes as above. With no cached major the Elixir side must come from
        // lib/elixir/ebin/Elixir.System.beam, which this home deliberately lacks, so the whole
        // comparison declines rather than reporting a mismatch against a guessed major.
        assertNull(
            "With no cached major and no Elixir.System.beam, the comparison must return null",
            detectValuesOnBackgroundThread(elixirHome, null, erlangHome),
        )
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun detectOnBackgroundThread(elixirSdk: Sdk, erlangSdk: Sdk): Pair<String, String>? =
        ApplicationManager.getApplication()
            .executeOnPooledThread(Callable { ElixirSdkValidation.detectOtpMismatch(elixirSdk, erlangSdk) })
            .get()

    /**
     * Runs the single-arg overload on a pooled thread, without an outer read action - it manages
     * its own short internal read action for the SDK resolution and does its file I/O unlocked.
     */
    private fun detectSingleArgOnBackgroundThread(elixirSdk: Sdk): Pair<String, String>? =
        ApplicationManager.getApplication()
            .executeOnPooledThread(Callable { ElixirSdkValidation.detectOtpMismatch(elixirSdk) })
            .get()

    private fun detectValuesOnBackgroundThread(
        elixirHome: String,
        cachedElixirOtpMajor: String?,
        erlangHome: String,
    ): Pair<String, String>? =
        ApplicationManager.getApplication()
            .executeOnPooledThread(
                Callable {
                    ElixirSdkValidation.detectOtpMismatch(
                        elixirHome = elixirHome,
                        cachedElixirOtpMajor = cachedElixirOtpMajor,
                        erlangHome = erlangHome,
                    )
                },
            )
            .get()

    /**
     * Creates a real Elixir home with the `lib/elixir/ebin` layout present but no
     * `Elixir.System.beam`, so the uncached path reaches the read and declines.
     */
    private fun createElixirHomeWithoutBeam(): String {
        val home = FileUtil.createTempDirectory("elixir_home_no_beam", null).also { tempDirs.add(it) }
        val ebin = File(home, "lib/elixir/ebin")
        assertTrue("Failed to create $ebin", ebin.mkdirs())
        return home.path
    }

    private fun newElixirSdk(otpMajor: String): Sdk {
        val sdk = ProjectJdkImpl("Test Elixir SDK", Type.instance, "/fake/elixir/1.16", "")
        // Home path value is irrelevant: a non-null path passes the guard and ELIXIR_OTP_MAJOR_KEY
        // short-circuits the on-disk version read.
        sdk.putUserData(ElixirBuildInfo.ELIXIR_OTP_MAJOR_KEY, otpMajor)
        createdSdks.add(sdk)
        return sdk
    }

    private fun newErlangSdk(homePath: String): Sdk {
        val sdk = ProjectJdkImpl("Test Erlang SDK", ErlangSdkType(), homePath, "")
        createdSdks.add(sdk)
        return sdk
    }

    /** Creates and registers an Erlang SDK so [SdkAdditionalData.getErlangSdk] can resolve it by name/home path. */
    private fun registerErlangSdk(homePath: String): Sdk {
        val sdk = ProjectJdkImpl("Paired Erlang SDK", ErlangSdkType(), homePath, "")
        WriteAction.run<Throwable> { ProjectJdkTable.getInstance().addJdk(sdk, testRootDisposable) }
        return sdk
    }

    private fun newElixirSdkPairedWith(erlangSdk: Sdk, otpMajor: String, suppressWarning: Boolean = false): Sdk {
        val sdk = ProjectJdkImpl("Paired Elixir SDK", Type.instance, "/fake/elixir/1.16", "")
        val data = SdkAdditionalData(erlangSdk, sdk).apply { setSuppressOtpMismatchWarning(suppressWarning) }
        WriteAction.run<Throwable> { sdk.sdkModificator.apply { sdkAdditionalData = data; commitChanges() } }
        sdk.putUserData(ElixirBuildInfo.ELIXIR_OTP_MAJOR_KEY, otpMajor)
        createdSdks.add(sdk)
        return sdk
    }

    /** Creates a real Erlang home directory containing `releases/<major>/OTP_VERSION`. */
    private fun createErlangHome(major: String, otpVersion: String): String {
        val home = FileUtil.createTempDirectory("erlang_home", null).also { tempDirs.add(it) }
        val releaseDir = File(home, "releases/$major")
        assertTrue("Failed to create $releaseDir", releaseDir.mkdirs())
        File(releaseDir, "OTP_VERSION").writeText("$otpVersion\n")
        return home.path
    }
}
