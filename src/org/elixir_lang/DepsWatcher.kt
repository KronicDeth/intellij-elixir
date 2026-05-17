package org.elixir_lang

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.elixir_lang.mix.sync.MixDepsSyncService
import org.elixir_lang.mix.sync.classifyVfsEvent

/**
 * Thin VFS listener adapter for Mix `deps/` and `_build/` structural changes.
 *
 * Each VFS event is classified by [classifyVfsEvent] and, if relevant, enqueued into
 * [MixDepsSyncService] for debounced, serialised execution. This callback is always lightweight:
 * it does no PSI access, no I/O, and no writes - only path inspection and a non-suspending
 * [MixDepsSyncService.enqueue] call.
 *
 * The actual sync/delete work now lives entirely in [MixDepsSyncService].
 *
 * Registered as a `<projectListeners>` entry in `plugin.xml` so the platform auto-instantiates
 * it with the correct [Project] per project lifetime.
 */
class DepsWatcher(val project: Project) : BulkFileListener {
    override fun after(events: MutableList<out VFileEvent>) {
        val service = project.service<MixDepsSyncService>()
        for (event in events) {
            classifyVfsEvent(project, event)?.let { service.enqueue(it) }
        }
    }
}
