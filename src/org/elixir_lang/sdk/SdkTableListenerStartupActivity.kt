package org.elixir_lang.sdk

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

private val LOG = logger<SdkTableListenerStartupActivity>()

class SdkTableListenerStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        SdkTableListenerInitializationService.getInstance().ensureInitialized()
    }
}

@Service
class SdkTableListenerInitializationService {
    @Volatile
    private var initialized = false

    fun ensureInitialized() {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    LOG.debug("Setting up SDK table listeners")
                    org.elixir_lang.sdk.elixir.Type.setupSdkTableListener()
                    org.elixir_lang.sdk.erlang.Type.setupSdkTableListener()
                    initialized = true
                    LOG.debug("SDK table listeners initialized successfully")
                }
            }
        }
    }

    companion object {
        fun getInstance(): SdkTableListenerInitializationService = service()
    }
}
