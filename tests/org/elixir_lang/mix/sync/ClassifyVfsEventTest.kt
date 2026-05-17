package org.elixir_lang.mix.sync

import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase

/**
 * Tests for [classifyVfsEvent] and [classifyByPath].
 *
 * Verifies that the dep-watcher layer-2 classifier produces the correct [SyncRequest] type for
 * every structural path pattern that [org.elixir_lang.DepsWatcher] handles. These tests are the
 * canonical parity anchor for the classification logic: if [org.elixir_lang.DepsWatcher] is
 * later refactored to delegate to [classifyVfsEvent], these tests must still be green.
 *
 * VFS structures are created with [MixTestFixtures] so the same layouts are reusable across
 * test classes without duplicating setup code.
 */
class ClassifyVfsEventTest : PlatformTestCase() {

    override fun tearDown() {
        runAll(
            { MixTestFixtures.removeAllContentRoots(myFixture) },
            { super.tearDown() },
        )
    }

    // ------------------------------------------------------------------
    // classifyByPath - _build structural paths → SyncRequest.All
    // ------------------------------------------------------------------

    fun testClassifyByPath_buildRoot_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val build = myFixture.tempDirFixture.findOrCreateDir("my_app/_build")

        val result = classifyByPath(project, build)

        assertEquals(SyncRequest.All, result)
    }

    fun testClassifyByPath_buildEnv_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val env = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev")

        val result = classifyByPath(project, env)

        assertEquals(SyncRequest.All, result)
    }

    fun testClassifyByPath_buildEnvConsolidated_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val consolidated = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/consolidated")

        val result = classifyByPath(project, consolidated)

        assertEquals(SyncRequest.All, result)
    }

    fun testClassifyByPath_buildEnvLib_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val lib = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib")

        val result = classifyByPath(project, lib)

        assertEquals(SyncRequest.All, result)
    }

    // ------------------------------------------------------------------
    // classifyByPath - deps/<dep>/source → SyncRequest.DepRoot
    // ------------------------------------------------------------------

    fun testClassifyByPath_depsDepLib_returnsDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/lib")

        val result = classifyByPath(project, libDir)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DepRoot)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
    }

    fun testClassifyByPath_depsDepSrc_returnsDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        val srcDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix/src")

        val result = classifyByPath(project, srcDir)

        assertNotNull(result)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
    }

    // ------------------------------------------------------------------
    // classifyByPath - _build/<env>/lib/<dep>/ebin → SyncRequest.DepRoot
    // ------------------------------------------------------------------

    fun testClassifyByPath_ebinPath_mapsToDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val ebin = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix/ebin")

        val result = classifyByPath(project, ebin)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DepRoot)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
    }

    fun testClassifyByPath_ebinPath_multipleEnvs_mapsToCorrectDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "test", "phoenix")
        val ebin = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/test/lib/phoenix/ebin")

        val result = classifyByPath(project, ebin)

        assertNotNull(result)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
    }

    // ------------------------------------------------------------------
    // classifyByPath - _build/<env>/lib/<dep> → SyncRequest.DepRoot
    // ------------------------------------------------------------------

    fun testClassifyByPath_buildEnvLibDep_mapsToDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix")

        val depLibDir = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix")

        val result = classifyByPath(project, depLibDir)

        assertNotNull(result)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
    }

    // ------------------------------------------------------------------
    // classifyByPath - unrelated paths → null
    // ------------------------------------------------------------------

    fun testClassifyByPath_libSourceDir_returnsNull() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app/lib")

        val result = classifyByPath(project, libDir)

        assertNull(result)
    }

    fun testClassifyByPath_configDir_returnsNull() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val configDir = myFixture.tempDirFixture.findOrCreateDir("my_app/config")

        val result = classifyByPath(project, configDir)

        assertNull(result)
    }

    fun testClassifyByPath_notUnderContentRoot_returnsNull() {
        val unrelated = myFixture.tempDirFixture.findOrCreateDir("unrelated/_build")

        val result = classifyByPath(project, unrelated)

        assertNull(result)
    }

    // ------------------------------------------------------------------
    // classifyVfsEvent - delete events
    // ------------------------------------------------------------------

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_deleteDepsDirItself_returnsDeleteAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps")

        val event = VFileDeleteEvent(null, depsDir)
        val result = classifyVfsEvent(project, event)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DeleteAll)
        assertEquals(depsDir.url, (result as SyncRequest.DeleteAll).depsUrl)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_deleteSingleDepDir_returnsDeleteOne() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")

        val event = VFileDeleteEvent(null, depDir)
        val result = classifyVfsEvent(project, event)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DeleteOne)
        assertEquals("phoenix", (result as SyncRequest.DeleteOne).depName)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_deleteEbinDir_returnsDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")
        MixTestFixtures.addBuildArtifacts(myFixture, "my_app", "dev", "phoenix")
        val ebin = myFixture.tempDirFixture.findOrCreateDir("my_app/_build/dev/lib/phoenix/ebin")

        val event = VFileDeleteEvent(null, ebin)
        val result = classifyVfsEvent(project, event)

        assertNotNull(result)
        assertTrue(result is SyncRequest.DepRoot)
        assertEquals(depRoot.path, (result as SyncRequest.DepRoot).depRoot.path)
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
        val result = classifyVfsEvent(project, event)

        assertNotNull(result)
        assertTrue("Expected DepsRoot for deps/ creation, got $result", result is SyncRequest.DepsRoot)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_createSingleDepDir_returnsDepRoot() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("my_app/deps")
        myFixture.tempDirFixture.findOrCreateDir("my_app/deps/phoenix")

        val event = VFileCreateEvent(null, depsDir, "phoenix", true, null, null, null)
        val result = classifyVfsEvent(project, event)

        assertNotNull(result)
        assertTrue("Expected DepRoot for deps/<dep> creation, got $result", result is SyncRequest.DepRoot)
    }

    // ------------------------------------------------------------------
    // classifyVfsEvent - content-change events → delegates to classifyByPath
    // ------------------------------------------------------------------

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_contentChangeOnBuild_returnsAll() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val build = myFixture.tempDirFixture.findOrCreateDir("my_app/_build")

        val event = VFileContentChangeEvent(null, build, 0, 1)
        val result = classifyVfsEvent(project, event)

        assertEquals(SyncRequest.All, result)
    }

    @Suppress("UnstableApiUsage")
    fun testClassifyVfsEvent_contentChangeOnUnrelated_returnsNull() {
        MixTestFixtures.createMixRoot(myFixture, "my_app")
        val libDir = myFixture.tempDirFixture.findOrCreateDir("my_app/lib")

        val event = VFileContentChangeEvent(null, libDir, 0, 1)
        val result = classifyVfsEvent(project, event)

        assertNull(result)
    }
}
