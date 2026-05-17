package org.elixir_lang.mix.sync

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.mix.library.Kind as MixLibraryKind

// WritePlan, ModuleWriteOp, LibraryRootsPlan, DeleteAllPlan, ModuleDepsPlan, buildWritePlan,
// applyWritePlan are internal to the sync package. Tests in the same package access them directly.

/**
 * Behavioural tests for [MixDepsSyncService].
 *
 * Covers:
 * - Pending-set deduplication: repeated identical enqueues collapse to one entry.
 * - Coalescing: a [SyncRequest.DeleteAll] suppresses any pending [SyncRequest.DepRoot] for the
 *   same deps tree, preventing a re-sync from undoing a deliberate delete.
 * - Service registration: the service is reachable via `project.service<MixDepsSyncService>()`.
 * - Delete-before-sync ordering: delete and sync requests for independent targets both execute
 *   in a single drain, with deletes always running first.
 * - mix.exs resolution: a listener-produced [SyncRequest.MixFile] resolves to the owning module
 *   during drain and wires any declared library deps into the module's order entries.
 * - Multi-module scoping:
 *   - Two content roots with the same dep name produce two distinct scoped libraries.
 *   - Syncing one deps root updates only that root's scoped library.
 *   - Deleting one dep removes only that root's scoped library.
 *   - `_build/<env>/lib/<dep>/ebin` is scoped to its own content root.
 *   - A single-root DepRoot request does not wire unrelated modules.
 *   - Legacy unscoped library is removed when a scoped replacement is created.
 * - [SyncRequest.DeleteAll] removes scoped placeholder libraries (empty, no roots) as well as
 *   libraries with roots, guarded by [MixLibraryKind] so unrelated user libraries survive.
 * - A dep with an external `path:` (outside all content roots) produces a module order entry
 *   whose name matches the library created for that external path, ensuring the order entry
 *   and the project library table agree on the same library identity.
 *
 * Intentionally deferred (not covered here):
 * - Cancellation robustness: the platform WARA guarantees that a cancelled `readAction` leaves
 *   no stale `CachedValue`; verifying this requires injecting cancellation mid-PSI-scan in a way
 *   not supported by the test framework.
 * - Write-starvation acceptance: the absence of `invokeAndWait` from [MixDepsSyncService] is
 *   the static proof that the deadlock vector is gone; a timing-sensitive concurrent test would
 *   be flaky.
 * - Full service-lifecycle (scope cancelled on project close): requires creating and disposing a
 *   separate project instance; the registration smoke test covers the normal liveness path.
 *
 * The delete-coalescing and delete+sync tests use [drainDirectly] to
 * avoid VFS-event interference from directories created during fixture setup.
 */
class MixDepsSyncServiceTest : PlatformTestCase() {

    override fun setUp() {
        super.setUp()
        // The light project and its services are reused across test methods in the same class.
        // Clear any pending requests from previous tests so that the pendingCount assertions
        // and `waitUntil` conditions start from a clean slate.
        project.service<MixDepsSyncService>().clearPendingForTesting()
    }

    override fun tearDown() {
        runAll(
            { MixTestFixtures.removeAllContentRoots(myFixture) },
            { MixSyncTestHelpers.removeAllLibraries(project) },
            { super.tearDown() }
        )
    }

    // ------------------------------------------------------------------
    // Test 6a - duplicate enqueues are deduplicated by the Set accumulator
    // ------------------------------------------------------------------

    fun testEnqueue_sameDepRootEnqueuedRepeatedly_deduplicatesToOneEntry() {
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("deps/phoenix")
        val request = SyncRequest.DepRoot(depRoot)
        val service = project.service<MixDepsSyncService>()

        // Clear any VFS-triggered enqueues that may have fired during findOrCreateDir
        // (the temp dir is a content root, so dep-dir creation events are classified and enqueued).
        service.clearPendingForTesting()

        repeat(100) { service.enqueue(request) }

        // The background debounce is 250 ms; this assertion runs in < 10 ms.
        assertEquals(
            "100 identical DepRoot enqueues must collapse to 1 pending entry (Set semantics)",
            1,
            service.pendingCount
        )
    }

    fun testEnqueue_allSingletonEnqueuedRepeatedly_deduplicatesToOneEntry() {
        val service = project.service<MixDepsSyncService>()

        // SyncRequest.All is a singleton object; repeated enqueues of the same instance = 1 entry.
        repeat(50) { service.enqueue(SyncRequest.All) }

        assertEquals(
            "50 enqueues of the All singleton should deduplicate to 1",
            1,
            service.pendingCount
        )
    }

    // ------------------------------------------------------------------
    // Test 6b - DeleteAll suppresses DepRoot for the same tree
    // (also covers Test 8 - delete-before-sync ordering for the same tree)
    // ------------------------------------------------------------------

    fun testDrain_deleteAllSuppressesDepRootForSameTree_libraryAbsent() {
        val myApp = MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val phoenixLibName = scopedDepLibraryName(myApp.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed: create the phoenix library via the production drain path.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)
        assertNotNull("Phoenix library must exist before drain", libraryTable.getLibraryByName(phoenixLibName))

        // Clear any VFS-triggered enqueues from directory creation above, so the drain only
        // sees the two requests we explicitly enqueue next.  VFS create events are delivered on
        // EDT; `clearPendingForTesting()` called here (before the next EDT pump) is safe because
        // the test thread IS the EDT, so no VFS event can fire between this call and the enqueues.
        service.clearPendingForTesting()

        // Enqueue DeleteAll (delete the whole deps/ tree) AND DepRoot (re-sync phoenix).
        // The dep files still exist on disk - WITHOUT coalescing the DepRoot sync would
        // re-add the library after the delete. WITH coalescing the DepRoot is suppressed
        // and phoenix must remain absent after the drain.
        service.enqueue(SyncRequest.DeleteAll(depsDir.url))
        service.enqueue(SyncRequest.DepRoot(depRoot))

        // Run drain() directly (bypasses the 250 ms debounce) and assert immediately after it
        // returns - before the background debounce can fire a second time for any VFS events
        // that were re-enqueued during the drain's EDT-pump cycle.
        drainDirectly(service)

        assertNull(
            "Phoenix library must be absent: DeleteAll suppressed the DepRoot",
            libraryTable.getLibraryByName(phoenixLibName)
        )
    }

    // ------------------------------------------------------------------
    // Test 7 (minimal) - service is registered and accepts events without throwing
    // ------------------------------------------------------------------

