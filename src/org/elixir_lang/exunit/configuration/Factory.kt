package org.elixir_lang.exunit.configuration

import com.intellij.execution.BeforeRunTask
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import org.elixir_lang.configuration.BeforeRunTaskProvider
import org.elixir_lang.exunit.Configuration

object Factory : ConfigurationFactory(Type.INSTANCE) {
    override fun configureBeforeRunTaskDefaults(providerID: Key<out BeforeRunTask<*>>?, task: BeforeRunTask<*>?) {
        val provider = ApplicationManager.getApplication().getService(BeforeRunTaskProvider::class.java)

        if (provider.isBuildStep(providerID)) {
            task!!.isEnabled = false
        }
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
            Configuration(Type.TYPE_NAME, project)

    override fun getId(): String = Type.TYPE_NAME
}
