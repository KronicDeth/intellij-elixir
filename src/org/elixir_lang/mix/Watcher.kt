package org.elixir_lang.mix

import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.elixir_lang.mix.sync.MixDepsSyncService
import org.elixir_lang.mix.sync.SyncRequest
import org.elixir_lang.mix.Project as MixProject

/**
 * Thin VFS listener adapter for `mix.exs` content changes.
 *
 * When a `mix.exs` file inside one of a module's content roots is saved, this listener
 * classifies the event as [SyncRequest.SyncModule] and enqueues it into [MixDepsSyncService]
 * for debounced, serialised execution. This callback is always lightweight: it does no PSI
 * access, no I/O, and no writes.
 *
 * Registered as a `<projectListeners>` entry in `plugin.xml` so the platform auto-instantiates
 * it with the correct [Project] per project lifetime.
 */
class Watcher(private val project: Project) : BulkFileListener {
    override fun after(events: MutableList<out VFileEvent>) {
        val service = project.service<MixDepsSyncService>()
        for (event in events) {
            if (event !is VFileContentChangeEvent) continue
            if (event.file.name != MixProject.MIX_EXS) continue
            val module = ModuleUtil.findModuleForFile(event.file, project) ?: continue
            val eventFileParent = event.file.parent
            val inContentRoot = ModuleRootManager.getInstance(module).contentRoots
                .any { contentRoot -> contentRoot == eventFileParent }
            if (inContentRoot) {
                service.enqueue(SyncRequest.SyncModule(module))
            }
        }
    }
}
