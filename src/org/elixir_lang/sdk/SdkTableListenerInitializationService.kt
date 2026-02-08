package org.elixir_lang.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.util.concurrency.AppExecutorUtil

private val logger = Logger.getInstance(SdkTableListenerInitializationService::class.java)

@Service
class SdkTableListenerInitializationService {
    @Volatile
    private var initialized = false

    init {
        ensureInitialized()
    }

    fun ensureInitialized() {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    logger.trace { "Setting up SDK table listeners" }
                    org.elixir_lang.sdk.elixir.Type.setupSdkTableListener()
                    org.elixir_lang.sdk.erlang.Type.setupSdkTableListener()
                    subscribeToSdkTableChanges()
                    initialized = true
                    logger.trace { "SDK table listeners initialized successfully" }
                }
            }
        }
    }

    private fun subscribeToSdkTableChanges() {
        val messageBus = ApplicationManager.getApplication().messageBus
        messageBus.connect().subscribe(ProjectJdkTable.JDK_TABLE_TOPIC, object : ProjectJdkTable.Listener {
            override fun jdkAdded(jdk: Sdk) {
                val elixirSdkType = org.elixir_lang.sdk.elixir.Type.instance
                if (jdk.sdkType === elixirSdkType) {
                    logger.trace { "Elixir SDK added: ${jdk.name}; warming CLI caches" }
                    warmCliDryRunCaches(listOf(jdk))
                }
            }

            override fun jdkRemoved(jdk: Sdk) {
                invalidateCliDryRunCaches(jdk)
            }

            override fun jdkNameChanged(jdk: Sdk, previousName: String) {
                invalidateCliDryRunCaches(jdk)
            }
        })
    }

    private fun warmCliDryRunCaches(elixirSdks: List<Sdk>) {
        if (elixirSdks.isEmpty()) return
        val projects = ProjectManager.getInstance().openProjects
        if (projects.isEmpty()) return

        AppExecutorUtil.getAppExecutorService().execute {
            for (project in projects) {
                if (project.isDisposed) continue
                val cache = org.elixir_lang.ElixirCliDryRunCache.getInstance(project)
                cache.warmUp(elixirSdks)
            }
            logger.trace { "Elixir CLI dry-run cache warm-up scheduled" }
        }
    }

    private fun invalidateCliDryRunCaches(sdk: Sdk) {
        val projects = ProjectManager.getInstance().openProjects
        if (projects.isEmpty()) return

        for (project in projects) {
            if (project.isDisposed) continue
            val cache = org.elixir_lang.ElixirCliDryRunCache.getInstance(project)
            cache.invalidateForHomePath(sdk.homePath)
        }
    }

    companion object {
        fun getInstance(): SdkTableListenerInitializationService = service()
    }
}
