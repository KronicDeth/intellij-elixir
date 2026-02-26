package org.elixir_lang.jps.shared.sdk

import org.elixir_lang.PlatformTestCase

/**
 * Tests for SdkPaths utility methods.
 */
class SdkPathsTest : PlatformTestCase() {

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
}
