package org.elixir_lang.mix.sync

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.common.runAll
import java.io.File
import java.util.concurrent.Callable

/**
 * Heavy tests for [MixDepsSyncService] stale-entry pruning.
 *
 * When a project's on-disk location changes (e.g. a WSL distro rename: every
 * `file:////wsl.localhost/<distro>/...` URL changes), the root-scoped library names embedded in a
 * module's library order entries go stale: the sync creates fresh libraries under the new
 * root-scoped names, but the module still carries order entries referencing the old names.
 * Those entries are invalid (their library no longer exists) and can never become valid again,
 * because no future sync will ever create a library scoped to a content root that is no longer
 * part of the project.
 *
 * These tests pin the pruning contract for a module drain:
 * - Invalid entries whose scoped name embeds a content-root URL that is NOT a current project
 *   content root are removed ([testStaleInvalidEntryForForeignRootIsPruned]).
 * - Invalid entries scoped to a CURRENT content root are preserved - they are the deliberate
 *   "declared but not yet fetched" placeholder wiring ([testInvalidEntryForCurrentRootIsPreserved]).
 * - Invalid entries without the `name \[url\]` scoped-name shape (e.g. user-created libraries)
 *   are never touched ([testUnscopedInvalidEntryIsPreserved]).
 */
class MixDepsSyncServicePruneStaleEntriesHeavyTest : HeavyPlatformTestCase() {

    private lateinit var rootVf: VirtualFile
    private lateinit var rootMixExs: VirtualFile
    private lateinit var mixModule: Module

    override fun setUp() {
        super.setUp()

        val rootDir = createTempDir("mix_root")
        FileUtil.writeToFile(
            File(rootDir, "mix.exs"),
            "defmodule MixRoot.MixProject do\n" +
                "  use Mix.Project\n\n" +
                "  def project do\n" +
                "    [app: :mix_root, version: \"0.1.0\", deps: deps()]\n" +
                "  end\n\n" +
                "  def deps do\n" +
                "    [{:phoenix, \">= 0.0.0\"}]\n" +
                "  end\nend\n"
        )
        File(rootDir, "deps/phoenix/lib").mkdirs()
        File(rootDir, "_build/dev/lib/phoenix/ebin").mkdirs()

        val rootVfRaw = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(rootDir)
            ?: error("mix_root not found in VFS after refresh")
        VfsUtil.markDirtyAndRefresh(false, true, true, rootVfRaw)
        rootVf = rootVfRaw
        rootMixExs = rootVf.findFileByRelativePath("mix.exs")
            ?: error("mix_root/mix.exs not found in VFS after refresh")

        mixModule = createModule("module_mix_root")
        PsiTestUtil.addContentRoot(mixModule, rootVf)
    }

    override fun tearDown() {
        runAll(
            { MixSyncTestHelpers.removeAllLibraries(project) },
            { super.tearDown() },
        )
    }

    private fun addInvalidProjectLibraryEntry(libraryName: String) {
        WriteAction.run<Throwable> {
            val model = ModuleRootManager.getInstance(mixModule).modifiableModel
            model.addInvalidLibrary(libraryName, LibraryTablesRegistrar.PROJECT_LEVEL)
            model.commit()
        }
    }

    private fun libraryEntryNames(): List<String> =
        ReadAction.nonBlocking(Callable {
            ModuleRootManager.getInstance(mixModule).orderEntries
                .filterIsInstance<LibraryOrderEntry>()
                .mapNotNull { it.libraryName }
        }).executeSynchronously()

    private fun drainForRootMixExs() {
        val service = project.service<MixDepsSyncService>()
        service.clearPendingForTesting()
        service.enqueue(SyncRequest.MixFile(rootMixExs))
        MixSyncTestHelpers.drainDirectly(service)
    }

    /**
     * An invalid library order entry whose scoped name embeds a foreign (no longer existing)
     * content-root URL must be removed by the drain: no current or future sync can ever create
     * a library under that name again, so the entry is permanently dead weight that also breaks
     * nothing when removed.
     */
    fun testStaleInvalidEntryForForeignRootIsPruned() {
        val staleName = scopedDepLibraryName("file:///old/renamed/away/mix_root", "phoenix")
        addInvalidProjectLibraryEntry(staleName)
        assertTrue("Sanity: stale entry must be present before drain", libraryEntryNames().contains(staleName))

        drainForRootMixExs()

        val entries = libraryEntryNames()
        assertFalse(
            "The invalid entry '$staleName' is scoped to a content root that is not part of the " +
                "project - the drain must prune it. Order entries: $entries",
            entries.contains(staleName)
        )

        // And the correctly-scoped replacement must be wired (the prune must not interfere).
        val currentName = scopedDepLibraryName(rootVf.url, "phoenix")
        assertTrue(
            "The current-root-scoped library '$currentName' must still be wired. Order entries: $entries",
            entries.contains(currentName)
        )
    }

    /**
     * An invalid entry scoped to a CURRENT content root is the placeholder wiring for a declared
     * dep that has not been fetched yet ("mix deps.get" not run).  It becomes valid as soon as
     * the dep is fetched and the library is created - the prune must leave it alone.
     */
    fun testInvalidEntryForCurrentRootIsPreserved() {
        val unfetchedName = scopedDepLibraryName(rootVf.url, "ecto")
        addInvalidProjectLibraryEntry(unfetchedName)

        drainForRootMixExs()

        val entries = libraryEntryNames()
        assertTrue(
            "The invalid entry '$unfetchedName' is scoped to a CURRENT content root (an unfetched " +
                "dep placeholder) and must NOT be pruned. Order entries: $entries",
            entries.contains(unfetchedName)
        )
    }

    /**
     * An invalid entry without the `name \[url\]` root-scoped shape may be a user-created library
     * reference - the sync has no ownership claim over it and must never remove it.
     */
    fun testUnscopedInvalidEntryIsPreserved() {
        val userLibraryName = "my_custom_library"
        addInvalidProjectLibraryEntry(userLibraryName)

        drainForRootMixExs()

        val entries = libraryEntryNames()
        assertTrue(
            "The invalid unscoped entry '$userLibraryName' may be user-created and must NOT be " +
                "pruned. Order entries: $entries",
            entries.contains(userLibraryName)
        )
    }
}
