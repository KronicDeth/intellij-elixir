package org.elixir_lang.cli

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.registerOrReplaceServiceInstance
import com.intellij.util.system.OS
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.sdk.erlang_dependent.ErlangSdkResolver
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@RunWith(Parameterized::class)
class CliArgumentsTest(private val targetOS: OS) : PlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "targetOS={0}")
        fun data(): List<Array<Any>> = listOf(
            arrayOf(OS.Linux),
            arrayOf(OS.Windows),
        )
    }

    private val erlangSdkByElixirSdk: MutableMap<Sdk, Sdk> = mutableMapOf()
    private val createdPaths: MutableList<Path> = mutableListOf()
    private lateinit var erlangHome: Path
    private lateinit var erlangSdk: Sdk
    private lateinit var erlangExec: String

    private val erlangVersion = "28.0.2"

    override fun setUp() {
        super.setUp()

        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            ErlangSdkResolver::class.java,
            TestErlangSdkResolver(erlangSdkByElixirSdk),
            testRootDisposable,
        )

        erlangHome = createErlangHome()
        erlangSdk = mockSdk(erlangVersion, erlangHome)
        erlangExec = expectedErlExecPath(erlangHome, targetOS)
    }

    override fun tearDown() {
        try {
            cleanup(*createdPaths.toTypedArray())
            createdPaths.clear()
            erlangSdkByElixirSdk.clear()
        } finally {
            super.tearDown()
        }
    }

    @Test
    fun testMixLegacyArguments() {
        val elixirHome = createElixirHome("eex", "elixir", "mix")
        val args = CliArguments.args(
            elixirSdk = mockSdk("1.14.5", elixirHome, erlangSdk),
            tool = CliTool.MIX,
            os = targetOS
        )
        assertNotNull(args)
        val tokens = normalize(args!!.arguments)
        assertEquals(args.exePath, erlangExec)
        assertTrue(tokens.contains("-pa"))
        assertFalse(tokens.contains("-elixir_root"))
        assertContainsSequence(tokens, listOf("-s", "elixir", "start_cli"))

        val expectedEbins = listOf("eex", "elixir", "mix").map { lib ->
            normalize(elixirHome.resolve("lib").resolve(lib).resolve("ebin").toString())
        }
        expectedEbins.forEach { ebin -> assertTrue("Missing legacy ebin path $ebin", tokens.contains(ebin)) }

        val mixPath = normalize(elixirHome.resolve("bin").resolve("mix").toString())
        val extraIndex = tokens.indexOf("-extra")
        assertTrue(extraIndex >= 0 && extraIndex + 1 < tokens.size)
        assertEquals(mixPath, tokens[extraIndex + 1])
    }

    @Test
    fun testMixModernArguments() {
        val elixirHome = createElixirHome("elixir")
        val args = CliArguments.args(
            elixirSdk = mockSdk("1.18.4-otp-27", elixirHome, erlangSdk),
            tool = CliTool.MIX,
            os = targetOS
        )
        assertNotNull(args)
        val tokens = normalize(args!!.arguments)
        assertEquals(args.exePath, erlangExec)

        val libPath = normalize(elixirHome.resolve("lib").toString())
        val elixirEbinPath = normalize(elixirHome.resolve("lib").resolve("elixir").resolve("ebin").toString())
        assertContainsSequence(tokens, listOf("-elixir_root", libPath))
        assertContainsSequence(tokens, listOf("-pa", elixirEbinPath))
        assertContainsSequence(tokens, listOf("-s", "elixir", "start_cli"))

        val mixPath = normalize(elixirHome.resolve("bin").resolve("mix").toString())
        val extraIndex = tokens.indexOf("-extra")
        assertTrue(extraIndex >= 0 && extraIndex + 1 < tokens.size)
        assertEquals(mixPath, tokens[extraIndex + 1])
    }

    @Test
    fun testIexArgumentsAcrossVersions() {
        val elixirHome = createElixirHome("elixir")
        val args150 = CliArguments.args(
            elixirSdk = mockSdk("1.15.0", elixirHome, erlangSdk),
            tool = CliTool.IEX,
            os = targetOS
        )
        assertNotNull(args150)
        val tokens150 = normalize(args150!!.arguments)
        assertEquals(args150.exePath, erlangExec)
        assertContainsSequence(tokens150, listOf("-s", "elixir", "start_cli"))
        assertTrue(tokens150.contains(":elixir.start_iex()"))

        val args161 = CliArguments.args(
            elixirSdk = mockSdk("1.16.1", elixirHome, erlangSdk),
            tool = CliTool.IEX,
            os = targetOS
        )
        assertNotNull(args161)
        val tokens161 = normalize(args161!!.arguments)
        assertContainsSequence(tokens161, listOf("-s", "elixir", "start_iex"))
        assertContainsSequence(tokens161, listOf("-user", "elixir"))
        assertFalse(tokens161.contains(":elixir.start_iex()"))

        val args170 = CliArguments.args(
            elixirSdk = mockSdk("1.17.0", elixirHome, erlangSdk),
            tool = CliTool.IEX,
            os = targetOS
        )
        assertNotNull(args170)
        val tokens170 = normalize(args170!!.arguments)
        assertContainsSequence(tokens170, listOf("-user", "elixir"))
        assertTrue(tokens170.contains("+iex"))
        assertFalse(tokens170.contains("start_iex"))
        assertFalse(tokens170.contains("start_cli"))
    }

    @Test
    fun testUnparseableVersionFallsBackToLegacy() {
        val elixirHome = createElixirHome("eex", "elixir")
        val args = CliArguments.args(
            elixirSdk = mockSdk("Unknown", elixirHome, erlangSdk),
            tool = CliTool.ELIXIR,
            os = targetOS
        )
        assertNotNull(args)
        val tokens = normalize(args!!.arguments)
        assertEquals(args.exePath, erlangExec)
        assertTrue(tokens.contains("-pa"))
        assertFalse(tokens.contains("-elixir_root"))
    }

    private fun mockSdk(version: String?, homePath: Path, erlangSdk: Sdk? = null): Sdk {
        val sdk = mock(Sdk::class.java)
        `when`(sdk.versionString).thenReturn(version)
        `when`(sdk.homePath).thenReturn(homePath.toString())
        `when`(sdk.name).thenReturn(version ?: homePath.fileName.toString())
        if (erlangSdk != null ) {
            erlangSdkByElixirSdk[sdk] = erlangSdk
        }
        return sdk
    }

    private fun expectedErlExecPath(erlangHome: Path, os: OS): String {
        val exeName = if (os == OS.Windows) "erl.exe" else "erl"
        return erlangHome.resolve("bin").resolve(exeName).absolutePathString()
    }

    private fun createElixirHome(vararg libs: String): Path {
        val home = trackPath(Files.createTempDirectory("elixir-home"))
        Files.createDirectories(home.resolve("bin"))
        val libDir = home.resolve("lib")
        libs.forEach { lib -> Files.createDirectories(libDir.resolve(lib).resolve("ebin")) }
        return home
    }

    private fun createErlangHome(): Path {
        val home = trackPath(Files.createTempDirectory("erlang-home"))
        val bin = Files.createDirectories(home.resolve("bin"), )
        Files.createFile(bin.resolve("erl"))
        val lib = Files.createDirectories(home.resolve("lib"), )
        listOf("asn1-5.0.18.1",
            "common_test-1.22.1",
            "compiler-8.1.1.1",
            "crypto-5.0.6.3",
            "debugger-5.2.1",
            "dialyzer-4.4.4.1",
            "diameter-2.2.5",
            "edoc-1.1",
            "eldap-1.2.10",
            "erl_docgen-1.2.1",
            "erl_interface-5.2.2",
            "erts-12.3.2.6",
            "et-1.6.5",
            "eunit-2.7",
            "ftp-1.1.1",
            "inets-7.5.3.1",
            "kernel-8.3.2.2",
            "megaco-4.3",
            "mnesia-4.20.4.1",
            "observer-2.11.1",
            "odbc-2.13.5",
            "os_mon-2.7.1",
            "parsetools-2.3.2",
            "public_key-1.12.0.1",
            "reltool-0.9",
            "runtime_tools-1.18",
            "sasl-4.1.2",
            "snmp-5.12",
            "ssh-4.13.2.1",
            "ssl-10.7.3.5",
            "stdlib-3.17.2.1",
            "syntax_tools-2.6",
            "tftp-1.0.3",
            "tools-3.5.2",
            "wx-2.1.4",
            "xmerl-1.3.28").forEach {
            Files.createDirectories(lib.resolve(it),)
        }
        return home
    }

    private fun cleanup(vararg paths: Path) {
        paths.forEach { path -> FileUtil.delete(path.toFile()) }
    }

    private fun normalize(values: List<String>): List<String> = values.map { normalize(it) }
    private fun normalize(value: String): String = FileUtil.toSystemIndependentName(value)

    private fun assertContainsSequence(tokens: List<String>, sequence: List<String>) {
        assertTrue(
            "Expected sequence ${sequence.joinToString(" ")} in ${tokens.joinToString(" ")}",
            containsSequence(tokens, sequence),
        )
    }

    private fun containsSequence(tokens: List<String>, sequence: List<String>): Boolean {
        if (sequence.isEmpty() || sequence.size > tokens.size) {
            return false
        }
        for (index in 0..(tokens.size - sequence.size)) {
            if (tokens.subList(index, index + sequence.size) == sequence) {
                return true
            }
        }
        return false
    }

    private fun trackPath(path: Path): Path {
        createdPaths.add(path)
        return path
    }

    private class TestErlangSdkResolver(
        private val erlangSdkByElixirSdk: Map<Sdk, Sdk>
    ) : ErlangSdkResolver {
        override fun resolveErlangSdk(elixirSdk: Sdk, sdkModel: com.intellij.openapi.projectRoots.SdkModel?): Sdk? {
            return erlangSdkByElixirSdk[elixirSdk]
        }
    }
}
