package org.elixir_lang

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.util.concurrency.annotations.RequiresEdt
import org.elixir_lang.mix.library.CONSOLIDATED_LIBRARY_SUFFIX
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.mix.Dep
import org.elixir_lang.mix.sync.MixDepsSyncService
import org.elixir_lang.psi.ElixirTuple

class DepsWatcherTest : PlatformTestCase() {
    private val depName = "my_dep"
    private val secondDepName = "other_dep"

    override fun tearDown() {
        try {
            removeLibrariesIfPresent()
            removeConsolidatedLibraries()
        } finally {
            super.tearDown()
        }
    }

    @RequiresEdt
    fun testSyncLibrariesRemovesInjectedStaleClassRootForSingleDep() {
        val depRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        val staleClassesRoot = myFixture.tempDirFixture.findOrCreateDir("stale_classes/$depName")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        WriteAction.run<Throwable> {
            service.syncLibraries(arrayOf(depRoot), libraryTable)
        }

        val firstSyncClassRootUrls = classRootUrls(depName)
        assertTrue(
            "Expected dev ebin class root after first sync",
            firstSyncClassRootUrls.any { it.contains("/_build/dev/lib/$depName/ebin") }
        )
        assertFalse(
            "Consolidated root must not be added to the per-dep library",
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
            service.syncLibraries(arrayOf(depRoot), libraryTable)
        }

        val secondSyncClassRootUrls = classRootUrls(depName)

        assertFalse(
            "Injected stale classes root should be removed after re-sync",
            secondSyncClassRootUrls.any { it.contains("/stale_classes/$depName") }
        )
        assertTrue(
            "Expected dev ebin class root after re-sync",
            secondSyncClassRootUrls.any { it.contains("/_build/dev/lib/$depName/ebin") }
        )
        assertFalse(
            "Consolidated root must not be added to the per-dep library after re-sync",
            secondSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )
    }

