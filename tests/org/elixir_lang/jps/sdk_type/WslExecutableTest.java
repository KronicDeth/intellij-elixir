package org.elixir_lang.jps.sdk_type;

import com.intellij.openapi.util.SystemInfo;
import junit.framework.TestCase;
import org.elixir_lang.jps.HomePath;

import java.io.File;
// ... existing code ...
public class WslExecutableTest extends TestCase {

    /**
     * Ensure Erlang executable resolution includes bin and correct extension.
     */
    public void testErlang_GetByteCodeInterpreterExecutable() {
        String sdkHome = SystemInfo.isWindows ? "C:\\Erlang" : "/usr/lib/erlang";
        File executable = Erlang.getByteCodeInterpreterExecutable(sdkHome);

        assertTrue("Path should include bin", executable.getPath().contains("bin"));
        String expectedName = SystemInfo.isWindows ? "erl.exe" : "erl";
        assertEquals("Executable name mismatch", expectedName, executable.getName());
    }

    /**
     * Ensure Elixir executable resolution includes bin and correct extensions.
     */
    public void testElixir_Executables() {
        String sdkHome = SystemInfo.isWindows ? "C:\\Elixir" : "/usr/local/lib/elixir";

        File elixir = Elixir.getScriptInterpreterExecutable(sdkHome);
        File elixirc = Elixir.getByteCodeCompilerExecutable(sdkHome);
        File iex = Elixir.getIExExecutable(sdkHome);
        File mix = Elixir.mixFile(sdkHome); // currently hardcoded to bare name

        String expectedExt = SystemInfo.isWindows ? ".bat" : "";
        assertTrue(elixir.getPath().contains("bin"));
        assertTrue(elixirc.getPath().contains("bin"));
        assertTrue(iex.getPath().contains("bin"));

        assertEquals("elixir" + expectedExt, elixir.getName());
        assertEquals("elixirc" + expectedExt, elixirc.getName());
        assertEquals("iex" + expectedExt, iex.getName());
        assertEquals("mix", mix.getName());
    }

    /**
     * Confirm WSL paths never receive extensions via HomePath (forward/backslash variants).
     */
    public void testWslFormatVariants_NoExtensions() {
        String[] wslPaths = {
                "\\\\wsl$\\Ubuntu\\usr",
                "//wsl$/Ubuntu/usr",
                "\\\\wsl.localhost\\Ubuntu-24.04\\home",
                "//wsl.localhost/Ubuntu-24.04/home"
        };

        for (String path : wslPaths) {
            assertEquals("erl", HomePath.getExecutableFileName(path, "erl", ".exe"));
            assertEquals("elixir", HomePath.getExecutableFileName(path, "elixir", ".bat"));
        }
    }
// ... existing code ...
}
