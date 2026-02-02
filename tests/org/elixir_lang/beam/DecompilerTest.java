package org.elixir_lang.beam;

import com.google.common.io.Files;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import org.elixir_lang.PlatformTestCase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DecompilerTest extends PlatformTestCase {
    /*
     * Tests
     */

    public void testIssue575() {
        String ebinDirectory = ebinDirectory();

        VfsRootAccess.allowRootAccess(getTestRootDisposable(), ebinDirectory);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(ebinDirectory + "Elixir.Bitwise.beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        CharSequence decompiled = decompiler.decompile(virtualFile);

        assertEquals("""
                        # Source code recreated from a .beam file by IntelliJ Elixir
                        defmodule Bitwise do
                          @moduledoc ~S""\"
                          A set of functions that perform calculations on bits.
                        
                          All bitwise functions work only on integers; otherwise an
                          `ArithmeticError` is raised.
                        
                          The functions in this module come in two flavors: named or
                          operators. For example:
                        
                              iex> use Bitwise
                              iex> bnot(1) # named
                              -2
                              iex> 1 &&& 1 # operator
                              1
                        
                          If you prefer to use only operators or skip them, you can
                          pass the following options:
                        
                            * `:only_operators` - includes only operators
                            * `:skip_operators` - skips operators
                        
                          For example:
                        
                              iex> use Bitwise, only_operators: true
                              iex> 1 &&& 1
                              1
                        
                          When invoked with no options, `use Bitwise` is equivalent
                          to `import Bitwise`.
                        
                          All bitwise functions can be used in guards:
                        
                              iex> odd? = fn
                              ...>   int when Bitwise.band(int, 1) == 1 -> true
                              ...>   _ -> false
                              ...> end
                              iex> odd?.(1)
                              true
                        
                          All functions in this module are inlined by the compiler.
                        
                          ""\"
                        
                          # Macros
                        
                          @doc false
                          defmacro __using__(options) do
                            (
                              except = cond() do
                                Keyword.get(options, :only_operators) ->
                                  [bnot: 1, band: 2, bor: 2, bxor: 2, bsl: 2, bsr: 2]
                                Keyword.get(options, :skip_operators) ->
                                  [~~~: 1, &&&: 2, |||: 2, ^^^: 2, <<<: 2, >>>: 2]
                                true ->
                                  []
                              end
                              {:import, [context: Bitwise], [{:__aliases__, [alias: false], [:"Bitwise"]}, [except: except]]}
                            )
                          end
                        
                          # Functions
                        
                          @doc ~S""\"
                          Bitwise AND operator.
                        
                          Calculates the bitwise AND of its arguments.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> 9 &&& 3
                              1
                        
                        
                          ""\"
                          def left &&& right do
                            Bitwise.band(left, right)
                          end
                        
                          @doc ~S""\"
                          Arithmetic left bitshift operator.
                        
                          Calculates the result of an arithmetic left bitshift.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> 1 <<< 2
                              4
                        
                              iex> 1 <<< -2
                              0
                        
                              iex> -1 <<< 2
                              -4
                        
                              iex> -1 <<< -2
                              -1
                        
                        
                          ""\"
                          def left <<< right do
                            Bitwise.bsl(left, right)
                          end
                        
                          @doc ~S""\"
                          Arithmetic right bitshift operator.
                        
                          Calculates the result of an arithmetic right bitshift.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> 1 >>> 2
                              0
                        
                              iex> 1 >>> -2
                              4
                        
                              iex> -1 >>> 2
                              -1
                        
                              iex> -1 >>> -2
                              -4
                        
                        
                          ""\"
                          def left >>> right do
                            Bitwise.bsr(left, right)
                          end
                        
                          @doc false
                          def left ^^^ right do
                            Bitwise.bxor(left, right)
                          end
                        
                          def __info__(p0) do
                            # body not decompiled
                          end
                        
                          @doc ~S""\"
                          Calculates the bitwise AND of its arguments.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> band(9, 3)
                              1
                        
                        
                          ""\"
                          def band(left, right) do
                            Bitwise.band(left, right)
                          end
                        
                          @doc ~S""\"
                          Calculates the bitwise NOT of the argument.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> bnot(2)
                              -3
                        
                              iex> bnot(2) &&& 3
                              1
                        
                        
                          ""\"
                          def bnot(expr) do
                            :erlang.bnot(expr)
                          end
                        
                          @doc ~S""\"
                          Calculates the bitwise OR of its arguments.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> bor(9, 3)
                              11
                        
                        
                          ""\"
                          def bor(left, right) do
                            Bitwise.bor(left, right)
                          end
                        
                          @doc ~S""\"
                          Calculates the result of an arithmetic left bitshift.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> bsl(1, 2)
                              4
                        
                              iex> bsl(1, -2)
                              0
                        
                              iex> bsl(-1, 2)
                              -4
                        
                              iex> bsl(-1, -2)
                              -1
                        
                        
                          ""\"
                          def bsl(left, right) do
                            Bitwise.bsl(left, right)
                          end
                        
                          @doc ~S""\"
                          Calculates the result of an arithmetic right bitshift.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> bsr(1, 2)
                              0
                        
                              iex> bsr(1, -2)
                              4
                        
                              iex> bsr(-1, 2)
                              -1
                        
                              iex> bsr(-1, -2)
                              -4
                        
                        
                          ""\"
                          def bsr(left, right) do
                            Bitwise.bsr(left, right)
                          end
                        
                          @doc ~S""\"
                          Calculates the bitwise XOR of its arguments.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> bxor(9, 3)
                              10
                        
                        
                          ""\"
                          def bxor(left, right) do
                            Bitwise.bxor(left, right)
                          end
                        
                          def module_info() do
                            # body not decompiled
                          end
                        
                          def module_info(p0) do
                            # body not decompiled
                          end
                        
                          @doc ~S""\"
                          Bitwise OR operator.
                        
                          Calculates the bitwise OR of its arguments.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> 9 ||| 3
                              11
                        
                        
                          ""\"
                          def left ||| right do
                            Bitwise.bor(left, right)
                          end
                        
                          @doc ~S""\"
                          Bitwise NOT unary operator.
                        
                          Calculates the bitwise NOT of the argument.
                        
                          Allowed in guard tests. Inlined by the compiler.
                        
                          ## Examples
                        
                              iex> ~~~2
                              -3
                        
                              iex> ~~~2 &&& 3
                              1
                        
                        
                          ""\"
                          def ~~~(expr) do
                            :erlang.bnot(expr)
                          end
                        end
                        """,
                decompiled.toString()
        );
    }

    public void testIssue672() throws IOException {
        assertDecompiled("rebar3_hex_config");
    }

    public void testIssue683() throws IOException {
        assertDecompiled("orber_ifr");
    }

    public void testIssue703() throws IOException {
        assertDecompiled("Elixir.LDAPEx.ELDAPv3");
    }

    public void testIssue772() throws IOException {
        assertDecompiled("OTP20/Elixir.Kernel");
    }

    public void testIssue803() throws IOException {
        assertDecompiled("certifi_cacerts");
    }

    public void testIssue833() throws IOException {
        assertDecompiled("docgen_xmerl_xml_cb");
    }

    public void testIssue859() throws IOException {
        assertDecompiled("erl_syntax");
    }

    public void testIssue860() throws IOException {
        assertDecompiled("OTP-PUB-KEY");
    }

    public void testIssue878() throws IOException {
        assertDecompiled("gl");
    }

    public void testIssue883() throws IOException {
        assertDecompiled("fprof");
    }

    public void testElixir_1_5_0() throws IOException {
        assertDecompiled("OTP20/Elixir.AtU8Test");
    }

    public void testDocsElixirKernel() throws IOException {
        assertDecompiled("Docs/Elixir.Kernel");
    }

    public void testDocsElixirKernelSpecialForms() throws IOException {
        assertDecompiled("Docs/Elixir.Kernel.SpecialForms");
    }

    public void testDocsElixirRuntimeError() throws IOException {
        assertDecompiled("Docs/Elixir.RuntimeError");
    }

    public void testDocsErlang() throws IOException {
        assertDecompiled("Docs/erlang");
    }

    public void testIssue1882() throws IOException {
        assertDecompiled("Elixir.Ecto.Query");
    }

    public void testIssue1886() throws IOException {
        assertDecompiled("Elixir.Module");
    }

    public void testHyphenInKeywordKeys() throws IOException {
        assertDecompiled("Elixir.Phoenix.HTML.Tag");
    }

    public void testIssue2221() throws IOException {
        assertDecompiled("queue");
    }

    // Issues 2251 and 2263
    public void testSSHOptions() throws IOException {
        assertDecompiled("ssh_options");
    }

    public void testIssue2257() throws IOException {
        assertDecompiled("hipe_icode_call_elim");
    }

    // Issues 2285 and 2286
    public void testASN1CT() throws IOException {
        assertDecompiled("asn1ct");
    }

    public void testIssue2287() throws IOException {
        assertDecompiled("diameter_gen_acct_rfc6733");
    }

    public void testIssue2288() throws IOException {
        assertDecompiled("diameter_gen_base_accounting");
    }

    public void testIssue2289() throws IOException {
        assertDecompiled("diameter_gen_relay");
    }

    public void testIssue2306() throws IOException {
        assertDecompiled("dialyzer_callgraph");
    }

    public void testCode() throws IOException {
        assertDecompiled("code");
    }

    public void testIssue2328() throws IOException {
        assertDecompiled("dbg_wx_trace_win");
    }

    public void testIssue2329() throws IOException {
        assertDecompiled("ex_cursor");
    }

    public void testIssue2331() throws IOException {
        assertDecompiled("gb_sets");
    }

    public void testIssue2332() throws IOException {
        assertDecompiled("idna");
    }

    public void testIssue2401() throws IOException {
        assertDecompiled("elixir/1.13.0/Elixir.Kernel");
    }

    public void testIssue2403() throws IOException {
        assertDecompiled("Elixir.EExTestWeb.PageController");
    }

    public void testIssue2410() throws IOException {
        assertDecompiled("Elixir.Ecto.Changeset");
    }

    public void testIssue2386() throws IOException {
        assertDecompiled("Elixir.RabbitMq.Handler");
    }

    public void testIssue2976() throws IOException {
        assertDecompiled("inet_db");
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
        String expected = Files.asCharSource(expectedFile, StandardCharsets.UTF_8).read();

        VfsRootAccess.allowRootAccess(getTestRootDisposable(), testDataPath);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(prefix + "beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        String actual = decompiler.decompile(virtualFile).toString();

        if (!expected.equals(actual)) {
            fail(buildCompactDiffMessage(name, expected, actual));
        }
    }

    private String buildCompactDiffMessage(String name, String expected, String actual) {
        int expectedLen = expected.length();
        int actualLen = actual.length();
        int minLen = Math.min(expectedLen, actualLen);

        // Find first difference
        int diffPos = 0;
        while (diffPos < minLen && expected.charAt(diffPos) == actual.charAt(diffPos)) {
            diffPos++;
        }

        // Calculate line and column
        int line = 1;
        int col = 1;
        int lineStart = 0;
        for (int i = 0; i < diffPos && i < expectedLen; i++) {
            if (expected.charAt(i) == '\n') {
                line++;
                col = 1;
                lineStart = i + 1;
            } else {
                col++;
            }
        }

        // Get the line containing the difference
        int lineEnd = expected.indexOf('\n', diffPos);
        if (lineEnd == -1) lineEnd = expectedLen;

        String expectedLine = expected.substring(lineStart, Math.min(lineEnd, expectedLen));

        lineEnd = actual.indexOf('\n', diffPos);
        if (lineEnd == -1) lineEnd = actualLen;
        String actualLine = actual.substring(lineStart, Math.min(lineEnd, actualLen));

        // Truncate lines if they're too long
        int maxLineLen = 120;
        String expectedLineDisplay = expectedLine.length() > maxLineLen
            ? expectedLine.substring(0, maxLineLen) + "..."
            : expectedLine;
        String actualLineDisplay = actualLine.length() > maxLineLen
            ? actualLine.substring(0, maxLineLen) + "..."
            : actualLine;

        // Get a few characters around the diff point for context
        int contextSize = 40;
        int contextStart = Math.max(0, diffPos - contextSize);
        int expectedEnd = Math.min(expectedLen, diffPos + contextSize);
        int actualEnd = Math.min(actualLen, diffPos + contextSize);

        String expectedSnippet = expected.substring(contextStart, expectedEnd);
        String actualSnippet = actual.substring(contextStart, actualEnd);

        // Show the exact character that differs
        char expectedChar = diffPos < expectedLen ? expected.charAt(diffPos) : '�';
        char actualChar = diffPos < actualLen ? actual.charAt(diffPos) : '�';

        return String.format(
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%n" +
            "DECOMPILATION MISMATCH: %s%n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%n" +
            "Position: char %d, line %d, col %d%n" +
            "Length:   expected=%d, actual=%d (diff: %d)%n" +
            "%n" +
            "Character at difference:%n" +
            "  Expected: '%s' (0x%04x)%n" +
            "  Actual:   '%s' (0x%04x)%n" +
            "%n" +
            "Line %d context:%n" +
            "  Expected: %s%n" +
            "  Actual:   %s%n" +
            "%n" +
            "Snippet (40 chars before/after):%n" +
            "  Expected: %s%n" +
            "  Actual:   %s%n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            name,
            diffPos, line, col,
            expectedLen, actualLen, (expectedLen - actualLen),
            escapeChar(expectedChar), (int) expectedChar,
            escapeChar(actualChar), (int) actualChar,
            line,
            expectedLineDisplay,
            actualLineDisplay,
            escapeForDisplay(expectedSnippet),
            escapeForDisplay(actualSnippet)
        );
    }

    private String escapeChar(char c) {
        return switch (c) {
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case ' ' -> "SPACE";
            case '�' -> "EOF";
            default -> String.valueOf(c);
        };
    }

    private String escapeForDisplay(String s) {
        return s.replace("\n", "⏎\n")
                .replace("\r", "␍")
                .replace("\t", "→   ");
    }

    private String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
    }
}
