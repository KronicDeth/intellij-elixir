package org.elixir_lang.mix

import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.testFramework.ExtensionTestUtil
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.mix.sync.MixSyncTestHelpers
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.package_manager.*
import org.elixir_lang.package_manager.DepGatherer
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.elixir_lang.PackageManager as RootPackageManager

/**
 * Shared test infrastructure for [DepsCheckerService] test classes.
 *
 * Provides:
 * - a fake `packageManager` EP that delegates to [statusByRootUrl] and records call counts,
 * - [setUp] that masks the EP, clears state, and resets the service,
 * - [tearDown] that clears the outdated notification and resets the service before delegating
 *   to the platform teardown,
 * - [depsStatus], [depsStatusCallCount], [resetDepsStatusCallCounts],
 * - [withMixDepsCheckEnabled] with an `enabled` parameter (defaults `true`),
 * - [runSuspendOnPooledThread] and [waitUntil] wait helpers.
 *
 * Subclasses add their own fixture topology and any helpers specific to the scenario
 * under test (e.g. facet management, VFS event firing, SDK registration).
 */
abstract class DepsCheckerServiceTestBase : PlatformTestCase() {

    private val packageManagerEp =
        ExtensionPointName.create<RootPackageManager>("org.elixir_lang.packageManager")

    /** Keyed by root URL. Set by each test before invoking the service. */
    protected val statusByRootUrl = mutableMapOf<String, DepsStatusResult>()

    /** Keyed by root URL. Accumulates across `checkDepsStatus` calls within one test. */
    protected val depsStatusCallCountByRootUrl = mutableMapOf<String, Int>()

    private val fakePackageManager = object : RootPackageManager {
        override val fileName: String = "mix.exs"

        override fun depGatherer(): DepGatherer = object : DepGatherer() {}

        override fun depsStatus(
            project: com.intellij.openapi.project.Project,
            packageVirtualFile: com.intellij.openapi.vfs.VirtualFile,
            sdk: Sdk?,
        ): DepsStatusResult {
            val rootUrl = packageVirtualFile.parent?.url ?: return DepsStatusResult.Unsupported
            depsStatusCallCountByRootUrl[rootUrl] = (depsStatusCallCountByRootUrl[rootUrl] ?: 0) + 1
            return statusByRootUrl[rootUrl] ?: DepsStatusResult.Unsupported
        }
    }

    override fun setUp() {
        super.setUp()
        ExtensionTestUtil.maskExtensions(packageManagerEp, listOf(fakePackageManager), testRootDisposable)
        statusByRootUrl.clear()
        depsStatusCallCountByRootUrl.clear()
        project.service<DepsCheckerService>().resetState()
    }

    /**
     * Clears the outdated notification and resets the service before delegating to the
     * platform teardown. Subclasses should call `super.tearDown()` as the last item in
     * their own `runAll` block so that this cleanup runs after subclass-specific teardown.
     */
    override fun tearDown() {
        runAll(
            { Notifier.clearMixDepsOutdated(project) },
            { project.service<DepsCheckerService>().resetState() },
            { super.tearDown() },
        )
    }

    // ── Shared test helpers ───────────────────────────────────────────────────

    protected fun depsStatus(depState: DepState): DepsStatusResult.Available =
        DepsStatusResult.Available(DepsStatus(listOf(DepStatus("phoenix", depState))))

    protected fun depsStatusCallCount(root: com.intellij.openapi.vfs.VirtualFile): Int =
        depsStatusCallCountByRootUrl[root.url] ?: 0

    protected fun resetDepsStatusCallCounts() {
        depsStatusCallCountByRootUrl.clear()
    }

    /**
     * Temporarily overrides the `enableMixDepsCheck` setting for the duration of [block].
     *
     * Pass `enabled = false` to bypass every settings-gated path: the
     * [DepsCheckerService.runCheck] early-return gate **and** the topology/VFS listener
     * gates. Use it for tests that drive [DepsCheckerService.checkDepsStatus]
     * directly without relying on a listener to queue work.
     *
     * Pass `enabled = true` (or omit the argument) when the test fires a real
     * `JDK_TABLE_TOPIC` / `rootsChanged` / VFS event and expects the listener to compute a
     * delta and queue pending roots, or when testing the full notification pipeline through
     * [DepsCheckerService.runCheck]. The setting is restored on block exit, so the debounced
     * collector's deferred `runCheck` becomes a no-op again once the (fast) block completes.
     */
    protected fun withMixDepsCheckEnabled(enabled: Boolean = true, block: () -> Unit) {
        val state = ElixirExperimentalSettings.instance.state
        val original = state.enableMixDepsCheck
        state.enableMixDepsCheck = enabled
        try {
            block()
        } finally {
            state.enableMixDepsCheck = original
        }
    }

    /**
     * Runs a suspending [block] on a pooled thread while the EDT (test thread) pumps
     * its event queue. Delegates to [MixSyncTestHelpers.runSuspendOnPooledThread].
     *
     * Required whenever the block dispatches work back to the EDT (via
     * [com.intellij.openapi.application.readAction] or
     * [com.intellij.openapi.application.edtWriteAction]), which would otherwise deadlock
     * if [kotlinx.coroutines.runBlocking] were called directly on the EDT test thread.
     */
    protected fun <T> runSuspendOnPooledThread(block: suspend () -> T): T =
        MixSyncTestHelpers.runSuspendOnPooledThread(block)

    /**
     * Polls [condition] in a tight loop, pumping the IDE event queue on each iteration
     * and yielding briefly between polls.
     *
     * Pumping lets EDT-dispatched work from the `checkFlow` collector (e.g. the
     * `withContext(Dispatchers.EDT)` notification dispatch inside [DepsCheckerService.runCheck])
     * execute while the test thread waits. The short sleep reduces CPU spin while still
     * reacting quickly enough for both topology-change (sub-second) and debounced (1.5 s+)
     * scenarios.
     *
     * Fails with [message] if [condition] does not become `true` within [timeoutMs].
     */
    protected fun waitUntil(message: String, timeoutMs: Long = 10_000, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (!condition()) {
            if (System.currentTimeMillis() >= deadline) {
                throw AssertionError(message)
            }
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
            Thread.sleep(20)
        }
    }
}
