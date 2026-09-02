package org.elixir_lang.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import org.elixir_lang.mix.sync.MixDepsSyncService
import org.elixir_lang.mix.sync.SyncRequest
import org.elixir_lang.notification.setup_sdk.Notifier

/**
 * Syncs every dependency library with what is currently on disk.
 *
 * The sync service is otherwise driven only by VFS events under `deps/` and `_build/`, plus a
 * first-time project configuration, so a project whose libraries are wrong has no way back: `mix
 * deps.get` on already-fetched deps changes nothing on disk and therefore produces no events, and
 * reopening the project does not re-sync one that is already configured.
 */
class SyncDependencyLibrariesAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        project.service<MixDepsSyncService>().enqueue(SyncRequest.All)

        Notifier.dependencyLibrarySyncScheduled(project)
    }

    override fun isDumbAware(): Boolean = true
}
