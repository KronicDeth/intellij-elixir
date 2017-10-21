package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.google.common.io.Files;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.LightCodeInsightTestCase;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DecompilerTest extends LightCodeInsightTestCase {
    /*
     * Tests
     */

    public void testIssue575() throws IOException, OtpErlangDecodeException {
        String ebinDirectory = ebinDirectory();

        VfsRootAccess.allowRootAccess(ebinDirectory);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(ebinDirectory + "Elixir.Bitwise.beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        CharSequence decompiled = decompiler.decompile(virtualFile);

        assertEquals(
                "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                "defmodule Bitwise do\n" +
                "\n" +
                "  # Macros\n" +
                "\n" +
                "  defmacro left &&& right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left <<< right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left >>> right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left ^^^ right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro __using__(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro band(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bnot(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bor(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bsl(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bsr(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bxor(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left ||| right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro ~~~(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  # Functions\n" +
                "\n" +
                "  def __info__(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "end\n",
                decompiled.toString()
        );
    }

    public void testIssue672() throws IOException, OtpErlangDecodeException {
        assertDecompiled("rebar3_hex_config");
    }

    public void testIssue683() throws IOException, OtpErlangDecodeException {
        assertDecompiled("orber_ifr");
    }

    public void testIssue703() throws IOException, OtpErlangDecodeException {
        assertDecompiled("Elixir.LDAPEx.ELDAPv3");
    }

    public void testIssue722() throws IOException, OtpErlangDecodeException {
        assertDecompiled("OTP20/Elixir.Kernel");
    }

    public void testIssue803() throws IOException, OtpErlangDecodeException {
        assertDecompiled("certifi_cacerts");
    }

    public void testIssue833() throws IOException, OtpErlangDecodeException {
        assertDecompiled("docgen_xmerl_xml_cb");
    }

    public void testIssue859() throws IOException, OtpErlangDecodeException {
        assertDecompiled("erl_syntax");
    }

    public void testIssue860() throws IOException, OtpErlangDecodeException {
        assertDecompiled("OTP-PUB-KEY");
    }

    public void testElixir_1_5_0() throws IOException, OtpErlangDecodeException {
        assertDecompiled("OTP20/Elixir.AtU8Test");
    }

    /*
     * Instance Methods
     */

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/beam/decompiler";
    }

    /*
     * Private Instance Methods
     */

    private void assertDecompiled(String name) throws IOException {
        String testDataPath = getTestDataPath();
        String prefix = testDataPath + "/" + name + ".";

        File expectedFile = new File(prefix + "ex");
        String expected = Files.toString(expectedFile, UTF_8);

        VfsRootAccess.allowRootAccess(testDataPath);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(prefix + "beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        CharSequence decompiled = decompiler.decompile(virtualFile);

        assertEquals(expected, decompiled.toString());
    }

    private String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
    }
}
