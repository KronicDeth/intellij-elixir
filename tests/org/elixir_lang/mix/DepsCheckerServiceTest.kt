package org.elixir_lang.mix

import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import org.elixir_lang.PlatformTestCase

class DepsCheckerServiceTest : PlatformTestCase() {
    fun testSelectTopLevelMixRootsPrefersUmbrellaRoot() {
        val umbrellaRoot = myFixture.tempDirFixture.findOrCreateDir("umbrella")
        myFixture.tempDirFixture.createFile("umbrella/mix.exs", "defmodule Umbrella.MixProject do\nend\n")

        val appOne = myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_one")
        myFixture.tempDirFixture.createFile("umbrella/apps/app_one/mix.exs", "defmodule AppOne.MixProject do\nend\n")

        val appTwo = myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_two")
        myFixture.tempDirFixture.createFile("umbrella/apps/app_two/mix.exs", "defmodule AppTwo.MixProject do\nend\n")

        val service = DepsCheckerService(project)
        Disposer.register(testRootDisposable, service)
        val selectedRoots = service.selectTopLevelMixRoots(listOf(umbrellaRoot, appOne, appTwo))

        assertEquals(1, selectedRoots.size)
        assertTrue(FileUtil.pathsEqual(umbrellaRoot.path, selectedRoots.single().path))
    }

    fun testSelectTopLevelMixRootsKeepsSeparateProjects() {
        val appOne = myFixture.tempDirFixture.findOrCreateDir("app_one")
        myFixture.tempDirFixture.createFile("app_one/mix.exs", "defmodule AppOne.MixProject do\nend\n")

        val appTwo = myFixture.tempDirFixture.findOrCreateDir("app_two")
        myFixture.tempDirFixture.createFile("app_two/mix.exs", "defmodule AppTwo.MixProject do\nend\n")

        val service = DepsCheckerService(project)
        Disposer.register(testRootDisposable, service)
        val selectedRoots = service.selectTopLevelMixRoots(listOf(appOne, appTwo))

        val selectedPaths = selectedRoots.map { it.path }.toSet()
        assertEquals(setOf(appOne.path, appTwo.path), selectedPaths)
    }
}
