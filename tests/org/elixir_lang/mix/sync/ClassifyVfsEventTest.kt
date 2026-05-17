package org.elixir_lang.mix.sync

import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase

/**
 * Tests for [classifyVfsEvent] and [classifyByPath].
 *
 * Verifies that the dep-watcher layer-2 classifier produces the correct path-shaped [SyncRequest]
 * type for every structural path pattern handled by [MixDepsBulkFileListener]. The classifier must
 * not perform project-model lookups; [MixDepsSyncService] resolves candidate content roots/modules
 * later under read access.
 *
 * VFS structures are created with [MixTestFixtures] so the same layouts are reusable across
 * test classes without duplicating setup code.
 */
class ClassifyVfsEventTest : PlatformTestCase() {

    override fun tearDown() {
        runAll(
            { project.service<MixDepsSyncService>().clearPendingForTesting() },
            { MixTestFixtures.removeAllContentRoots(myFixture) },
            { super.tearDown() },
        )
    }

    // ------------------------------------------------------------------
    // classifyByPath - _build structural paths → unresolved SyncRequest.BuildPath
    // ------------------------------------------------------------------

    fun testClassifyByPath_buildRoot_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val root = myFixture.tempDirFixture.findOrCreateDir("my_app")
        val build = myFixture.tempDirFixture.findOrCreateDir("my_app/_build")

