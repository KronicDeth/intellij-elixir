package org.elixir_lang.mix.sync

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase

/**
 * Unit tests for [MixEventClassifier].
 *
 * Covers the root-detection API shared by [org.elixir_lang.mix.DepsCheckerService] and the
 * dep-sync listener adapters in [org.elixir_lang.DepsWatcher] / [org.elixir_lang.mix.Watcher].
 *
 * Tests for the fine-grained per-event `SyncRequest` mapping live in `DepsWatcherTest`.
 *
 * VFS setup uses [MixTestFixtures] so that the same project structures are reusable across
 * all Mix-related test classes without duplicating setup code.
 *
 * [MixEventClassifier.findAffectedMixRoots] has two overloads:
 * - The public `(Project, List<VFileEvent>)` overload depends on Elixir modules being registered
 *   in the project with correct content root configuration. Integration tests for this path will
 *   be added alongside the sync service implementation once module registration is wired up.
 * - The `internal (List<VirtualFile>, List<String>)` overload contains the pure filtering logic
 *   and is tested here directly using fixture-created VirtualFiles.
 */
class MixEventClassifierTest : PlatformTestCase() {

    override fun tearDown() {
        runAll(
            { MixTestFixtures.removeAllContentRoots(myFixture) },
            { super.tearDown() },
        )
    }

    // ------------------------------------------------------------------
    // isDepsPathForRoot
    // ------------------------------------------------------------------

    fun testIsDepsPathForRoot_mixExs() {
        assertTrue(MixEventClassifier.isDepsPathForRoot("/project/mix.exs", "/project"))
    }

    fun testIsDepsPathForRoot_mixExs_wrongRoot() {
        assertFalse(MixEventClassifier.isDepsPathForRoot("/other/mix.exs", "/project"))
    }

    fun testIsDepsPathForRoot_mixLock() {
        assertTrue(MixEventClassifier.isDepsPathForRoot("/project/mix.lock", "/project"))
    }

    fun testIsDepsPathForRoot_mixLock_wrongRoot() {
        assertFalse(MixEventClassifier.isDepsPathForRoot("/other/mix.lock", "/project"))
    }

    fun testIsDepsPathForRoot_depsSubdirectory() {
        assertTrue(MixEventClassifier.isDepsPathForRoot("/project/deps/phoenix/lib/phoenix.ex", "/project"))
    }

    fun testIsDepsPathForRoot_depsSubdirectory_wrongRoot() {
        assertFalse(MixEventClassifier.isDepsPathForRoot("/other/deps/phoenix/lib/phoenix.ex", "/project"))
    }

    fun testIsDepsPathForRoot_buildSubdirectory() {
        assertTrue(MixEventClassifier.isDepsPathForRoot("/project/_build/dev/lib/my_app.beam", "/project"))
    }

    fun testIsDepsPathForRoot_buildSubdirectory_wrongRoot() {
        assertFalse(MixEventClassifier.isDepsPathForRoot("/other/_build/dev/lib/my_app.beam", "/project"))
    }

    fun testIsDepsPathForRoot_unrelatedFile_lib() {
        assertFalse(MixEventClassifier.isDepsPathForRoot("/project/lib/my_app.ex", "/project"))
    }

    fun testIsDepsPathForRoot_unrelatedFile_config() {
        assertFalse(MixEventClassifier.isDepsPathForRoot("/project/config/config.exs", "/project"))
    }

    fun testIsDepsPathForRoot_depsRootItself() {
        // deps/ directory itself is included (non-strict ancestor => includes the path itself)
        assertTrue(MixEventClassifier.isDepsPathForRoot("/project/deps", "/project"))
    }

    fun testIsDepsPathForRoot_buildRootItself() {
        assertTrue(MixEventClassifier.isDepsPathForRoot("/project/_build", "/project"))
    }

    // ------------------------------------------------------------------
    // selectTopLevelMixRoots
    // ------------------------------------------------------------------

    fun testSelectTopLevelMixRoots_prefersUmbrellaRoot() {
        val (umbrellaRoot, appRoots) = MixTestFixtures.createUmbrellaRoot(myFixture, "umbrella", "app_one", "app_two")

        val result = MixEventClassifier.selectTopLevelMixRoots(listOf(umbrellaRoot) + appRoots)

        assertEquals(1, result.size)
        assertTrue(FileUtil.pathsEqual(umbrellaRoot.path, result.single().path))
    }

    fun testSelectTopLevelMixRoots_keepsSeparateProjects() {
        val (appOne, appTwo) = MixTestFixtures.createSeparateRoots(myFixture, "app_one", "app_two")

        val result = MixEventClassifier.selectTopLevelMixRoots(listOf(appOne, appTwo))

        val resultPaths = result.map { it.path }.toSet()
        assertEquals(setOf(appOne.path, appTwo.path), resultPaths)
    }

    fun testSelectTopLevelMixRoots_emptyList() {
        val result = MixEventClassifier.selectTopLevelMixRoots(emptyList())
        assertTrue(result.isEmpty())
    }

