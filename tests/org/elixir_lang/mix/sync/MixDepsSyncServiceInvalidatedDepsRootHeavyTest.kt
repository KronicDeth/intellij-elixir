package org.elixir_lang.mix.sync

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.common.runAll
import java.io.File

/**
 * Heavy tests for the read phase's handling of a `deps/` directory that is deleted while a sync is
 * already in flight.
 *
 * `resolvePathShapedRequests` and [buildSyncPlan] take separate read actions, so the read lock is
 * released between them and a `deps/` handle that passed the first check can be invalid by the time
 * the second one reads its children. Reading [VirtualFile.getChildren] on an invalidated directory
 * throws `InvalidVirtualFileAccessException`, so [buildSyncPlan] re-checks validity rather than
 * relying on the earlier check - an out-of-band `mix deps.clean` or a branch switch lands in exactly
 * that window.
 */
class MixDepsSyncServiceInvalidatedDepsRootHeavyTest : HeavyPlatformTestCase() {

    private lateinit var rootVf: VirtualFile
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

        mixModule = createModule("module_mix_root")
        PsiTestUtil.addContentRoot(mixModule, rootVf)
    }

    override fun tearDown() {
        runAll(
            { MixSyncTestHelpers.removeAllLibraries(project) },
            { super.tearDown() },
        )
    }

    /**
     * A `deps/` handle invalidated after its request was coalesced yields no library plans instead of
     * throwing out of the read phase.
     */
    fun testDepsRootInvalidatedAfterCoalescingYieldsNoLibraryPlans() {
        val depsVf = depsDirectory()
        val coalescedRequests = coalesceRequests(listOf(SyncRequest.DepsRoot(depsVf)))

        WriteAction.run<Throwable> { depsVf.delete(this) }
        assertFalse("deps/ must be invalid for this test to exercise the guard", depsVf.isValid)

        val syncPlan = MixSyncTestHelpers.runSuspendOnPooledThread { buildSyncPlan(project, coalescedRequests) }

        assertEmpty(syncPlan.libraryPlans)
    }

    /**
     * Control for [testDepsRootInvalidatedAfterCoalescingYieldsNoLibraryPlans]: the same request with a
     * live handle still plans the dep, so the guard cannot be satisfied by never reading the children.
     */
    fun testValidDepsRootPlansItsDeps() {
        val depsVf = depsDirectory()
        val coalescedRequests = coalesceRequests(listOf(SyncRequest.DepsRoot(depsVf)))

        val syncPlan = MixSyncTestHelpers.runSuspendOnPooledThread { buildSyncPlan(project, coalescedRequests) }

        assertEquals(listOf("phoenix"), syncPlan.libraryPlans.map { it.depName })
    }

    private fun depsDirectory(): VirtualFile =
        rootVf.findChild("deps") ?: error("mix_root/deps not found in VFS after refresh")
}
