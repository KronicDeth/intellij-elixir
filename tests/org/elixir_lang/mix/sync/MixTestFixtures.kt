package org.elixir_lang.mix.sync

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

/**
 * Shared VFS fixture helpers for Mix-related tests.
 *
 * Creates representative Mix project structures - single-module, umbrella, and multi-root -
 * on the temp-dir VFS provided by [com.intellij.testFramework.fixtures.BasePlatformTestCase].
 *
 * Centralising setup here means every test class that needs a `deps/`, `_build/`, or
 * `mix.exs` layout uses the same factory methods, so a change to the expected layout only
 * needs to be made in one place.
 *
 * Usage:
 * ```kotlin
 * val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
 * val (umbrella, apps) = MixTestFixtures.createUmbrellaRoot(myFixture, "umbrella", "app_one", "app_two")
 * val roots = MixTestFixtures.createSeparateRoots(myFixture, "project_a", "project_b")
 * MixTestFixtures.addDeps(myFixture, "my_app", "phoenix", "ecto")
 * MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix", "ecto")
 * // For nested roots, pass the full relative path used in createMixRoot:
 * MixTestFixtures.addDeps(myFixture, "umbrella/apps/app_one", "phoenix")
 * ```
 */
object MixTestFixtures {

    // Minimal mix.exs content - just enough for virtualFile() to find it via package_manager detection.
    private fun mixExsContent(moduleName: String) =
        "defmodule ${moduleName}.MixProject do\n  use Mix.Project\nend\n"

    /**
     * Creates a single Mix project root at [rootPath] inside the fixture temp dir.
     *
     * Creates:
     * - `<rootPath>/mix.exs`
     *
     * Returns the root [VirtualFile].
     */
    fun createMixRoot(fixture: CodeInsightTestFixture, rootPath: String): VirtualFile {
        val root = fixture.tempDirFixture.findOrCreateDir(rootPath)
        val moduleName = rootPath.split("/").last().split("_").joinToString("") { it.replaceFirstChar(Char::uppercase) }
        fixture.tempDirFixture.createFile("$rootPath/mix.exs", mixExsContent(moduleName))
        return root
    }

    /**
     * Creates an umbrella project at [umbrellaPath] with sub-apps at
     * `<umbrellaPath>/apps/<appName>` for each name in [appNames].
     *
     * Creates:
     * - `<umbrellaPath>/mix.exs`
     * - `<umbrellaPath>/apps/<appName>/mix.exs` for each [appNames]
     *
     * Returns a [Pair] of (umbrellaRoot, list of app roots).
     */
    fun createUmbrellaRoot(
        fixture: CodeInsightTestFixture,
        umbrellaPath: String,
        vararg appNames: String,
    ): Pair<VirtualFile, List<VirtualFile>> {
        val umbrellaRoot = createMixRoot(fixture, umbrellaPath)
        val appRoots = appNames.map { appName ->
            createMixRoot(fixture, "$umbrellaPath/apps/$appName")
        }
        return umbrellaRoot to appRoots
    }

    /**
     * Creates multiple independent (non-nested) Mix project roots.
     *
     * Each root path gets a `mix.exs`. Suitable for multi-module workspace fixtures and for the
     * "same dep name across different content roots" parity tests.
     *
     * Returns the list of root [VirtualFile]s in the same order as [rootPaths].
     */
    fun createSeparateRoots(
        fixture: CodeInsightTestFixture,
        vararg rootPaths: String,
    ): List<VirtualFile> = rootPaths.map { createMixRoot(fixture, it) }

    /**
     * Adds `deps/<depName>/` directories (without `mix.exs`) under [rootPath] for each [depNames].
     *
     * [rootPath] must be the same relative path passed to [createMixRoot] - using `VirtualFile.name`
     * would silently create directories at the wrong location for nested roots such as
     * `umbrella/apps/app_one` (where `.name` is just `"app_one"`).
     *
     * Mirrors the real `deps/` layout produced by `mix deps.get`. Creates a `lib/` child
     * inside each dep root so it registers as a source root in sync tests.
     *
     * Returns the list of dep root [VirtualFile]s.
     */
    fun addDeps(
        fixture: CodeInsightTestFixture,
        rootPath: String,
        vararg depNames: String,
    ): List<VirtualFile> {
        return depNames.map { depName ->
            fixture.tempDirFixture.findOrCreateDir("$rootPath/deps/$depName").also {
                fixture.tempDirFixture.findOrCreateDir("$rootPath/deps/$depName/lib")
            }
        }
    }

    /**
     * [VirtualFile]-accepting overload of [addDeps].
     *
     * Derives the temp-dir-relative path from [root] automatically, so call sites that already
     * hold a [VirtualFile] (e.g. from [createMixRoot] or [createUmbrellaRoot]) cannot pass a
     * mismatched string path by accident.
     */
    fun addDeps(
        fixture: CodeInsightTestFixture,
        root: VirtualFile,
        vararg depNames: String,
    ): List<VirtualFile> = addDeps(fixture, relativeToTempDir(fixture, root), *depNames)

    /**
     * Adds `_build/<env>/lib/<depName>/ebin/` artifacts under [rootPath] for each [depNames].
     *
     * [rootPath] must be the same relative path passed to [createMixRoot] - see [addDeps] for
     * why `VirtualFile.name` is not used here.
     *
     * Also adds `_build/<env>/consolidated/` to mirror the full `_build` layout expected by
     * `DepsWatcher.syncLibrary`.
     *
     * Returns the list of ebin [VirtualFile]s.
     */
    fun addBuildArtifacts(
        fixture: CodeInsightTestFixture,
        rootPath: String,
        env: String,
        vararg depNames: String,
    ): List<VirtualFile> {
        fixture.tempDirFixture.findOrCreateDir("$rootPath/_build/$env/consolidated")
        return depNames.map { depName ->
            fixture.tempDirFixture.findOrCreateDir("$rootPath/_build/$env/lib/$depName/ebin")
        }
    }

    /**
     * [VirtualFile]-accepting overload of [addBuildArtifacts].
     *
     * Derives the temp-dir-relative path from [root] automatically, so call sites that already
     * hold a [VirtualFile] cannot pass a mismatched string path by accident.
     */
    fun addBuildArtifacts(
        fixture: CodeInsightTestFixture,
        root: VirtualFile,
        env: String,
        vararg depNames: String,
    ): List<VirtualFile> = addBuildArtifacts(fixture, relativeToTempDir(fixture, root), env, *depNames)

    /**
     * Derives the path of [root] relative to the fixture's temp directory.
     *
     * Throws [IllegalArgumentException] if [root] is not under the fixture temp dir, which
     * indicates the VirtualFile was not created by this fixture.
     */
    private fun relativeToTempDir(fixture: CodeInsightTestFixture, root: VirtualFile): String {
        val tempDir = FileUtil.toSystemIndependentName(fixture.tempDirPath)
        val rootPath = FileUtil.toSystemIndependentName(root.path)
        return FileUtil.getRelativePath(tempDir, rootPath, '/')
            ?: error("VirtualFile '${root.path}' is not under fixture temp dir '${fixture.tempDirPath}'")
    }
}
