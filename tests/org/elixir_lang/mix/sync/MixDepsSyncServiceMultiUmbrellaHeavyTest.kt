package org.elixir_lang.mix.sync

import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar

/**
 * Heavy (multi-module) tests for [MixDepsSyncService] covering multi-umbrella scenarios that
 * cannot be exercised in light tests because a light project has a single module whose content
 * roots all belong to the same module - inter-module isolation cannot be verified.
 *
 * Uses the shared four-module umbrella topology from [MixDepsSyncServiceHeavyTestBase]:
 * ```
 *   module_umbrella_a  content-root: umbrella_a/
 *   module_umbrella_b  content-root: umbrella_b/
 *   module_child_a     content-root: umbrella_a/apps/child_a/
 *   module_child_b     content-root: umbrella_b/apps/child_b/
 * ```
 * Each umbrella has `deps/phoenix/lib/` and `_build/dev/lib/phoenix/ebin/`.
 * Each child has `mix.exs` declaring `{:phoenix, ">= 0.0.0"}`.
 * Child modules hold ONLY the child app dir as content root, so
 * `findFileByRelativePath("deps/phoenix")` returns null from the child module,
 * forcing [buildModuleDepsPlan] through the ancestor-walk fallback path.
 */
class MixDepsSyncServiceMultiUmbrellaHeavyTest : MixDepsSyncServiceHeavyTestBase() {

    // -----------------------------------------------------------------------
    // H1 - affectedModuleNamesForLibraryPlans ancestor fan-out
    // -----------------------------------------------------------------------

