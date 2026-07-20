package org.elixir_lang.mix.sync

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
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
 * Heavy tests for [MixDepsSyncService] covering the SINGLE-module umbrella topology:
 * ```
 *   module_single_umbrella  content-root: umbrella/
 *
 *   umbrella/
 *     mix.exs                    (umbrella root - declares NO deps of its own)
 *     deps/phoenix/lib/
 *     _build/dev/lib/phoenix/ebin/
 *     apps/child_app/mix.exs     (declares {:phoenix, ">= 0.0.0"})
 * ```
 *
 * This is the canonical layout produced by the plugin's project setup (one IntelliJ module whose
 * content root is the umbrella root; apps are plain sub-directories, NOT separate modules).  It
 * differs from [MixDepsSyncServiceHeavyTestBase], where every app is carved out into its own
 * child module - there, deps declared by an app are wired via the child module's own mix.exs.
 *
 * Here the ONLY mix.exs directly at a content root is the umbrella root's, which declares no
 * deps.  App-declared deps are therefore only discoverable by descending into `apps/<app>/mix.exs`
 * during module-plan construction.  These tests pin that behavior:
 *
 * - [testRootMixFileSync_wiresAppDeclaredDepToUmbrellaModule]: a drain for the umbrella module
 *   must wire deps declared in `apps/<app>/mix.exs` as library order entries on the module.
 * - [testAppMixFileEvent_resolvesToOwningUmbrellaModuleAndWires]: a [SyncRequest.MixFile] for
 *   `apps/<app>/mix.exs` must resolve to the owning umbrella module (not be dropped) and wire
 *   the app-declared deps.
 */
class MixDepsSyncServiceSingleModuleUmbrellaHeavyTest : HeavyPlatformTestCase() {

    private lateinit var umbrellaVf: VirtualFile
    private lateinit var rootMixExs: VirtualFile
    private lateinit var appMixExs: VirtualFile

    override fun setUp() {
        super.setUp()

        val umbrellaDir = createTempDir("single_umbrella")
        FileUtil.writeToFile(
            File(umbrellaDir, "mix.exs"),
            "defmodule SingleUmbrella.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [apps_path: \"apps\"]\n" +
                "  end\nend\n"
        )
        File(umbrellaDir, "deps/phoenix/lib").mkdirs()
        File(umbrellaDir, "_build/dev/lib/phoenix/ebin").mkdirs()
        File(umbrellaDir, "apps/child_app").mkdirs()
        FileUtil.writeToFile(
            File(umbrellaDir, "apps/child_app/mix.exs"),
            "defmodule ChildApp.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [app: :child_app, version: \"0.1.0\", deps: deps()]\n" +
                "  end\n\n" +
                "  def deps do\n" +
                "    [{:phoenix, \">= 0.0.0\"}, {:sibling_app, in_umbrella: true}]\n" +
                "  end\nend\n"
        )
        File(umbrellaDir, "apps/sibling_app").mkdirs()
        FileUtil.writeToFile(
            File(umbrellaDir, "apps/sibling_app/mix.exs"),
            "defmodule SiblingApp.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [app: :sibling_app, version: \"0.1.0\"]\n" +
                "  end\nend\n"
        )

        val umbrellaVfRaw = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(umbrellaDir)
            ?: error("single_umbrella not found in VFS after refresh")
        VfsUtil.markDirtyAndRefresh(false, true, true, umbrellaVfRaw)
        umbrellaVf = umbrellaVfRaw

        rootMixExs = umbrellaVf.findFileByRelativePath("mix.exs")
            ?: error("single_umbrella/mix.exs not found in VFS after refresh")
        appMixExs = umbrellaVf.findFileByRelativePath("apps/child_app/mix.exs")
            ?: error("single_umbrella/apps/child_app/mix.exs not found in VFS after refresh")

        val module = createModule("module_single_umbrella")
        PsiTestUtil.addContentRoot(module, umbrellaVf)
    }

    override fun tearDown() {
        runAll(
            { MixSyncTestHelpers.removeAllLibraries(project) },
            { super.tearDown() },
        )
    }

    private fun umbrellaModule() =
        ModuleManager.getInstance(project).findModuleByName("module_single_umbrella")
            ?: error("module_single_umbrella not found")

