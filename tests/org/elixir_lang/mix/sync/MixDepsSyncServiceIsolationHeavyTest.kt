package org.elixir_lang.mix.sync

import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.testFramework.PsiTestUtil
import java.io.File

/**
 * Heavy (multi-module) regression tests for [MixDepsSyncService] isolation behaviour.
 *
 * Uses the shared four-module umbrella topology from [MixDepsSyncServiceHeavyTestBase]:
 * ```
 *   module_umbrella_a  content-root: umbrella_a/
 *   module_umbrella_b  content-root: umbrella_b/
 *   module_child_a     content-root: umbrella_a/apps/child_a/  (child dir only)
 *   module_child_b     content-root: umbrella_b/apps/child_b/  (child dir only)
 * ```
 *
 * The child modules own ONLY their child app dir so that `findFileByRelativePath("deps/phoenix")`
 * returns null, forcing [buildModuleDepsPlan] through the ancestor-constrained fallback path.
 *
 * Enqueuing umbrella_B first makes umbrella_b's plan appear first in `requestedLibraryPlans`.
 * The old `firstOrNull { depName }` would wire child_a to umbrella_b's library (wrong).
 * The ancestor-constrained fix correctly picks umbrella_a's plan.
 */
class MixDepsSyncServiceIsolationHeavyTest : MixDepsSyncServiceHeavyTestBase() {

    /**
     * Verifies that [MixDepsSyncService] wires module_child_a to umbrella_a's phoenix library even
     * when umbrella_b's plan appears first in `requestedLibraryPlans`.
     *
     * This is the real-production topology for the umbrella fallback isolation bug:
     * - `SyncRoot(umbrella_b)` enqueued first → "phoenix \[umbrella_b\]" first in requestedLibraryPlans
     * - `SyncRoot(umbrella_a)` enqueued second → "phoenix \[umbrella_a\]" second
     * - MixFile(child_a/mix.exs) produces SyncModule(module_child_a)
     * - buildModuleDepsPlan for module_child_a: content-root is child_a/ only, deps/phoenix not found
     *   via findFileByRelativePath → falls to ancestor-constrained requestedLibraryPlans lookup
     * - Old bug: firstOrNull picks "phoenix \[umbrella_b\]" for child_a (wrong - umbrella_b is not its owner)
     * - Fix: ancestor check filters to plans whose contentRootUrl is an ancestor of the module's content
     *   root; umbrella_b/ is NOT an ancestor of umbrella_a/apps/child_a → filtered; umbrella_a/ IS → picked
     */
    fun testUmbrellaFallback_twoModuleSeparatedUmbrellas_childModuleWiredToOwningUmbrella() {
        val libNameA = scopedDepLibraryName(umbrellaAVf.url, "phoenix")
        val libNameB = scopedDepLibraryName(umbrellaBVf.url, "phoenix")
        assertFalse("Sanity: two umbrella roots must produce distinct library names", libNameA == libNameB)

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        service.clearPendingForTesting()
        // Enqueue B first: umbrella_b's plan lands first in requestedLibraryPlans, triggering the old
        // firstOrNull bug path that would wire child_a to the wrong (umbrella_b) library.
        service.enqueue(SyncRequest.SyncRoot(umbrellaBVf.url))
        service.enqueue(SyncRequest.SyncRoot(umbrellaAVf.url))
        service.enqueue(SyncRequest.MixFile(childAMixExs))
        drainDirectly(service)

        assertNotNull(
            "phoenix library for umbrella_a must have been created",
            libraryTable.getLibraryByName(libNameA)
        )
        assertNotNull(
            "phoenix library for umbrella_b must have been created",
            libraryTable.getLibraryByName(libNameB)
        )

        val moduleChildA = ModuleManager.getInstance(project).findModuleByName("module_child_a")
            ?: error("module_child_a not found")
        val entries = ModuleRootManager.getInstance(moduleChildA).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }

