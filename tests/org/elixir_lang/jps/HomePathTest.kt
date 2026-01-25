package org.elixir_lang.jps

import com.intellij.openapi.util.SystemInfo
import org.elixir_lang.PlatformTestCase

/**
 * Tests for HomePath utility methods.
 */
class HomePathTest : PlatformTestCase() {

    fun testDetectSource_mise() {
        assertEquals("mise", HomePath.detectSource("/Users/josh/.local/share/mise/installs/elixir/1.15.0"))
        assertEquals("mise", HomePath.detectSource("/Users/josh/.local/share/mise/installs/erlang/26.0"))
        assertEquals("mise", HomePath.detectSource("/home/user/.local/share/mise/installs/elixir/1.14.0-otp-25"))
    }

    fun testDetectSource_asdf() {
        assertEquals("asdf", HomePath.detectSource("/Users/josh/.asdf/installs/elixir/1.15.0"))
        assertEquals("asdf", HomePath.detectSource("/Users/josh/.asdf/installs/erlang/26.0"))
        assertEquals("asdf", HomePath.detectSource("/home/user/.asdf/installs/elixir/1.14.0"))
    }

    fun testDetectSource_homebrew() {
        assertEquals("Homebrew", HomePath.detectSource("/usr/local/Cellar/elixir/1.15.0"))
        assertEquals("Homebrew", HomePath.detectSource("/opt/homebrew/Cellar/erlang/26.0"))
        assertEquals("Homebrew", HomePath.detectSource("/opt/homebrew/Cellar/elixir/1.14.0/libexec"))
    }

    fun testDetectSource_nix() {
        assertEquals("Nix", HomePath.detectSource("/nix/store/abc123-elixir-1.15.0"))
        assertEquals("Nix", HomePath.detectSource("/nix/store/xyz789-erlang-26.0/lib/erlang"))
    }

    fun testDetectSource_kerl() {
        assertEquals("kerl", HomePath.detectSource("/Users/josh/otp/25.0"))
        assertEquals("kerl", HomePath.detectSource("/home/user/otp/26.1"))
    }

    fun testDetectSource_unknown() {
        assertNull(HomePath.detectSource("/usr/local/elixir"))
        assertNull(HomePath.detectSource("/opt/elixir/1.15.0"))
        assertNull(HomePath.detectSource("/custom/path/to/erlang"))
    }

    fun testDetectSource_nullSafe() {
        // Edge cases
        assertNull(HomePath.detectSource(""))
        assertNull(HomePath.detectSource("/"))
    }

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
