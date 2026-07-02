package org.elixir_lang.sdk.wsl

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.util.system.OS
import org.elixir_lang.PlatformTestCase
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.*

class WslCompatServiceExtensionsTest : PlatformTestCase() {
    private val wslCompatMock = MockWslCompatService()

    fun testConvertLinuxPathToWindowsUncFromContext_convertsForWslContext() {
        val contextPath = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project"
        val linuxPath = "/home/testuser/.local/share/mise/installs/elixir/1.15.7"

        val converted = wslCompatMock.convertLinuxPathToWindowsUncFromContext(contextPath, linuxPath)

        assertEquals(
            "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7",
            converted,
        )
    }

    fun testConvertLinuxPathToWindowsUncFromContext_returnsNullForNonWslContext() {
        val converted = wslCompatMock.convertLinuxPathToWindowsUncFromContext(
            "C:\\Users\\steve\\IdeaProjects\\intellij-elixir",
            "/home/testuser/.local/share/mise/installs/elixir/1.15.7",
        )

        assertNull(converted)
    }

    fun testConvertLinuxPathToWindowsUncFromContext_returnsNullForNonLinuxPath() {
        val converted = wslCompatMock.convertLinuxPathToWindowsUncFromContext(
            "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project",
            "C:\\Program Files\\Elixir",
        )

        assertNull(converted)
    }

    fun testConvertLinuxPathToWindowsUncFromContext_returnsNullWhenDistributionNotResolvable() {
        val serviceWithUnknownDistribution = MockWslCompatService(distributionOverride = { null })

        val converted = serviceWithUnknownDistribution.convertLinuxPathToWindowsUncFromContext(
            "\\\\wsl.localhost\\Unknown\\home\\testuser\\project",
            "/home/testuser/.local/share/mise/installs/elixir/1.15.7",
        )

        assertNull(converted)
    }

    fun testConvertLinuxPathToWindowsUncFromContext_returnsNullWhenConversionFails() {
        val distro = Mockito.mock(WSLDistribution::class.java)
        `when`(distro.msId).thenReturn("Ubuntu-24.04")

        val serviceWithFailingConversion = MockWslCompatService(
            distributionOverride = { distro },
            conversionOverride = { _, _ -> null },
        )

        val converted = serviceWithFailingConversion.convertLinuxPathToWindowsUncFromContext(
            "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project",
            "/home/testuser/.local/share/mise/installs/elixir/1.15.7",
        )

        assertNull(converted)
    }

    fun testMaybeConvertLinuxPathToWindowsUncFromContext_convertsForWslContext() {
        val contextPath = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project"
        val linuxPath = "/home/testuser/.local/share/mise/installs/elixir/1.15.7"
        val expected = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val converted = wslCompatMock.maybeConvertLinuxPathToWindowsUncFromContext(contextPath, linuxPath)

        assertEquals(expected, converted)
    }

    fun testMaybeConvertLinuxPathToWindowsUncFromContext_returnsOriginalOnNonWslContext() {
        val linuxPath = "/home/testuser/.local/share/mise/installs/elixir/1.15.7"

        val converted = wslCompatMock.maybeConvertLinuxPathToWindowsUncFromContext(
            "C:\\Users\\steve\\IdeaProjects\\intellij-elixir",
            linuxPath,
        )

        assertEquals(linuxPath, converted)
    }

    fun testPathsEqualWslAware_returnsTrueWhenEqualButRealpathThrowsWindows() {
        val myPath = "C:\\sdk-a"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath)

