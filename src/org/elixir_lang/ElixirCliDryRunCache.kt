package org.elixir_lang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.util.concurrency.AppExecutorUtil
import org.elixir_lang.sdk.HomePath
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
internal class ElixirCliDryRunCache(private val project: Project) {
    private val cache = ConcurrentHashMap<CacheKey, CompletableFuture<kotlin.collections.List<String>?>>()
    private val executor = AppExecutorUtil.getAppExecutorService()

    fun getOrComputeBaseArguments(
        tool: ElixirCliDryRun.Tool,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
    ): kotlin.collections.List<String>? {
        if (project.isDisposed) {
            return null
        }
        val sdkHome = elixirSdk.homePath ?: return null
        val key = CacheKey(tool, sdkHome, environmentSignature(environment))
        val newFuture = CompletableFuture<kotlin.collections.List<String>?>()
        val future = cache.putIfAbsent(key, newFuture) ?: newFuture

        if (future === newFuture) {
            if (ApplicationManager.getApplication().isDispatchThread) {
                executor.execute {
                    computeAndComplete(newFuture, key, tool, environment, workingDirectory, elixirSdk)
                }
                return null
            }
            return computeAndComplete(newFuture, key, tool, environment, workingDirectory, elixirSdk)
        }

        return awaitFuture(future, key)
    }

    fun warmUp(elixirSdks: Iterable<Sdk>) {
        if (project.isDisposed) {
            return
        }
        for (sdk in elixirSdks) {
            warmUpSdk(sdk)
        }
    }

    fun invalidateForHomePath(homePath: String?) {
        if (homePath == null) {
            return
        }
        cache.keys.removeIf { it.sdkHome == homePath }
    }

    private fun warmUpSdk(elixirSdk: Sdk) {
        for (tool in ElixirCliDryRun.Tool.entries) {
            val environment = mutableMapOf<String, String>()
            if (tool == ElixirCliDryRun.Tool.MIX) {
                HomePath.maybeUpdateMixHome(environment, elixirSdk.homePath)
            }
            val workingDirectory = elixirSdk.homePath
            executor.execute {
                getOrComputeBaseArguments(tool, environment, workingDirectory, elixirSdk)
            }
        }
    }

    private fun computeAndComplete(
        future: CompletableFuture<kotlin.collections.List<String>?>,
        key: CacheKey,
        tool: ElixirCliDryRun.Tool,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
    ): kotlin.collections.List<String>? {
        val result = try {
            ElixirCliDryRun.baseArguments(tool, environment, workingDirectory, elixirSdk)
        } catch (_: Exception) {
            null
        }
        future.complete(result)
        if (result == null) {
            cache.remove(key, future)
        }
        return result
    }

    private fun awaitFuture(
        future: CompletableFuture<kotlin.collections.List<String>?>,
        key: CacheKey,
    ): kotlin.collections.List<String>? {
        if (ApplicationManager.getApplication().isDispatchThread) {
            return if (future.isDone && !future.isCompletedExceptionally) {
                future.getNow(null)
            } else {
                null
            }
        }

        return try {
            future.get()
        } catch (_: Exception) {
            cache.remove(key, future)
            null
        }
    }

    private fun environmentSignature(environment: Map<String, String>): Int {
        if (environment.isEmpty()) {
            return 0
        }
        var hash = 1
        environment.toSortedMap().forEach { (key, value) ->
            hash = 31 * hash + key.hashCode()
            hash = 31 * hash + value.hashCode()
        }
        return hash
    }

    private data class CacheKey(
        val tool: ElixirCliDryRun.Tool,
        val sdkHome: String,
        val environmentSignature: Int,
    )

    companion object {
        internal fun getInstance(project: Project): ElixirCliDryRunCache = project.service()
    }
}
