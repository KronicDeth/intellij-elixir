package org.elixir_lang.mix.sync

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.common.runAll
import java.io.File

/**
 * Abstract base class for heavy (multi-module) tests of [MixDepsSyncService].
 *
 * Provides a shared four-module umbrella topology:
 * ```
 *   module_umbrella_a  content-root: umbrella_a/
 *   module_umbrella_b  content-root: umbrella_b/
 *   module_child_a     content-root: umbrella_a/apps/child_a/
 *   module_child_b     content-root: umbrella_b/apps/child_b/
 * ```
 *
 * Each umbrella has `deps/phoenix/lib/`, `_build/dev/lib/phoenix/ebin/`,
 * `_build/dev/consolidated/`, and a child app with `mix.exs` declaring `{:phoenix, ">= 0.0.0"}`.
 *
 * The child modules own ONLY their child app dir - no umbrella content root - so that
 * `findFileByRelativePath("deps/phoenix")` returns null from the child content root,
 * forcing [buildModuleDepsPlan] through the ancestor-constrained fallback path.
 *
 * [drainDirectly] and [runSuspendOnPooledThread] delegate to [MixSyncTestHelpers].
 * [tearDown] removes all [org.elixir_lang.mix.library.Kind]-bearing libraries to prevent
 * cross-test leakage.
 */
abstract class MixDepsSyncServiceHeavyTestBase : HeavyPlatformTestCase() {

    protected lateinit var umbrellaAVf: VirtualFile
    protected lateinit var umbrellaBVf: VirtualFile
    protected lateinit var childAMixExs: VirtualFile
    protected lateinit var childBMixExs: VirtualFile
    protected lateinit var childAVf: VirtualFile
    protected lateinit var childBVf: VirtualFile

    override fun setUp() {
        super.setUp()

        val umbrellaADir = createTempDir("umbrella_a")
        val umbrellaBDir = createTempDir("umbrella_b")
        createUmbrellaStructure(umbrellaADir, "UmbrellaA", "child_a")
        createUmbrellaStructure(umbrellaBDir, "UmbrellaB", "child_b")

        val umbrellaAVfRaw = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(umbrellaADir)
            ?: error("umbrella_a not found in VFS after refresh")
        val umbrellaBVfRaw = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(umbrellaBDir)
            ?: error("umbrella_b not found in VFS after refresh")
        VfsUtil.markDirtyAndRefresh(false, true, true, umbrellaAVfRaw, umbrellaBVfRaw)
        umbrellaAVf = umbrellaAVfRaw
        umbrellaBVf = umbrellaBVfRaw

        childAVf = umbrellaAVf.findFileByRelativePath("apps/child_a")
            ?: error("umbrella_a/apps/child_a not found in VFS after refresh")
        childBVf = umbrellaBVf.findFileByRelativePath("apps/child_b")
            ?: error("umbrella_b/apps/child_b not found in VFS after refresh")
        childAMixExs = umbrellaAVf.findFileByRelativePath("apps/child_a/mix.exs")
            ?: error("umbrella_a/apps/child_a/mix.exs not found in VFS after refresh")
        childBMixExs = umbrellaBVf.findFileByRelativePath("apps/child_b/mix.exs")
            ?: error("umbrella_b/apps/child_b/mix.exs not found in VFS after refresh")

        val moduleUmbrellaA = createModule("module_umbrella_a")
        val moduleUmbrellaB = createModule("module_umbrella_b")
        val moduleChildA = createModule("module_child_a")
        val moduleChildB = createModule("module_child_b")

        PsiTestUtil.addContentRoot(moduleUmbrellaA, umbrellaAVf)
        PsiTestUtil.addContentRoot(moduleUmbrellaB, umbrellaBVf)
        PsiTestUtil.addContentRoot(moduleChildA, childAVf)
        PsiTestUtil.addContentRoot(moduleChildB, childBVf)
    }

    override fun tearDown() {
        runAll(
            { MixSyncTestHelpers.removeAllLibraries(project) },
            { super.tearDown() },
        )
    }

    /**
     * Creates the full umbrella directory + file structure on disk:
     * ```
     * <umbrellaDir>/
     *   mix.exs
     *   deps/phoenix/lib/
     *   _build/dev/lib/phoenix/ebin/
     *   _build/dev/consolidated/
     *   apps/<childAppName>/
     *     mix.exs  (declares {:phoenix, ">= 0.0.0"})
     * ```
     */
    protected fun createUmbrellaStructure(umbrellaDir: File, moduleName: String, childAppName: String) {
        FileUtil.writeToFile(
            File(umbrellaDir, "mix.exs"),
            "defmodule ${moduleName}.MixProject do\n  use Mix.Project\nend\n"
        )
        File(umbrellaDir, "deps/phoenix/lib").mkdirs()
        File(umbrellaDir, "_build/dev/lib/phoenix/ebin").mkdirs()
        File(umbrellaDir, "_build/dev/consolidated").mkdirs()
        File(umbrellaDir, "apps/$childAppName").mkdirs()
        val childModule = childAppName.split("_")
            .joinToString("") { it.replaceFirstChar(Char::uppercase) }
        FileUtil.writeToFile(
            File(umbrellaDir, "apps/$childAppName/mix.exs"),
            "defmodule ${childModule}.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [app: :$childAppName, version: \"0.1.0\", deps: deps()]\n" +
                "  end\n\n" +
                "  def deps do\n" +
                "    [{:phoenix, \">= 0.0.0\"}]\n" +
                "  end\nend\n"
        )
    }

    /**
     * Calls [MixDepsSyncService.drain] directly on a pooled thread (bypassing the 250 ms
     * debounce) while pumping the IDE event queue from the EDT test thread so that
     * [com.intellij.openapi.application.edtWriteAction] blocks can execute.
     * Delegates to [MixSyncTestHelpers.drainDirectly].
     */
    protected fun drainDirectly(service: MixDepsSyncService) = MixSyncTestHelpers.drainDirectly(service)

    /**
     * Runs a suspending [block] on a pooled thread while the EDT (test thread) continues
     * pumping its event queue. Delegates to [MixSyncTestHelpers.runSuspendOnPooledThread].
     */
    protected fun <T> runSuspendOnPooledThread(block: suspend () -> T): T =
        MixSyncTestHelpers.runSuspendOnPooledThread(block)
}
