package org.elixir_lang.sdk

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.Version
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.jps.shared.sdk.SdkPaths
import java.io.File
import java.nio.file.Paths

/**
 * Tests for SdkHomeScan consolidated SDK scanning logic.
 *
 * Tests cover:
 * - Config validation and behavior
 * - Path transformation logic
 * - WSL distribution filtering
 * - Integration with mocked file systems
 */
class SdkHomeScanTest : PlatformTestCase() {

    // ========== Config Validation Tests ==========

    fun `test elixir config uses correct tool name`() {
        val config = createElixirConfig()
        assertEquals("elixir", config.toolName)
    }

    fun `test erlang config uses correct tool name`() {
        val config = createErlangConfig()
        assertEquals("erlang", config.toolName)
    }

    fun `test elixir config has null kerl transforms`() {
        val config = createElixirConfig()
        assertNull("Elixir should not support kerl", config.kerlTransform)
        assertNull("Elixir should not support Travis CI kerl", config.travisCIKerlTransform)
    }

    fun `test erlang config has non-null kerl transforms`() {
        val config = createErlangConfig()
        assertNotNull("Erlang should support kerl", config.kerlTransform)
        assertNotNull("Erlang should support Travis CI kerl", config.travisCIKerlTransform)
    }

    fun `test elixir config windows paths`() {
        val config = createElixirConfig()
        assertEquals("C:\\Program Files (x86)\\Elixir", config.windowsDefaultPath)
        assertEquals("C:\\Program Files\\Elixir", config.windows32BitPath)
    }

    fun `test erlang config windows paths`() {
        val config = createErlangConfig()
        assertEquals("C:\\Program Files\\erl9.0", config.windowsDefaultPath)
        assertNull("Erlang uses same path for 32-bit", config.windows32BitPath)
    }

    fun `test elixir linux system home paths`() {
        assertEquals(
            listOf("/usr/local/lib/elixir", "/usr/lib/elixir"),
            SdkHomePaths.linuxSystemHomePaths("elixir")
        )
    }

    fun `test erlang linux system home paths`() {
        assertEquals(
            listOf("/usr/local/lib/erlang", "/usr/lib/erlang"),
            SdkHomePaths.linuxSystemHomePaths("erlang")
        )
    }

