package org.elixir_lang

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.mix.Dep
import org.elixir_lang.psi.ElixirTuple

class DepsWatcherTest : PlatformTestCase() {
    private val depName = "my_dep"
    private val secondDepName = "other_dep"

    override fun tearDown() {
        try {
            removeLibrariesIfPresent(depName, secondDepName)
        } finally {
            super.tearDown()
        }
    }

    fun testSyncLibrariesRemovesInjectedStaleClassRootForSingleDep() {
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        val staleClassesRoot = myFixture.tempDirFixture.findOrCreateDir("stale_classes/$depName")

        val watcher = DepsWatcher(project)
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val indicator = EmptyProgressIndicator()

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(depRoot), libraryTable, indicator)
        }

        val firstSyncClassRootUrls = classRootUrls(depName)
        assertTrue(
            "Expected dev consolidated root after first sync",
            firstSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )

        WriteAction.run<Throwable> {
            val library = libraryTable.getLibraryByName(depName)
            assertNotNull("Expected library '$depName' to exist before injecting stale root", library)

            library!!.modifiableModel.apply {
                addRoot(staleClassesRoot, OrderRootType.CLASSES)
                commit()
            }
        }

        val urlsWithInjectedStaleRoot = classRootUrls(depName)
        assertTrue(
            "Expected injected stale classes root before re-sync",
            urlsWithInjectedStaleRoot.any { it.contains("/stale_classes/$depName") }
        )

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(depRoot), libraryTable, indicator)
        }

        val secondSyncClassRootUrls = classRootUrls(depName)

        assertFalse(
            "Injected stale classes root should be removed after re-sync",
            secondSyncClassRootUrls.any { it.contains("/stale_classes/$depName") }
        )
        assertTrue(
            "Expected dev consolidated root after re-sync",
            secondSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )
    }

    fun testSyncLibrariesRemovesInjectedStaleClassRootsForMultipleDeps() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")
        val firstStaleClassesRoot = myFixture.tempDirFixture.findOrCreateDir("stale_classes/$depName")
        val secondStaleClassesRoot = myFixture.tempDirFixture.findOrCreateDir("stale_classes/$secondDepName")

        val watcher = DepsWatcher(project)
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val indicator = EmptyProgressIndicator()

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable, indicator)
        }

        assertTrue(
            "First dep should include dev consolidated root after first sync",
            classRootUrls(depName).any { it.contains("/_build/dev/consolidated") }
        )
        assertTrue(
            "Second dep should include dev consolidated root after first sync",
            classRootUrls(secondDepName).any { it.contains("/_build/dev/consolidated") }
        )

        WriteAction.run<Throwable> {
            val firstLibrary = libraryTable.getLibraryByName(depName)
            val secondLibrary = libraryTable.getLibraryByName(secondDepName)

            assertNotNull("Expected library '$depName' to exist before injecting stale root", firstLibrary)
            assertNotNull("Expected library '$secondDepName' to exist before injecting stale root", secondLibrary)

            firstLibrary!!.modifiableModel.apply {
                addRoot(firstStaleClassesRoot, OrderRootType.CLASSES)
                commit()
            }
            secondLibrary!!.modifiableModel.apply {
                addRoot(secondStaleClassesRoot, OrderRootType.CLASSES)
                commit()
            }
        }

        val firstDepUrlsWithInjectedStaleRoot = classRootUrls(depName)
        val secondDepUrlsWithInjectedStaleRoot = classRootUrls(secondDepName)

        assertTrue(
            "First dep should include injected stale classes root before re-sync",
            firstDepUrlsWithInjectedStaleRoot.any { it.contains("/stale_classes/$depName") }
        )
        assertTrue(
            "Second dep should include injected stale classes root before re-sync",
            secondDepUrlsWithInjectedStaleRoot.any { it.contains("/stale_classes/$secondDepName") }
        )

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable, indicator)
        }

        val firstDepSecondSyncClassRootUrls = classRootUrls(depName)
        val secondDepSecondSyncClassRootUrls = classRootUrls(secondDepName)

        assertFalse(
            "First dep should not keep injected stale classes root after re-sync",
            firstDepSecondSyncClassRootUrls.any { it.contains("/stale_classes/$depName") }
        )
        assertFalse(
            "Second dep should not keep injected stale classes root after re-sync",
            secondDepSecondSyncClassRootUrls.any { it.contains("/stale_classes/$secondDepName") }
        )
        assertTrue(
            "First dep should still include dev consolidated root after re-sync",
            firstDepSecondSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )
        assertTrue(
            "Second dep should still include dev consolidated root after re-sync",
            secondDepSecondSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )
    }

    fun testSyncLibrariesIsIdempotentForMultipleDepsWhenFilesystemIsUnchanged() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/priv")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")

        val watcher = DepsWatcher(project)
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val indicator = EmptyProgressIndicator()
        val libraryNames = listOf(depName, secondDepName)

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable, indicator)
        }

        val firstClassRootsByLibrary = libraryNames.associateWith { classRootUrls(it) }
        val firstSourceRootsByLibrary = libraryNames.associateWith { sourceRootUrls(it) }

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable, indicator)
        }

        libraryNames.forEach { libraryName ->
            val secondClassRoots = classRootUrls(libraryName)
            val secondSourceRoots = sourceRootUrls(libraryName)

            assertEquals(
                "$libraryName classes roots should be stable across repeated sync",
                firstClassRootsByLibrary.getValue(libraryName).toSet(),
                secondClassRoots.toSet()
            )
            assertEquals(
                "$libraryName source roots should be stable across repeated sync",
                firstSourceRootsByLibrary.getValue(libraryName).toSet(),
                secondSourceRoots.toSet()
            )
            assertEquals(
                "$libraryName should not have duplicate class roots",
                secondClassRoots.toSet().size,
                secondClassRoots.size
            )
            assertEquals(
                "$libraryName should not have duplicate source roots",
                secondSourceRoots.toSet().size,
                secondSourceRoots.size
            )
        }
    }

    fun testDeleteAllLibrariesRemovesAllDepsLibraries() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")

        val watcher = DepsWatcher(project)
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val indicator = EmptyProgressIndicator()

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable, indicator)
        }

        assertNotNull("Expected first dep library to exist before delete-all", libraryTable.getLibraryByName(depName))
        assertNotNull("Expected second dep library to exist before delete-all", libraryTable.getLibraryByName(secondDepName))

        watcher.deleteAllLibraries(myFixture.tempDirFixture.findOrCreateDir("deps").url)

        assertNull("Expected first dep library to be deleted", libraryTable.getLibraryByName(depName))
        assertNull("Expected second dep library to be deleted", libraryTable.getLibraryByName(secondDepName))
    }

    fun testDeleteLibraryRemovesOnlyMatchingDepLibrary() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")

        val watcher = DepsWatcher(project)
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val indicator = EmptyProgressIndicator()

        WriteAction.run<Throwable> {
            watcher.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable, indicator)
        }

        watcher.deleteLibrary(depName)

        assertNull("Expected target dep library to be deleted", libraryTable.getLibraryByName(depName))
        assertNotNull("Expected non-target dep library to remain", libraryTable.getLibraryByName(secondDepName))
    }

    fun testSyncRequestForBuildPathsAndEbinMapsToDepRoot() {
        @Suppress("UNUSED_VARIABLE")
        val depsRoot = myFixture.tempDirFixture.findOrCreateDir("deps")
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val buildRoot = myFixture.tempDirFixture.findOrCreateDir("_build")
        val envRoot = myFixture.tempDirFixture.findOrCreateDir("_build/dev")
        val consolidatedRoot = myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        val libRoot = myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib")
        val ebinRoot = myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")

        assertNotNull("deps root should be created under temp project root", depsRoot)

        val watcher = DepsWatcher(project)

        assertEquals("Expected All sync request for _build", SyncRequest.All, watcher.syncRequestFor(buildRoot))
        assertEquals("Expected All sync request for _build/dev", SyncRequest.All, watcher.syncRequestFor(envRoot))
        assertEquals("Expected All sync request for consolidated", SyncRequest.All, watcher.syncRequestFor(consolidatedRoot))
        assertEquals("Expected All sync request for lib", SyncRequest.All, watcher.syncRequestFor(libRoot))

        val ebinRequest = watcher.syncRequestFor(ebinRoot)
        assertNotNull("Expected a sync request for ebin path", ebinRequest)
        assertTrue("Expected dep sync request for ebin path", ebinRequest is SyncRequest.Dep)
        assertEquals(
            "Expected ebin request to map to deps/<dep>",
            depRoot.path,
            (ebinRequest as SyncRequest.Dep).depRoot.path
        )
    }

    fun testDepPathCallValueKeepsDefaultDepsPath() {
        val psiFile = myFixture.configureByText(
            "mix.exs",
            """
            defmodule Sample.MixProject do
              def project do
                [
                  deps: [
                    {:$depName, path: dep_path()}
                  ]
                ]
              end
            end
            """.trimIndent()
        )

        val depTuple = PsiTreeUtil.findChildOfType(psiFile, ElixirTuple::class.java)
        assertNotNull("Expected to find dep tuple in mix file", depTuple)

        val dep = Dep.from(depTuple!!)
        assertNotNull("Expected dep tuple to parse", dep)
        assertEquals("Expected path call value to keep fallback deps path", "deps/$depName", dep!!.path)
    }

    private fun classRootUrls(libraryName: String): kotlin.collections.List<String> {
        val library = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName(libraryName)
        assertNotNull("Expected library '$libraryName' to exist", library)

        return library!!.getUrls(OrderRootType.CLASSES).toList()
    }

    private fun sourceRootUrls(libraryName: String): kotlin.collections.List<String> {
        val library = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName(libraryName)
        assertNotNull("Expected library '$libraryName' to exist", library)

        return library!!.getUrls(OrderRootType.SOURCES).toList()
    }

    private fun removeLibrariesIfPresent(vararg libraryNames: String) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val libraries = libraryNames.mapNotNull { libraryTable.getLibraryByName(it) }

        if (libraries.isEmpty()) {
            return
        }

        WriteAction.run<Throwable> {
            libraries.forEach { libraryTable.removeLibrary(it) }
        }
    }

}