    fun testSelectTopLevelMixRoots_singleRoot() {
        val app = MixTestFixtures.createMixRoot(myFixture, "my_app")

        val result = MixEventClassifier.selectTopLevelMixRoots(listOf(app))
        assertEquals(1, result.size)
        assertTrue(FileUtil.pathsEqual(app.path, result.single().path))
    }

    fun testSelectTopLevelMixRoots_excludesRootsWithoutMixExs() {
        val realApp = MixTestFixtures.createMixRoot(myFixture, "real_app")
        val noMixExs = myFixture.tempDirFixture.findOrCreateDir("no_mix_exs") // intentionally no mix.exs

        val result = MixEventClassifier.selectTopLevelMixRoots(listOf(realApp, noMixExs))
        assertEquals(1, result.size)
        assertTrue(FileUtil.pathsEqual(realApp.path, result.single().path))
    }

    fun testSelectTopLevelMixRoots_arrayOverload() {
        val (appOne, appTwo) = MixTestFixtures.createSeparateRoots(myFixture, "arr_app_one", "arr_app_two")

        val result = MixEventClassifier.selectTopLevelMixRoots(arrayOf(appOne, appTwo))
        val resultPaths = result.map { it.path }.toSet()
        assertEquals(setOf(appOne.path, appTwo.path), resultPaths)
    }

    // ------------------------------------------------------------------
    // findAffectedMixRoots(candidateRoots, eventPaths) - internal overload
    //
    // Tests the pure filtering logic independently of IntelliJ module
    // discovery. The public (Project, List<VFileEvent>) overload is covered
    // by integration tests in MixDepsSyncServiceTest once modules are wired.
    // ------------------------------------------------------------------

    fun testFindAffectedMixRoots_mixExsEventMatchesRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")

        val result = MixEventClassifier.findAffectedMixRoots(listOf(root), listOf("${root.path}/mix.exs"))

        assertEquals(setOf(root), result)
    }

    fun testFindAffectedMixRoots_mixLockEventMatchesRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")

        val result = MixEventClassifier.findAffectedMixRoots(listOf(root), listOf("${root.path}/mix.lock"))

        assertEquals(setOf(root), result)
    }

    fun testFindAffectedMixRoots_depsEventMatchesRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")

        val result = MixEventClassifier.findAffectedMixRoots(
            listOf(root),
            listOf("${root.path}/deps/phoenix/lib/phoenix.ex")
        )

        assertEquals(setOf(root), result)
    }

    fun testFindAffectedMixRoots_buildEventMatchesRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")

        val result = MixEventClassifier.findAffectedMixRoots(
            listOf(root),
            listOf("${root.path}/_build/dev/lib/my_app.beam")
        )

        assertEquals(setOf(root), result)
    }

    fun testFindAffectedMixRoots_unrelatedEventsProduceEmptySet() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")

        val result = MixEventClassifier.findAffectedMixRoots(
            listOf(root),
            listOf("${root.path}/lib/my_app.ex", "${root.path}/config/config.exs")
        )

        assertTrue(result.isEmpty())
    }

    fun testFindAffectedMixRoots_mixedBatchOnlyMatchingRootsReturned() {
        // Event touches app_a/deps but not app_b - only app_a should be returned.
        val (rootA, rootB) = MixTestFixtures.createSeparateRoots(myFixture, "batch_app_a", "batch_app_b")

        val result = MixEventClassifier.findAffectedMixRoots(
            listOf(rootA, rootB),
            listOf("${rootA.path}/deps/phoenix", "${rootA.path}/lib/my_app.ex")
        )

        assertEquals(setOf(rootA), result)
        assertFalse(rootB in result)
    }

    fun testFindAffectedMixRoots_umbrellaEventOnlyTouchesTopLevelRoot() {
        // selectTopLevelMixRoots filters to just umbrellaRoot; event under umbrella/deps
        // should match only that top-level root, not any app sub-root.
        val (umbrellaRoot, _) = MixTestFixtures.createUmbrellaRoot(myFixture, "umbrella_fam", "fam_app_one", "fam_app_two")
        val topLevel = listOf(umbrellaRoot) // simulates what elixirMixContentRoots returns after selectTopLevel

        val result = MixEventClassifier.findAffectedMixRoots(
            topLevel,
            listOf("${umbrellaRoot.path}/deps/ecto")
        )

        assertEquals(1, result.size)
        assertTrue(FileUtil.pathsEqual(umbrellaRoot.path, result.single().path))
    }

    fun testFindAffectedMixRoots_emptyEventListProducesEmptySet() {
        val root = MixTestFixtures.createMixRoot(myFixture, "empty_events_app")

        val result = MixEventClassifier.findAffectedMixRoots(listOf(root), emptyList())

        assertTrue(result.isEmpty())
    }

    fun testFindAffectedMixRoots_emptyCandidateListProducesEmptySet() {
        val result = MixEventClassifier.findAffectedMixRoots(
            emptyList(),
            listOf("/some/path/mix.exs")
        )

        assertTrue(result.isEmpty())
    }
}
