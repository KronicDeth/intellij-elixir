package org.elixir_lang.iex.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.elixir_lang.iex.Configuration

object Factory : ConfigurationFactory(Type.getInstance()) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration =
            Configuration("IEx", project, this)
}
