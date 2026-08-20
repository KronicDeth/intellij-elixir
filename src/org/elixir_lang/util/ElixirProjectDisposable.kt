package org.elixir_lang.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * Project-scoped parent disposable for plugin resources.
 *
 * Avoid using [Project] itself as a parent disposable in plugin code because it outlives the plugin on unload.
 * See the IntelliJ Platform ["Choosing a Disposable Parent"](https://plugins.jetbrains.com/docs/intellij/disposers.html#choosing-a-disposable-parent) guidance.
 */
@Service(Service.Level.PROJECT)
class ElixirProjectDisposable : Disposable {
    override fun dispose() {}

    companion object {
        fun getInstance(project: Project): ElixirProjectDisposable = project.service()
    }
}