    /**
     * H1: A scoped [SyncRequest.DepRoot] for `umbrella_a/deps/phoenix` must fan out to
     * `module_child_a` (whose content root is a descendant of `umbrella_a/`) but must NOT
     * fan out to `module_child_b` (whose content root is under a completely different tree).
     *
     * This exercises the ancestor-match branch inside `affectedModuleNamesForLibraryPlans`:
     * `module_child_a`'s content root `umbrella_a/apps/child_a/` is a descendant of the
     * library plan's `contentRootUrl` (`umbrella_a/`), so it is included.
     * `module_child_b`'s content root `umbrella_b/apps/child_b/` is NOT a descendant of
     * `umbrella_a/`, so it is excluded and receives no phoenix library entry.
     */
    fun testAffectedModuleNames_depRootFanOutIncludesOnlyAncestorChild() {
        val libNameA = scopedDepLibraryName(umbrellaAVf.url, "phoenix")
        val libNameB = scopedDepLibraryName(umbrellaBVf.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        // Enqueue only umbrella_a's DepRoot + child_a's MixFile - umbrella_b is untouched.
        val depRootA = umbrellaAVf.findFileByRelativePath("deps/phoenix")
            ?: error("umbrella_a/deps/phoenix not found in VFS")
        service.enqueue(SyncRequest.DepRoot(depRootA))
        service.enqueue(SyncRequest.MixFile(childAMixExs))
        drainDirectly(service)

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        assertNotNull(
            "phoenix library for umbrella_a must have been created",
            libraryTable.getLibraryByName(libNameA)
        )
        assertNull(
            "phoenix library for umbrella_b must NOT have been created - only umbrella_a was synced",
            libraryTable.getLibraryByName(libNameB)
        )

        val moduleChildA = ModuleManager.getInstance(project).findModuleByName("module_child_a")
            ?: error("module_child_a not found")
        val moduleChildB = ModuleManager.getInstance(project).findModuleByName("module_child_b")
            ?: error("module_child_b not found")

        val entriesA = ModuleRootManager.getInstance(moduleChildA).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }
        assertTrue(
            "module_child_a must be wired to phoenix [umbrella_a]. Order entries: $entriesA",
            entriesA.contains(libNameA)
        )

        val entriesB = ModuleRootManager.getInstance(moduleChildB).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }
        assertFalse(
            "module_child_b must NOT have any phoenix library entry - it belongs to umbrella_b " +
                "which was not synced. Order entries: $entriesB",
            entriesB.any { it.startsWith("phoenix [") }
        )
    }

    // -----------------------------------------------------------------------
    // H2 - Full two-sided cross-root wiring isolation (deferred #18 / #25)
    // -----------------------------------------------------------------------

    /**
     * H2: An `All`-style drain (both [SyncRequest.SyncRoot]s + both [SyncRequest.MixFile]s)
     * must wire each child exclusively to its own umbrella's scoped library.
     *
     * `module_child_a` → `phoenix \[umbrella_a\]` and NOT `phoenix \[umbrella_b\]`
     * `module_child_b` → `phoenix \[umbrella_b\]` and NOT `phoenix \[umbrella_a\]`
     *
     * This is the authoritative two-sided cross-root isolation test. The existing single-sided
     * regression test (`testUmbrellaFallback_twoModuleSeparatedUmbrellas_*`) only asserts
     * child_a's wiring. This test verifies BOTH children independently.
     */
    fun testFullTwoSidedCrossRootWiringIsolation() {
        val libNameA = scopedDepLibraryName(umbrellaAVf.url, "phoenix")
        val libNameB = scopedDepLibraryName(umbrellaBVf.url, "phoenix")
        assertFalse("Sanity: two umbrella roots must produce distinct library names", libNameA == libNameB)

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        // Enqueue B first to reproduce the ordering that triggered the original bug.
        service.enqueue(SyncRequest.SyncRoot(umbrellaBVf.url))
        service.enqueue(SyncRequest.SyncRoot(umbrellaAVf.url))
        service.enqueue(SyncRequest.MixFile(childAMixExs))
        service.enqueue(SyncRequest.MixFile(childBMixExs))
        drainDirectly(service)

        val moduleChildA = ModuleManager.getInstance(project).findModuleByName("module_child_a")
            ?: error("module_child_a not found")
        val moduleChildB = ModuleManager.getInstance(project).findModuleByName("module_child_b")
            ?: error("module_child_b not found")

        val entriesA = ModuleRootManager.getInstance(moduleChildA).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }
        assertTrue(
            "module_child_a must be wired to phoenix [umbrella_a]. Order entries: $entriesA",
            entriesA.contains(libNameA)
        )
        assertFalse(
            "module_child_a must NOT be wired to phoenix [umbrella_b]. Order entries: $entriesA",
            entriesA.contains(libNameB)
        )

        val entriesB = ModuleRootManager.getInstance(moduleChildB).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }
        assertTrue(
            "module_child_b must be wired to phoenix [umbrella_b]. Order entries: $entriesB",
            entriesB.contains(libNameB)
        )
        assertFalse(
            "module_child_b must NOT be wired to phoenix [umbrella_a]. Order entries: $entriesB",
            entriesB.contains(libNameA)
        )
    }

    // -----------------------------------------------------------------------
    // H3 - SyncModule-only drain triggers ancestor walk
    // -----------------------------------------------------------------------

    /**
     * H3: Enqueuing only a [SyncRequest.MixFile] for `child_a/mix.exs` - no [SyncRequest.SyncRoot]
     * and no [SyncRequest.DepRoot] - must wire `module_child_a` to `phoenix \[umbrella_a\]`, not to
     * `phoenix [umbrella_a/apps/child_a]`.
     *
     * This exercises the `syncModuleNames.isNotEmpty()` branch in `buildSyncPlan` together with the
     * `findAncestorDeps` walk. Because `module_child_a`'s content root is `umbrella_a/apps/child_a/`
     * (no `deps/` directory), the walk ascends to `umbrella_a/` which carries both `mix.exs` and
     * `deps/phoenix/` - the library plan is scoped to the umbrella root URL, not the child dir URL.
     *
     * Without the ancestor walk, the drain would produce a mismatched placeholder library named
     * `phoenix [umbrella_a/apps/child_a]` instead of the correct `phoenix \[umbrella_a\]`.
     */
    fun testSyncModuleOnly_ancestorWalkProducesCorrectlyScopedLibrary() {
        val correctLibName = scopedDepLibraryName(umbrellaAVf.url, "phoenix")
        val wrongLibName = scopedDepLibraryName(childAVf.url, "phoenix")
        assertFalse("Sanity: correct and wrong library names must differ", correctLibName == wrongLibName)

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        // Enqueue ONLY the child's mix.exs - no SyncRoot, no DepRoot.
        service.enqueue(SyncRequest.MixFile(childAMixExs))
        drainDirectly(service)

        val moduleChildA = ModuleManager.getInstance(project).findModuleByName("module_child_a")
            ?: error("module_child_a not found")
        val entries = ModuleRootManager.getInstance(moduleChildA).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }

        assertTrue(
            "module_child_a must be wired to the umbrella-scoped library '$correctLibName' " +
                "(proves findAncestorDeps walked up to umbrella_a). Order entries: $entries",
            entries.contains(correctLibName)
        )
        assertFalse(
            "module_child_a must NOT be wired to a child-scoped library '$wrongLibName' " +
                "(that would mean the ancestor walk failed and the child dir was used as the scope). " +
                "Order entries: $entries",
            entries.contains(wrongLibName)
        )
    }

    // -----------------------------------------------------------------------
    // H4 - Scoped DepRoot does not wire unrelated modules across umbrellas
    // -----------------------------------------------------------------------

    /**
     * H4: A [SyncRequest.DepRoot] scoped to `umbrella_a/deps/phoenix` must not add any phoenix
     * library order entry to `module_child_b` or `module_umbrella_b`.
     *
     * This is the authoritative cross-module isolation test, complementing the light test
     * `testSingleRootDepRootDoesNotWireUnrelatedModules` which only verifies intra-module
     * isolation (two content roots on the same module). Here we verify that separate modules
     * belonging to a different umbrella tree are never touched by a foreign scoped sync.
     */
    fun testScopedDepRoot_doesNotWireUnrelatedModulesAcrossUmbrellas() {
        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        val depRootA = umbrellaAVf.findFileByRelativePath("deps/phoenix")
            ?: error("umbrella_a/deps/phoenix not found in VFS")
        service.enqueue(SyncRequest.DepRoot(depRootA))
        service.enqueue(SyncRequest.MixFile(childAMixExs))
        drainDirectly(service)

        val moduleChildB = ModuleManager.getInstance(project).findModuleByName("module_child_b")
            ?: error("module_child_b not found")
        val moduleUmbrellaB = ModuleManager.getInstance(project).findModuleByName("module_umbrella_b")
            ?: error("module_umbrella_b not found")

        val entriesChildB = ModuleRootManager.getInstance(moduleChildB).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }
        assertFalse(
            "module_child_b must have no phoenix library entry - it belongs to umbrella_b, " +
                "which was not involved in the sync. Order entries: $entriesChildB",
            entriesChildB.any { it.startsWith("phoenix [") }
        )

        val entriesUmbrellaB = ModuleRootManager.getInstance(moduleUmbrellaB).orderEntries
            .filterIsInstance<LibraryOrderEntry>().mapNotNull { it.libraryName }
        assertFalse(
            "module_umbrella_b must have no phoenix library entry - it owns umbrella_b, " +
                "which was not involved in the sync. Order entries: $entriesUmbrellaB",
            entriesUmbrellaB.any { it.startsWith("phoenix [") }
        )
    }
}