    fun `test mergeLinuxSystemHomePaths adds only directories that exist`() {
        withTempRoot { root ->
            val present = File(root, "usr/lib/elixir")
            assertTrue("could not create $present", present.mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeLinuxSystemHomePaths(homePathByVersion, "elixir") { File(root, it).path }

            // /usr/local/lib/elixir was a candidate too, but was never created.
            assertEquals(
                setOf(FileUtil.toSystemIndependentName(present.absolutePath)),
                homePathByVersion.values.map { FileUtil.toSystemIndependentName(it) }.toSet()
            )
        }
    }

    fun `test mergeLinuxSystemHomePaths covers every candidate`() {
        withTempRoot { root ->
            val candidates = SdkHomePaths.linuxSystemHomePaths("elixir")
            candidates.forEach { assertTrue(File(root, it).mkdirs()) }

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeLinuxSystemHomePaths(homePathByVersion, "elixir") { File(root, it).path }

            assertEquals(
                "every path linuxSystemHomePaths names must be scanned",
                candidates.map { FileUtil.toSystemIndependentName(File(root, it).absolutePath) }.toSet(),
                homePathByVersion.values.map { FileUtil.toSystemIndependentName(it) }.toSet()
            )
        }
    }

    fun `test mergeLinuxSystemHomePaths skips an unreachable path`() {
        withTempRoot { root ->
            assertTrue(File(root, "usr/lib/elixir").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            // A WSL distribution that cannot map the path returns null, as convertLinuxPathToWindowsUnc does.
            SdkHomePaths.mergeLinuxSystemHomePaths(homePathByVersion, "elixir") { null }

            assertEmpty(homePathByVersion.values)
        }
    }

    // ========== Transform Behavior Tests ==========

    fun `test null homebrewTransform uses identity`() {
        val config = createElixirConfig()
        // Kotlin lambdas use invoke(), not apply()
        val transform: (File) -> File = config.homebrewTransform ?: { it }
        val testFile = File("/test")
        assertSame(testFile, transform(testFile))
    }

    fun `test null nixTransform uses identity`() {
        val config = createElixirConfig()
        // Kotlin lambdas use invoke(), not apply()
        val transform: (File) -> File = config.nixTransform ?: { it }
        val testFile = File("/nix/store/test")
        assertSame(testFile, transform(testFile))
    }

    fun `test erlang homebrewTransform adds lib slash erlang`() {
        val config = createErlangConfig()
        assertNotNull(config.homebrewTransform)

        val versionPath = File("/usr/local/Cellar/erlang/27.0")
        val expected = File("/usr/local/Cellar/erlang/27.0/lib/erlang")
        val result = config.homebrewTransform!!(versionPath)

        assertEquals(expected, result)
    }

    fun `test erlang nixTransform adds lib slash erlang`() {
        val config = createErlangConfig()
        assertNotNull(config.nixTransform)

        val versionPath = File("/nix/store/xyz-erlang-27")
        val expected = File("/nix/store/xyz-erlang-27/lib/erlang")
        val result = config.nixTransform!!(versionPath)

        assertEquals(expected, result)
    }

    fun `test erlang kerlTransform is identity`() {
        val config = createErlangConfig()
        assertNotNull(config.kerlTransform)

        val testFile = File("/home/user/.kerl/builds/27.0")
        val result = config.kerlTransform!!(testFile)

        assertEquals(testFile, result)
    }

    fun `test erlang travisCIKerlTransform is identity`() {
        val config = createErlangConfig()
        assertNotNull(config.travisCIKerlTransform)

        val testFile = File("/home/travis/otp/27.0")
        val result = config.travisCIKerlTransform!!(testFile)

        assertEquals(testFile, result)
    }

    // ========== Integration Tests with Mocked File System ==========

    fun `test homePathByVersion returns empty map for unconfigured platform`() {
        val config = createElixirConfig()
        // This test runs on whatever platform the CI is on
        // Just verify it returns a map (may be empty if no SDKs installed)
        val result = SdkHomeScan.homePathByVersion(null, config)
        assertNotNull(result)
    }

    fun `test homePathByVersion with path parameter passes through`() {
        val config = createElixirConfig()
        val testPath = Paths.get("/test/project")

        // Just verify it doesn't crash with a path parameter
        val result = SdkHomeScan.homePathByVersion(testPath, config)
        assertNotNull(result)
    }

    fun `test homePathByVersion sorts versions in descending order`() {
        val config = createElixirConfig()
        val result = SdkHomeScan.homePathByVersion(null, config)

        // Verify the map maintains descending order
        val versions = result.keys.toList()
        if (versions.size > 1) {
            for (i in 0 until versions.size - 1) {
                assertTrue(
                    "Versions should be in descending order",
                    versions[i].version >= versions[i + 1].version
                )
            }
        }
    }

    // ========== Edge Cases ==========

    fun `test config with all null transforms`() {
        val config = SdkHomeScan.Config(
            toolName = "test",
            nixPattern = SdkHomePaths.nixPattern("test"),
            windowsDefaultPath = null,
            windows32BitPath = null,
            homebrewTransform = null,
            nixTransform = null,
            kerlTransform = null,
            travisCIKerlTransform = null,
            elixirInstallScriptDirName = "test"
        )

        assertEquals("test", config.toolName)
        assertNull(config.homebrewTransform)
        assertNull(config.nixTransform)
        assertNull(config.kerlTransform)
        assertNull(config.travisCIKerlTransform)
    }

    fun `test config with all non-null transforms`() {
        val testTransform: (File) -> File = { it }

        val config = SdkHomeScan.Config(
            toolName = "test",
            nixPattern = SdkHomePaths.nixPattern("test"),
            windowsDefaultPath = "C:\\test",
            windows32BitPath = "C:\\test32",
            homebrewTransform = testTransform,
            nixTransform = testTransform,
            kerlTransform = testTransform,
            travisCIKerlTransform = testTransform,
            elixirInstallScriptDirName = "test"
        )

        assertNotNull(config.homebrewTransform)
        assertNotNull(config.nixTransform)
        assertNotNull(config.kerlTransform)
        assertNotNull(config.travisCIKerlTransform)
    }

    // ========== Dispatch ==========

    fun `test homePathByVersion wires the version managers into the scan`() {
        withTempRoot { home ->
            assertTrue(File(home, "${SdkPaths.ASDF_INSTALLS_PATH_FROM_HOME}/elixir/1.20.3").mkdirs())
            assertTrue(File(home, "${SdkPaths.MISE_POSIX_PATH_FROM_HOME}/elixir/1.19.5").mkdirs())
            assertTrue(
                File(home, "${SdkPaths.ELIXIR_INSTALL_INSTALLS_PATH_FROM_HOME}/elixir/1.18.4").mkdirs()
            )

            val found = withUserHome(home.path) {
                SdkHomeScan.homePathByVersion(null, createElixirConfig())
            }

            // One scan, one source list: a source that stops being wired in fails here rather than
            // going missing on whichever platform nobody checked.
            assertTrue(
                "expected asdf, mise and elixir-install homes, got ${found.values}",
                found.keys.map { it.version }
                    .containsAll(listOf(Version(1, 20, 3), Version(1, 19, 5), Version(1, 18, 4)))
            )
        }
    }

    private fun withTempRoot(body: (File) -> Unit) {
        val root = FileUtil.createTempDirectory("sdkHome", null)

        try {
            body(root)
        } finally {
            FileUtil.delete(root)
        }
    }

    private fun <T> withUserHome(userHome: String, body: () -> T): T {
        val previous = System.getProperty("user.home")
        System.setProperty("user.home", userHome)

        try {
            return body()
        } finally {
            if (previous != null) System.setProperty("user.home", previous)
        }
    }

    // ========== Helper Methods ==========

    private fun createElixirConfig() = SdkHomeScan.Config(
        toolName = "elixir",
        nixPattern = SdkHomePaths.nixPattern("elixir"),
        windowsDefaultPath = "C:\\Program Files (x86)\\Elixir",
        windows32BitPath = "C:\\Program Files\\Elixir",
        homebrewTransform = null,
        nixTransform = null,
        kerlTransform = null,
        travisCIKerlTransform = null,
        elixirInstallScriptDirName = "elixir"
    )

    private fun createErlangConfig() = SdkHomeScan.Config(
        toolName = "erlang",
        nixPattern = SdkHomePaths.nixPattern("erlang"),
        windowsDefaultPath = "C:\\Program Files\\erl9.0",
        windows32BitPath = null,
        homebrewTransform = { File(it, "lib/erlang") },
        nixTransform = { File(it, "lib/erlang") },
        kerlTransform = { it },
        travisCIKerlTransform = { it },
        elixirInstallScriptDirName = "otp"
    )
}