        assertTrue(
            "child_a must be wired to umbrella_a's phoenix library (ancestor-constrained fix). " +
                    "Old bug: B-first ordering picks umbrella_b's plan for child_a. Order entries: $entries",
            entries.contains(libNameA)
        )
        assertFalse(
            "child_a must NOT be wired to umbrella_b's phoenix library. " +
                    "Old bug: firstOrNull picks first matching dep name regardless of ownership. " +
                    "Order entries: $entries",
            entries.contains(libNameB)
        )
    }

    /**
     * Exclude folders produced by a [SyncRequest.DepRoot] sync must only be applied to the content
     * entry that is an ancestor of the `_build` directory - not to an unrelated content root.
     *
     * This test uses a two-module topology with separate content roots:
     * - `module_my_app` with content-root `my_app/` (has deps and _build)
     * - `module_other_app` with content-root `other_app/` (no deps, no _build)
     *
     * After syncing my_app/deps/phoenix via [SyncRequest.DepRoot], the exclude folder for
     * `_build/dev/lib/phoenix` must appear in my_app's content entry only. The other_app content
     * entry must remain untouched.
     *
     * This test lives in the heavy test class because [com.intellij.openapi.module.ModuleUtil.findModuleForFile] (used inside
     * `buildLibraryRootsPlansInCurrentContext` to resolve which module owns the `_build` dir)
     * requires a fully-synchronized [com.intellij.openapi.roots.ProjectFileIndex]. In light tests,
     * VFS events from `findOrCreateDir` may not reach the file index before the pooled-thread
     * `readAction` executes, causing the test to flake.
     */
    fun testExcludeFolder_onlyAppliesToAncestorContentEntry() {
        // Create two separate project dirs on disk.
        val myAppDir = createTempDir("my_app")
        val otherAppDir = createTempDir("other_app")

        // my_app has deps/phoenix + _build artifacts.
        FileUtil.writeToFile(
            File(myAppDir, "mix.exs"),
            "defmodule MyApp.MixProject do\n  use Mix.Project\nend\n"
        )
        File(myAppDir, "deps/phoenix/lib").mkdirs()
        File(myAppDir, "_build/dev/lib/phoenix/ebin").mkdirs()
        File(myAppDir, "_build/dev/consolidated").mkdirs()

        // other_app is a bare mix project - no deps, no _build.
        FileUtil.writeToFile(
            File(otherAppDir, "mix.exs"),
            "defmodule OtherApp.MixProject do\n  use Mix.Project\nend\n"
        )

        // Refresh VFS.
        val myAppVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(myAppDir)
            ?: error("my_app not found in VFS")
        val otherAppVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(otherAppDir)
            ?: error("other_app not found in VFS")
        VfsUtil.markDirtyAndRefresh(false, true, true, myAppVf, otherAppVf)

        // Create two modules with separate content roots.
        val moduleMyApp = createModule("module_my_app")
        val moduleOtherApp = createModule("module_other_app")
        PsiTestUtil.addContentRoot(moduleMyApp, myAppVf)
        PsiTestUtil.addContentRoot(moduleOtherApp, otherAppVf)

        // Drain a DepRoot for my_app/deps/phoenix.
        val depRoot = myAppVf.findFileByRelativePath("deps/phoenix")
            ?: error("my_app/deps/phoenix not found in VFS")
        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)

        // Verify: my_app's content entry has at least one exclude under _build.
        val myAppExcludes = ModuleRootManager.getInstance(moduleMyApp).contentEntries
            .firstOrNull { it.file == myAppVf }
            ?.excludeFolderUrls.orEmpty()
        assertTrue(
            "my_app content entry should have at least one exclude (the _build/dev/lib/phoenix dir). " +
                "Excludes: $myAppExcludes",
            myAppExcludes.any { it.contains("my_app") && it.contains("_build") }
        )

        // Verify: other_app's content entry has NO excludes from my_app.
        val otherAppExcludes = ModuleRootManager.getInstance(moduleOtherApp).contentEntries
            .firstOrNull { it.file == otherAppVf }
            ?.excludeFolderUrls.orEmpty()
        assertTrue(
            "other_app content entry must have no excludes from my_app's build dir. " +
                "Excludes: $otherAppExcludes",
            otherAppExcludes.none { it.contains("my_app") }
        )
    }
}
