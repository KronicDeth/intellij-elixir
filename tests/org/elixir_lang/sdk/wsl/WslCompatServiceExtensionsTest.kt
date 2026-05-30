package org.elixir_lang.sdk.wsl

import com.intellij.execution.wsl.WSLDistribution
import org.elixir_lang.PlatformTestCase
import org.mockito.Mockito

class WslCompatServiceExtensionsTest : PlatformTestCase() {
    private val service = MockWslCompatService(canonicalWslPrefixOverride = MODERN_WSL_PREFIX)

    fun testConvertLinuxPathToWindowsUncFromContext_convertsForWslContext() {
        val contextPath = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\project"
        val linuxPath = "/home/testuser/.local/share/mise/installs/elixir/1.15.7"

        val converted = service.convertLinuxPathToWindowsUncFromContext(contextPath, linuxPath)

        assertEquals(
            "\\\\wsl.localhost\\Ubuntu-24.04\\home\\testuser\\.local\\share\\mise\\installs\\elixir\\1.15.7",
            converted,
        )
    }

    fun testConvertLinuxPathToWindowsUncFromContext_returnsNullForNonWslContext() {
        val converted = service.convertLinuxPathToWindowsUncFromContext(
            "C:\\Users\\steve\\IdeaProjects\\intellij-elixir",
            "/home/testuser/.local/share/mise/installs/elixir/1.15.7",
        )

        assertNull(converted)
    }

    fun testConvertLinuxPathToWindowsUncFromContext_returnsNullForNonLinuxPath() {
        val converted = service.convertLinuxPathToWindowsUncFromContext(
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
        Mockito.`when`(distro.msId).thenReturn("Ubuntu-24.04")

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
        val converted = service.maybeConvertLinuxPathToWindowsUncFromContext(contextPath, linuxPath)

        assertEquals(expected, converted)
    }

    fun testMaybeConvertLinuxPathToWindowsUncFromContext_returnsOriginalOnNonWslContext() {
        val linuxPath = "/home/testuser/.local/share/mise/installs/elixir/1.15.7"

        val converted = service.maybeConvertLinuxPathToWindowsUncFromContext(
            "C:\\Users\\steve\\IdeaProjects\\intellij-elixir",
            linuxPath,
        )

        assertEquals(linuxPath, converted)
    }
}
