package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.registerOrReplaceServiceInstance
import com.intellij.util.system.OS
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.erlang_dependent.ErlangSdkResolver
import org.elixir_lang.sdk.erlang_dependent.ErlangSdkResult
import org.elixir_lang.sdk.erlang_dependent.MissingErlangSdkReason
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType

/**
 * The IEx Mix configuration once passed a bare `-S mix`, which the child process resolved through
 * `PATH` - under a shim-based version manager that is a shell script, and Elixir fails parsing it as
 * Elixir source. Nothing else in the suite builds a run configuration's command line, so these tests
 * pin the SDK-derived absolute path that replaced it.
 */
class MixTest : PlatformTestCase() {
    private val erlangSdkByElixirSdk: MutableMap<Sdk, Sdk> = mutableMapOf()
    private val createdPaths: MutableList<Path> = mutableListOf()
    private lateinit var erlangSdk: Sdk

    override fun setUp() {
        super.setUp()

        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            ErlangSdkResolver::class.java,
            ErlangSdkResolverMockImpl(erlangSdkByElixirSdk),
            testRootDisposable,
        )

        erlangSdk = mockSdk(ErlangSdkType.instance, "28.0.2", createErlangHome())
    }

    override fun tearDown() {
        try {
            createdPaths.forEach { path -> FileUtil.delete(path.toFile()) }
            createdPaths.clear()
            erlangSdkByElixirSdk.clear()
        } finally {
            super.tearDown()
        }
    }

    @Test
    fun testMixPathIsAbsoluteAndDerivedFromTheElixirSdkHome() {
        val elixirHome = createElixirHome()
        val parameters = commandLine(elixirHome).parametersList.parameters

        val expectedMixPath = elixirHome.resolve("bin").resolve(mixExecutableFilename()).absolutePathString()
        val sIndex = parameters.indexOf("-S")
        assertTrue("Expected -S in ${parameters.joinToString(" ")}", sIndex >= 0 && sIndex + 1 < parameters.size)
        assertEquals(normalize(expectedMixPath), normalize(parameters[sIndex + 1]))
        assertTrue("mix path is not absolute", Path.of(parameters[sIndex + 1]).isAbsolute)
    }

    @Test
    fun testMixIsNotLeftForThePathToResolve() {
        val parameters = commandLine(createElixirHome()).parametersList.parameters

        assertFalse(
            "Bare \"mix\" is resolved through PATH by the child process",
            parameters.contains("mix"),
        )
    }

    private fun commandLine(elixirHome: Path): GeneralCommandLine =
        runReadAction {
            Mix.commandLine(
                environment = emptyMap(),
                workingDirectory = null,
                elixirSdk = mockSdk(ElixirSdkType.instance, "1.18.4-otp-27", elixirHome, erlangSdk),
                erlArgumentList = emptyList(),
                iexArgumentList = emptyList(),
            )
        }

    private fun mixExecutableFilename(): String = if (OS.CURRENT == OS.Windows) "mix.bat" else "mix"

    private fun normalize(value: String): String = FileUtil.toSystemIndependentName(value)

    private fun mockSdk(sdkType: SdkType, version: String, homePath: Path, erlangSdk: Sdk? = null): Sdk {
        val sdk = ProjectJdkImpl(version, sdkType, homePath.toString(), version)
        if (erlangSdk != null) {
            erlangSdkByElixirSdk[sdk] = erlangSdk
        }
        return sdk
    }

    private fun createElixirHome(): Path {
        val home = trackPath(Files.createTempDirectory("elixir-home"))
        Files.createDirectories(home.resolve("bin"))
        Files.createDirectories(home.resolve("bin/../lib").resolve("elixir").resolve("ebin"))
        return home
    }

    private fun createErlangHome(): Path {
        val home = trackPath(Files.createTempDirectory("erlang-home"))
        Files.createFile(Files.createDirectories(home.resolve("bin")).resolve("erl"))
        return home
    }

    private fun trackPath(path: Path): Path {
        createdPaths.add(path)
        return path
    }

    private class ErlangSdkResolverMockImpl(
        private val erlangSdkByElixirSdk: Map<Sdk, Sdk>
    ) : ErlangSdkResolver {
        override fun resolveErlangSdkResult(
            elixirSdk: Sdk,
            sdkModel: SdkModel?
        ): ErlangSdkResult {
            val erlangSdk = erlangSdkByElixirSdk[elixirSdk]
                ?: return ErlangSdkResult.Missing(
                    elixirSdk,
                    MissingErlangSdkReason.NOT_FOUND,
                )
            return ErlangSdkResult.Success(erlangSdk)
        }
    }
}
