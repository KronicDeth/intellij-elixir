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
            listOf("/usr/local/lib/elixir", "/usr/lib/elixir", "/usr/lib64/elixir"),
            SdkHomePaths.linuxSystemHomePaths("elixir")
        )
    }

    fun `test erlang linux system home paths`() {
        assertEquals(
            listOf("/usr/local/lib/erlang", "/usr/lib/erlang", "/usr/lib64/erlang"),
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

    private fun withTempRoot(body: (File) -> Unit) {
        val root = FileUtil.createTempDirectory("sdkHome", null)

        try {
            body(root)
        } finally {
            FileUtil.delete(root)
        }
    }

    // ========== Transform Behavior Tests ==========

    fun `test both tools resolve a prefix with the same rule`() {
        withTempRoot { root ->
            val elixirPrefix = File(root, "elixir/1.20.3")
            val erlangPrefix = File(root, "erlang/29.0.5")
            assertTrue(File(elixirPrefix, "lib/elixir/bin").mkdirs())
            assertTrue(File(erlangPrefix, "lib/erlang/bin").mkdirs())

            assertEquals(
                File(elixirPrefix, "lib/elixir"),
                createElixirConfig().toolHome(elixirPrefix)
            )
            assertEquals(
                File(erlangPrefix, "lib/erlang"),
                createErlangConfig().toolHome(erlangPrefix)
            )
        }
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
            kerlTransform = null,
            travisCIKerlTransform = null,
            elixirInstallScriptDirName = "test"
        )

        assertEquals("test", config.toolName)
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
            kerlTransform = testTransform,
            travisCIKerlTransform = testTransform,
            elixirInstallScriptDirName = "test"
        )

        assertNotNull(config.kerlTransform)
        assertNotNull(config.travisCIKerlTransform)
    }

    // ========== /usr/share, version-scoped (Fedora, RHEL) ==========

    fun `test mergeSystemShare discovers a versioned home`() {
        withTempRoot { root ->
            val home = File(root, "elixir/1.20.3")
            assertTrue("could not create $home", home.mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeSystemShare(homePathByVersion, "elixir", root.absolutePath)

            val key = homePathByVersion.keys.single()
            assertEquals(Version(1, 20, 3), key.version)
            assertEquals("1.20.3", key.qualifier)
            assertNull("a system install is not attributed to an installer", key.source)
            assertEquals(
                FileUtil.toSystemIndependentName(home.absolutePath),
                FileUtil.toSystemIndependentName(homePathByVersion.getValue(key))
            )
        }
    }

    fun `test mergeSystemShare finds every version side by side`() {
        withTempRoot { root ->
            assertTrue(File(root, "elixir/1.19.5").mkdirs())
            assertTrue(File(root, "elixir/1.20.3").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeSystemShare(homePathByVersion, "elixir", root.absolutePath)

            assertEquals(
                setOf(Version(1, 19, 5), Version(1, 20, 3)),
                homePathByVersion.keys.map { it.version }.toSet()
            )
        }
    }

    fun `test mergeSystemShare ignores a non-version directory`() {
        withTempRoot { root ->
            // Fedora puts Erlang's ERL_LIBS at /usr/share/erlang/lib, which is not an SDK home.
            assertTrue(File(root, "erlang/lib").mkdirs())
            assertTrue(File(root, "erlang/28.1").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeSystemShare(homePathByVersion, "erlang", root.absolutePath)

            assertEquals(
                setOf(FileUtil.toSystemIndependentName(File(root, "erlang/28.1").absolutePath)),
                homePathByVersion.values.map { FileUtil.toSystemIndependentName(it) }.toSet()
            )
        }
    }

    fun `test mergeSystemShare ignores another tool`() {
        withTempRoot { root ->
            assertTrue(File(root, "erlang/28.1").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeSystemShare(homePathByVersion, "elixir", root.absolutePath)

            assertEmpty(homePathByVersion.values)
        }
    }

    fun `test mergeSystemShare tolerates a missing share root`() {
        withTempRoot { root ->
            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeSystemShare(homePathByVersion, "elixir", File(root, "absent").absolutePath)

            assertEmpty(homePathByVersion.values)
        }
    }

    // ========== Homebrew version prefix ==========

    fun `test toolHomePath descends into the nested home`() {
        withTempRoot { root ->
            // make install PREFIX=<prefix>, the layout Homebrew has produced since 2024-09-22
            val prefix = File(root, "elixir/1.18.0")
            assertTrue(File(prefix, "lib/elixir/bin").mkdirs())
            assertTrue(File(prefix, "lib/elixir/lib/elixir/ebin").mkdirs())
            assertTrue(File(prefix, "bin").mkdirs())

            assertEquals(
                FileUtil.toSystemIndependentName(File(prefix, "lib/elixir").absolutePath),
                FileUtil.toSystemIndependentName(SdkHomePaths.toolHomePath(prefix, "elixir").absolutePath)
            )
        }
    }

    fun `test toolHomePath keeps the prefix for the pre-2024 layout`() {
        withTempRoot { root ->
            // bin.install plus per-app lib/<app>/ebin: lib/elixir exists but has no bin of its own
            val prefix = File(root, "elixir/1.17.3")
            assertTrue(File(prefix, "bin").mkdirs())
            assertTrue(File(prefix, "lib/elixir/ebin").mkdirs())
            assertTrue(File(prefix, "lib/eex/ebin").mkdirs())

            assertEquals(
                "the prefix is already the home; descending would break installs predating 2024-09-22",
                FileUtil.toSystemIndependentName(prefix.absolutePath),
                FileUtil.toSystemIndependentName(SdkHomePaths.toolHomePath(prefix, "elixir").absolutePath)
            )
        }
    }

    fun `test toolHomePath keeps the prefix when there is no lib at all`() {
        withTempRoot { root ->
            val prefix = File(root, "elixir/1.18.0")
            assertTrue(File(prefix, "bin").mkdirs())

            assertEquals(
                FileUtil.toSystemIndependentName(prefix.absolutePath),
                FileUtil.toSystemIndependentName(SdkHomePaths.toolHomePath(prefix, "elixir").absolutePath)
            )
        }
    }

    fun `test homebrewCellarPaths names every prefix Homebrew uses`() {
        val cellars = SdkHomePaths.homebrewCellarPaths("/home/me") { it }

        // Nothing else asserts which roots the scan reaches, so dropping one here is invisible.
        assertEquals(
            listOf(
                "/usr/local/Cellar",
                "/opt/homebrew/Cellar",
                "/home/linuxbrew/.linuxbrew/Cellar",
                "/home/me/.linuxbrew/Cellar"
            ).map { FileUtil.toSystemIndependentName(it) },
            cellars.map { FileUtil.toSystemIndependentName(it) }
        )
    }

    fun `test homebrewCellarPaths maps every root through the reader`() {
        // A distribution root, not a UNC path: `File` keeps a leading `//` only on Windows, so a
        // UNC-shaped root would assert the home-derived Cellar differently on each OS.
        val cellars = SdkHomePaths.homebrewCellarPaths("/mnt/distro/home/me") { "/mnt/distro$it" }

        assertEquals(
            "a WSL scan must reach the same roots through its own mapping",
            listOf(
                "/mnt/distro/usr/local/Cellar",
                "/mnt/distro/opt/homebrew/Cellar",
                "/mnt/distro/home/linuxbrew/.linuxbrew/Cellar",
                "/mnt/distro/home/me/.linuxbrew/Cellar"
            ),
            cellars.map { FileUtil.toSystemIndependentName(it) }
        )
    }

    fun `test homebrewCellarPaths drops roots the reader cannot reach`() {
        val cellars = SdkHomePaths.homebrewCellarPaths(null) { null }

        assertEmpty(cellars)
    }

    fun `test mergeHomebrew scans every explicit cellar`() {
        withTempRoot { root ->
            val intel = File(root, "usr/local/Cellar")
            val linuxbrew = File(root, "home/linuxbrew/.linuxbrew/Cellar")
            assertTrue(File(intel, "elixir/1.17.3/bin").mkdirs())
            assertTrue(File(linuxbrew, "elixir/1.18.4/bin").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeHomebrew(
                homePathByVersion, "elixir", { SdkHomePaths.toolHomePath(it, "elixir") },
                listOf(intel.path, linuxbrew.path)
            )

            assertEquals(
                "Homebrew on Linux and WSL uses a .linuxbrew prefix, not a macOS one",
                setOf(Version(1, 17, 3), Version(1, 18, 4)),
                homePathByVersion.keys.map { it.version }.toSet()
            )
        }
    }

    fun `test mergeHomebrew tolerates a cellar that does not exist`() {
        withTempRoot { root ->
            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeHomebrew(
                homePathByVersion, "elixir", { it }, listOf(File(root, "absent").path)
            )

            assertEmpty(homePathByVersion.values)
        }
    }

    fun `test mergeHomebrew finds version-pinned formulae`() {
        withTempRoot { root ->
            val cellar = File(root, "Cellar")
            // erlang@25..28 are real homebrew-core formulae and live beside the unpinned one
            assertTrue(File(cellar, "erlang/28.1.2/lib/erlang/bin").mkdirs())
            assertTrue(File(cellar, "erlang@26/26.2.5/lib/erlang/bin").mkdirs())
            assertTrue(File(cellar, "erlang@27/27.3.4/lib/erlang/bin").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeHomebrew(
                homePathByVersion, "erlang", { File(it, "lib/erlang") }, listOf(cellar.path)
            )

            assertEquals(
                "a pinned Erlang is how an OTP release is held for a given Elixir",
                setOf(Version(28, 1, 2), Version(26, 2, 5), Version(27, 3, 4)),
                homePathByVersion.keys.map { it.version }.toSet()
            )
        }
    }

    fun `test mergeHomebrew keeps every version of one formula`() {
        withTempRoot { root ->
            val cellar = File(root, "Cellar")
            // Homebrew keeps superseded versions until brew cleanup runs
            assertTrue(File(cellar, "elixir/1.18.4/lib/elixir/bin").mkdirs())
            assertTrue(File(cellar, "elixir/1.19.5/lib/elixir/bin").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeHomebrew(
                homePathByVersion, "elixir", { SdkHomePaths.toolHomePath(it, "elixir") },
                listOf(cellar.path)
            )

            assertEquals(
                setOf(Version(1, 18, 4), Version(1, 19, 5)),
                homePathByVersion.keys.map { it.version }.toSet()
            )
        }
    }

    fun `test mergeHomebrew does not confuse another tool with a pinned prefix`() {
        withTempRoot { root ->
            val cellar = File(root, "Cellar")
            assertTrue(File(cellar, "erlang@27/27.3.4/lib/erlang/bin").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeHomebrew(
                homePathByVersion, "elixir", { it }, listOf(cellar.path)
            )

            assertEmpty(homePathByVersion.values)
        }
    }

    // ========== Chooser selection (adjustSelectedSdkHome) ==========

    /** Layout of `make install PREFIX=<prefix>`: home in lib/<tool>, symlinks in <prefix>/bin. */
    private fun installedUnderPrefix(prefix: File, toolName: String): File {
        val home = File(prefix, "lib/$toolName")
        assertTrue(File(home, "bin").mkdirs())
        assertTrue(File(home, "lib/$toolName/ebin").mkdirs())
        assertTrue(File(prefix, "bin").mkdirs())

        return home
    }

    fun `test choosing a Homebrew version prefix resolves to the home`() {
        withTempRoot { root ->
            val prefix = File(root, "Cellar/elixir/1.20.3")
            val home = installedUnderPrefix(prefix, "elixir")

            assertEquals(home.path, SdkHomePaths.adjustSelectedSdkHome(prefix.path, "elixir"))
        }
    }

    fun `test choosing bare usr resolves to the distro home`() {
        withTempRoot { root ->
            val usr = File(root, "usr")
            val home = installedUnderPrefix(usr, "elixir")

            assertEquals(home.path, SdkHomePaths.adjustSelectedSdkHome(usr.path, "elixir"))
        }
    }

    fun `test choosing a prefix resolves for erlang too`() {
        withTempRoot { root ->
            val prefix = File(root, "Cellar/erlang/29.0.5")
            val home = installedUnderPrefix(prefix, "erlang")

            assertEquals(home.path, SdkHomePaths.adjustSelectedSdkHome(prefix.path, "erlang"))
        }
    }

    fun `test choosing bin lib or src steps up to the home`() {
        withTempRoot { root ->
            // a home selected by one of its own children, not a prefix
            val home = File(root, "elixir")
            assertTrue(File(home, "bin").mkdirs())
            assertTrue(File(home, "lib/elixir/ebin").mkdirs())
            assertTrue(File(home, "src").mkdirs())

            for (child in listOf("bin", "lib", "src")) {
                assertEquals(
                    "selecting $child should resolve to its parent",
                    home.path,
                    SdkHomePaths.adjustSelectedSdkHome(File(home, child).path, "elixir")
                )
            }
        }
    }

    fun `test choosing a home leaves it alone`() {
        withTempRoot { root ->
            val home = File(root, "elixir")
            assertTrue(File(home, "bin").mkdirs())
            assertTrue(File(home, "lib/elixir/ebin").mkdirs())

            assertEquals(home.path, SdkHomePaths.adjustSelectedSdkHome(home.path, "elixir"))
        }
    }

    fun `test choosing a kiex version directory resolves to the home`() {
        withTempRoot { root ->
            // kiex keeps a false bin beside lib/elixir; the general rule covers it
            val versionDir = File(root, "elixirs/elixir-1.9.1")
            val home = installedUnderPrefix(versionDir, "elixir")

            assertEquals(home.path, SdkHomePaths.adjustSelectedSdkHome(versionDir.path, "elixir"))
            assertEquals(
                home.path,
                SdkHomePaths.adjustSelectedSdkHome(File(versionDir, "bin").path, "elixir")
            )
        }
    }

    fun `test choosing an erlang home child steps up`() {
        withTempRoot { root ->
            // an Erlang home keeps releases and usr beside bin and lib
            val home = File(root, "erlang")
            assertTrue(File(home, "bin").mkdirs())
            assertTrue(File(home, "lib/stdlib-7.0/ebin").mkdirs())
            assertTrue(File(home, "releases/29").mkdirs())
            assertTrue(File(home, "usr/include").mkdirs())

            for (child in listOf("bin", "lib", "releases")) {
                assertEquals(
                    "selecting $child should resolve to its parent",
                    home.path,
                    SdkHomePaths.adjustSelectedSdkHome(File(home, child).path, "erlang")
                )
            }
        }
    }

    fun `test usr is not treated as a home child`() {
        withTempRoot { root ->
            // /usr is an install prefix in its own right; stepping up from it would reach the
            // filesystem root and lose the distribution install underneath it
            val usr = File(root, "usr")
            val home = installedUnderPrefix(usr, "elixir")

            assertEquals(home.path, SdkHomePaths.adjustSelectedSdkHome(usr.path, "elixir"))
        }
    }

    fun `test choosing an unrelated bin or src still steps up`() {
        withTempRoot { root ->
            // documents the cost of matching on name alone: an unrelated child resolves to its
            // parent, which the chooser then rejects as an invalid home
            val unrelated = File(root, "some/project/src")
            assertTrue(unrelated.mkdirs())

            assertEquals(
                File(root, "some/project").path,
                SdkHomePaths.adjustSelectedSdkHome(unrelated.path, "elixir")
            )
        }
    }

    fun `test choosing a file is left alone`() {
        withTempRoot { root ->
            val file = File(root, "elixir.txt")
            assertTrue(file.createNewFile())

            assertEquals(file.path, SdkHomePaths.adjustSelectedSdkHome(file.path, "elixir"))
        }
    }

    fun `test choosing a missing path is left alone`() {
        withTempRoot { root ->
            val missing = File(root, "absent")
            assertEquals(missing.path, SdkHomePaths.adjustSelectedSdkHome(missing.path, "elixir"))
        }
    }

    fun `test choosing a filesystem root is left alone`() {
        val root = File(File("/").absolutePath)
        // parentFile is null here; the fallback must return the selection rather than throw
        assertEquals(
            SdkHomePaths.toolHomePath(root, "elixir").path,
            SdkHomePaths.adjustSelectedSdkHome(root.path, "elixir")
        )
    }

    // ========== Version managers ==========

    private fun assertFindsVersionedHome(
        installsFromHome: String,
        merge: (MutableMap<SdkHomeKey, String>, String) -> Unit,
    ) {
        withTempRoot { home ->
            val installed = File(home, "$installsFromHome/elixir/1.20.3")
            assertTrue("could not create $installed", installed.mkdirs())
            // a sibling tool must not be picked up for elixir
            assertTrue(File(home, "$installsFromHome/erlang/29.0.5").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            merge(homePathByVersion, home.path)

            assertEquals(
                setOf(FileUtil.toSystemIndependentName(installed.absolutePath)),
                homePathByVersion.values.map { FileUtil.toSystemIndependentName(it) }.toSet()
            )
            assertEquals(Version(1, 20, 3), homePathByVersion.keys.single().version)
        }
    }

    fun `test mergeASDF finds an installed version`() {
        assertFindsVersionedHome(SdkPaths.ASDF_INSTALLS_PATH_FROM_HOME) { map, home ->
            SdkHomePaths.mergeASDF(map, "elixir", home)
        }
    }

    fun `test mergeMise finds an installed version`() {
        assertFindsVersionedHome(SdkPaths.MISE_POSIX_PATH_FROM_HOME) { map, home ->
            SdkHomePaths.mergeMise(map, "elixir", home)
        }
    }

    fun `test mergeMise finds a Windows-layout install`() {
        assertFindsVersionedHome(SdkPaths.MISE_WINDOWS_PATH_FROM_HOME) { map, home ->
            SdkHomePaths.mergeMise(map, "elixir", home)
        }
    }

    fun `test mergeElixirInstallScript finds an installed version`() {
        assertFindsVersionedHome(SdkPaths.ELIXIR_INSTALL_INSTALLS_PATH_FROM_HOME) { map, home ->
            SdkHomePaths.mergeElixirInstallScript(map, "elixir", home)
        }
    }

    fun `test a version manager attributes the source it came from`() {
        withTempRoot { home ->
            assertTrue(File(home, "${SdkPaths.ASDF_INSTALLS_PATH_FROM_HOME}/elixir/1.20.3").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeASDF(homePathByVersion, "elixir", home.path)

            assertEquals(SdkPaths.SOURCE_NAME_ASDF, homePathByVersion.keys.single().source)
        }
    }

    fun `test a version manager tolerates a missing install root`() {
        withTempRoot { home ->
            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeASDF(homePathByVersion, "elixir", File(home, "absent").path)

            assertEmpty(homePathByVersion.values)
        }
    }

    // ========== Nix store ==========

    fun `test mergeNixStore finds a derivation and reads its version`() {
        withTempRoot { store ->
            assertTrue(File(store, "abc123-elixir-1.20.3").mkdirs())
            assertTrue(File(store, "def456-erlang-29.0.5").mkdirs())
            assertTrue(File(store, "ghi789-elixir-nodots").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeNixStore(
                homePathByVersion, SdkHomePaths.nixPattern("elixir"), { it }, store.path
            )

            val key = homePathByVersion.keys.single()
            assertEquals(Version(1, 20, 3), key.version)
            assertEquals(SdkPaths.SOURCE_NAME_NIX, key.source)
        }
    }

    fun `test mergeNixStore applies the prefix rule to a derivation`() {
        withTempRoot { store ->
            val derivation = File(store, "abc123-erlang-29.0.5")
            assertTrue(File(derivation, "lib/erlang/bin").mkdirs())

            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeNixStore(
                homePathByVersion, SdkHomePaths.nixPattern("erlang"),
                { SdkHomePaths.toolHomePath(it, "erlang") }, store.path
            )

            assertEquals(
                setOf(FileUtil.toSystemIndependentName(File(derivation, "lib/erlang").absolutePath)),
                homePathByVersion.values.map { FileUtil.toSystemIndependentName(it) }.toSet()
            )
        }
    }

    fun `test mergeNixStore tolerates a missing store`() {
        withTempRoot { root ->
            val homePathByVersion = mutableMapOf<SdkHomeKey, String>()
            SdkHomePaths.mergeNixStore(
                homePathByVersion, SdkHomePaths.nixPattern("elixir"), { it },
                File(root, "absent").path
            )

            assertEmpty(homePathByVersion.values)
        }
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
        kerlTransform = null,
        travisCIKerlTransform = null,
        elixirInstallScriptDirName = "elixir"
    )

    private fun createErlangConfig() = SdkHomeScan.Config(
        toolName = "erlang",
        nixPattern = SdkHomePaths.nixPattern("erlang"),
        windowsDefaultPath = "C:\\Program Files\\erl9.0",
        windows32BitPath = null,
        kerlTransform = { it },
        travisCIKerlTransform = { it },
        elixirInstallScriptDirName = "otp"
    )
}
