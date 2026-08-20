package org.elixir_lang.sdk.wsl

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.system.OS
import org.elixir_lang.PlatformTestCase
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.concurrent.Callable

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

    // -------------------------------------------------------------------------
    // pathsEqualWslAware: pure lexical comparison, no filesystem access at all.
    // -------------------------------------------------------------------------

    fun testPathsEqualWslAware_neverTouchesTheFilesystem() {
        // toRealPath throws AssertionError rather than a caught type, so any call that reaches it
        // fails the test loudly instead of silently falling back.
        val spiedService = spy(wslCompatMock)
        doThrow(AssertionError("pathsEqualWslAware must not perform filesystem I/O"))
            .`when`(spiedService)
            .toRealPath(anyString())

        assertTrue(spiedService.pathsEqualWslAware("C:\\sdk-a", "C:\\sdk-a"))
        assertFalse(spiedService.pathsEqualWslAware("C:\\sdk-a", "C:\\sdk-b"))
        verify(spiedService, never()).toRealPath(anyString())
    }

    fun testPathsEqualWslAware_returnsTrueForIdenticalWindowsPaths() {
        val myPath = "C:\\sdk-a"
        assertTrue(wslCompatMock.pathsEqualWslAware(myPath, myPath))
    }

    fun testPathsEqualWslAware_returnsFalseForDifferentWindowsPaths() {
        assertFalse(wslCompatMock.pathsEqualWslAware("C:\\sdk-a", "C:\\sdk-b"))
    }

    fun testPathsEqualWslAware_returnsTrueForIdenticalLinuxPaths() {
        val myPath = "/home/testuser/.local/share/mise/installs/elixir/1.15.7"
        assertTrue(wslCompatMock.pathsEqualWslAware(myPath, myPath))
    }

    fun testPathsEqualWslAware_returnsFalseForDifferentLinuxPaths() {
        val myPath = "/home/testuser/.local/share/mise/installs/elixir/1.15.7"
        val myPath2 = "/home/testuser/.local/share/mise/installs/elixir/1.19.7"
        assertFalse(wslCompatMock.pathsEqualWslAware(myPath, myPath2))
    }

    fun testPathsEqualWslAware_rewritesLegacyWslPrefixBeforeComparing() {
        val modern = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val legacy = "\\\\wsl$\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        // Default mock policy is legacy -> modern.
        assertTrue(wslCompatMock.pathsEqualWslAware(modern, legacy))
    }

    fun testPathsEqualWslAware_rewritesModernWslPrefixBeforeComparing() {
        val legacyOnlyPolicy = MockWslCompatService(prefixConversionOverride = MODERN_WSL_PREFIX to LEGACY_WSL_PREFIX)
        val modern = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        val legacy = "\\\\wsl$\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7"
        assertTrue(legacyOnlyPolicy.pathsEqualWslAware(modern, legacy))
    }

    fun testPathsEqualWslAware_returnsFalseForDifferentWslDistros() {
        val ubuntuA = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project"
        val ubuntuB = "\\\\wsl.localhost\\ItronUbuntu\\home\\testuser\\project"
        assertFalse(wslCompatMock.pathsEqualWslAware(ubuntuA, ubuntuB))
    }

    fun testPathsEqualWslAware_treatsMixedSeparatorsAsEqual() {
        val forwardSlash = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\a/b"
        val backslash = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\a\\b"
        assertTrue(wslCompatMock.pathsEqualWslAware(forwardSlash, backslash))
    }

    fun testPathsEqualWslAware_returnsFalseForNullOrBlank() {
        assertFalse(wslCompatMock.pathsEqualWslAware(null, "C:\\sdk-a"))
        assertFalse(wslCompatMock.pathsEqualWslAware("C:\\sdk-a", null))
        assertFalse(wslCompatMock.pathsEqualWslAware(null, null))
        assertFalse(wslCompatMock.pathsEqualWslAware("", "C:\\sdk-a"))
        assertFalse(wslCompatMock.pathsEqualWslAware("   ", "   "))
    }

    fun testPathsEqualWslAware_matchesFileUtilPathsEqualCaseRule() {
        // Asserted against FileUtil.pathsEqual itself, not a hard-coded expectation, so this
        // passes under either case-sensitivity rule the CI matrix runs under.
        val lower = "\\\\wsl.localhost\\ubuntu-24.04\\home\\testuser\\project"
        val upper = "\\\\wsl.localhost\\Ubuntu-24.04\\HOME\\testuser\\project"
        assertEquals(FileUtil.pathsEqual(lower, upper), wslCompatMock.pathsEqualWslAware(lower, upper))
    }

    fun testPathsEqualWslAware_treatsWindowsDriveLettersCaseInsensitively() {
        if (OS.CURRENT != OS.Windows) return
        assertTrue(wslCompatMock.pathsEqualWslAware("C:\\Elixir", "c:/elixir"))
    }

    // -------------------------------------------------------------------------
    // canonicalizePath: the I/O funnel - must never run under a read lock, and
    // must never throw for a path that simply cannot be resolved.
    // -------------------------------------------------------------------------

    fun testCanonicalizePath_throwsUnderReadLock() {
        val real = WslCompatServiceImpl()
        // Must run on a pooled thread: test methods themselves run on the EDT, and the EDT's
        // implicit write-intent read access does not register as holdsReadLock().
        val threw = ApplicationManager.getApplication().executeOnPooledThread(Callable {
            try {
                ReadAction.nonBlocking(Callable {
                    real.canonicalizePath("C:\\sdk-a")
                }).executeSynchronously()
                false
            } catch (_: IllegalStateException) {
                true
            }
        }).get()

        assertTrue("Expected canonicalizePath to throw when called under a read lock", threw)
    }

    fun testCanonicalizePath_fallsBackToLexicalFormForNonexistentPath() {
        val real = WslCompatServiceImpl()
        val nonexistent = "\\\\wsl.localhost\\NoSuchDistro-doesNotExist\\home\\nobody"

        // Must not throw, and must not boot anything - it returns the prefix-rewritten string.
        val expected = with(real) { nonexistent.canonicalizeWslPrefix() }
        val result = real.canonicalizePath(nonexistent)

        assertEquals(expected, result)
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
