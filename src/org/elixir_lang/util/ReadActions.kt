package org.elixir_lang.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import java.util.concurrent.Callable

/**
 * Read-action helper for blocking callers whose thread context varies.
 *
 * Mirrors [WriteActions] for the read side: use it for model reads in non-suspending code that can
 * be reached both from a caller that already holds the read lock (or the EDT, which holds
 * write-intent) and from a bare pooled thread, such as
 * [com.intellij.openapi.projectRoots.SdkType.setupSdkPaths].
 *
 * Code that always runs in one known context should keep taking the lock explicitly - prefer a
 * `@RequiresReadLock` contract on the caller where the caller can honour it.
 */
object ReadActions {
    /**
     * Runs [action] under a read action, or directly when the lock is already held.
     *
     * The guard is load-bearing, not an optimisation: when
     * [com.intellij.openapi.application.NonBlockingReadAction.executeSynchronously] cannot start the
     * computation immediately it waits in `blockUntilWriteActionIsDone`, which asserts that the
     * calling thread holds *no* read access. Entering it while already holding the lock - or on the
     * EDT, where read access is always allowed - would trip that assertion.
     */
    fun <T> compute(action: () -> T): T =
        if (ApplicationManager.getApplication().isReadAccessAllowed) {
            action()
        } else {
            ReadAction.nonBlocking(Callable { action() }).executeSynchronously()
        }
}
