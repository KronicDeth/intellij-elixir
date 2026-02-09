package org.elixir_lang.sdk

import com.intellij.openapi.util.SystemInfo
import org.elixir_lang.PlatformTestCase

/**
 * Tests for HomePath utility methods.
 */
class HomePathTest : PlatformTestCase() {
    fun testGetExecutableFileName_wslPathsNeverGetExtensions() {
        val paths = listOf(
            "\\\\wsl$\\Ubuntu\\usr\\lib\\erlang",
            "//wsl$/Ubuntu/usr/lib/elixir",
            "\\\\wsl.localhost\\Ubuntu-24.04\\home\\user\\sdk",
            "//wsl.localhost/Ubuntu-24.04/home/user/sdk"
        )

        paths.forEach { path ->
            assertEquals("erl", HomePath.getExecutableFileName(path, "erl", ".exe"))
            assertEquals("elixir", HomePath.getExecutableFileName(path, "elixir", ".bat"))
        }
    }


    fun testGetExecutableFileName_localPathsFollowSystemInfo() {
        val localPath = "C:\\Program Files\\Elixir"
        val expectedErl = if (SystemInfo.isWindows) "erl.exe" else "erl"
        val expectedElixir = if (SystemInfo.isWindows) "elixir.bat" else "elixir"

        assertEquals(expectedErl, HomePath.getExecutableFileName(localPath, "erl", ".exe"))
        assertEquals(expectedElixir, HomePath.getExecutableFileName(localPath, "elixir", ".bat"))
    }


    fun testGetExecutableFileName_nullSdkHomeUsesSystemInfo() {
        val expected = if (SystemInfo.isWindows) "erl.exe" else "erl"
        assertEquals(expected, HomePath.getExecutableFileName(null, "erl", ".exe"))
    }
}
