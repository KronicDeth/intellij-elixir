package org.elixir_lang.sdk.erlang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.registerOrReplaceServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.wsl.MockWslCompatService
import org.elixir_lang.sdk.wsl.WslCompatService
import java.io.File

/**
 * Tests for ErlangVersionDetector - file-based OTP version detection from
 * `<sdkHome>/releases/<N>/OTP_VERSION`.
 */
class ErlangVersionDetectorTest : PlatformTestCase() {

    private lateinit var tempSdkHome: File

    override fun setUp() {
        super.setUp()

        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            WslCompatService::class.java,
            MockWslCompatService(),
            testRootDisposable,
        )

        tempSdkHome = FileUtil.createTempDirectory("erlang_sdk_home", null, true)
    }

    fun testDetectRelease_standardInstall() {
        // Simulate: releases/26/OTP_VERSION contains "26.2.5.6"
        val releasesDir = File(tempSdkHome, "releases/26")
        releasesDir.mkdirs()
        File(releasesDir, "OTP_VERSION").writeText("26.2.5.6\n")

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }

        assertNotNull("Should detect release from OTP_VERSION", release)
        assertEquals("26", release!!.otpMajor)
        assertEquals("26.2.5.6", release.otpVersion)
    }

    fun testDetectRelease_multipleReleaseDirs_picksHighest() {
        // Simulate multiple release directories (e.g. OTP upgrade in place)
        File(tempSdkHome, "releases/25").mkdirs()
        File(tempSdkHome, "releases/25/OTP_VERSION").writeText("25.3.2.12\n")
        File(tempSdkHome, "releases/26").mkdirs()
        File(tempSdkHome, "releases/26/OTP_VERSION").writeText("26.2.5.6\n")

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }

        assertNotNull(release)
        assertEquals("26", release!!.otpMajor)
        assertEquals("26.2.5.6", release.otpVersion)
    }

    fun testDetectRelease_missingReleasesDir() {
        // No releases/ directory at all
        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }
        assertNull("Should return null when releases/ is missing", release)
    }

    fun testDetectRelease_emptyReleasesDir() {
        File(tempSdkHome, "releases").mkdirs()
        // No numeric subdirectories

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }
        assertNull("Should return null when releases/ has no numeric subdirectories", release)
    }

    fun testDetectRelease_missingOtpVersionFile() {
        // Directory exists but no OTP_VERSION file
        File(tempSdkHome, "releases/26").mkdirs()

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }
        assertNull("Should return null when OTP_VERSION file is missing", release)
    }

    fun testDetectRelease_emptyOtpVersionFile() {
        File(tempSdkHome, "releases/26").mkdirs()
        File(tempSdkHome, "releases/26/OTP_VERSION").writeText("   \n")

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }
        assertNull("Should return null when OTP_VERSION is blank", release)
    }

    fun testDetectRelease_ignoresNonNumericDirs() {
        // Non-numeric directories should be ignored
        File(tempSdkHome, "releases/RELEASES").mkdirs()
        File(tempSdkHome, "releases/start_erl").mkdirs()
        File(tempSdkHome, "releases/26").mkdirs()
        File(tempSdkHome, "releases/26/OTP_VERSION").writeText("26.0\n")

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }

        assertNotNull(release)
        assertEquals("26", release!!.otpMajor)
        assertEquals("26.0", release.otpVersion)
    }

    fun testDetectRelease_trimsWhitespace() {
        File(tempSdkHome, "releases/28").mkdirs()
        File(tempSdkHome, "releases/28/OTP_VERSION").writeText("  28.0.3  \n")

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }

        assertNotNull(release)
        assertEquals("28.0.3", release!!.otpVersion)
    }

    fun testDetectRelease_cachesResult() {
        File(tempSdkHome, "releases/26").mkdirs()
        File(tempSdkHome, "releases/26/OTP_VERSION").writeText("26.2.5.6\n")

        val release1 = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }
        val release2 = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }

        assertNotNull(release1)
        // Same object reference means it came from the cache, not a fresh file read.
        assertSame(release1, release2)
    }

    fun testDetectRelease_patchedInPlace_stripsAsterisks() {
        // otp_patch_apply appends "**" to OTP_VERSION when patching in place
        File(tempSdkHome, "releases/26").mkdirs()
        File(tempSdkHome, "releases/26/OTP_VERSION").writeText("26.2.5.1**\n")

        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease(tempSdkHome.absolutePath)
        }

        assertNotNull("Should detect release from patched OTP_VERSION", release)
        assertEquals("26", release!!.otpMajor)
        assertEquals("26.2.5.1", release.otpVersion)
    }

    fun testDetectRelease_nonExistentPath() {
        val release = runBlocking(Dispatchers.IO) {
            ErlangVersionDetector.detectRelease("/nonexistent/path/erlang")
        }
        assertNull("Should return null for non-existent path", release)
    }
}
