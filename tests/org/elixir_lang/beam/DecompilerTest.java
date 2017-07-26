package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.LightCodeInsightTestCase;

import java.io.*;

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
        String testDataPath = getTestDataPath();

        VfsRootAccess.allowRootAccess(testDataPath);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(testDataPath + "/rebar3_hex_config.beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        CharSequence decompiled = decompiler.decompile(virtualFile);

        assertEquals(
                "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                "defmodule :rebar3_hex_config do\n" +
                "\n" +
                "  # Macros\n" +
                "\n" +
                "  def api_url() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def auth() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def cdn_url() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:do)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def format_error(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def http_proxy() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def https_proxy() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def init(p0) do\n" +
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
                "\n" +
                "  def path() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def read() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def update(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def username() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def write(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "end\n",
                decompiled.toString()
        );
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


    private String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
    }
}
