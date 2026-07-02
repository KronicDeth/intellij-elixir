package org.elixir_lang.mix.sync

import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.elixir_lang.mix.sync.MixTestFixtures.addBuildArtifacts
import org.elixir_lang.mix.sync.MixTestFixtures.addDeps
import org.elixir_lang.mix.sync.MixTestFixtures.createMixRoot
import org.elixir_lang.mix.sync.MixTestFixtures.createMixRootWithDeps
import org.elixir_lang.mix.sync.MixTestFixtures.createSeparateRoots
import org.elixir_lang.mix.sync.MixTestFixtures.createUmbrellaRoot

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
     * Creates a single Mix project root at [rootPath] inside the fixture temp dir and registers
     * it as a module content root so that `isModuleContentRoot` in the path classifier
     * recognises it during classification tests.
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
        // Register as a content root so isModuleContentRoot() returns true for this directory.
        // PsiTestUtil.addContentRoot handles the write action internally and the test framework
        // cleans up content root entries on tearDown.
        PsiTestUtil.addContentRoot(fixture.module, root)
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
     * Creates a Mix project root at [rootPath] whose `mix.exs` declares [depNames] as library deps.
     *
     * The generated `mix.exs` includes a `def deps` function listing each dep as
     * `{:<depName>, ">= 0.0.0"}` so that [org.elixir_lang.mix.watcher.TransitiveResolution] can
     * resolve them via PSI and [org.elixir_lang.mix.sync.MixDepsSyncService.syncLibrariesForModule]
     * can wire the corresponding project libraries into the module's order entries.
     *
     * Registers the root as a module content root (same as [createMixRoot]).
     */
    fun createMixRootWithDeps(
        fixture: CodeInsightTestFixture,
        rootPath: String,
        vararg depNames: String,
    ): VirtualFile {
        val root = fixture.tempDirFixture.findOrCreateDir(rootPath)
        val moduleName = rootPath.split("/").last().split("_")
            .joinToString("") { it.replaceFirstChar(Char::uppercase) }
        val appAtom = rootPath.split("/").last()
        val depsList = if (depNames.isEmpty()) "[]" else
            "[\n" + depNames.joinToString(",\n") { "      {:$it, \">= 0.0.0\"}" } + "\n    ]"
        // DepGatherer requires a project/0 function whose keyword list has `deps: deps()`;
        // it then finds the `def deps` function and reads its returned list.
        fixture.tempDirFixture.createFile(
            "$rootPath/mix.exs",
            "defmodule ${moduleName}.MixProject do\n" +
                "  use Mix.Project\n" +
                "\n" +
                "  def project do\n" +
                "    [\n" +
                "      app: :$appAtom,\n" +
                "      version: \"0.1.0\",\n" +
                "      deps: deps()\n" +
                "    ]\n" +
                "  end\n" +
                "\n" +
                "  def deps do\n" +
                "    $depsList\n" +
                "  end\n" +
                "end\n"
        )
        PsiTestUtil.addContentRoot(fixture.module, root)
        return root
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
     * `MixDepsSyncService.syncLibraryRoots`.
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
    @Suppress("unused")
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

    /**
     * Removes all content entries that were added to [CodeInsightTestFixture.module] inside the
     * fixture's temp directory by [createMixRoot], [createUmbrellaRoot], [createSeparateRoots],
     * or [createMixRootWithDeps].
     *
     * [PsiTestUtil.addContentRoot] is **not** tracked by the IntelliJ fixture teardown machinery;
     * callers that use these factory methods must invoke this helper in their `tearDown` (wrapped
     * in `runAll`) to prevent content-root leakage into subsequent test classes that share the
     * same light module.
     *
     * Only entries whose URL is under the fixture's temp dir are affected; framework-registered
     * roots outside that tree are left untouched.
     */
    fun removeAllContentRoots(fixture: CodeInsightTestFixture) {
        val module = fixture.module
        val tempDirUrl = VfsUtilCore.pathToUrl(FileUtil.toSystemIndependentName(fixture.tempDirPath))
        val toRemove = ModuleRootManager.getInstance(module).contentEntries
            .filter { entry -> entry.url.startsWith(tempDirUrl) }
        if (toRemove.isNotEmpty()) {
            // ModuleRootModificationUtil.updateModel uses ApplicationManager.invokeAndWait
            // internally. In test tearDown the call always originates from the EDT thread,
            // so invokeAndWait is a same-thread no-op and there is no deadlock risk.
            // Production sync code must NOT use this helper (it would deadlock on any
            // coroutine-dispatched thread), which is why WritePlanApplicator uses direct
            // modifiableModel.commit() inside edtWriteAction instead.
            ModuleRootModificationUtil.updateModel(module) { model ->
                toRemove.forEach { entry ->
                    model.contentEntries
                        .firstOrNull { it.url == entry.url }
                        ?.let { model.removeContentEntry(it) }
                }
            }
        }
    }
}
