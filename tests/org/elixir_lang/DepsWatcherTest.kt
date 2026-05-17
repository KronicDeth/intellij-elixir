package org.elixir_lang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.PsiTestUtil
import kotlinx.coroutines.runBlocking
import org.elixir_lang.mix.Dep
import org.elixir_lang.mix.sync.MixDepsSyncService
import org.elixir_lang.mix.sync.SyncRequest
import org.elixir_lang.mix.sync.scopedDepLibraryName
import org.elixir_lang.psi.ElixirTuple
import java.util.concurrent.atomic.AtomicBoolean

class DepsWatcherTest : PlatformTestCase() {
    private val depName = "my_dep"
    private val secondDepName = "other_dep"

    override fun setUp() {
        super.setUp()
        // Register the fixture temp dir as a content root so that resolvePathShapedRequests
        // accepts DepRoot/DepsRoot requests (it checks depRoot.parent.parent.url in contentRootUrls).
        val tempDirVf = myFixture.tempDirFixture.findOrCreateDir("")
        PsiTestUtil.addContentRoot(myFixture.module, tempDirVf)
        project.service<MixDepsSyncService>().clearPendingForTesting()
    }

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
        val libName = depLibraryName(depRoot)

        val service = project.service<MixDepsSyncService>()

        // First sync via drain pipeline.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)

        val firstSyncClassRootUrls = classRootUrls(libName)
        assertTrue(
            "Expected dev consolidated root after first sync",
            firstSyncClassRootUrls.any { it.contains("/_build/dev/consolidated") }
        )

        // Inject a stale class root directly into the library.
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        WriteAction.run<Throwable> {
            val library = libraryTable.getLibraryByName(libName)
            assertNotNull("Expected library '$libName' to exist before injecting stale root", library)

            library!!.modifiableModel.apply {
                addRoot(staleClassesRoot, OrderRootType.CLASSES)
                commit()
            }
        }

        val urlsWithInjectedStaleRoot = classRootUrls(libName)
        assertTrue(
            "Expected injected stale classes root before re-sync",
            urlsWithInjectedStaleRoot.any { it.contains("/stale_classes/$depName") }
        )

        // Re-sync: stale root should be removed by the diff computation in buildWritePlan.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(depRoot))
        drainDirectly(service)

        val secondSyncClassRootUrls = classRootUrls(libName)

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
        val firstLibName = depLibraryName(firstDepRoot)
        val secondLibName = depLibraryName(secondDepRoot)

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // First sync via drain pipeline.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(firstDepRoot))
        service.enqueue(SyncRequest.DepRoot(secondDepRoot))
        drainDirectly(service)

        assertTrue(
            "First dep should include dev consolidated root after first sync",
            classRootUrls(firstLibName).any { it.contains("/_build/dev/consolidated") }
        )
        assertTrue(
            "Second dep should include dev consolidated root after first sync",
            classRootUrls(secondLibName).any { it.contains("/_build/dev/consolidated") }
        )

        // Inject stale class roots.
        WriteAction.run<Throwable> {
            val firstLibrary = libraryTable.getLibraryByName(firstLibName)
            val secondLibrary = libraryTable.getLibraryByName(secondLibName)

            assertNotNull("Expected library '$firstLibName' to exist before injecting stale root", firstLibrary)
            assertNotNull("Expected library '$secondLibName' to exist before injecting stale root", secondLibrary)

            firstLibrary!!.modifiableModel.apply {
                addRoot(firstStaleClassesRoot, OrderRootType.CLASSES)
                commit()
            }
            secondLibrary!!.modifiableModel.apply {
                addRoot(secondStaleClassesRoot, OrderRootType.CLASSES)
                commit()
            }
        }

        assertTrue(
            "First dep should include injected stale classes root before re-sync",
            classRootUrls(firstLibName).any { it.contains("/stale_classes/$depName") }
        )
        assertTrue(
            "Second dep should include injected stale classes root before re-sync",
            classRootUrls(secondLibName).any { it.contains("/stale_classes/$secondDepName") }
        )

        // Re-sync: stale roots should be removed.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(firstDepRoot))
        service.enqueue(SyncRequest.DepRoot(secondDepRoot))
        drainDirectly(service)