    @RequiresEdt
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

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        WriteAction.run<Throwable> {
            service.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable)
        }

        assertTrue(
            "First dep should include its dev ebin class root after first sync",
            classRootUrls(depName).any { it.contains("/_build/dev/lib/$depName/ebin") }
        )
        assertTrue(
            "Second dep should include its dev ebin class root after first sync",
            classRootUrls(secondDepName).any { it.contains("/_build/dev/lib/$secondDepName/ebin") }
        )
        assertFalse(
            "First dep library must not include the shared consolidated root",
            classRootUrls(depName).any { it.contains("/_build/dev/consolidated") }
        )
        assertFalse(
            "Second dep library must not include the shared consolidated root",
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
            service.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable)
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
            "First dep should still include its dev ebin class root after re-sync",
            firstDepSecondSyncClassRootUrls.any { it.contains("/_build/dev/lib/$depName/ebin") }
        )
        assertTrue(
            "Second dep should still include its dev ebin class root after re-sync",
            secondDepSecondSyncClassRootUrls.any { it.contains("/_build/dev/lib/$secondDepName/ebin") }
        )
        assertFalse(
            "First dep library must not include the shared consolidated root after re-sync",
            firstDepSecondSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )
        assertFalse(
            "Second dep library must not include the shared consolidated root after re-sync",
            secondDepSecondSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )
    }

    @RequiresEdt
    fun testSyncLibrariesIsIdempotentForMultipleDepsWhenFilesystemIsUnchanged() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/priv")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val libraryNames = listOf(depName, secondDepName)

        WriteAction.run<Throwable> {
            service.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable)
        }

        val firstClassRootsByLibrary = libraryNames.associateWith { classRootUrls(it) }
        val firstSourceRootsByLibrary = libraryNames.associateWith { sourceRootUrls(it) }

        WriteAction.run<Throwable> {
            service.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable)
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

    @RequiresEdt
    fun testDeleteAllLibrariesRemovesAllDepsLibraries() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        WriteAction.run<Throwable> {
            service.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable)
        }

        assertNotNull("Expected first dep library to exist before delete-all", libraryTable.getLibraryByName(depName))
        assertNotNull("Expected second dep library to exist before delete-all", libraryTable.getLibraryByName(secondDepName))

        WriteAction.run<Throwable> {
            service.deleteAllLibraries(myFixture.tempDirFixture.findOrCreateDir("deps").url)
        }

        assertNull("Expected first dep library to be deleted", libraryTable.getLibraryByName(depName))
        assertNull("Expected second dep library to be deleted", libraryTable.getLibraryByName(secondDepName))
    }

    @RequiresEdt
    fun testDeleteLibraryRemovesOnlyMatchingDepLibrary() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        WriteAction.run<Throwable> {
            service.syncLibraries(arrayOf(firstDepRoot, secondDepRoot), libraryTable)
        }

        WriteAction.run<Throwable> {
            service.deleteLibrary(depName)
        }

        assertNull("Expected target dep library to be deleted", libraryTable.getLibraryByName(depName))
        assertNotNull("Expected non-target dep library to remain", libraryTable.getLibraryByName(secondDepName))
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

    @RequiresEdt
    fun testSyncConsolidatedLibraryCreatesSeparateSharedLibrary() {
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/test/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        val buildRoot = myFixture.tempDirFixture.findOrCreateDir("_build")

        val service = project.service<MixDepsSyncService>()

        WriteAction.run<Throwable> {
            service.syncConsolidatedLibrary(buildRoot)
        }

        val consolidatedLibraryName = "${buildRoot.parent!!.name} $CONSOLIDATED_LIBRARY_SUFFIX"
        val consolidatedClassRoots = classRootUrls(consolidatedLibraryName)

        assertTrue(
            "Consolidated library should include the dev consolidated root",
            consolidatedClassRoots.any { it.contains("/_build/dev/consolidated") }
        )
        assertTrue(
            "Consolidated library should include the test consolidated root",
            consolidatedClassRoots.any { it.contains("/_build/test/consolidated") }
        )
        assertFalse(
            "Consolidated library must not include per-dep ebin roots",
            consolidatedClassRoots.any { it.contains("/ebin") }
        )
    }

    @RequiresEdt
    fun testDeleteAllLibrariesRemovesConsolidatedLibraryWithoutDepLibraries() {
        val depsRoot = myFixture.tempDirFixture.findOrCreateDir("deps")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        val buildRoot = myFixture.tempDirFixture.findOrCreateDir("_build")

        val service = project.service<MixDepsSyncService>()
        val consolidatedLibraryName = "${buildRoot.parent!!.name} $CONSOLIDATED_LIBRARY_SUFFIX"

        WriteAction.run<Throwable> {
            service.syncConsolidatedLibrary(buildRoot)
        }
        assertNotNull(
            "Expected consolidated library to exist before deleting all deps libraries",
            LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName(consolidatedLibraryName)
        )

        WriteAction.run<Throwable> {
            service.deleteAllLibraries(depsRoot.url)
        }

        assertNull(
            "Consolidated library should be removed even when there are no per-dep libraries",
            LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName(consolidatedLibraryName)
        )
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

    private fun removeLibrariesIfPresent() {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val libraries = listOf(depName, secondDepName).mapNotNull { libraryTable.getLibraryByName(it) }

        if (libraries.isEmpty()) {
            return
        }

        WriteAction.run<Throwable> {
            libraries.forEach { libraryTable.removeLibrary(it) }
        }
    }

    private fun removeConsolidatedLibraries() {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val consolidatedLibraries = libraryTable.libraries.filter {
            it.name?.endsWith(CONSOLIDATED_LIBRARY_SUFFIX) == true
        }

        if (consolidatedLibraries.isEmpty()) {
            return
        }

        WriteAction.run<Throwable> {
            consolidatedLibraries.forEach { libraryTable.removeLibrary(it) }
        }
    }

}