    fun testService_isRegisteredAndEnqueueDoesNotThrow() {
        val service = project.service<MixDepsSyncService>()
        assertNotNull("MixDepsSyncService must be registered as a project service", service)
        service.enqueue(SyncRequest.All)
    }

    // ------------------------------------------------------------------
    // Test 8 - delete and sync for independent targets execute in the same drain
    // ------------------------------------------------------------------

    fun testDrain_deleteOneAndDepRootForDifferentTargets_bothEffectsApplied() {
        val myApp = MixTestFixtures.createMixRoot(myFixture, "my_app")
        val phoenixRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        val ectoRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/ecto")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/ecto/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix", "ecto")
        val phoenixLibName = scopedDepLibraryName(myApp.url, "phoenix")
        val ectoLibName = scopedDepLibraryName(myApp.url, "ecto")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed: create both libraries via the production drain path.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(phoenixRoot))
        service.enqueue(SyncRequest.DepRoot(ectoRoot))
        drainDirectly(service)
        assertNotNull("phoenix library must exist before drain", libraryTable.getLibraryByName(phoenixLibName))
        assertNotNull("ecto library must exist before drain", libraryTable.getLibraryByName(ectoLibName))

        // Clear VFS-triggered setup events so this test drains exactly the two explicit
        // requests below. Otherwise fixture-created _build events can add SyncRequest.All,
        // which legitimately rebuilds phoenix from the still-present test fixture dep root.
        service.clearPendingForTesting()

        // Delete phoenix only (DeleteOne); re-sync ecto (DepRoot).
        // DeleteOne does NOT suppress DepRoot syncs - the two requests are independent.
        // Expected after drain: phoenix absent (deleted), ecto present (re-synced from files).
        service.enqueue(SyncRequest.DeleteOne("phoenix", phoenixRoot.parent?.url, myApp.url))
        service.enqueue(SyncRequest.DepRoot(ectoRoot))

        drainDirectly(service)

        assertNull("Phoenix must be absent - it was targeted by DeleteOne", libraryTable.getLibraryByName(phoenixLibName))
        assertNotNull(
            "Ecto must be present - its DepRoot sync ran in the same drain " +
                "(delete-before-sync ordering preserved)",
            libraryTable.getLibraryByName(ectoLibName)
        )
    }

    // ------------------------------------------------------------------
    // Test 9 - unresolved mix.exs request resolves to module and wires library order entries
    // ------------------------------------------------------------------

    fun testMixFileRequest_resolvesOwningModuleAndWiresLibraryIntoModuleOrderEntries() {
        val root = MixTestFixtures.createMixRootWithDeps(myFixture, "my_app", "phoenix")
        val mixFile = root.findChild("mix.exs")!!
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val phoenixLibName = scopedDepLibraryName(root.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed: create the phoenix library via the production drain path.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)
        assertNotNull("phoenix project library must exist before fan-out", libraryTable.getLibraryByName(phoenixLibName))

        service.clearPendingForTesting()
        service.enqueue(SyncRequest.MixFile(mixFile))
        drainDirectly(service)

        val orderEntries = ModuleRootManager.getInstance(myFixture.module).orderEntries
        val hasPhoenixLibEntry = orderEntries.any { it is LibraryOrderEntry && it.libraryName == phoenixLibName }
        assertTrue(
            "After fan-out, myFixture.module should have a LibraryOrderEntry for '$phoenixLibName'. " +
                "Order entries: ${orderEntries.map { it.presentableName }}",
            hasPhoenixLibEntry
        )
    }

    // ------------------------------------------------------------------
    // Multi-module scoping tests
    // ------------------------------------------------------------------

    /**
     * Two content roots with the same dep name produce two distinct scoped libraries.
     *
     * Without root-scoped naming, both roots would share a single `"phoenix"` library, causing
     * cross-contamination. With scoped naming, each root gets its own `"phoenix [<contentRootUrl>]"`
     * library.
     */
    fun testSameDepNameInTwoContentRootsCreatesTwoDistinctLibraries() {
        val rootA = MixTestFixtures.createMixRoot(myFixture, "project_a")
        val rootB = MixTestFixtures.createMixRoot(myFixture, "project_b")
        val depRootA = myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix/lib")
        val depRootB = myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_a", "dev", "phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_b", "dev", "phoenix")

        val libNameA = scopedDepLibraryName(rootA.url, "phoenix")
        val libNameB = scopedDepLibraryName(rootB.url, "phoenix")

        // The two scoped names must be distinct.
        assertFalse(
            "Two different content roots must produce different scoped library names",
            libNameA == libNameB
        )

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRootA))
        service.enqueue(SyncRequest.DepRoot(depRootB))
        drainDirectly(service)

        assertNotNull("Library for project_a/phoenix must exist", libraryTable.getLibraryByName(libNameA))
        assertNotNull("Library for project_b/phoenix must exist", libraryTable.getLibraryByName(libNameB))
        assertNull(
            "Unscoped legacy library 'phoenix' must NOT be created",
            libraryTable.getLibraryByName("phoenix")
        )
    }

    /**
     * Syncing a single deps root updates only that root's scoped library, leaving the other
     * root's library untouched.
     */
    fun testSyncingOneDepsRootUpdatesOnlyThatRootsLibrary() {
        val rootA = MixTestFixtures.createMixRoot(myFixture, "project_a")
        val rootB = MixTestFixtures.createMixRoot(myFixture, "project_b")
        val depRootA = myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix/lib")
        val depRootB = myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_a", "dev", "phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_b", "dev", "phoenix")

        val libNameA = scopedDepLibraryName(rootA.url, "phoenix")
        val libNameB = scopedDepLibraryName(rootB.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed both libraries via the production drain path.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRootA))
        service.enqueue(SyncRequest.DepRoot(depRootB))
        drainDirectly(service)
        val initialSourceUrlsB = libraryTable.getLibraryByName(libNameB)!!.getUrls(OrderRootType.SOURCES).toSet()

        // Enqueue only a DepRoot for project_a and drain.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRootA))
        drainDirectly(service)

        // project_a's library still present.
        assertNotNull("project_a phoenix library must still exist", libraryTable.getLibraryByName(libNameA))
        // project_b's library source roots must be unchanged (sync did not touch it).
        val finalSourceUrlsB = libraryTable.getLibraryByName(libNameB)?.getUrls(OrderRootType.SOURCES)?.toSet()
        assertEquals(
            "project_b phoenix library source roots must be unchanged after syncing only project_a",
            initialSourceUrlsB,
            finalSourceUrlsB
        )
    }

    /**
     * Deleting `deps/phoenix` in one content root removes only that root's scoped library,
     * leaving the other root's scoped library intact.
     */
    fun testDeletingOneDepRemovesOnlyThatRootsScopedLibrary() {
        val rootA = MixTestFixtures.createMixRoot(myFixture, "project_a")
        val rootB = MixTestFixtures.createMixRoot(myFixture, "project_b")
        val depRootA = myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix/lib")
        val depRootB = myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_a", "dev", "phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_b", "dev", "phoenix")

        val libNameA = scopedDepLibraryName(rootA.url, "phoenix")
        val libNameB = scopedDepLibraryName(rootB.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed both libraries via the production drain path.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRootA))
        service.enqueue(SyncRequest.DepRoot(depRootB))
        drainDirectly(service)
        assertNotNull(libraryTable.getLibraryByName(libNameA))
        assertNotNull(libraryTable.getLibraryByName(libNameB))

        // Delete only the scoped library for project_a.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DeleteOne("phoenix", depRootA.parent?.url, rootA.url))
        drainDirectly(service)

        assertNull(
            "project_a phoenix library must be removed after DeleteOne targeting project_a",
            libraryTable.getLibraryByName(libNameA)
        )
        assertNotNull(
            "project_b phoenix library must remain after DeleteOne targeting only project_a",
            libraryTable.getLibraryByName(libNameB)
        )
    }

    /**
     * `_build/<env>/lib/<dep>/ebin` in project_a is mapped to project_a's phoenix library and
     * NOT included in project_b's phoenix library. Scoped _build scanning prevents
     * cross-contamination between content roots.
     */
    fun testBuildEbinMapsToDepUnderSameContentRoot() {
        val rootA = MixTestFixtures.createMixRoot(myFixture, "project_a")
        val rootB = MixTestFixtures.createMixRoot(myFixture, "project_b")
        val depRootA = myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix/lib")
        val depRootB = myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix/lib")
        // Only project_a has _build artifacts for phoenix.
        MixTestFixtures.addBuildArtifacts(myFixture, "project_a", "dev", "phoenix")
        // project_b has no phoenix ebin artifact.
        myFixture.tempDirFixture.findOrCreateDir("project_b/_build/dev/consolidated")

        val libNameA = scopedDepLibraryName(rootA.url, "phoenix")
        val libNameB = scopedDepLibraryName(rootB.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRootA))
        service.enqueue(SyncRequest.DepRoot(depRootB))
        drainDirectly(service)

        val classUrlsA = libraryTable.getLibraryByName(libNameA)?.getUrls(OrderRootType.CLASSES)?.toList().orEmpty()
        val classUrlsB = libraryTable.getLibraryByName(libNameB)?.getUrls(OrderRootType.CLASSES)?.toList().orEmpty()

        assertTrue(
            "project_a phoenix library must include its own ebin",
            classUrlsA.any { it.contains("project_a/_build") }
        )
        assertFalse(
            "project_b phoenix library must NOT include project_a's ebin (scoping prevents cross-contamination)",
            classUrlsB.any { it.contains("project_a/_build") }
        )
    }

    /**
     * A single-root DepRoot request does NOT wire dependencies into unrelated modules.
     *
     * Verifies that the affected-module-only fan-out (which replaced the old all-modules fan-out)
     * prevents unrelated-module dependency entries from being modified.
     */
    fun testSingleRootDepRootDoesNotWireUnrelatedModules() {
        // This test relies on PSI resolution of mix.exs, so we create a proper mix root with deps.
        val myApp = MixTestFixtures.createMixRootWithDeps(myFixture, "my_app", "phoenix")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val phoenixLibName = scopedDepLibraryName(myApp.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Pre-populate the library via the production drain path.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)
        assertNotNull(libraryTable.getLibraryByName(phoenixLibName))

        // Capture the current order entries before the scoped drain.
        val entriesBefore = ModuleRootManager.getInstance(myFixture.module).orderEntries
            .filterIsInstance<LibraryOrderEntry>()
            .map { it.libraryName }
            .toSet()

        // Drain only the DepRoot for my_app - affected-module fan-out must only touch the module
        // owning my_app's content root (myFixture.module in this single-module test).
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)

        // The library for my_app/phoenix must still be present (DepRoot re-sync ran).
        assertNotNull(libraryTable.getLibraryByName(phoenixLibName))
        // No spurious extra library entries added to the module from unrelated content roots.
        val entriesAfter = ModuleRootManager.getInstance(myFixture.module).orderEntries
            .filterIsInstance<LibraryOrderEntry>()
            .map { it.libraryName }
            .toSet()
        val unexpectedNewEntries = entriesAfter - entriesBefore - setOf(phoenixLibName)
        assertTrue(
            "No unexpected library entries must be wired into the module from an unrelated content root. " +
                "Unexpected: $unexpectedNewEntries",
            unexpectedNewEntries.isEmpty()
        )
    }

    /**
     * A legacy unscoped library with [MixLibraryKind] (e.g. `"phoenix"`) is removed when a scoped
     * replacement is created (e.g. `"phoenix [file:///my_app]"`).
     *
     * A user-created library that happens to share the dep name but does NOT carry [MixLibraryKind]
     * must NOT be removed - the guard verifies library.kind before deletion.
     */
    fun testLegacyUnscopedLibraryRemovedWhenScopedReplacementCreated() {
        val myApp = MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val scopedLibName = scopedDepLibraryName(myApp.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Pre-plant a legacy unscoped Mix dep library - has Kind, so should be cleaned up.
        // Also plant an unrelated user library with the same name but WITHOUT Kind - must survive.
        WriteAction.run<Throwable> {
            val model = libraryTable.modifiableModel
            model.createLibrary("phoenix", MixLibraryKind)           // legacy Mix dep library
            model.createLibrary("phoenix-unrelated", null)           // user library, no kind
            model.commit()
        }
        assertNotNull("Legacy Mix dep library must exist before sync", libraryTable.getLibraryByName("phoenix"))
        assertNotNull("Unrelated library must exist before sync", libraryTable.getLibraryByName("phoenix-unrelated"))

        // Run the production drain path - scoped library is created and the legacy Mix dep library is cleaned up.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)

        assertNotNull("Scoped library must be created", libraryTable.getLibraryByName(scopedLibName))
        assertNull(
            "Legacy unscoped Mix dep library must be removed when the scoped replacement is created",
            libraryTable.getLibraryByName("phoenix")
        )
        assertNotNull(
            "Unrelated user library (no Kind) must NOT be removed by the legacy cleanup guard",
            libraryTable.getLibraryByName("phoenix-unrelated")
        )
    }

    // ------------------------------------------------------------------
    // C3 remediation tests
    // ------------------------------------------------------------------

    /**
     * Unfetched dep declared in mix.exs but not yet on disk still appears in the module's
     * library deps as an empty placeholder (missingLibraryDeps code path).
     *
     * Before the fix, mapNotNullTo skipped deps whose virtualFile was null, losing them entirely.
     * After the fix, resolution falls back to the first content root containing mix.exs so the
     * scoped name is always produced and the placeholder library is created.
     */
    fun testUnfetchedDepProducesPlaceholderLibraryEntry() {
        // Create a mix root with a dep declared in mix.exs, but deliberately DO NOT create
        // the deps/<dep> directory - simulating a dep that has not been fetched yet.
        val myApp = MixTestFixtures.createMixRootWithDeps(myFixture, "my_app", "unfetched_dep")
        // Verify the dep directory really does not exist.
        assertNull("deps/unfetched_dep must not exist for this test", myApp.findChild("deps")?.findChild("unfetched_dep"))

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        service.clearPendingForTesting()
        service.enqueue(SyncRequest.MixFile(myApp.findChild("mix.exs")!!))
        drainDirectly(service)

        // The library must exist (even if empty) so that the module order entry may be wired.
        // We search by dep-name prefix rather than exact URL since the exact content-root URL
        // depends on the fallback heuristic (first mix.exs-bearing root in contentRoots).
        val createdLibrary = libraryTable.libraries.firstOrNull { lib ->
            val name = lib.name ?: return@firstOrNull false
            name.startsWith("unfetched_dep [") && name.endsWith("]")
        }
        assertNotNull(
            "A placeholder library for unfetched_dep must be created in the project library table " +
                "so that the module order entry is wired correctly. Libraries: ${libraryTable.libraries.mapNotNull { it.name }}",
            createdLibrary
        )
    }

    /**
     * A `SyncRequest.SyncRoot` (produced by resolving a `BuildPath` event for a registered content
     * root) scopes the sync to that single content root's deps, not all content roots.
     *
     * Before the fix, `BuildPath` resolved to `SyncRequest.All`, syncing all roots. After the fix it
     * resolves to `SyncRequest.SyncRoot` and only the affected root's deps are re-synced.
     */
    fun testSyncRootRequestScopesDepSyncToSingleContentRoot() {
        val rootA = MixTestFixtures.createMixRoot(myFixture, "project_a")
        val rootB = MixTestFixtures.createMixRoot(myFixture, "project_b")
        val depRootA = myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_a/deps/phoenix/lib")
        val depRootB = myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("project_b/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_a", "dev", "phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "project_b", "dev", "phoenix")

        val libNameA = scopedDepLibraryName(rootA.url, "phoenix")
        val libNameB = scopedDepLibraryName(rootB.url, "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed both libraries via the production drain path.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRootA))
        service.enqueue(SyncRequest.DepRoot(depRootB))
        drainDirectly(service)
        val initialClassUrlsB = libraryTable.getLibraryByName(libNameB)!!.getUrls(OrderRootType.CLASSES).toSet()

        // Enqueue a SyncRoot for project_a only and drain.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.SyncRoot(rootA.url))
        drainDirectly(service)

        // project_a's library must still be present and project_b's class roots must be unchanged.
        assertNotNull("project_a phoenix library must still exist", libraryTable.getLibraryByName(libNameA))
        val finalClassUrlsB = libraryTable.getLibraryByName(libNameB)?.getUrls(OrderRootType.CLASSES)?.toSet()
        assertEquals(
            "project_b phoenix library class roots must be unchanged after SyncRoot targeting project_a only",
            initialClassUrlsB,
            finalClassUrlsB
        )
    }

    // ------------------------------------------------------------------
    // DeleteAll and external-path dep tests
    // ------------------------------------------------------------------

    /**
     * [SyncRequest.DeleteAll] must remove scoped placeholder libraries that have no source or class
     * roots, in addition to the normally-populated scoped libraries it already removed.
     *
     * Placeholder libraries are created for deps declared in `mix.exs` but not yet fetched (no
     * `deps/<name>` directory exists). They have [MixLibraryKind] set but carry zero roots. The
     * source-root URI strategy used previously skipped them because their source-root list was
     * empty (`urls.isNotEmpty()` was false). The scoped-name + Kind strategy now catches them.
     *
     * Also verifies that an unrelated user library without [MixLibraryKind] is NOT removed, so
     * that user-created libraries that happen to share a dep name are never accidentally deleted.
     */
    fun testDeleteAllRemovesEmptyPlaceholderLibraryWithKind() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        val contentRootUrl = root.url
        val depsUrl = "$contentRootUrl/deps"

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed the library table:
        //   (a) scoped placeholder with Kind but NO roots (simulates an unfetched dep)
        //   (b) user library without Kind (must survive delete)
        val placeholderName = scopedDepLibraryName(contentRootUrl, "placeholder_dep")
        WriteAction.run<Throwable> {
            val model = libraryTable.modifiableModel
            model.createLibrary(placeholderName, MixLibraryKind)  // placeholder - no roots added
            model.createLibrary("user_lib_no_kind", null)         // unrelated user library
            model.commit()
        }
        assertNotNull("Placeholder library must exist before DeleteAll", libraryTable.getLibraryByName(placeholderName))
        assertNotNull("User library must exist before DeleteAll", libraryTable.getLibraryByName("user_lib_no_kind"))

        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DeleteAll(depsUrl))
        drainDirectly(service)

        assertNull(
            "Scoped placeholder library (no roots) must be deleted by DeleteAll",
            libraryTable.getLibraryByName(placeholderName)
        )
        assertNotNull(
            "User library without Kind must NOT be deleted by DeleteAll (Kind guard)",
            libraryTable.getLibraryByName("user_lib_no_kind")
        )
    }

    /**
     * For a dep whose `path:` option points outside all registered content roots (an external
     * dep), the module order entry name must match the library that [MixDepsSyncService] creates
     * for that external path so that the IDE can actually resolve the entry to a real library.
     *
     * When `buildExternalLibraryPlans` finds an external dep directory it derives the library
     * name from the physical grandparent of that directory. Without a matching lookup in
     * `buildModuleDepsPlan`, the module order entry falls back to a name scoped to the mix.exs
     * content root instead - a different string - so the order entry points at a library that
     * does not exist in the project library table.
     *
     * This test verifies that both sides agree on the library name so the module order entry is
     * correctly resolved.
     */
    fun testExternalPathDepModuleOrderEntryMatchesExternalLibraryPlan() {
        // Create external_lib at the fixture temp dir root - NOT under any registered content root.
        val externalLib = myFixture.tempDirFixture.findOrCreateDir("external_lib")
        myFixture.tempDirFixture.findOrCreateDir("external_lib/lib")

        // Allow VFS root access to the external dir (it lives outside any module content root).
        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, externalLib.path)

        // Create my_app as the content root whose mix.exs declares the external path dep.
        // Use externalLib.path (VirtualFile.path, always forward-slash) as the Elixir string so
        // that Dep.virtualFile() can later find the directory via VfsUtil.findFile(Paths.get(path)).
        val myApp = myFixture.tempDirFixture.findOrCreateDir("my_app")
        val externalPath = externalLib.path  // absolute, forward-slash, no "file://" prefix
        myFixture.tempDirFixture.createFile(
            "my_app/mix.exs",
            "defmodule MyApp.MixProject do\n" +
                "  use Mix.Project\n" +
                "\n" +
                "  def project do\n" +
                "    [app: :my_app, version: \"0.1.0\", deps: deps()]\n" +
                "  end\n" +
                "\n" +
                "  def deps do\n" +
                "    [{:external_lib, path: \"$externalPath\"}]\n" +
                "  end\n" +
                "end\n"
        )
        PsiTestUtil.addContentRoot(myFixture.module, myApp)

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.MixFile(myApp.findChild("mix.exs")!!))
        drainDirectly(service)

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Find whichever library was created for external_lib (name is runtime-determined by the
        // VirtualFile grandparent path, which is platform-dependent).
        val externalLibrary = libraryTable.libraries.firstOrNull { lib ->
            val name = lib.name ?: return@firstOrNull false
            name.startsWith("external_lib [") && name.endsWith("]")
        }
        assertNotNull(
            "A library for external_lib must have been created. Libraries: " +
                "${libraryTable.libraries.mapNotNull { it.name }}",
            externalLibrary
        )

        // The module order entry must reference the SAME library name that was created.
        val orderEntries = ModuleRootManager.getInstance(myFixture.module).orderEntries
        val externalLibEntry = orderEntries
            .filterIsInstance<LibraryOrderEntry>()
            .firstOrNull { it.libraryName?.startsWith("external_lib [") == true }
        assertNotNull(
            "A module order entry for external_lib must have been wired. Order entries: " +
                "${orderEntries.map { it.presentableName }}",
            externalLibEntry
        )
        assertEquals(
            "Module order entry name must match the library created for the external dep " +
                "(both must use the same scoped name so the order entry resolves to a real library)",
            externalLibrary!!.name,
            externalLibEntry!!.libraryName
        )
    }

    // ------------------------------------------------------------------
    // DeleteAll for a sibling content root must NOT suppress SyncRoot for an
    // unrelated content root whose URL is a string prefix of the depsUrl
    // (e.g. "app" vs "app2").
    // ------------------------------------------------------------------

    /**
     * Regression test: `coalesceRequests` previously used `startsWith` to check whether
     * a [SyncRequest.DeleteAll].depsUrl "belongs to" a [SyncRequest.SyncRoot].contentRootUrl.
     *
     * Given two content roots whose URLs share a string prefix:
     *   - `file:///tmp/app`  (the target of SyncRoot)
     *   - `file:///tmp/app2` (the target of DeleteAll - depsUrl = `file:///tmp/app2/deps`)
     *
     * The old `startsWith` check incorrectly matched because `"file:///tmp/app2/deps".startsWith("file:///tmp/app")`
     * is `true`. The fixed code uses exact equality: `depsUrl == "${contentRootUrl}/deps"`, which
     * correctly keeps the SyncRoot for `app` when the DeleteAll targets `app2`.
     */
    fun testCoalesceRequests_deleteAllForSiblingRoot_doesNotSuppressSyncRootForUnrelatedRoot() {
        // Two content roots that share a string prefix: "app" and "app2".
        val app = myFixture.tempDirFixture.findOrCreateDir("app")
        val app2 = myFixture.tempDirFixture.findOrCreateDir("app2")
        myFixture.tempDirFixture.findOrCreateDir("app/deps")
        myFixture.tempDirFixture.findOrCreateDir("app2/deps")
        myFixture.tempDirFixture.findOrCreateDir("app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "app", "dev", "phoenix")
        PsiTestUtil.addContentRoot(myFixture.module, app)
        PsiTestUtil.addContentRoot(myFixture.module, app2)

        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        // Seed: create the phoenix library so we can verify it is re-created by the SyncRoot.
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val phoenixLibName = scopedDepLibraryName(app.url, "phoenix")
        service.enqueue(SyncRequest.DepRoot(myFixture.tempDirFixture.getFile("app/deps/phoenix")!!))
        drainDirectly(service)
        assertNotNull("phoenix library must exist after seed", libraryTable.getLibraryByName(phoenixLibName))

        // Delete the phoenix library so SyncRoot has a visible effect.
        WriteAction.run<Throwable> { libraryTable.getLibraryByName(phoenixLibName)?.let { libraryTable.removeLibrary(it) } }
        assertNull("phoenix library must be absent before drain", libraryTable.getLibraryByName(phoenixLibName))

        // Enqueue DeleteAll for app2 (the *sibling* root) and SyncRoot for app.
        // With the old startsWith bug the SyncRoot for "app" would be suppressed because
        //   "file:///.../app2/deps".startsWith("file:///.../app") == true.
        // With the fix the SyncRoot survives and phoenix is re-created.
        service.enqueue(SyncRequest.DeleteAll("${app2.url}/deps"))
        service.enqueue(SyncRequest.SyncRoot(app.url))

        drainDirectly(service)

        assertNotNull(
            "phoenix library must be present: DeleteAll for app2 must NOT suppress SyncRoot for app",
            libraryTable.getLibraryByName(phoenixLibName)
        )
    }

    // ------------------------------------------------------------------
    // DeleteAll must not remove a library whose source root lives
    // under a sibling path like "deps2" (path-boundary safety).
    // ------------------------------------------------------------------

    /**
     * Regression test: the delete-all logic previously used `java.net.URI.relativize` to
     * check whether a library's source roots sit under the given `depsUrl`.
     *
     * `URI.relativize` is not path-boundary-safe: given
     *   - depsUrl   = `file:///tmp/app/deps`
     *   - sourceUrl = `file:///tmp/app/deps2/sibling_dep/lib`
     *
     * `URI("file:///tmp/app/deps").relativize(URI("file:///tmp/app/deps2/sibling_dep/lib"))`
     * returns `"2/sibling_dep/lib"` - a relative URI with no `../` prefix. The old guard
     * (`!relativeURI.isAbsolute && !relativeURI.toString().startsWith("../")`) treats this as a
     * match and incorrectly removes the sibling library.
     *
     * The fix replaces `URI.relativize` with `VfsUtilCore.isEqualOrAncestor(depsPrefixUrl, url)`
     * where `depsPrefixUrl` is `depsUrl + "/"`. The trailing slash ensures the path-boundary is
     * respected: `.../deps/` is an ancestor of `.../deps/phoenix/lib` but NOT of `.../deps2/...`.
     */
    fun testDeleteAllLibraries_doesNotRemoveLibraryUnderSiblingDeps2Path() {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val service = project.service<MixDepsSyncService>()

        // Create two directories to use as fake source roots, simulating:
        //   - phoenix under  app/deps/phoenix/lib  (SHOULD be removed)
        //   - sibling under  app/deps2/sibling_dep/lib  (must NOT be removed)
        val phoenixLib = myFixture.tempDirFixture.findOrCreateDir("app/deps/phoenix/lib")
        val siblingLib = myFixture.tempDirFixture.findOrCreateDir("app/deps2/sibling_dep/lib")

        WriteAction.run<Throwable> {
            // Create a legacy unscoped library for phoenix (Strategy-2 target: has source roots).
            val phoenixModel = libraryTable.modifiableModel
            val phoenixLib2 = phoenixModel.createLibrary("phoenix")
            phoenixLib2.modifiableModel.let { libModel ->
                libModel.addRoot(phoenixLib.url, OrderRootType.SOURCES)
                libModel.commit()
            }
            phoenixModel.commit()

            // Create a library for sibling_dep whose source root is under deps2 (the sibling).
            val siblingModel = libraryTable.modifiableModel
            val siblingLib2 = siblingModel.createLibrary("sibling_dep")
            siblingLib2.modifiableModel.let { libModel ->
                libModel.addRoot(siblingLib.url, OrderRootType.SOURCES)
                libModel.commit()
            }
            siblingModel.commit()
        }

        assertNotNull("phoenix library must exist before delete", libraryTable.getLibraryByName("phoenix"))
        assertNotNull("sibling_dep library must exist before delete", libraryTable.getLibraryByName("sibling_dep"))

        // Delete all libraries for app/deps - should remove phoenix but NOT sibling_dep.
        val app = myFixture.tempDirFixture.findOrCreateDir("app")
        PsiTestUtil.addContentRoot(myFixture.module, app)
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DeleteAll("${app.url}/deps"))
        drainDirectly(service)

        assertNull(
            "phoenix library must be removed: its source root is under app/deps/",
            libraryTable.getLibraryByName("phoenix")
        )
        assertNotNull(
            "sibling_dep library must NOT be removed: its source root is under app/deps2/, not app/deps/",
            libraryTable.getLibraryByName("sibling_dep")
        )

        // Cleanup the sibling_dep library (phoenix was already removed by the test).
        WriteAction.run<Throwable> {
            libraryTable.getLibraryByName("sibling_dep")?.let { libraryTable.removeLibrary(it) }
        }
    }

    // ==========================================================================
    // Narrow write critical section - buildWritePlan / applyWritePlan tests
    // ==========================================================================

    // ------------------------------------------------------------------
    // Diff-correctness - existing library with identical roots produces no write op;
    //  existing library with changed roots produces a minimal diff write op.
    // ------------------------------------------------------------------

    /**
     * Seeds a phoenix library with a specific set of source roots, then builds a [WritePlan] via
     * [buildWritePlan] requesting a different set of roots for that library.  Asserts that:
     * - A [LibraryWriteOp] is emitted only for the library that changed.
     * - The op carries the exact add/remove diff (not a clear+rebuild).
     * - No write op is emitted for a library whose roots are already up to date.
     */
    fun testBuildWritePlan_diffCorrectnessForExistingLibrary() {
        val myApp = myFixture.tempDirFixture.findOrCreateDir("my_app")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        val libOld = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib_old")
        val libNew = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib_new")
        PsiTestUtil.addContentRoot(myFixture.module, myApp)

        val contentRootUrl = myApp.url
        val libName = scopedDepLibraryName(contentRootUrl, "phoenix")

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed: phoenix library with lib_old as source root.
        WriteAction.run<Throwable> {
            val model = libraryTable.modifiableModel
            val lib = model.createLibrary(libName)
            lib.modifiableModel.let { lm ->
                lm.addRoot(libOld.url, OrderRootType.SOURCES)
                lm.commit()
            }
            model.commit()
        }

        // Build a SyncPlan requesting lib_new (not lib_old) as the source root.
        val plan = SyncPlan(
            libraryPlans = listOf(
                LibraryRootsPlan(
                    contentRootUrl = contentRootUrl,
                    depName = "phoenix",
                    classRootUrls = emptyList(),
                    sourceRootUrls = listOf(libNew.url),
                    excludeFolders = emptyList(),
                )
            ),
            modulePlans = emptyList(),
        )

        val writePlan = runSuspendOnPooledThread { buildWritePlan(project, plan) }

        // Exactly one LibraryWriteOp for phoenix.
        val op = writePlan.libraryWriteOps.singleOrNull { it.libraryName == libName }
        assertNotNull("Expected a LibraryWriteOp for $libName", op)
        requireNotNull(op)

        assertFalse("createWithKind must be false for an existing library", op.createWithKind)
        assertEquals("addSourceUrls must contain only lib_new", listOf(libNew.url), op.addSourceUrls)
        assertEquals("removeSourceUrls must contain only lib_old", listOf(libOld.url), op.removeSourceUrls)
        assertTrue("addClassUrls must be empty", op.addClassUrls.isEmpty())
        assertTrue("removeClassUrls must be empty", op.removeClassUrls.isEmpty())

        // Cleanup
        WriteAction.run<Throwable> { libraryTable.getLibraryByName(libName)?.let { libraryTable.removeLibrary(it) } }
    }

    /**
     * Seeds a phoenix library with the exact roots that the sync plan requests.  Asserts that
     * [buildWritePlan] emits NO [LibraryWriteOp] for that library (nothing to change → no write).
     */
    fun testBuildWritePlan_noOpForAlreadyUpToDateLibrary() {
        val myApp = myFixture.tempDirFixture.findOrCreateDir("my_app_noop")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app_noop/deps/phoenix/lib")
        PsiTestUtil.addContentRoot(myFixture.module, myApp)

        val contentRootUrl = myApp.url
        val libName = scopedDepLibraryName(contentRootUrl, "phoenix")
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed with the exact same source roots as the plan.
        WriteAction.run<Throwable> {
            val model = libraryTable.modifiableModel
            val lib = model.createLibrary(libName)
            lib.modifiableModel.let { lm ->
                lm.addRoot(libDir.url, OrderRootType.SOURCES)
                lm.commit()
            }
            model.commit()
        }

        val plan = SyncPlan(
            libraryPlans = listOf(
                LibraryRootsPlan(
                    contentRootUrl = contentRootUrl,
                    depName = "phoenix",
                    classRootUrls = emptyList(),
                    sourceRootUrls = listOf(libDir.url),
                    excludeFolders = emptyList(),
                )
            ),
        )

        val writePlan = runSuspendOnPooledThread { buildWritePlan(project, plan) }

        assertNull(
            "No LibraryWriteOp expected when roots are already up to date",
            writePlan.libraryWriteOps.firstOrNull { it.libraryName == libName }
        )

        // Cleanup
        WriteAction.run<Throwable> { libraryTable.getLibraryByName(libName)?.let { libraryTable.removeLibrary(it) } }
    }

    // ------------------------------------------------------------------
    // Stale-plan resilience - applyWritePlan skips gracefully when a
    //  referenced module does not exist.
    // ------------------------------------------------------------------

    /**
     * Constructs a [WritePlan] that references a non-existent module name and verifies that
     * [applyWritePlan] completes without throwing, applying any library mutations that don't
     * depend on the missing module.
     */
    fun testApplyWritePlan_nonExistentModuleSkippedGracefully() {
        val contentRootUrl = myFixture.tempDirFixture.findOrCreateDir("stale_test").url
        val libName = scopedDepLibraryName(contentRootUrl, "phoenix")

        val writePlan = WritePlan(
            librariesToRemove = emptyList(),
            // Create a placeholder library to verify library work still proceeds.
            placeholderLibraries = setOf(libName),
            libraryWriteOps = emptyList(),
            legacyLibrariesToRemove = emptyList(),
            // ModuleWriteOp with a module name that doesn't exist.
            moduleWriteOps = listOf(
                ModuleWriteOp(
                    moduleName = "nonexistent_module_write_plan_test",
                    addModuleDeps = emptySet(),
                    addInvalidModuleDeps = emptySet(),
                    addLibraryDeps = setOf(libName),
                    addInvalidLibraryDeps = emptySet(),
                    addExcludeFolderUrls = emptyList(),
                )
            ),
        )

        // Must complete without throwing even though the module doesn't exist.
        val stats = runSuspendOnPooledThread { applyWritePlan(project, writePlan) }

        // Library was created (placeholder).
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        assertNotNull(
            "Placeholder library must be created even when a referenced module is absent",
            libraryTable.getLibraryByName(libName)
        )
        // Library counts: only placeholder creation.
        assertTrue("librariesChanged must be >= 1", stats.librariesChanged >= 1)
        // Module count: 'nonexistent' is skipped → 0 modules changed.
        assertEquals("modulesChanged must be 0 when the only module doesn't exist", 0, stats.modulesChanged)

        // Cleanup.
        WriteAction.run<Throwable> { libraryTable.getLibraryByName(libName)?.let { libraryTable.removeLibrary(it) } }
    }

    // ------------------------------------------------------------------
    // Write-phase parity - drain through the new pipeline produces the
    //  same library-table state as the old wide-write path.
    // ------------------------------------------------------------------

    /**
     * End-to-end parity test: verify that the [buildWritePlan] → [applyWritePlan] pipeline
     * (invoked via [MixDepsSyncService.drain]) populates a phoenix library's source and class roots from an actual
     * `deps/` and `_build/` fixture.
     *
     * This test verifies that the [buildWritePlan] → [applyWritePlan] pipeline
     * (invoked via [MixDepsSyncService.drain]) populates a phoenix library's source and class roots from an actual
     * `deps/` and `_build/` fixture, ensuring the production code path is exercised.
     */
    fun testDrain_buildWritePlanApplyWritePlanParity_libraryRootsPopulated() {
        val myApp = myFixture.tempDirFixture.findOrCreateDir("parity_test_app")
        myFixture.tempDirFixture.findOrCreateDir("parity_test_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("parity_test_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "parity_test_app", "dev", "phoenix")
        PsiTestUtil.addContentRoot(myFixture.module, myApp)

        val depsPhoenix = myFixture.tempDirFixture.getFile("parity_test_app/deps/phoenix")!!
        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depsPhoenix))
        drainDirectly(service)

        val libName = scopedDepLibraryName(myApp.url, "phoenix")
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val lib = libraryTable.getLibraryByName(libName)

        assertNotNull("phoenix library must be created by drain pipeline", lib)
        requireNotNull(lib)
        assertTrue(
            "phoenix library must have at least one source root (lib dir)",
            lib.getUrls(OrderRootType.SOURCES).isNotEmpty()
        )
        assertTrue(
            "phoenix library must have at least one class root (ebin dir)",
            lib.getUrls(OrderRootType.CLASSES).isNotEmpty()
        )

        // Cleanup
        WriteAction.run<Throwable> { libraryTable.getLibraryByName(libName)?.let { libraryTable.removeLibrary(it) } }
    }

    // ------------------------------------------------------------------
    // buildWritePlan placeholder computation accounts for libraries
    //  that will be removed by delete operations in the same plan.
    // ------------------------------------------------------------------

    /**
     * Verifies that [buildWritePlan] correctly treats a library as "missing" when it currently
     * exists but will be removed by a [DeleteAllPlan] in the same sync plan.  The library must
     * appear in [WritePlan.placeholderLibraries] so that [applyWritePlan] recreates it as an
     * empty placeholder for the module order entry to reference.
     */
    fun testBuildWritePlan_placeholderForLibraryScheduledForDeletion() {
        val myApp = myFixture.tempDirFixture.findOrCreateDir("placeholder_del_test")
        myFixture.tempDirFixture.findOrCreateDir("placeholder_del_test/deps")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("placeholder_del_test/deps/placeholder_dep/lib")
        PsiTestUtil.addContentRoot(myFixture.module, myApp)

        val contentRootUrl = myApp.url
        val libName = scopedDepLibraryName(contentRootUrl, "placeholder_dep")
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed the library so it exists in the snapshot.
        WriteAction.run<Throwable> {
            val model = libraryTable.modifiableModel
            val lib = model.createLibrary(libName)
            lib.modifiableModel.let { lm ->
                lm.addRoot(libDir.url, OrderRootType.SOURCES)
                lm.commit()
            }
            model.commit()
        }

        // Build a SyncPlan with a DeleteAll AND a ModuleDepsPlan that needs the library.
        val syncPlan = SyncPlan(
            deleteAlls = listOf(DeleteAllPlan("${contentRootUrl}/deps")),
            libraryPlans = emptyList(),
            modulePlans = listOf(
                ModuleDepsPlan(
                    moduleName = myFixture.module.name,
                    moduleDeps = emptySet(),
                    libraryDeps = setOf(libName),
                    externalLibraryPlans = emptyList(),
                )
            ),
        )

        val writePlan = runSuspendOnPooledThread { buildWritePlan(project, syncPlan) }

        // The library is in librariesToRemove (scheduled for deletion).
        assertTrue(
            "librariesToRemove must contain $libName (scoped match from DeleteAll)",
            writePlan.librariesToRemove.contains(libName)
        )
        // Because it will be deleted AND is not in libraryWriteOps, it must appear as a placeholder.
        assertTrue(
            "placeholderLibraries must contain $libName because it is needed by the module plan " +
                "but will be removed by the DeleteAll",
            writePlan.placeholderLibraries.contains(libName)
        )

        // Cleanup
        WriteAction.run<Throwable> { libraryTable.getLibraryByName(libName)?.let { libraryTable.removeLibrary(it) } }
    }

    /**
     * Regression test: a DeleteOne plus a re-sync (LibraryRootsPlan) for the same dep must not
     * silently drop the recreation.  The library scheduled for removal must be treated as absent
     * when computing libraryWriteOps, so the write plan emits a createWithKind op to recreate
     * the library after deletion.
     *
     * This test validates the [buildWritePlan] diff logic - the critical invariant is that a
     * library in librariesToRemove ALSO produces a [LibraryWriteOp] with createWithKind=true
     * when a [LibraryRootsPlan] for the same library is present.
     */
    fun testBuildWritePlan_deleteAndResyncSameDepRecreatesLibrary() {
        val myApp = myFixture.tempDirFixture.findOrCreateDir("delete_resync_test")
        myFixture.tempDirFixture.findOrCreateDir("delete_resync_test/deps")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("delete_resync_test/deps/phoenix/lib")
        PsiTestUtil.addContentRoot(myFixture.module, myApp)

        val contentRootUrl = myApp.url
        val libName = scopedDepLibraryName(contentRootUrl, "phoenix")
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed the library with the same roots the re-sync will request - this is the critical
        // scenario: identical roots means a naive diff sees no changes and emits no write op.
        WriteAction.run<Throwable> {
            val model = libraryTable.modifiableModel
            val lib = model.createLibrary(libName, MixLibraryKind)
            lib.modifiableModel.let { lm ->
                lm.addRoot(libDir.url, OrderRootType.SOURCES)
                lm.commit()
            }
            model.commit()
        }

        // Build a SyncPlan with a DeleteOne for phoenix AND a LibraryRootsPlan requesting the
        // same roots (simulating a delete event immediately followed by a re-sync event in the
        // same coalesced drain).
        val syncPlan = SyncPlan(
            deleteOnes = listOf(DeleteOnePlan("phoenix", contentRootUrl)),
            libraryPlans = listOf(
                LibraryRootsPlan(
                    contentRootUrl = contentRootUrl,
                    depName = "phoenix",
                    classRootUrls = emptyList(),
                    sourceRootUrls = listOf(libDir.url),
                    excludeFolders = emptyList(),
                )
            ),
            modulePlans = emptyList(),
        )

        val writePlan = runSuspendOnPooledThread { buildWritePlan(project, syncPlan) }

        // The library MUST appear in librariesToRemove (from the DeleteOne).
        assertTrue(
            "librariesToRemove must contain $libName",
            writePlan.librariesToRemove.contains(libName)
        )

        // The library MUST ALSO have a LibraryWriteOp to recreate it (treated as absent because
        // it is scheduled for removal - the fix for the delete-before-sync regression).
        val op = writePlan.libraryWriteOps.singleOrNull { it.libraryName == libName }
        assertNotNull(
            "Expected a LibraryWriteOp with createWithKind=true for $libName (delete+re-sync " +
                "must recreate the library after deletion)",
            op
        )
        requireNotNull(op)
        assertTrue("createWithKind must be true (library is being recreated)", op.createWithKind)
        assertEquals("addSourceUrls must contain the re-synced source root", listOf(libDir.url), op.addSourceUrls)
        assertTrue("removeClassUrls must be empty (full recreation, not diff)", op.removeClassUrls.isEmpty())
        assertTrue("removeSourceUrls must be empty (full recreation, not diff)", op.removeSourceUrls.isEmpty())

        // Cleanup
        WriteAction.run<Throwable> { libraryTable.getLibraryByName(libName)?.let { libraryTable.removeLibrary(it) } }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Runs an arbitrary [suspend] block on a pooled thread while the EDT (test thread) continues
     * pumping its event queue.  Delegates to [MixSyncTestHelpers.runSuspendOnPooledThread].
     */
    private fun <T> runSuspendOnPooledThread(block: suspend () -> T): T =
        MixSyncTestHelpers.runSuspendOnPooledThread(block)

    /**
     * Calls [MixDepsSyncService.drain] directly on a pooled thread (bypassing the 250 ms debounce),
     * while pumping the IDE event queue from the EDT test thread so that
     * [com.intellij.openapi.application.edtWriteAction] blocks dispatched by the drain coroutine
     * can execute.  Delegates to [MixSyncTestHelpers.drainDirectly].
     */
    private fun drainDirectly(service: MixDepsSyncService) = MixSyncTestHelpers.drainDirectly(service)
}
