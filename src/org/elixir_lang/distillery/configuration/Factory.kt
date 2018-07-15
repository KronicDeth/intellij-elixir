package org.elixir_lang.distillery.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.elixir_lang.distillery.Configuration

object Factory : ConfigurationFactory(Type.getInstance()) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration =
            Configuration("Distillery Release CLI", project, this)
}
