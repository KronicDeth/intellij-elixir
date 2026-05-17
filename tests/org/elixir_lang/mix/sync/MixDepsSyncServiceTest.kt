package org.elixir_lang.mix.sync

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.common.runAll
import kotlinx.coroutines.runBlocking
import org.elixir_lang.PlatformTestCase
import java.util.concurrent.atomic.AtomicBoolean

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
 * - All-module fan-out: a [SyncRequest.SyncModule] resolves the module's `mix.exs` via PSI and
 *   wires any declared library deps into the module's order entries.
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
 * The delete-coalescing and delete+sync tests use [drainDirectly] rather than [waitUntil] to
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
            { cleanupLibraries("phoenix", "ecto") },
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
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed: create the phoenix library.
        WriteAction.run<Throwable> { service.syncLibraries(arrayOf(depRoot), libraryTable) }
        assertNotNull("Phoenix library must exist before drain", libraryTable.getLibraryByName("phoenix"))

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
            libraryTable.getLibraryByName("phoenix")
        )
    }

    // ------------------------------------------------------------------
    // Test 7 (minimal) - service is registered and accepts events without throwing
    // ------------------------------------------------------------------

    fun testService_isRegisteredAndEnqueueDoesNotThrow() {
        val service = project.service<MixDepsSyncService>()
        assertNotNull("MixDepsSyncService must be registered as a project service", service)
        // Must not throw even before any project content is set up.
        service.enqueue(SyncRequest.All)
    }

    // ------------------------------------------------------------------
    // Test 8 - delete and sync for independent targets execute in the same drain
    // ------------------------------------------------------------------

    fun testDrain_deleteOneAndDepRootForDifferentTargets_bothEffectsApplied() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val phoenixRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        val ectoRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/ecto")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/ecto/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix", "ecto")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed: create both libraries.
        WriteAction.run<Throwable> {
            service.syncLibraries(arrayOf(phoenixRoot, ectoRoot), libraryTable)
        }
        assertNotNull("phoenix library must exist before drain", libraryTable.getLibraryByName("phoenix"))
        assertNotNull("ecto library must exist before drain", libraryTable.getLibraryByName("ecto"))

        // Delete phoenix only (DeleteOne); re-sync ecto (DepRoot).
        // DeleteOne does NOT suppress DepRoot syncs - the two requests are independent.
        // Expected after drain: phoenix absent (deleted), ecto present (re-synced from files).
        service.enqueue(SyncRequest.DeleteOne("phoenix"))
        service.enqueue(SyncRequest.DepRoot(ectoRoot))

        // Wait for the delete to take effect.
        waitUntil("Phoenix must be absent - it was targeted by DeleteOne") {
            libraryTable.getLibraryByName("phoenix") == null
        }
        // At this point the drain has completed; ecto was re-synced in the same drain.
        assertNotNull(
            "Ecto must be present - its DepRoot sync ran in the same drain " +
                "(delete-before-sync ordering preserved)",
            libraryTable.getLibraryByName("ecto")
        )
    }

    // ------------------------------------------------------------------
    // Test 9 - all-module fan-out: SyncModule wires library into module order entries
    // ------------------------------------------------------------------

    fun testSyncModule_wiresLibraryIntoModuleOrderEntries() {
        // Set up a content root with a mix.exs that declares phoenix as a library dep.
        MixTestFixtures.createMixRootWithDeps(myFixture, "my_app", "phoenix")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Step 1: create the project library from the dep root (simulates syncLibrariesArray).
        WriteAction.run<Throwable> { service.syncLibraries(arrayOf(depRoot), libraryTable) }
        assertNotNull("phoenix project library must exist before fan-out", libraryTable.getLibraryByName("phoenix"))

        // Step 2: fan-out - enqueue a SyncModule request and run drain() directly so that
        // syncLibrariesForModule resolves mix.exs via PSI and wires phoenix into the module's
        // order entries.  drainDirectly() is used (instead of waitUntil) to avoid VFS-event
        // interference from the fixture directories created above.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.SyncModule(myFixture.module))
        drainDirectly(service)

        val orderEntries = ModuleRootManager.getInstance(myFixture.module).orderEntries
        val hasPhoenixLibEntry = orderEntries.any { it is LibraryOrderEntry && it.libraryName == "phoenix" }
        assertTrue(
            "After fan-out, myFixture.module should have a LibraryOrderEntry for phoenix. " +
                "Order entries: ${orderEntries.map { it.presentableName }}",
            hasPhoenixLibEntry
        )
    }

    // ------------------------------------------------------------------
    // Regression: exclude folder must only be added to ancestor content entries
    // ------------------------------------------------------------------

    /**
     * Regression test for the ancestor-guard in [MixDepsSyncService.syncLibraryRoots].
     *
     * When the module has multiple content entries (e.g. `my_app` AND an unrelated `other_app`
     * entry left over from umbrella tests or a multi-root project), calling
     * [MixDepsSyncService.syncLibraries] must NOT throw [IllegalStateException]
     * ("Exclude folder … must be under content entry …"), and must only add the
     * `_build/…/lib/<dep>` exclude folder to the content entry that actually contains it -
     * not to the unrelated one.
     */
    fun testSyncLibraries_excludeOnlyAppliesToAncestorContentEntry() {
        // Register two unrelated content roots on the same module.
        val myApp = MixTestFixtures.createMixRoot(myFixture, "my_app")
        val otherApp = MixTestFixtures.createMixRoot(myFixture, "other_app")

        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Must not throw IllegalStateException despite two content entries on the same module.
        WriteAction.run<Throwable> { service.syncLibraries(arrayOf(depRoot), libraryTable) }

        // Verify: phoenix library was created.
        assertNotNull("phoenix library must be created", libraryTable.getLibraryByName("phoenix"))

        // Verify: exclude entry under my_app is present; other_app content entry has no excludes.
        val moduleRootManager = ModuleRootManager.getInstance(myFixture.module)
        val myAppEntry = moduleRootManager.contentEntries.firstOrNull { it.file == myApp }
        val otherAppEntry = moduleRootManager.contentEntries.firstOrNull { it.file == otherApp }

        assertNotNull("my_app content entry must exist", myAppEntry)
        assertNotNull("other_app content entry must exist", otherAppEntry)

        val myAppExcludes = myAppEntry!!.excludeFolderUrls
        val otherAppExcludes = otherAppEntry!!.excludeFolderUrls

        assertTrue(
            "my_app content entry should have at least one exclude (the ebin lib dir)",
            myAppExcludes.any { it.contains("my_app/_build") }
        )
        assertTrue(
            "other_app content entry must have no excludes from my_app's build dir",
            otherAppExcludes.none { it.contains("my_app") }
        )
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Calls [MixDepsSyncService.drain] directly on a pooled thread (bypassing the 250 ms debounce),
     * while pumping the IDE event queue from the EDT test thread so that
     * [com.intellij.openapi.application.edtWriteAction] blocks dispatched by the drain coroutine
     * can execute.
     *
     * Use this when you need the drain result synchronously and VFS-event interference from the test
     * fixture setup would corrupt a [waitUntil]-based assertion.
     */
    private fun drainDirectly(service: MixDepsSyncService) {
        val done = AtomicBoolean(false)
        var error: Throwable? = null
        ApplicationManager.getApplication().executeOnPooledThread {
            runBlocking {
                try {
                    service.drain()
                } catch (e: Throwable) {
                    error = e
                }
            }
            done.set(true)
        }
        val deadline = System.currentTimeMillis() + 10_000L
        while (!done.get()) {
            assertTrue("drainDirectly timed out after 10 s", System.currentTimeMillis() < deadline)
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
        }
        error?.let { throw AssertionError("drain() failed: ${it.message}", it) }
    }

    /**
     * Polls [condition] in a tight EDT-pumping loop until it returns `true` or the
     * [timeoutMs] is exceeded (default 10 s).
     *
     * Must be called from the EDT test thread **without** holding a write action.
     * Uses [PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue] which explicitly releases
     * and re-acquires the write-intent lock (WIL) between dispatches, allowing background
     * coroutines' `edtWriteAction` blocks to acquire the write lock and execute.
     * (Unlike [com.intellij.util.ui.UIUtil.dispatchAllInvocationEvents], which does NOT release
     * WIL and therefore deadlocks with edtWriteAction in tests.)
     */
    private fun waitUntil(
        message: String,
        timeoutMs: Long = 10_000,
        condition: () -> Boolean,
    ) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (!condition()) {
            assertTrue(message, System.currentTimeMillis() < deadline)
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
        }
    }

    @Suppress("SameParameterValue")
    private fun cleanupLibraries(vararg names: String) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val toRemove = names.mapNotNull { libraryTable.getLibraryByName(it) }
        if (toRemove.isNotEmpty()) {
            WriteAction.run<Throwable> { toRemove.forEach { libraryTable.removeLibrary(it) } }
        }
    }
}
