package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.registerOrReplaceServiceInstance
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
 * A version manager installs Hex under its own Elixir install rather than `~/.mix`, and only exports
 * `MIX_HOME` inside the shim's exec-env - which these command lines bypass by construction, since
 * they run the SDK's `mix` directly. `SdkPathsTest` pins the derivation itself, but nothing pinned
 * that [Mix.commandLine] applies it, so dropping that call left the suite green while sending `mix`
 * back to `~/.mix`, where the archive is not.
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
    fun testMixHomeIsDerivedFromAVersionManagerElixirSdkHome() {
        val elixirHome = createVersionManagerElixirHome()
        val environment = commandLine(elixirHome).environment

        val expectedMixHome = elixirHome.resolve(".mix")
        assertEquals(normalize(expectedMixHome.absolutePathString()), normalize(environment["MIX_HOME"]))
        assertEquals(
            normalize(expectedMixHome.resolve("archives").absolutePathString()),
            normalize(environment["MIX_ARCHIVES"]),
        )
    }

    @Test
    fun testMixHomeIsLeftAloneForAnElixirSdkHomeOutsideAVersionManager() {
        val environment = commandLine(createElixirHome()).environment

        assertNull("MIX_HOME was set for a home no version manager owns", environment["MIX_HOME"])
        assertNull("MIX_ARCHIVES was set for a home no version manager owns", environment["MIX_ARCHIVES"])
    }

    private fun commandLine(elixirHome: Path): GeneralCommandLine =
        runReadAction {
            Mix.commandLine(
                environment = emptyMap(),
                workingDirectory = null,
                elixirSdk = mockSdk(ElixirSdkType.instance, ELIXIR_VERSION, elixirHome, erlangSdk),
            )
        }

    private fun normalize(value: String?): String? = value?.let { FileUtil.toSystemIndependentName(it) }

    private fun mockSdk(sdkType: SdkType, version: String, homePath: Path, erlangSdk: Sdk? = null): Sdk {
        val sdk = ProjectJdkImpl(version, sdkType, homePath.toString(), version)
        if (erlangSdk != null) {
            erlangSdkByElixirSdk[sdk] = erlangSdk
        }
        return sdk
    }

    /** An asdf-shaped home, since `detectSource` recognises a version manager by path shape alone. */
    private fun createVersionManagerElixirHome(): Path =
        populateElixirHome(
            Files.createDirectories(
                trackPath(Files.createTempDirectory("asdf-user-home"))
                    .resolve(".asdf/installs/elixir/$ELIXIR_VERSION")
            )
        )

    private fun createElixirHome(): Path =
        populateElixirHome(trackPath(Files.createTempDirectory("elixir-home")))

    private fun populateElixirHome(home: Path): Path {
        Files.createDirectories(home.resolve("bin"))
        val libDir = home.resolve("bin/../lib")
        listOf("eex", "elixir", "mix").forEach { lib -> Files.createDirectories(libDir.resolve(lib).resolve("ebin")) }
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

    private companion object {
        private const val ELIXIR_VERSION = "1.18.4-otp-27"
    }
}