        assertTrue(equal)
        verify(throwingService, times(2)).toRealPath(anyString())
    }

    fun testPathsEqualWslAware_returnsTrueWhenEqualButRealpathThrowsLinux() {
        val myPath = "/home/testuser/.local/share//mise//installs//elixir//1.15.7"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath)

        assertTrue(equal)
        verify(throwingService, times(2)).toRealPath(anyString())
    }

    fun testPathsEqualWslAware_returnsTrueWhenEqualButRealpathThrowsWsl() {
        val myPath = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath)

        assertTrue(equal)
        verify(throwingService, times(2)).toRealPath(anyString())
    }

    fun testPathsEqualWslAware_returnsTrueWhenEqualButRealpathThrowsDifferentPrefixWsl() {
        val myPath = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val myPath2 = "\\\\wsl$\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath2)

        assertTrue(equal)
        // path is canonicalized before comparison, so same path calls twice
        verify(throwingService, times(2)).toRealPath(anyString())
    }

    fun testPathsEqualWslAware_returnsFalseWhenNotEqualButRealpathThrowsWindows() {
        val myPath = "C:\\sdk-a"
        val myPath2 = "C:\\sdk-b"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath2)

        assertFalse(equal)
        verify(throwingService).toRealPath(myPath)
        verify(throwingService).toRealPath(myPath2)
    }

    fun testPathsEqualWslAware_returnsFalseWhenNotEqualButRealpathThrowsLinux() {
        val myPath = "/home/testuser/.local/share//mise//installs//elixir//1.15.7"
        val myPath2 = "/home/testuser/.local/share//mise//installs//elixir//1.19.7"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath2)

        assertFalse(equal)
        verify(throwingService).toRealPath(myPath)
        verify(throwingService).toRealPath(myPath2)
    }

    fun testPathsEqualWslAware_returnsFalseWhenNotEqualButRealpathThrowsWsl() {
        val myPath = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val myPath2 = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.19.7"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath2)

        assertFalse(equal)
        verify(throwingService).toRealPath(myPath)
        verify(throwingService).toRealPath(myPath2)
    }

    fun testPathsEqualWslAware_returnsFalseWhenNotEqualButRealpathThrowsDifferentPrefixWsl() {
        val myPath = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val myPath2 = "\\\\wsl$\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.19.7"
        val myPath2Canonicalized = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.19.7"
        val throwingService = spy(wslCompatMock)
        doThrow(IllegalStateException("boom"))
            .`when`(throwingService)
            .toRealPath(anyString())

        val equal = throwingService.pathsEqualWslAware(myPath, myPath2)

        assertFalse(equal)
        // path is canonicalized before comparison, so same path calls twice
        verify(throwingService).toRealPath(myPath)
        verify(throwingService).toRealPath(myPath2Canonicalized)
    }

    // -------------------------------------------------------------------------
    // WSL prefix canonicalization: OS-policy seam ([wslPrefixConversion]) and the
    // pure rewrite it drives ([canonicalizeWslPrefix]).
    // -------------------------------------------------------------------------

    fun testWslPrefixConversion_returnsNullForNonWindows() {
        // Exercises the REAL production decision: off Windows there is no prefix conversion.
        // Deterministic on every runner because Linux/macOS are never OS.Windows.
        val real = WslCompatServiceImpl()
        assertNull(real.wslPrefixConversion(OS.Linux))
        assertNull(real.wslPrefixConversion(OS.macOS))
    }

    fun testCanonicalizeWslPrefix_rewritesLegacyToModern() {
        val legacy = "\\\\wsl$\\Ubuntu-24.04\\home\\testuser\\project"
        val modern = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project"
        // Default mock policy is legacy -> modern; the real string rewrite still runs.
        with(MockWslCompatService()) {
            assertEquals(modern, legacy.canonicalizeWslPrefix())
            // Already-modern paths are left unchanged.
            assertEquals(modern, modern.canonicalizeWslPrefix())
        }
    }

    fun testCanonicalizeWslPrefix_rewritesModernToLegacy() {
        val legacy = "\\\\wsl$\\Ubuntu-24.04\\home\\testuser\\project"
        val modern = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project"
        with(MockWslCompatService(prefixConversionOverride = MODERN_WSL_PREFIX to LEGACY_WSL_PREFIX)) {
            assertEquals(legacy, modern.canonicalizeWslPrefix())
            assertEquals(legacy, legacy.canonicalizeWslPrefix())
        }
    }

    fun testCanonicalizeWslPrefix_leavesPathUnchangedWhenNoConversion() {
        val legacy = "\\\\wsl$\\Ubuntu-24.04\\home\\testuser\\project"
        val modern = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project"
        // null policy simulates the non-Windows "no conversion" branch.
        with(MockWslCompatService(prefixConversionOverride = null)) {
            assertEquals(legacy, legacy.canonicalizeWslPrefix())
            assertEquals(modern, modern.canonicalizeWslPrefix())
        }
    }
}
