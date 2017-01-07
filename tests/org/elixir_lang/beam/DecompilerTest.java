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

    /*
     * Instance Methods
     */

    private String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
    }
}
