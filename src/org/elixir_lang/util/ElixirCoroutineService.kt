package org.elixir_lang.util

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Creates a supervised child scope from an existing parent [CoroutineScope].
 *
 * Use this helper when a class already receives a lifecycle-bound scope and must create a
 * child scope for a nested component it constructs (for example, `Process -> Node -> MailBox`).
 *
 * Why this helper exists:
 * - Keeps child-scope construction consistent across the codebase.
 * - Uses a [SupervisorJob] so failure in one child does not cancel sibling children.
 * - Adds a [CoroutineName] for easier diagnostics in coroutine dumps and logs.
 *
 * JetBrains guidance:
 * - https://plugins.jetbrains.com/docs/intellij/launching-coroutines.html
 * - https://plugins.jetbrains.com/docs/intellij/coroutine-dispatchers.html
 */
fun CoroutineScope.supervisedChildScope(name: String): CoroutineScope =
    CoroutineScope(coroutineContext + SupervisorJob(coroutineContext[Job]) + CoroutineName(name))

/**
 * Provides a project-scoped [CoroutineScope] for non-service code (e.g. UI factories,
 * tool-window components) that cannot receive a scope via constructor injection.
 *
 * Use this service as the project-lifetime scope anchor, then derive child scopes with
 * [supervisedChildScope].
 *
 * The injected service scope is cancelled automatically by the platform when the project is
 * closed or when the plugin is unloaded.
 *
 * JetBrains guidance:
 * - https://plugins.jetbrains.com/docs/intellij/launching-coroutines.html
 * - https://plugins.jetbrains.com/docs/intellij/execution-contexts.html
 *
 * Usage (child scope, cancelled on component dispose):
 * ```kotlin
 * val cs = project.service<ElixirCoroutineService>().supervisedChildScope(javaClass.simpleName)
 * // ...
 * // Cancel early at component teardown; project close/plugin unload is the outer safety net.
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
     * The returned scope is a supervised child of the project service scope: it is cancelled
     * automatically when the project closes or the plugin is unloaded, but its failure does not
     * affect sibling scopes. The caller should still cancel it when its own lifecycle ends
     * (e.g. in `Disposable.dispose()`) so work stops as soon as the component is disposed rather
     * than waiting for project/plugin teardown.
     *
     * Prefer this method when you need to derive the first component-level scope from a project
     * (for non-service classes that cannot receive injected scopes directly).
     *
     * @param name used for coroutine debugging and thread-dump identification.
     */
    fun supervisedChildScope(name: String): CoroutineScope = scope.supervisedChildScope(name)
}

/**
 * Provides an application-scoped [CoroutineScope] for non-service code that operates
 * outside any particular project (e.g. global SDK management, application-wide settings).
 *
 * The injected service scope is cancelled automatically by the platform when the application
 * shuts down or when the plugin is unloaded.
 *
 * Prefer [ElixirCoroutineService] (project-level) whenever a [com.intellij.openapi.project.Project]
 * is available - application-level scopes live longer and are only cancelled on IDE shutdown.
 *
 * Usage:
 * ```kotlin
 * val cs = service<ElixirAppCoroutineService>().supervisedChildScope(javaClass.simpleName)
 * // ...
 * override fun dispose() { cs.cancel() }
 * ```
 */
@Suppress("unused")
@Service(Service.Level.APP)
class ElixirAppCoroutineService(
    val scope: CoroutineScope,
) {
    /**
     * Creates a child [CoroutineScope] scoped to the caller's lifecycle.
     *
     * @param name used for coroutine debugging and thread-dump identification.
     * @see ElixirCoroutineService.supervisedChildScope for project-scoped equivalent.
     */
    fun supervisedChildScope(name: String): CoroutineScope = scope.supervisedChildScope(name)
}
