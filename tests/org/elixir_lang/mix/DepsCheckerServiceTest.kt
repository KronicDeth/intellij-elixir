package org.elixir_lang.mix

import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import org.elixir_lang.PlatformTestCase

class DepsCheckerServiceTest : PlatformTestCase() {

    // ------------------------------------------------------------------
    // isDepsPathForRoot - targeted re-scan path classification (#58)
    // ------------------------------------------------------------------

    fun testIsDepsPathForRoot_mixExs() {
        val service = makeService()
        assertTrue(service.isDepsPathForRoot("/project/mix.exs", "/project"))
        assertFalse(service.isDepsPathForRoot("/other/mix.exs", "/project"))
    }

    fun testIsDepsPathForRoot_mixLock() {
        val service = makeService()
        assertTrue(service.isDepsPathForRoot("/project/mix.lock", "/project"))
        assertFalse(service.isDepsPathForRoot("/other/mix.lock", "/project"))
    }

    fun testIsDepsPathForRoot_depsSubdirectory() {
        val service = makeService()
        assertTrue(service.isDepsPathForRoot("/project/deps/phoenix/lib/phoenix.ex", "/project"))
        assertFalse(service.isDepsPathForRoot("/other/deps/phoenix/lib/phoenix.ex", "/project"))
    }

    fun testIsDepsPathForRoot_buildSubdirectory() {
        val service = makeService()
        assertTrue(service.isDepsPathForRoot("/project/_build/dev/lib/my_app.beam", "/project"))
        assertFalse(service.isDepsPathForRoot("/other/_build/dev/lib/my_app.beam", "/project"))
    }

    fun testIsDepsPathForRoot_unrelatedFile() {
        val service = makeService()
        assertFalse(service.isDepsPathForRoot("/project/lib/my_app.ex", "/project"))
        assertFalse(service.isDepsPathForRoot("/project/config/config.exs", "/project"))
    }

    // ------------------------------------------------------------------
    // selectTopLevelMixRoots (pre-existing tests)
    // ------------------------------------------------------------------

    fun testSelectTopLevelMixRootsPrefersUmbrellaRoot() {
        val umbrellaRoot = myFixture.tempDirFixture.findOrCreateDir("umbrella")
        myFixture.tempDirFixture.createFile("umbrella/mix.exs", "defmodule Umbrella.MixProject do\nend\n")

        val appOne = myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_one")
        myFixture.tempDirFixture.createFile("umbrella/apps/app_one/mix.exs", "defmodule AppOne.MixProject do\nend\n")

        val appTwo = myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_two")
        myFixture.tempDirFixture.createFile("umbrella/apps/app_two/mix.exs", "defmodule AppTwo.MixProject do\nend\n")

        val service = makeService()
        val selectedRoots = service.selectTopLevelMixRoots(listOf(umbrellaRoot, appOne, appTwo))

        assertEquals(1, selectedRoots.size)
        assertTrue(FileUtil.pathsEqual(umbrellaRoot.path, selectedRoots.single().path))
    }

    fun testSelectTopLevelMixRootsKeepsSeparateProjects() {
        val appOne = myFixture.tempDirFixture.findOrCreateDir("app_one")
        myFixture.tempDirFixture.createFile("app_one/mix.exs", "defmodule AppOne.MixProject do\nend\n")

        val appTwo = myFixture.tempDirFixture.findOrCreateDir("app_two")
        myFixture.tempDirFixture.createFile("app_two/mix.exs", "defmodule AppTwo.MixProject do\nend\n")

        val service = makeService()
        val selectedRoots = service.selectTopLevelMixRoots(listOf(appOne, appTwo))

        val selectedPaths = selectedRoots.map { it.path }.toSet()
        assertEquals(setOf(appOne.path, appTwo.path), selectedPaths)
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private fun makeService(): DepsCheckerService {
        val service = DepsCheckerService(project)
        Disposer.register(testRootDisposable, service)
        return service
    }
}
