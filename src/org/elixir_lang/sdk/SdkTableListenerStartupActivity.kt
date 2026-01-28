package org.elixir_lang.sdk

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

private val LOG = logger<SdkTableListenerStartupActivity>()

class SdkTableListenerStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        LOG.trace("Initializing SDK table listener service from startup activity")
        SdkTableListenerInitializationService.getInstance().ensureInitialized()
    }
}
