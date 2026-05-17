package org.elixir_lang.mix.sync

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.testFramework.PlatformTestUtil
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Shared test helpers for [MixDepsSyncService] light and heavy test classes.
 *
 * Centralised here so that both [MixDepsSyncServiceTest] (light, [com.intellij.testFramework.fixtures.BasePlatformTestCase])
 * and [MixDepsSyncServiceHeavyTestBase] (heavy, [com.intellij.testFramework.HeavyPlatformTestCase]) use identical
 * implementations without duplicating ~50 lines of infrastructure across two different base classes.
 */
internal object MixSyncTestHelpers {

    /**
     * Runs a suspending [block] on a pooled thread while the EDT (test thread) continues pumping
     * its event queue.
     *
     * Required for calling [buildWritePlan] / [applyWritePlan] / [MixDepsSyncService.drain] directly
     * from tests: all three functions dispatch to the EDT internally (via
     * [com.intellij.openapi.application.readAction] / [com.intellij.openapi.application.edtWriteAction])
     * and would deadlock if invoked via plain [runBlocking] from the EDT test thread.
     */
    fun <T> runSuspendOnPooledThread(block: suspend () -> T): T {
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
            if (System.currentTimeMillis() >= deadline) {
                throw AssertionError("runSuspendOnPooledThread timed out after 10 s")
            }
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
        }
        error?.let { throw AssertionError("Suspended block failed: ${it.message}", it) }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    /**
     * Calls [MixDepsSyncService.drain] directly on a pooled thread (bypassing the 250 ms debounce),
     * while pumping the IDE event queue from the EDT test thread so that
     * [com.intellij.openapi.application.edtWriteAction] blocks dispatched by the drain coroutine
     * can execute.
     */
    fun drainDirectly(service: MixDepsSyncService) = runSuspendOnPooledThread { service.drain() }

    /**
     * Removes all project-level libraries, preventing cross-test leakage.
     *
     * The Kind guard used in production code (to protect user-created libraries) does not apply
     * here: in tests every library in the project table was created by the test itself, so all
     * of them must be removed unconditionally.
     */
    fun removeAllLibraries(project: Project) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val toRemove = libraryTable.libraries.toList()
        if (toRemove.isNotEmpty()) {
            WriteAction.run<Throwable> { toRemove.forEach { libraryTable.removeLibrary(it) } }
        }
    }
}
