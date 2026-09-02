package org.elixir_lang.mix.sync

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.common.runAll
import java.io.File
import java.util.concurrent.Callable

/**
 * Heavy tests for [MixDepsSyncService] covering an umbrella imported one module per app, where one
 * app declares another with `in_umbrella:`:
 * ```
 *   module_app_a  content-root: umbrella/apps/app_a/
 *   module_app_b  content-root: umbrella/apps/app_b/
 *
 *   umbrella/
 *     mix.exs                  (apps_path: "apps", declares no deps)
 *     deps/phoenix/lib/
 *     _build/dev/lib/phoenix/ebin/
 *     apps/app_a/mix.exs       (declares {:app_b, in_umbrella: true})
 *     apps/app_b/mix.exs       (declares {:phoenix, ">= 0.0.0"})
 * ```
 *
 * The umbrella root is deliberately not a content root of any module - that is the import shape
 * [#3990](https://github.com/KronicDeth/intellij-elixir/issues/3990) reports, and it is what makes
 * `app_a`'s own content root the only place the resolver starts from.
 */
class MixDepsSyncServiceSiblingAppHeavyTest : HeavyPlatformTestCase() {

    private lateinit var umbrellaVf: VirtualFile
    private lateinit var appAMixExs: VirtualFile

    override fun setUp() {
        super.setUp()

        val umbrellaDir = createTempDir("sibling_umbrella")
        FileUtil.writeToFile(
            File(umbrellaDir, "mix.exs"),
            "defmodule SiblingUmbrella.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [apps_path: \"apps\"]\n" +
                "  end\nend\n"
        )
        File(umbrellaDir, "deps/phoenix/lib").mkdirs()
        File(umbrellaDir, "_build/dev/lib/phoenix/ebin").mkdirs()
        File(umbrellaDir, "apps/app_a").mkdirs()
        FileUtil.writeToFile(
            File(umbrellaDir, "apps/app_a/mix.exs"),
            "defmodule AppA.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [app: :app_a, version: \"0.1.0\", deps: deps()]\n" +
                "  end\n\n" +
                "  def deps do\n" +
                "    [{:app_b, in_umbrella: true}]\n" +
                "  end\nend\n"
        )
        File(umbrellaDir, "apps/app_b").mkdirs()
        FileUtil.writeToFile(
            File(umbrellaDir, "apps/app_b/mix.exs"),
            "defmodule AppB.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [app: :app_b, version: \"0.1.0\", deps: deps()]\n" +
                "  end\n\n" +
                "  def deps do\n" +
                "    [{:phoenix, \">= 0.0.0\"}]\n" +
                "  end\nend\n"
        )

        val umbrellaVfRaw = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(umbrellaDir)
            ?: error("sibling_umbrella not found in VFS after refresh")
        VfsUtil.markDirtyAndRefresh(false, true, true, umbrellaVfRaw)
        umbrellaVf = umbrellaVfRaw

        appAMixExs = umbrellaVf.findFileByRelativePath("apps/app_a/mix.exs")
            ?: error("sibling_umbrella/apps/app_a/mix.exs not found in VFS after refresh")

        // One module per app, and no module for the umbrella root - the app dirs are the only
        // content roots there are.
        PsiTestUtil.addContentRoot(
            createModule("module_app_a"),
            umbrellaVf.findFileByRelativePath("apps/app_a")!!
        )
        PsiTestUtil.addContentRoot(
            createModule("module_app_b"),
            umbrellaVf.findFileByRelativePath("apps/app_b")!!
        )
    }

    override fun tearDown() {
        runAll(
            { MixSyncTestHelpers.removeAllLibraries(project) },
            { super.tearDown() },
        )
    }

    private fun module(name: String): Module =
        ModuleManager.getInstance(project).findModuleByName(name) ?: error("$name not found")

    private fun libraryEntryNames(moduleName: String): List<String> =
        ReadAction.nonBlocking(Callable {
            ModuleRootManager.getInstance(module(moduleName)).orderEntries
                .filterIsInstance<LibraryOrderEntry>()
                .mapNotNull { it.libraryName }
        }).executeSynchronously()

    private fun moduleEntryNames(moduleName: String): List<String> =
        ReadAction.nonBlocking(Callable {
            ModuleRootManager.getInstance(module(moduleName)).orderEntries
                .filterIsInstance<ModuleOrderEntry>()
                .map { it.moduleName }
        }).executeSynchronously()

    private fun drainForAppA() {
        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.MixFile(appAMixExs))
        MixSyncTestHelpers.drainDirectly(service)
    }

    /**
     * `phoenix` is declared only by `app_b`, and `app_a` reaches it by declaring `app_b`. Without
     * walking the sibling's own `mix.exs`, `app_a`'s module gets no entry for it and every alias
     * `app_a` writes against a `phoenix` module is unresolved - the report on #3990.
     *
     * `app_b`'s own entry is asserted alongside it as the precondition: a run that wired nothing at
     * all would otherwise satisfy nothing here.
     */
    fun testSiblingsDepIsWiredToTheDeclaringAppsModule() {
        val libName = scopedDepLibraryName(contentRootToken(project, umbrellaVf.url), "phoenix")

        drainForAppA()

        assertTrue(
            "Precondition: app_b declares {:phoenix, ...} directly, so its own module must be " +
                "wired to '$libName'. Order entries: ${libraryEntryNames("module_app_b")}",
            libraryEntryNames("module_app_b").contains(libName),
        )
        assertTrue(
            "app_a reaches phoenix through {:app_b, in_umbrella: true}, and a module order entry " +
                "carries no unexported library, so app_a needs '$libName' in its own right. " +
                "Order entries: ${libraryEntryNames("module_app_a")}",
            libraryEntryNames("module_app_a").contains(libName),
        )
    }

    /**
     * The sibling is a module of this project, so it stays a module order entry rather than
     * becoming a library of its own - the library entry above is in addition to it, not instead.
     */
    fun testSiblingRemainsAModuleOrderEntry() {
        drainForAppA()

        assertTrue(
            "app_b is a module of the project. Module dep entries: ${moduleEntryNames("module_app_a")}",
            moduleEntryNames("module_app_a").contains("app_b"),
        )
        assertFalse(
            "app_b must not also be wired as a library. Order entries: ${libraryEntryNames("module_app_a")}",
            libraryEntryNames("module_app_a").any { it.contains("app_b") },
        )
    }
}
