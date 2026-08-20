package org.elixir_lang.sdk.erlang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.registerOrReplaceServiceInstance
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.wsl.MockWslCompatService
import org.elixir_lang.sdk.wsl.WslCompatService

/**
 * Tests for Erlang SDK Type naming methods.
 */
class TypeNamingTest : PlatformTestCase() {

    private val erlangType = Type()

    override fun setUp() {
        super.setUp()

        // Replace the real WslCompatService with MockWslCompatService for testing
        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            WslCompatService::class.java,
            MockWslCompatService(),
            testRootDisposable,
        )
    }

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
        val name = Type.getDefaultSdkName(
            "/custom/path/erlang/26.0",
            null
        )
        assertEquals("Erlang for Elixir at /custom/path/erlang/26.0", name)
    }

    fun testGetDefaultSdkName_withVersion() {
        val release = Release("26", "26.0.1")
        val name = Type.getDefaultSdkName(
            "/Users/josh/.local/share/mise/installs/erlang/26.0.1",
            release
        )
        assertNotNull(name)
        // Should use directory name since it starts with the OTP major
        assertTrue("Name should contain mise", name.contains("mise"))
        assertTrue("Name should contain Erlang for Elixir", name.contains("Erlang for Elixir"))
        assertTrue("Name should contain 26.0.1", name.contains("26.0.1"))
    }
}
