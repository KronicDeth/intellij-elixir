package org.elixir_lang.jps.sdk_type;

import com.intellij.openapi.util.SystemInfo;
import junit.framework.TestCase;

/**
 * Tests for WSL path handling in JPS SDK types.
 * <p>
 * These tests verify that executable file name generation correctly handles
 * both WSL paths and local paths on different operating systems.
 */
public class WslExecutableTest extends TestCase {

    /**
     * Test Case 1: Erlang executable names for old WSL format paths.
     */
    public void testErlang_GetExecutableFileName_OldWslFormat() {
        // Given: Old WSL format paths (both slash variants)
        String wslPathBackslash = "\\\\wsl$\\Ubuntu\\usr\\lib\\erlang";
        String wslPathForwardSlash = "//wsl$/Ubuntu/usr/lib/erlang";

        // When: Getting executable file name
        String executableBackslash = Erlang.getExecutableFileName(wslPathBackslash, "erl");
        String executableForwardSlash = Erlang.getExecutableFileName(wslPathForwardSlash, "erl");

        // Then: Should return bare executable name without .exe extension
        assertEquals("Old WSL format (backslash) should return bare executable", "erl", executableBackslash);
        assertEquals("Old WSL format (forward slash) should return bare executable", "erl", executableForwardSlash);
    }

    /**
     * Test Case 2: Erlang executable names for new WSL format paths.
     */
    public void testErlang_GetExecutableFileName_NewWslFormat() {
        // Given: New WSL format paths (both slash variants)
        String wslPathBackslash = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\steve\\.local\\erlang";
        String wslPathForwardSlash = "//wsl.localhost/Ubuntu-24.04/home/steve/.local/erlang";

        // When: Getting executable file name
        String executableBackslash = Erlang.getExecutableFileName(wslPathBackslash, "erl");
        String executableForwardSlash = Erlang.getExecutableFileName(wslPathForwardSlash, "erl");

        // Then: Should return bare executable name without .exe extension
        assertEquals("New WSL format (backslash) should return bare executable", "erl", executableBackslash);
        assertEquals("New WSL format (forward slash) should return bare executable", "erl", executableForwardSlash);
    }

    /**
     * Test Case 3: Erlang executable names for local Windows paths.
     */
    public void testErlang_GetExecutableFileName_LocalWindowsPath() {
        // Given: A local Windows path
        String windowsPath = "C:\\Program Files\\Erlang\\erl9.0";

        // When: Getting executable file name
        String executable = Erlang.getExecutableFileName(windowsPath, "erl");

        // Then: Should add .exe extension on Windows, bare name on other OS
        String expected = SystemInfo.isWindows ? "erl.exe" : "erl";
        assertEquals("Local Windows path should respect SystemInfo.isWindows", expected, executable);
    }

    /**
     * Test Case 4: Erlang executable names for local Linux paths.
     */
    public void testErlang_GetExecutableFileName_LocalLinuxPath() {
        // Given: A local Linux path
        String linuxPath = "/usr/local/lib/erlang";

        // When: Getting executable file name
        String executable = Erlang.getExecutableFileName(linuxPath, "erl");

        // Then: Should add .exe extension on Windows, bare name on Linux
        String expected = SystemInfo.isWindows ? "erl.exe" : "erl";
        assertEquals("Local Linux path should respect SystemInfo.isWindows", expected, executable);
    }

    /**
     * Test Case 5: Erlang executable names with null SDK home.
     */
    public void testErlang_GetExecutableFileName_NullSdkHome() {
        // Given: null SDK home
        String sdkHome = null;

        // When: Getting executable file name
        String executable = Erlang.getExecutableFileName(sdkHome, "erl");

        // Then: Should respect SystemInfo.isWindows
        String expected = SystemInfo.isWindows ? "erl.exe" : "erl";
        assertEquals("Null SDK home should respect SystemInfo.isWindows", expected, executable);
    }

    /**
     * Test Case 6: Elixir executable names for old WSL format paths.
     */
    public void testElixir_GetExecutableFileName_OldWslFormat() {
        // Given: Old WSL format paths (both slash variants)
        String wslPathBackslash = "\\\\wsl$\\Ubuntu\\usr\\lib\\elixir";
        String wslPathForwardSlash = "//wsl$/Ubuntu/usr/lib/elixir";

        // When: Getting executable file name
        String executableBackslash = Elixir.getExecutableFileName(wslPathBackslash, "elixir");
        String executableForwardSlash = Elixir.getExecutableFileName(wslPathForwardSlash, "elixir");

        // Then: Should return bare executable name without .bat extension
        assertEquals("Old WSL format (backslash) should return bare executable", "elixir", executableBackslash);
        assertEquals("Old WSL format (forward slash) should return bare executable", "elixir", executableForwardSlash);
    }

