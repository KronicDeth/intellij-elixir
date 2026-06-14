package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.registerOrReplaceServiceInstance
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.wsl.MockWslCompatService
import org.elixir_lang.sdk.wsl.WslCompatService
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import java.io.File
import java.nio.file.Files

/**
 * Tests for Elixir SDK Type naming methods.
 *
 * Version detection now uses `elixir.app` file parsing (no subprocess, no directory-name parsing).
 * Tests verify source detection via [Type.suggestSdkNameForHome] with an explicit `resolvedVersion`,
 * and end-to-end version reading via a temp directory containing a real `elixir.app` file.
 */
class TypeNamingTest : PlatformTestCase() {

    private lateinit var elixirType: Type

    override fun setUp() {
        super.setUp()
        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            WslCompatService::class.java,
            MockWslCompatService(),
            testRootDisposable,
        )
        elixirType = Type.instance
    }

    // ---------------------------------------------------------------
    // suggestSdkNameForHome (static, resolvedVersion bypasses file I/O)
    // These tests verify source detection without needing a real .app file.
    // ---------------------------------------------------------------

    fun testSuggestSdkNameForHome_miseElixir() {
        val name = Type.suggestSdkNameForHome(
            "/Users/josh/.local/share/mise/installs/elixir/1.15.7",
            "1.15.7",
        )
        assertEquals("mise Elixir 1.15.7", name)
    }

    fun testSuggestSdkNameForHome_miseElixirWithOtpSuffix() {
        // resolvedVersion from .app is the bare version; OTP suffix is stripped on the mise side
        val name = Type.suggestSdkNameForHome(
            "/Users/josh/.local/share/mise/installs/elixir/1.15.7-otp-26",
            "1.15.7",
        )
        assertEquals("mise Elixir 1.15.7", name)
    }

    fun testSuggestSdkNameForHome_asdfElixir() {
        val name = Type.suggestSdkNameForHome(
            "/Users/josh/.asdf/installs/elixir/1.14.0",
            "1.14.0",
        )
        assertEquals("asdf Elixir 1.14.0", name)
    }

    fun testSuggestSdkNameForHome_unknownSource() {
        val name = Type.suggestSdkNameForHome("/custom/path/elixir", "1.15.0")
        assertEquals("Elixir 1.15.0", name)
    }

    fun testSuggestSdkNameForHome_variantQualified() {
        val name = Type.suggestSdkNameForHome(
            "/Users/josh/.local/share/mise/installs/elixir/1.15.7-otp-26",
            "1.15.7",
            "26",
            "26.2.5",
        )
        assertEquals("mise Elixir 1.15.7-otp-26 (Erlang 26.2.5)", name)
    }

    fun testSuggestSdkNameForHome_noOtpMajorFallsBackToTwoArg() {
        val name = Type.suggestSdkNameForHome(
            "/Users/josh/.local/share/mise/installs/elixir/1.15.7",
            "1.15.7",
            null,
            null,
        )
        assertEquals("mise Elixir 1.15.7", name)
    }

    // ---------------------------------------------------------------
    // suggestSdkName (instance, reads .app file or falls back to "at <path>")
    // ---------------------------------------------------------------

    fun testSuggestSdkName_withRealAppFile() {
        val sdkHome = createElixirSdkHome("1.15.7")
        VfsRootAccess.allowRootAccess(testRootDisposable, sdkHome)

        val name = elixirType.suggestSdkName(null, sdkHome)
        assertTrue("Name should contain version; was: $name", name.contains("1.15.7"))
        assertTrue("Name should contain 'Elixir'; was: $name", name.contains("Elixir"))
    }

    fun testSuggestSdkName_fallbackWhenNoAppFile() {
        val fakePath = "/not/a/real/elixir/install"
        val name = elixirType.suggestSdkName(null, fakePath)
        assertTrue("Name should show path when version unreadable; was: $name", name.contains("at"))
        assertEquals(
            "Should contain exactly one 'Elixir'; was: $name",
            1,
            name.split("Elixir").size - 1,
        )
    }

    fun testSuggestSdkName_noDuplicateElixir_withAppFile() {
        val sdkHome = createElixirSdkHome("1.17.4")
        VfsRootAccess.allowRootAccess(testRootDisposable, sdkHome)

        val name = elixirType.suggestSdkName(null, sdkHome)
        assertEquals("Should contain exactly one 'Elixir'; was: $name", 1, name.split("Elixir").size - 1)
    }

    fun testSuggestSdkName_doesNotThrowWhenCanonicalizeFails() {
        val throwingService = spy(MockWslCompatService())
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            WslCompatService::class.java,
            throwingService,
            testRootDisposable,
        )

        val name = elixirType.suggestSdkName(null, "/not/a/real/elixir/install")

        assertTrue("Name should still be produced even if canonicalization fails; was: $name", name.contains("Elixir"))
        verify(throwingService, atLeastOnce()).toRealPath(anyString())
    }

    // ---------------------------------------------------------------
    // getVersionString
    // ---------------------------------------------------------------

    fun testGetVersionString_withRealAppFile() {
        val sdkHome = createElixirSdkHome("1.15.7")
        VfsRootAccess.allowRootAccess(testRootDisposable, sdkHome)

        val version = elixirType.getVersionString(sdkHome)
        assertNotNull(version)
        assertTrue("Version string should contain 1.15.7; was: $version", version.contains("1.15.7"))
        assertTrue("Version string should contain 'Elixir'; was: $version", version.contains("Elixir"))
    }

    fun testGetVersionString_unknownWhenNoAppFile() {
        val version = elixirType.getVersionString("/not/a/real/path/elixir")
        assertTrue("Fallback should still contain 'Elixir'; was: $version", version.contains("Elixir"))
    }

    fun testGetVersionString_includesElixir() {
        // Even the fallback path must contain "Elixir" for display correctness
        val version = elixirType.getVersionString("/Users/josh/.local/share/mise/installs/elixir/1.15.7")
        assertTrue("Version string should contain 'Elixir'; was: $version", version.contains("Elixir"))
    }

    fun testGetVersionString_doesNotThrowWhenCanonicalizeFails() {
        val throwingService = spy(MockWslCompatService())
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            WslCompatService::class.java,
            throwingService,
            testRootDisposable,
        )

        val version = elixirType.getVersionString("/not/a/real/path/elixir")

        assertTrue("Version string should still be produced when canonicalization fails; was: $version", version.contains("Elixir"))
        verify(throwingService, atLeastOnce()).toRealPath(anyString())
    }

    fun testGetVersionString_withRealAppFile_doesNotThrowWhenCanonicalizeFails() {
        val sdkHome = createElixirSdkHome("1.15.7")
        VfsRootAccess.allowRootAccess(testRootDisposable, sdkHome)

        val throwingService = spy(MockWslCompatService())
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            WslCompatService::class.java,
            throwingService,
            testRootDisposable,
        )

        val version = elixirType.getVersionString(sdkHome)

        assertTrue("Version string should still be produced when canonicalization fails; was: $version", version.contains("Elixir"))
        assertTrue("Version string should still contain parsed version when canonicalization fails; was: $version", version.contains("1.15.7"))
        verify(throwingService, atLeastOnce()).toRealPath(anyString())
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /**
     * Creates a temporary Elixir SDK home directory containing a minimal `elixir.app`.
     *
     * Structure: `<tmpDir>/lib/elixir/ebin/elixir.app`
     *
     * @return the path to the SDK home root (the directory containing `lib/`).
     */
    private fun createElixirSdkHome(version: String): String {
        val tmpRoot = Files.createTempDirectory("elixir-sdk-test-").toFile()
        tmpRoot.deleteOnExit()
        val ebinDir = File(tmpRoot, "lib/elixir/ebin")
        ebinDir.mkdirs()
        File(ebinDir, "elixir.app").writeText(
            """
            {application, elixir, [
              {description, "elixir"},
              {vsn, "$version"},
              {modules, []},
              {registered, []},
              {applications, [kernel, stdlib]}
            ]}.
            """.trimIndent(),
        )
        return tmpRoot.path
    }
}
