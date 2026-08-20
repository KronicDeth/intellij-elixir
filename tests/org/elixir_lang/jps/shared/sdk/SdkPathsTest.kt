package org.elixir_lang.jps.shared.sdk

import org.elixir_lang.PlatformTestCase
import java.io.File

/**
 * Tests for SdkPaths utility methods.
 */
class SdkPathsTest : PlatformTestCase() {
    private val asdfElixir117 = "/home/user/.asdf/installs/elixir/1.17.0"
    private val asdfElixir116 = "/home/user/.asdf/installs/elixir/1.16.0"
    private val customMixHome = "/custom/mix/home"

    fun testDetectSource_mise() {
        assertEquals("mise", SdkPaths.detectSource("/Users/josh/.local/share/mise/installs/elixir/1.15.0"))
        assertEquals("mise", SdkPaths.detectSource("/Users/josh/.local/share/mise/installs/erlang/26.0"))
        assertEquals("mise", SdkPaths.detectSource("/home/user/.local/share/mise/installs/elixir/1.14.0-otp-25"))
    }

    fun testDetectSource_asdf() {
        assertEquals("asdf", SdkPaths.detectSource("/Users/josh/.asdf/installs/elixir/1.15.0"))
        assertEquals("asdf", SdkPaths.detectSource("/Users/josh/.asdf/installs/erlang/26.0"))
        assertEquals("asdf", SdkPaths.detectSource("/home/user/.asdf/installs/elixir/1.14.0"))
    }

    fun testDetectSource_homebrew() {
        assertEquals("Homebrew", SdkPaths.detectSource("/usr/local/Cellar/elixir/1.15.0"))
        assertEquals("Homebrew", SdkPaths.detectSource("/opt/homebrew/Cellar/erlang/26.0"))
        assertEquals("Homebrew", SdkPaths.detectSource("/opt/homebrew/Cellar/elixir/1.14.0/libexec"))
    }

    fun testDetectSource_nix() {
        assertEquals("Nix", SdkPaths.detectSource("/nix/store/abc123-elixir-1.15.0"))
        assertEquals("Nix", SdkPaths.detectSource("/nix/store/xyz789-erlang-26.0/lib/erlang"))
    }

    fun testDetectSource_kerl() {
        assertEquals("kerl", SdkPaths.detectSource("/Users/josh/otp/25.0"))
        assertEquals("kerl", SdkPaths.detectSource("/home/user/otp/26.1"))
    }

    fun testDetectSource_unknown() {
        assertNull(SdkPaths.detectSource("/usr/local/elixir"))
        assertNull(SdkPaths.detectSource("/opt/elixir/1.15.0"))
        assertNull(SdkPaths.detectSource("/custom/path/to/erlang"))
    }

    fun testDetectSource_nullSafe() {
        // Edge cases
        assertNull(SdkPaths.detectSource(""))
        assertNull(SdkPaths.detectSource("/"))
    }

    fun testMaybeUpdateMixHome_setsWhenMissing() {
        val environment = mutableMapOf<String, String>()

        SdkPaths.maybeUpdateMixHome(environment, asdfElixir117)

        val expectedMixHome = mixHomePath(asdfElixir117)
        assertEquals(expectedMixHome, environment["MIX_HOME"])
        assertEquals(File(expectedMixHome, "archives").absolutePath, environment["MIX_ARCHIVES"])
    }

    fun testMaybeUpdateMixHome_replacesWhenUnderManagerPrefix() {
        val environment = mutableMapOf<String, String>()
        environment["MIX_HOME"] = mixHomePath(asdfElixir116)
        environment["MIX_ARCHIVES"] = File(environment["MIX_HOME"]!!, "archives").absolutePath

        SdkPaths.maybeUpdateMixHome(environment, asdfElixir117)

        val expectedMixHome = mixHomePath(asdfElixir117)
        assertEquals(expectedMixHome, environment["MIX_HOME"])
        assertEquals(File(expectedMixHome, "archives").absolutePath, environment["MIX_ARCHIVES"])
    }

    fun testMaybeUpdateMixHome_preservesCustomMixHome() {
        val environment = mutableMapOf<String, String>()
        environment["MIX_HOME"] = customMixHome
        environment["MIX_ARCHIVES"] = "$customMixHome/archives"

        SdkPaths.maybeUpdateMixHome(environment, asdfElixir117)

        assertEquals(customMixHome, environment["MIX_HOME"])
        assertEquals("$customMixHome/archives", environment["MIX_ARCHIVES"])
    }

    private fun mixHomePath(homePath: String): String = File(homePath, ".mix").absolutePath
}
