package org.elixir_lang.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.SingleAlarm

/**
 * Debounces VFS bulk events so callers can coalesce many VFS callbacks into a single flush.
 *
 * Implementations should enqueue any relevant work in [enqueue] and perform the batched work in [flushPending].
 * This keeps VFS callbacks fast and avoids expensive work on the event thread.
 *
 * The parent [Disposable] should be [ElixirProjectDisposable], not the [com.intellij.openapi.project.Project] itself.
 */
abstract class DebouncedBulkFileListener(
    parentDisposable: Disposable,
    delayMs: Int
) : BulkFileListener {
    private val mergeAlarm = SingleAlarm({ flushPending() }, delayMs, parentDisposable)

    override fun after(events: MutableList<out VFileEvent>) {
        var hasRequests = false

        for (event in events) {
            if (enqueue(event)) {
                hasRequests = true
            }
        }

        if (hasRequests) {
            mergeAlarm.request()
        }
    }

    protected abstract fun enqueue(event: VFileEvent): Boolean

    protected abstract fun flushPending()
}