    /**
     * Test Case 7: Elixir executable names for new WSL format paths.
     */
    public void testElixir_GetExecutableFileName_NewWslFormat() {
        // Given: New WSL format paths (both slash variants)
        String wslPathBackslash = "\\\\wsl.localhost\\Ubuntu-24.04\\home\\steve\\.local\\elixir";
        String wslPathForwardSlash = "//wsl.localhost/Ubuntu-24.04/home/steve/.local/elixir";

        // When: Getting executable file name
        String executableBackslash = Elixir.getExecutableFileName(wslPathBackslash, "elixir");
        String executableForwardSlash = Elixir.getExecutableFileName(wslPathForwardSlash, "elixir");

        // Then: Should return bare executable name without .bat extension
        assertEquals("New WSL format (backslash) should return bare executable", "elixir", executableBackslash);
        assertEquals("New WSL format (forward slash) should return bare executable", "elixir", executableForwardSlash);
    }

    /**
     * Test Case 8: Elixir executable names for local Windows paths.
     */
    public void testElixir_GetExecutableFileName_LocalWindowsPath() {
        // Given: A local Windows path
        String windowsPath = "C:\\Program Files\\Elixir";

        // When: Getting executable file name
        String executable = Elixir.getExecutableFileName(windowsPath, "elixir");

        // Then: Should add .bat extension on Windows, bare name on other OS
        String expected = SystemInfo.isWindows ? "elixir.bat" : "elixir";
        assertEquals("Local Windows path should respect SystemInfo.isWindows", expected, executable);
    }

    /**
     * Test Case 9: Elixir executable names for local Linux paths.
     */
    public void testElixir_GetExecutableFileName_LocalLinuxPath() {
        // Given: A local Linux path
        String linuxPath = "/usr/local/lib/elixir";

        // When: Getting executable file name
        String executable = Elixir.getExecutableFileName(linuxPath, "elixir");

        // Then: Should add .bat extension on Windows, bare name on Linux
        String expected = SystemInfo.isWindows ? "elixir.bat" : "elixir";
        assertEquals("Local Linux path should respect SystemInfo.isWindows", expected, executable);
    }

    /**
     * Test Case 10: Elixir executable names with null SDK home.
     */
    public void testElixir_GetExecutableFileName_NullSdkHome() {
        // Given: null SDK home
        String sdkHome = null;

        // When: Getting executable file name
        String executable = Elixir.getExecutableFileName(sdkHome, "elixir");

        // Then: Should respect SystemInfo.isWindows
        String expected = SystemInfo.isWindows ? "elixir.bat" : "elixir";
        assertEquals("Null SDK home should respect SystemInfo.isWindows", expected, executable);
    }

    /**
     * Test Case 11: Multiple different executable names for Erlang.
     */
    public void testErlang_GetExecutableFileName_MultipleExecutables() {
        // Given: A WSL path
        String wslPath = "//wsl.localhost/Ubuntu-24.04/usr/lib/erlang";

        // When: Getting different executable names
        String erl = Erlang.getExecutableFileName(wslPath, "erl");
        String erlc = Erlang.getExecutableFileName(wslPath, "erlc");
        String escript = Erlang.getExecutableFileName(wslPath, "escript");

        // Then: All should return bare names without extensions
        assertEquals("erl should be bare", "erl", erl);
        assertEquals("erlc should be bare", "erlc", erlc);
        assertEquals("escript should be bare", "escript", escript);
    }

    /**
     * Test Case 12: Multiple different executable names for Elixir.
     */
    public void testElixir_GetExecutableFileName_MultipleExecutables() {
        // Given: A WSL path
        String wslPath = "//wsl.localhost/Ubuntu-24.04/usr/lib/elixir";

        // When: Getting different executable names
        String elixir = Elixir.getExecutableFileName(wslPath, "elixir");
        String elixirc = Elixir.getExecutableFileName(wslPath, "elixirc");
        String iex = Elixir.getExecutableFileName(wslPath, "iex");
        String mix = Elixir.getExecutableFileName(wslPath, "mix");

        // Then: All should return bare names without extensions
        assertEquals("elixir should be bare", "elixir", elixir);
        assertEquals("elixirc should be bare", "elixirc", elixirc);
        assertEquals("iex should be bare", "iex", iex);
        assertEquals("mix should be bare", "mix", mix);
    }
}
