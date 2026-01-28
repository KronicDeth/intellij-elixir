package org.elixir_lang.sdk.erlang

import org.elixir_lang.PlatformTestCase

/**
 * Tests for Erlang SDK Type naming methods.
 */
class TypeNamingTest : PlatformTestCase() {

    private val erlangType = Type()

    fun testGetVersionString_miseErlang() {
        // Note: getVersionString returns null if it can't detect the version from the path
        // For paths without actual Erlang installations, we test the format
        val homePath = "/Users/josh/.local/share/mise/installs/erlang/26.0"
        val result: Pair<String?, String?> = captureLoggedWarning(
            "org.elixir_lang.sdk.erlang.Type"
        ) { erlangType.getVersionString(homePath) }

        val version = result.first
        // Will be null without actual installation, but method should not throw
        assertNull(version)
    }

    fun testGetDefaultSdkName_miseErlang() {
        val name = Type.getDefaultSdkName(
            "/Users/josh/.local/share/mise/installs/erlang/26.0",
            null
        )
        assertEquals("mise Erlang for Elixir at /Users/josh/.local/share/mise/installs/erlang/26.0", name)
    }

    fun testGetDefaultSdkName_asdfErlang() {
        val name = Type.getDefaultSdkName(
            "/Users/josh/.asdf/installs/erlang/26.0",
            null
        )
        assertEquals("asdf Erlang for Elixir at /Users/josh/.asdf/installs/erlang/26.0", name)
    }

    fun testGetDefaultSdkName_unknownSource() {
        val logger = com.intellij.openapi.diagnostic.Logger.getInstance("org.elixir_lang.sdk.erlang.Type")
        logger.setLevel(com.intellij.openapi.diagnostic.LogLevel.ERROR)
        val name = Type.getDefaultSdkName(
            "/custom/path/erlang/26.0",
            null
        )
        assertEquals("Erlang for Elixir at /custom/path/erlang/26.0", name)
    }

    fun testGetDefaultSdkName_withVersion() {
        val release = Release("26.0.1", "14.0.2")
        val result: Pair<String?, String?> = captureLoggedWarning<String?>(
            "org.elixir_lang.sdk.erlang.Type"
        ) {
            Type.getDefaultSdkName(
                "/Users/josh/.local/share/mise/installs/erlang/26.0.1",
                release
            )
        }
        val name = result.first
        assertNotNull(name)
        // Should use directory name if more specific
        assertTrue("Name should contain mise", name?.contains("mise") ?: false)
        assertTrue("Name should contain Erlang for Elixir", name?.contains("Erlang for Elixir") ?: false)
    }
}
