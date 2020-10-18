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

        assertEquals("# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                        "defmodule Bitwise do\n" +
                        "  @moduleDoc \"\"\"\n" +
                        "  A set of macros that perform calculations on bits.\n" +
                        "  \n" +
                        "  The macros in this module come in two flavors: named or\n" +
                        "  operators. For example:\n" +
                        "  \n" +
                        "      iex> use Bitwise\n" +
                        "      iex> bnot(1) # named\n" +
                        "      -2\n" +
                        "      iex> 1 &&& 1 # operator\n" +
                        "      1\n" +
                        "  \n" +
                        "  If you prefer to use only operators or skip them, you can\n" +
                        "  pass the following options:\n" +
                        "  \n" +
                        "    * `:only_operators` - includes only operators\n" +
                        "    * `:skip_operators` - skips operators\n" +
                        "  \n" +
                        "  For example:\n" +
                        "  \n" +
                        "      iex> use Bitwise, only_operators: true\n" +
                        "      iex> 1 &&& 1\n" +
                        "      1\n" +
                        "  \n" +
                        "  When invoked with no options, `use Bitwise` is equivalent\n" +
                        "  to `import Bitwise`.\n" +
                        "  \n" +
                        "  All bitwise macros can be used in guards:\n" +
                        "  \n" +
                        "      iex> use Bitwise\n" +
                        "      iex> odd? = fn\n" +
                        "      ...>   int when band(int, 1) == 1 -> true\n" +
                        "      ...>   _ -> false\n" +
                        "      ...> end\n" +
                        "      iex> odd?.(1)\n" +
                        "      true\n" +
                        "  \"\"\"\n" +
                        "  # Macros\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Infix operator; calculates the bitwise AND of its arguments.\n" +
                        "  \n" +
                        "      iex> 9 &&& 3\n" +
                        "      1\n" +
                        "  \"\"\"\n" +
                        "  def left &&& right do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Infix operator; calculates the result of an arithmetic left bitshift.\n" +
                        "  \n" +
                        "      iex> 1 <<< 2\n" +
                        "      4\n" +
                        "      iex> 1 <<< -2\n" +
                        "      0\n" +
                        "      iex> -1 <<< 2\n" +
                        "      -4\n" +
                        "      iex> -1 <<< -2\n" +
                        "      -1\n" +
                        "  \"\"\"\n" +
                        "  def left <<< right do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Infix operator; calculates the result of an arithmetic right bitshift.\n" +
                        "  \n" +
                        "      iex> 1 >>> 2\n" +
                        "      0\n" +
                        "      iex> 1 >>> -2\n" +
                        "      4\n" +
                        "      iex> -1 >>> 2\n" +
                        "      -1\n" +
                        "      iex> -1 >>> -2\n" +
                        "      -4\n" +
                        "  \"\"\"\n" +
                        "  def left >>> right do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Infix operator; calculates the bitwise XOR of its arguments.\n" +
                        "  \n" +
                        "      iex> 9 ^^^ 3\n" +
                        "      10\n" +
                        "  \"\"\"\n" +
                        "  def left ^^^ right do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "  def __using__(options) do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Calculates the bitwise AND of its arguments.\n" +
                        "  \n" +
                        "      iex> band(9, 3)\n" +
                        "      1\n" +
                        "  \"\"\"\n" +
                        "  def band(left, right) do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Calculates the bitwise NOT of its argument.\n" +
                        "  \n" +
                        "      iex> bnot(2)\n" +
                        "      -3\n" +
                        "      iex> bnot(2) &&& 3\n" +
                        "      1\n" +
                        "  \"\"\"\n" +
                        "  def bnot(expr) do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Calculates the bitwise OR of its arguments.\n" +
                        "  \n" +
                        "      iex> bor(9, 3)\n" +
                        "      11\n" +
                        "  \"\"\"\n" +
                        "  def bor(left, right) do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Calculates the result of an arithmetic left bitshift.\n" +
                        "  \n" +
                        "      iex> bsl(1, 2)\n" +
                        "      4\n" +
                        "      iex> bsl(1, -2)\n" +
                        "      0\n" +
                        "      iex> bsl(-1, 2)\n" +
                        "      -4\n" +
                        "      iex> bsl(-1, -2)\n" +
                        "      -1\n" +
                        "  \"\"\"\n" +
                        "  def bsl(left, right) do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Calculates the result of an arithmetic right bitshift.\n" +
                        "  \n" +
                        "      iex> bsr(1, 2)\n" +
                        "      0\n" +
                        "      iex> bsr(1, -2)\n" +
                        "      4\n" +
                        "      iex> bsr(-1, 2)\n" +
                        "      -1\n" +
                        "      iex> bsr(-1, -2)\n" +
                        "      -4\n" +
                        "  \"\"\"\n" +
                        "  def bsr(left, right) do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Calculates the bitwise XOR of its arguments.\n" +
                        "  \n" +
                        "      iex> bxor(9, 3)\n" +
                        "      10\n" +
                        "  \"\"\"\n" +
                        "  def bxor(left, right) do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Infix operator; calculates the bitwise OR of its arguments.\n" +
                        "  \n" +
                        "      iex> 9 ||| 3\n" +
                        "      11\n" +
                        "  \"\"\"\n" +
                        "  def left ||| right do\n" +
                        "    # body not decompiled\n" +
                        "  end\n" +
                        "\n" +
                        "\n" +
                        "  @doc \"\"\"\n" +
                        "  Prefix (unary) operator; calculates the bitwise NOT of its argument.\n" +
                        "  \n" +
                        "      iex> ~~~2\n" +
                        "      -3\n" +
                        "      iex> ~~~2 &&& 3\n" +
                        "      1\n" +
                        "  \"\"\"\n" +
                        "  def ~~~expr do\n" +
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

    public void testIssue878() throws IOException, OtpErlangDecodeException {
        assertDecompiled("gl");
    }

    public void testIssue883() throws IOException, OtpErlangDecodeException {
        assertDecompiled("fprof");
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
