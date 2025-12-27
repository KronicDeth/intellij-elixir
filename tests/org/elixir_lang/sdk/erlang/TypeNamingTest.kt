package org.elixir_lang.sdk.erlang

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for Erlang SDK Type naming methods.
 */
class TypeNamingTest {

    private val erlangType = Type()

    @Test
    fun testGetVersionString_miseErlang() {
        // Note: getVersionString returns null if it can't detect the version from the path
        // For paths without actual Erlang installations, we test the format
        val homePath = "/Users/josh/.local/share/mise/installs/erlang/26.0"
        val version = erlangType.getVersionString(homePath)
        // Will be null without actual installation, but method should not throw
    }

    @Test
    fun testGetDefaultSdkName_miseErlang() {
        val name = Type.getDefaultSdkName(
            "/Users/josh/.local/share/mise/installs/erlang/26.0",
            null
        )
        assertEquals("mise Erlang for Elixir at /Users/josh/.local/share/mise/installs/erlang/26.0", name)
    }

    @Test
    fun testGetDefaultSdkName_asdfErlang() {
        val name = Type.getDefaultSdkName(
            "/Users/josh/.asdf/installs/erlang/26.0",
            null
        )
        assertEquals("asdf Erlang for Elixir at /Users/josh/.asdf/installs/erlang/26.0", name)
    }

    @Test
    fun testGetDefaultSdkName_unknownSource() {
        val name = Type.getDefaultSdkName(
            "/custom/path/erlang/26.0",
            null
        )
        assertEquals("Erlang for Elixir at /custom/path/erlang/26.0", name)
    }

    @Test
    fun testGetDefaultSdkName_withVersion() {
        val release = Release("26.0.1", "14.0.2")
        val name = Type.getDefaultSdkName(
            "/Users/josh/.local/share/mise/installs/erlang/26.0.1",
            release
        )
        // Should use directory name if more specific
        assertTrue("Name should contain mise", name.contains("mise"))
        assertTrue("Name should contain Erlang for Elixir", name.contains("Erlang for Elixir"))
    }
}
