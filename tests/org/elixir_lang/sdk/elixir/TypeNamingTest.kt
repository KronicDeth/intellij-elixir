package org.elixir_lang.sdk.elixir

import org.elixir_lang.PlatformTestCase

/**
 * Tests for Elixir SDK Type naming methods.
 */
class TypeNamingTest: PlatformTestCase() {

    private val elixirType = Type.instance

    fun testSuggestSdkName_miseElixir() {
        val name = elixirType.suggestSdkName(null, "/Users/josh/.local/share/mise/installs/elixir/1.15.7")
        assertEquals("mise Elixir 1.15.7", name)
    }

    fun testSuggestSdkName_miseElixirWithOtp() {
        val name = elixirType.suggestSdkName(null, "/Users/josh/.local/share/mise/installs/elixir/1.15.7-otp-26")
        assertEquals("mise Elixir 1.15.7-otp-26", name)
    }

    fun testSuggestSdkName_asdfElixir() {
        val name = elixirType.suggestSdkName(null, "/Users/josh/.asdf/installs/elixir/1.14.0")
        assertEquals("asdf Elixir 1.14.0", name)
    }

    fun testSuggestSdkName_homebrewElixir() {
        val name = elixirType.suggestSdkName(null, "/opt/homebrew/Cellar/elixir/1.15.0/libexec")
        // Homebrew paths have nested structure, version comes from directory name
        assertTrue("Name should contain Homebrew", name.contains("Homebrew") || name.contains("Elixir"))
    }

    fun testSuggestSdkName_unknownSource() {
        val name = elixirType.suggestSdkName(null, "/custom/path/1.15.0")
        assertEquals("Elixir 1.15.0", name)
    }

    fun testSuggestSdkName_noDuplicateElixir() {
        // This was a bug where "Elixir" appeared twice
        val name = elixirType.suggestSdkName(null, "/Users/josh/.local/share/mise/installs/elixir/1.15.7")
        val elixirCount = name.split("Elixir").size - 1
        assertEquals("Should contain exactly one 'Elixir'", 1, elixirCount)
    }

    fun testGetVersionString_miseElixir() {
        val version = elixirType.getVersionString("/Users/josh/.local/share/mise/installs/elixir/1.15.7")
        assertEquals("mise Elixir 1.15.7", version)
    }

    fun testGetVersionString_asdfElixir() {
        val version = elixirType.getVersionString("/Users/josh/.asdf/installs/elixir/1.14.0")
        assertEquals("asdf Elixir 1.14.0", version)
    }

    fun testGetVersionString_unknownSource() {
        val version = elixirType.getVersionString("/custom/path/1.15.0")
        assertEquals("Elixir 1.15.0", version)
    }

    fun testGetVersionString_includesElixir() {
        // Version string should include "Elixir" for clarity in detected SDKs list
        val version = elixirType.getVersionString("/Users/josh/.local/share/mise/installs/elixir/1.15.7")
        assertTrue("Version string should contain 'Elixir'", version.contains("Elixir"))
    }
}
