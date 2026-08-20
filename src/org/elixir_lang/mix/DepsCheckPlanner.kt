package org.elixir_lang.mix

/**
 * Per-root status cached from the last [DepsCheckerService.checkDepsStatus] run.
 *
 * Keyed by root URL. Protected by [DepsCheckerService]'s `checkMutex`; no concurrent
 * access from outside the lock is permitted.
 *
 * Moved here from `DepsCheckerService` so that [DepsCheckPlanner] (and therefore tests) can
 * reference the type without importing the service.
 */
internal sealed interface CachedRootStatus {
    data object Ok : CachedRootStatus
    data object NonOk : CachedRootStatus
    data class Error(val message: String) : CachedRootStatus
    data object Unsupported : CachedRootStatus
}

/**
 * Result of [DepsCheckPlanner.computeSdkDelta].
 *
 * @property changedRootUrls Root URLs whose effective Elixir SDK name changed relative to
 *   the previous snapshot (includes roots newly assigned any SDK, and roots whose SDK was
 *   cleared or renamed).
 * @property removedRootExists `true` if the previous snapshot referenced at least one root
 *   URL that is no longer present in the current top-level root list (i.e. a root was removed
 *   from the project).
 */
internal data class SdkDelta(
    val changedRootUrls: Set<String>,
    val removedRootExists: Boolean,
)

/**
 * Pure, stateless helper that encapsulates the cache-selection and SDK-delta logic for
 * [DepsCheckerService].
 *
 * Every method takes plain data inputs (URL strings, immutable maps) and returns plain data
 * outputs - no coroutines, no VFS/platform I/O, no message-bus interaction.  This makes the
 * algorithms directly testable with plain `junit.framework.TestCase` tests, without needing
 * a `com.intellij.testFramework.fixtures.BasePlatformTestCase` fixture.
 */
internal object DepsCheckPlanner {

    /**
     * Returns the ordered subset of [allTopLevelRootUrls] whose deps status should actually
     * be re-queried, given the current scheduling state.
     *
     * Selection rules (evaluated in priority order):
     *
     * 1. **Pending roots present** - re-query the pending roots that are still in the project
     *    plus any live root that has no cached entry yet.  Pending roots that are no longer in
     *    the project are silently dropped.
     * 2. **Topology-only flag set, no pending roots** - re-query only live roots that have no
     *    cached entry (cache-warm path after a root removal or SDK topology change with no
     *    changed roots).
     * 3. **Neither** (startup or explicit full check) - re-query all top-level roots.
     *
     * The returned list preserves stable ordering (pending-first, then uncached) and is
     * deduplicated.
     *
     * @param allTopLevelRootUrls Ordered list of all current top-level Mix root URLs.
     * @param pendingRootUrls URLs of roots whose deps files changed during the debounce window.
     * @param cachedOnlyWhenNoPending If `true` and [pendingRootUrls] is empty, only uncached
     *   roots are returned (topology-only path).
     * @param existingCacheKeys Set of root URLs that already have a cached status entry.
     */
    fun selectRootsToCheck(
        allTopLevelRootUrls: List<String>,
        pendingRootUrls: Set<String>,
        cachedOnlyWhenNoPending: Boolean,
        existingCacheKeys: Set<String>,
    ): List<String> {
        val currentSet = allTopLevelRootUrls.toHashSet()
        return when {
            pendingRootUrls.isNotEmpty() -> {
                val pendingInProject = pendingRootUrls.filter { it in currentSet }
                val uncached = allTopLevelRootUrls.filter { it !in existingCacheKeys }
                (pendingInProject + uncached).distinct()
            }
            cachedOnlyWhenNoPending -> allTopLevelRootUrls.filter { it !in existingCacheKeys }
            else -> allTopLevelRootUrls.toList()
        }
    }

    /**
     * Computes the delta between [previousSdkByRootUrl] and [currentSdkByRootUrl].
     *
     * A root URL is included in [SdkDelta.changedRootUrls] when its effective Elixir SDK name
     * differs between the two snapshots (null → non-null, non-null → null, or one name →
     * another).  Only URLs present in [allTopLevelRootUrls] are considered.
     *
     * [SdkDelta.removedRootExists] is `true` if any URL in [previousSdkByRootUrl] is absent
     * from [currentSdkByRootUrl], indicating that a root was removed from the project between
     * the two snapshots.
     *
     * @param allTopLevelRootUrls Ordered list of all current top-level Mix root URLs.
     * @param currentSdkByRootUrl Map from root URL to the *current* Elixir SDK name (or null).
     * @param previousSdkByRootUrl Map from root URL to the *previous* Elixir SDK name snapshot.
     */
    fun computeSdkDelta(
        allTopLevelRootUrls: List<String>,
        currentSdkByRootUrl: Map<String, String?>,
        previousSdkByRootUrl: Map<String, String?>,
    ): SdkDelta {
        val changedUrls = LinkedHashSet<String>()
        for (url in allTopLevelRootUrls) {
            if (previousSdkByRootUrl[url] != currentSdkByRootUrl[url]) {
                changedUrls.add(url)
            }
        }
        val removedExists = previousSdkByRootUrl.keys.any { it !in currentSdkByRootUrl }
        return SdkDelta(changedUrls, removedExists)
    }
}
