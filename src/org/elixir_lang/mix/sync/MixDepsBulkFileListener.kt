package org.elixir_lang.mix.sync

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

/**
 * Canonical VFS listener for Mix dep sync requests.
 *
 * The callback is intentionally bounded: iterate the supplied events, classify cheap path shape,
 * enqueue into [MixDepsSyncService], and return. Module/content-root ownership is resolved later
 * by the service under read access.
 */
class MixDepsBulkFileListener(private val project: Project) : BulkFileListener {
    override fun after(events: MutableList<out VFileEvent>) {
        val service = project.service<MixDepsSyncService>()
        for (event in events) {
            classifyVfsEvent(event)?.let { request -> service.enqueue(request) }
        }
    }
}
