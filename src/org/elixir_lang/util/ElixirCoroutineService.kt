package org.elixir_lang.util

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Provides a project-scoped [CoroutineScope] for non-service code (e.g. UI factories,
 * tool-window components) that cannot receive a scope via constructor injection.
 *
 * The scope is automatically cancelled by the platform when the project is closed.
 *
 * Usage (child scope, cancelled on component dispose):
 * ```kotlin
 * val cs = project.service<ElixirCoroutineService>().supervisedChildScope(javaClass.simpleName)
 * // ...
 * override fun dispose() { cs.cancel() }
 * ```
 */
@Service(Service.Level.PROJECT)
class ElixirCoroutineService(
    val scope: CoroutineScope,
) {
    /**
     * Creates a child [CoroutineScope] scoped to the caller's lifecycle.
     *
     * The returned scope is a supervised child of the project scope: it is cancelled automatically
     * when the project closes, but its failure does not affect sibling scopes. The caller is
     * responsible for cancelling it when its own lifecycle ends (e.g. in `Disposable.dispose()`).
     *
     * @param name used for coroutine debugging and thread-dump identification.
     */
    fun supervisedChildScope(name: String): CoroutineScope = CoroutineScope(scope.coroutineContext + SupervisorJob(scope.coroutineContext[Job]) + CoroutineName(name))
}