        val result = classifyByPath(build)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildPath)
        assertEquals(root.path, (result as SyncRequest.BuildPath).contentRootCandidate.path)
    }

    fun testClassifyByPath_buildEnv_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val root = myFixture.tempDirFixture.findOrCreateDir("my_app")
        val env = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev")

        val result = classifyByPath(env)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildPath)
        assertEquals(root.path, (result as SyncRequest.BuildPath).contentRootCandidate.path)
    }

    fun testClassifyByPath_buildEnvConsolidated_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val root = myFixture.tempDirFixture.findOrCreateDir("my_app")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val consolidated = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/consolidated")

        val result = classifyByPath(consolidated)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildPath)
        assertEquals(root.path, (result as SyncRequest.BuildPath).contentRootCandidate.path)
    }

    fun testClassifyByPath_buildEnvLib_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val root = myFixture.tempDirFixture.findOrCreateDir("my_app")
        val lib = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib")

        val result = classifyByPath(lib)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildPath)
        assertEquals(root.path, (result as SyncRequest.BuildPath).contentRootCandidate.path)
    }

    // ------------------------------------------------------------------
    // classifyByPath - deps/<dep>/source → SyncRequest.DepRoot
    // ------------------------------------------------------------------

    fun testClassifyByPath_depsDepLib_returnsDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")

        val result = classifyByPath(libDir)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DepRoot)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
    }

    fun testClassifyByPath_depsDepSrc_returnsDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        val srcDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/src")

        val result = classifyByPath(srcDir)

        assertNotNull(result)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
    }

    // ------------------------------------------------------------------
    // classifyByPath - _build/<env>/lib/<dep>/ebin → SyncRequest.DepRoot
    // ------------------------------------------------------------------

    fun testClassifyByPath_ebinPath_mapsToDepRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val ebin = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix/ebin")

        val result = classifyByPath(ebin)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildDep)
        assertEquals(root.path, (result as SyncRequest.BuildDep).contentRootCandidate.path)
        assertEquals("phoenix", result.depName)
    }

    fun testClassifyByPath_ebinPath_multipleEnvs_mapsToCorrectDepRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "test", "phoenix")
        val ebin = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/test/lib/phoenix/ebin")

        val result = classifyByPath(ebin)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildDep)
        assertEquals(root.path, (result as SyncRequest.BuildDep).contentRootCandidate.path)
        assertEquals("phoenix", result.depName)
    }

    // ------------------------------------------------------------------
    // classifyByPath - _build/<env>/lib/<dep> → SyncRequest.DepRoot
    // ------------------------------------------------------------------

    fun testClassifyByPath_buildEnvLibDep_mapsToDepRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix")

        val depLibDir = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix")

        val result = classifyByPath(depLibDir)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildDep)
        assertEquals(root.path, (result as SyncRequest.BuildDep).contentRootCandidate.path)
        assertEquals("phoenix", result.depName)
    }

    // ------------------------------------------------------------------
    // classifyByPath - unrelated paths → null
    // ------------------------------------------------------------------

    fun testClassifyByPath_libSourceDir_returnsNull() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app/lib")

        val result = classifyByPath(libDir)

        assertNull(result)
    }

    fun testClassifyByPath_configDir_returnsNull() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val configDir = myFixture.tempDirFixture.findOrCreateDir("my_app/config")

        val result = classifyByPath(configDir)

        assertNull(result)
    }

    fun testClassifyByPath_notUnderContentRoot_returnsUnresolvedBuildPath() {
        val unrelated = myFixture.tempDirFixture.findOrCreateDir("unrelated/_build")
        val root = myFixture.tempDirFixture.findOrCreateDir("unrelated")

        val result = classifyByPath(unrelated)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildPath)
        assertEquals(root.path, (result as SyncRequest.BuildPath).contentRootCandidate.path)
    }

    // ------------------------------------------------------------------
    // classifyVfsEvent - delete events
    // ------------------------------------------------------------------

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_deleteDepsDirItself_returnsDeleteAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps")

        val event = VFileDeleteEvent(null, depsDir)
        val result = classifyVfsEvent(event)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DeleteAll)
        assertEquals(depsDir.url, (result as SyncRequest.DeleteAll).depsUrl)
        assertEquals(depsDir.parent.url, result.contentRootUrl)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_deleteSingleDepDir_returnsDeleteOne() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")

        val event = VFileDeleteEvent(null, depDir)
        val result = classifyVfsEvent(event)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DeleteOne)
        result as SyncRequest.DeleteOne
        assertEquals("phoenix", result.depName)
        assertEquals(depDir.parent.url, result.depsUrl)
        assertEquals(depDir.parent.parent.url, result.contentRootUrl)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_deleteEbinDir_returnsDepRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val ebin = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix/ebin")

        val event = VFileDeleteEvent(null, ebin)
        val result = classifyVfsEvent(event)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildDep)
        assertEquals(root.path, (result as SyncRequest.BuildDep).contentRootCandidate.path)
        assertEquals("phoenix", result.depName)
    }

    // ------------------------------------------------------------------
    // classifyVfsEvent - create events
    // @Internal constructor is acceptable in tests for constructing synthetic events.
    // ------------------------------------------------------------------

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_createDepsDirItself_returnsDepsRoot() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps")

        val event = VFileCreateEvent(null, root, "deps", true, null, null, null)
        val result = classifyVfsEvent(event)

        assertNotNull(result)
        assertTrue("Expected DepsRoot for deps/ creation, got $result", result is SyncRequest.DepsRoot)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_createSingleDepDir_returnsDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")

        val event = VFileCreateEvent(null, depsDir, "phoenix", true, null, null, null)
        val result = classifyVfsEvent(event)

        assertNotNull(result)
        assertTrue("Expected DepRoot for deps/<dep> creation, got $result", result is SyncRequest.DepRoot)
    }

    // ------------------------------------------------------------------
    // classifyVfsEvent - content-change events → mix.exs or path-shape requests
    // ------------------------------------------------------------------

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_contentChangeOnBuild_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val build = myFixture.tempDirFixture.findOrCreateDir("my_app/_build")

        val event = VFileContentChangeEvent(null, build, 0, 1)
        val result = classifyVfsEvent(event)

        assertNotNull(result)
        assertTrue(result is SyncRequest.BuildPath)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_contentChangeOnMixExs_returnsUnresolvedMixFile() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        val mixFile = root.findChild("mix.exs")!!

        val event = VFileContentChangeEvent(null, mixFile, 0, 1)
        val result = classifyVfsEvent(event)

        assertNotNull(result)
        assertTrue("Expected MixFile, not SyncModule: $result", result is SyncRequest.MixFile)
        assertEquals(mixFile.path, (result as SyncRequest.MixFile).mixFile.path)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_contentChangeOnUnrelated_returnsNull() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app/lib")

        val event = VFileContentChangeEvent(null, libDir, 0, 1)
        val result = classifyVfsEvent(event)

        assertNull(result)
    }

    @Suppress("UnstableApiUsage")
    fun testMixDepsBulkFileListener_mixedBatchEnqueuesDepRootAndMixFileRequests() {
        val root = MixTestFixtures.createMixRoot(myFixture, "my_app")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")
        val mixFile = root.findChild("mix.exs")!!
        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()

        MixDepsBulkFileListener(project).after(
            mutableListOf(
                VFileContentChangeEvent(null, libDir, 0, 1),
                VFileContentChangeEvent(null, mixFile, 0, 1),
            )
        )

        assertEquals("Mixed listener batch should enqueue both relevant requests", 2, service.pendingCount)
    }
}