    private fun umbrellaModuleLibraryEntryNames(): List<String> =
        ReadAction.nonBlocking(Callable {
            ModuleRootManager.getInstance(umbrellaModule()).orderEntries
                .filterIsInstance<LibraryOrderEntry>()
                .mapNotNull { it.libraryName }
        }).executeSynchronously()

    /**
     * A drain that plans the umbrella module (triggered here via a [SyncRequest.MixFile] for the
     * umbrella root's own mix.exs) must discover `{:phoenix, ...}` declared in
     * `apps/child_app/mix.exs` and wire `phoenix \[umbrella\]` as a library order entry on the
     * module - even though the umbrella root mix.exs declares no deps at all.
     *
     * Without descending into `apps/<app>/mix.exs`, transitiveResolution sees only the depless
     * root mix.exs, buildModuleDepsPlan returns null, and the module is never wired (regression:
     * unresolved module aliases for all app-declared deps in single-module umbrellas).
     */
    fun testRootMixFileSync_wiresAppDeclaredDepToUmbrellaModule() {
        val libName = scopedDepLibraryName(umbrellaVf.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        service.enqueue(SyncRequest.MixFile(rootMixExs))
        MixSyncTestHelpers.drainDirectly(service)

        assertNotNull(
            "phoenix library scoped to the umbrella root must have been created",
            LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName(libName)
        )

        val entries = umbrellaModuleLibraryEntryNames()
        assertTrue(
            "module_single_umbrella must be wired to '$libName' because apps/child_app/mix.exs " +
                "declares {:phoenix, ...} and the app dir belongs to the umbrella module's " +
                "content root. Order entries: $entries",
            entries.contains(libName)
        )
    }

    /**
     * A [SyncRequest.MixFile] for `apps/child_app/mix.exs` must resolve to the umbrella module
     * that owns the file (its content root is an ancestor via the `apps/<app>` layout) and the
     * resulting drain must wire the app-declared dep.
     *
     * Without app-aware resolution, resolveMixFileRequest drops the event because
     * `apps/child_app` is not itself a content root - so editing an app's mix.exs can never
     * trigger dependency wiring in a single-module umbrella.
     */
    fun testAppMixFileEvent_resolvesToOwningUmbrellaModuleAndWires() {
        val libName = scopedDepLibraryName(umbrellaVf.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        service.enqueue(SyncRequest.MixFile(appMixExs))
        MixSyncTestHelpers.drainDirectly(service)

        val entries = umbrellaModuleLibraryEntryNames()
        assertTrue(
            "A MixFile event for apps/child_app/mix.exs must resolve to module_single_umbrella " +
                "and wire '$libName'. Order entries: $entries",
            entries.contains(libName)
        )
    }

    /**
     * An `{:sibling_app, in_umbrella: true}` dep declared by one app on another app of the SAME
     * single-module umbrella must NOT produce a module order entry: both apps belong to
     * `module_single_umbrella` itself, so a module dep on "sibling_app" would always be an
     * invalid (red) entry - there is no such module.
     *
     * The phoenix assertion doubles as the precondition that app-declared deps were discovered
     * at all (otherwise this test would pass vacuously against the pre-fix code).
     */
    fun testInUmbrellaSiblingDep_doesNotCreateModuleOrderEntryOnOwnModule() {
        val libName = scopedDepLibraryName(umbrellaVf.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        service.enqueue(SyncRequest.MixFile(rootMixExs))
        MixSyncTestHelpers.drainDirectly(service)

        val libraryEntries = umbrellaModuleLibraryEntryNames()
        assertTrue(
            "Precondition: app-declared deps must be discovered ('$libName' wired). " +
                "Order entries: $libraryEntries",
            libraryEntries.contains(libName)
        )

        val moduleDepNames = ReadAction.nonBlocking(Callable {
            ModuleRootManager.getInstance(umbrellaModule()).orderEntries
                .filterIsInstance<com.intellij.openapi.roots.ModuleOrderEntry>()
                .map { it.moduleName }
        }).executeSynchronously()
        assertFalse(
            "sibling_app lives under module_single_umbrella's own content root - an in_umbrella " +
                "dep between two apps of the same module must not create a (necessarily invalid) " +
                "module order entry. Module dep entries: $moduleDepNames",
            moduleDepNames.contains("sibling_app")
        )
    }
}