        assertFalse(
            "First dep should not keep injected stale classes root after re-sync",
            classRootUrls(firstLibName).any { it.contains("/stale_classes/$depName") }
        )
        assertFalse(
            "Second dep should not keep injected stale classes root after re-sync",
            classRootUrls(secondLibName).any { it.contains("/stale_classes/$secondDepName") }
        )
        assertTrue(
            "First dep should still include dev consolidated root after re-sync",
            classRootUrls(firstLibName).any { it.contains("/_build/dev/consolidated") }
        )
        assertTrue(
            "Second dep should still include dev consolidated root after re-sync",
            classRootUrls(secondLibName).any { it.contains("/_build/dev/consolidated") }
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
        val firstLibName = depLibraryName(firstDepRoot)
        val secondLibName = depLibraryName(secondDepRoot)

        val service = project.service<MixDepsSyncService>()
        val libraryNames = listOf(firstLibName, secondLibName)

        // First sync.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(firstDepRoot))
        service.enqueue(SyncRequest.DepRoot(secondDepRoot))
        drainDirectly(service)

        val firstClassRootsByLibrary = libraryNames.associateWith { classRootUrls(it) }
        val firstSourceRootsByLibrary = libraryNames.associateWith { sourceRootUrls(it) }

        // Second sync (same filesystem state).
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(firstDepRoot))
        service.enqueue(SyncRequest.DepRoot(secondDepRoot))
        drainDirectly(service)

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
        val firstLibName = depLibraryName(firstDepRoot)
        val secondLibName = depLibraryName(secondDepRoot)

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed libraries via drain.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(firstDepRoot))
        service.enqueue(SyncRequest.DepRoot(secondDepRoot))
        drainDirectly(service)

        assertNotNull("Expected first dep library to exist before delete-all", libraryTable.getLibraryByName(firstLibName))
        assertNotNull("Expected second dep library to exist before delete-all", libraryTable.getLibraryByName(secondLibName))

        // Delete all deps libraries via drain.
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("deps")
        val contentRootUrl = depsDir.parent?.url
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DeleteAll(depsDir.url, contentRootUrl))
        drainDirectly(service)

        assertNull("Expected first dep library to be deleted", libraryTable.getLibraryByName(firstLibName))
        assertNull("Expected second dep library to be deleted", libraryTable.getLibraryByName(secondLibName))
    }

    fun testDeleteLibraryRemovesOnlyMatchingDepLibrary() {
        val firstDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$depName")
        val secondDepRoot = myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName")
        myFixture.tempDirFixture.findOrCreateDir("deps/$depName/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps/$secondDepName/lib")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/consolidated")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$depName/ebin")
        myFixture.tempDirFixture.findOrCreateDir("_build/dev/lib/$secondDepName/ebin")
        val firstLibName = depLibraryName(firstDepRoot)
        val secondLibName = depLibraryName(secondDepRoot)

        val service = project.service<MixDepsSyncService>()
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        // Seed libraries via drain.
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DepRoot(firstDepRoot))
        service.enqueue(SyncRequest.DepRoot(secondDepRoot))
        drainDirectly(service)

        assertNotNull("Expected first dep library to exist before delete", libraryTable.getLibraryByName(firstLibName))
        assertNotNull("Expected second dep library to exist before delete", libraryTable.getLibraryByName(secondLibName))

        // Delete only the first dep library via drain.
        val depsDir = myFixture.tempDirFixture.findOrCreateDir("deps")
        val contentRootUrl = depsDir.parent?.url
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.DeleteOne(depName, depsDir.url, contentRootUrl))
        drainDirectly(service)

        assertNull("Expected target dep library to be deleted", libraryTable.getLibraryByName(firstLibName))
        assertNotNull("Expected non-target dep library to remain", libraryTable.getLibraryByName(secondLibName))
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

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Computes the root-scoped library name for [depRoot] using [scopedDepLibraryName].
     * The content root is the grandparent of the dep directory (<contentRoot>/deps/<depName>).
     */
    private fun depLibraryName(depRoot: VirtualFile): String =
        scopedDepLibraryName(depRoot.parent?.parent?.url ?: "", depRoot.name)

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

    /**
     * Removes all libraries whose name equals [libraryNames] as a plain dep name OR whose scoped
     * name starts with `"<depName> ["` and ends with `"]"`.
     * This handles both legacy unscoped names and the new scoped names.
     */
    @Suppress("SameParameterValue")
    private fun removeLibrariesIfPresent(vararg libraryNames: String) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val nameSet = libraryNames.toSet()
        val libraries = libraryTable.libraries.filter { lib ->
            val name = lib.name ?: return@filter false
            name in nameSet || nameSet.any { dep -> name.startsWith("$dep [") && name.endsWith("]") }
        }

        if (libraries.isEmpty()) {
            return
        }

        WriteAction.run<Throwable> {
            libraries.forEach { libraryTable.removeLibrary(it) }
        }
    }

    private fun <T> runSuspendOnPooledThread(block: suspend () -> T): T {
        var result: T? = null
        var error: Throwable? = null
        val done = AtomicBoolean(false)
        ApplicationManager.getApplication().executeOnPooledThread {
            runBlocking {
                try {
                    @Suppress("UNCHECKED_CAST")
                    result = block()
                } catch (e: Throwable) {
                    error = e
                }
            }
            done.set(true)
        }
        val deadline = System.currentTimeMillis() + 10_000L
        while (!done.get()) {
            assertTrue("runSuspendOnPooledThread timed out after 10 s", System.currentTimeMillis() < deadline)
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
        }
        error?.let { throw AssertionError("Suspended block failed: ${it.message}", it) }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    private fun drainDirectly(service: MixDepsSyncService) = runSuspendOnPooledThread { service.drain() }
}
